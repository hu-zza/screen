package hu.zza.screen.core;

import java.nio.file.Path;

final class Converters {
    static Path fromPathString(String path) {
        return Path.of(path);
    }
}
