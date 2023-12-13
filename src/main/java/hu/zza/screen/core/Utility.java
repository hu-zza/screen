package hu.zza.screen.core;

import java.util.List;

import hu.zza.screen.core.model.Input;
import hu.zza.screen.core.model.TestCase;

public final class Utility {

    private Utility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Class<?>[] getParameters(TestCase testCase) {
        return getParameters(testCase.getInputs());
    }

    public static Class<?>[] getParameters(List<Input> inputs) {
        return inputs.stream()
                .map(Input::getType)
                .toArray(Class[]::new);
    }
}
