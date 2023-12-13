package hu.zza.screen.core;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.UUID;

import org.opentest4j.AssertionFailedError;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import hu.zza.screen.core.model.CheckRequest;
import hu.zza.screen.core.model.InitRequest;
import hu.zza.screen.core.model.Input;
import hu.zza.screen.core.model.SimpleProblemDetail;
import hu.zza.screen.core.model.TestCase;


public final class TestManager {

    private static final URI ENDPOINT = URI.create("https://erre.hu/screen/");
    private static final URI ENDPOINT_INIT = ENDPOINT.resolve("init.php");
    private static final URI ENDPOINT_CHECK = ENDPOINT.resolve("check.php");
    private static final int MAX_TEST_PER_TASK = 1000;
    private static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(30);
    private static final HttpClient CLIENT =
            HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(Redirect.NORMAL)
                    .connectTimeout(CONNECTION_TIMEOUT)
                    .build();
    private static final int HTTP_COMPLETED = 299;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateTask(UUID taskId) {
        try {
            var request = buildInitialRequest(taskId);
            validate(request);
        } catch (Exception e) {
            throw new AssertionFailedError(String.format("Cannot validate the task: %s", taskId), e);
        }
    }

    private static HttpRequest buildInitialRequest(UUID taskId) throws JsonProcessingException {
        var request = new InitRequest(taskId);
        var payload = MAPPER.writeValueAsString(request);

        return HttpRequest.newBuilder(ENDPOINT_INIT)
                .POST(BodyPublishers.ofString(payload))
                .build();
    }

    private static void validate(HttpRequest request) throws Exception {
        HttpResponse<String> response;
        for (int i = 0; i < MAX_TEST_PER_TASK; i++) {
            response = CLIENT.send(request, BodyHandlers.ofString());

            if (response.statusCode() == HTTP_COMPLETED) {
                System.out.println(response.body());
                break;
            }
            var testCase = extractTestCase(response);
            var result = startTestCase(testCase.getMethod(), testCase);
            request = buildCheckRequest(testCase, result);
        }
    }

    private static TestCase extractTestCase(HttpResponse<String> response) throws Exception {
        var body = response.body().replaceAll("\\p{C}", "");

        if (response.statusCode() == HTTP_OK) {
            return MAPPER.readValue(body, TestCase.class);
        }
        throw new AssertionFailedError(getProblemDetail(body));
    }

    private static String getProblemDetail(String body) {
        SimpleProblemDetail problem;
        try {
            problem = MAPPER.readValue(body, SimpleProblemDetail.class);
        } catch (Exception e) {
            return body;
        }
        return problem.toString();
    }

    private static Object startTestCase(Method method, TestCase testCase) throws Exception {
        if (testCase.getOutputType().isAssignableFrom(Throwable.class)) {

            var expected = (Class<? extends Throwable>) testCase.getOutputType();
            var exception = assertThrows(expected, () -> invokeImplementation(method, testCase));
            return exception.getMessage();
        }
        return invokeImplementation(method, testCase);
    }

    private static Object invokeImplementation(Method method, TestCase testCase) throws Exception {
        return method.invoke(null, getArguments(testCase));
    }

    private static Object[] getArguments(TestCase testCase) {
        return testCase.getInputs().stream()
                .map(TestManager::prepareInput)
                .toArray();
    }

    private static Object prepareInput(Input input) throws RuntimeException {
        if (input.getValue() == null) {
            return null;
        }
        try {
            return getParserMethod(input).invoke(null, input.getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Method getParserMethod(Input input) throws Exception {
        var type = input.getType();
        switch (type.getCanonicalName()) {
            case "java.nio.file.Path":
                return Converters.class.getDeclaredMethod("fromPathString", String.class);
            case "java.time.LocalDate":
            case "java.time.LocalTime":
            case "java.time.LocalDateTime":
            case "java.time.OffsetDateTime":
            case "java.time.ZonedDateTime":
                return type.getDeclaredMethod("parse", CharSequence.class);
            case "java.lang.Integer":
            case "java.lang.Long":
            case "java.lang.Double":
                return type.getDeclaredMethod("valueOf", String.class);
            default:
                return String.class.getDeclaredMethod("valueOf", Object.class);
        }
    }

    private static HttpRequest buildCheckRequest(TestCase testCase, Object result) throws JsonProcessingException {
        var request = new CheckRequest(testCase.getTestId(), result);
        var payload = MAPPER.writeValueAsString(request);

        return HttpRequest.newBuilder(ENDPOINT_CHECK)
                .POST(BodyPublishers.ofString(payload))
                .build();
    }
}
