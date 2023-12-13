package hu.zza.screen.core.model;

import static hu.zza.screen.core.Utility.getParameters;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TestCase {

    private static final String TASK_BINARY_NAME = "hu.zza.screen.Task";
    private static final Class<?> TASK_CLASS;

    static {
        try {
            TASK_CLASS = Class.forName(TASK_BINARY_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot initiate the test suit", e);
        }
    }

    private final UUID testId;
    private final Method method;
    private final List<Input> inputs;
    private final Class<?> outputType;

    TestCase(
            @JsonProperty("test_id") String testId,
            @JsonProperty("method_name") String methodName,
            @JsonProperty("inputs") List<Input> inputs,
            @JsonProperty("output_type") String outputType
    ) throws ClassNotFoundException, NoSuchMethodException {
        this.testId = UUID.fromString(testId);
        this.method = TASK_CLASS.getDeclaredMethod(methodName, getParameters(inputs));
        this.inputs = List.copyOf(inputs);
        this.outputType = Class.forName(outputType);
    }

    public UUID getTestId() {
        return testId;
    }

    public Method getMethod() {
        return method;
    }

    public List<Input> getInputs() {
        return List.copyOf(inputs);
    }

    public Class<?> getOutputType() {
        return outputType;
    }
}
