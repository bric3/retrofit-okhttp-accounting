package com.github.bric3.accountable;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class Suppliers {
    public static <T> Supplier<T> memoize(Supplier<T> delegate) {
        AtomicReference<T> value = new AtomicReference<>();
        return () -> {
            T val = value.get();
            if (val == null) {
                val = value.updateAndGet(cur -> cur == null ?
                                                Objects.requireNonNull(delegate.get()) : cur);
            }
            return val;
        };
    }
}
