package dsinczak.fp.validation.javadsl.cf;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.stream.Collectors.toList;

abstract class CfCommon {
    private CfCommon() {
    }

    static <T> CompletableFuture<List<T>> sequence(CompletableFuture<T>[] cfs) {
        return allOf(cfs)
                .thenApply(ignore -> Arrays.stream(cfs)
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

    static <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> cfs) {
        return allOf(cfs.toArray(CompletableFuture[]::new))
                .thenApply(ignore -> cfs.stream()
                        .map(CompletableFuture::join)
                        .collect(toList())
                );
    }

    static <T extends Throwable> T softenedException(final T e) {
        uncheck(e);
        return e; //never reached.
    }

    static <T extends Throwable> void uncheck(Throwable throwable) throws T {
        throw (T) throwable;
    }

}
