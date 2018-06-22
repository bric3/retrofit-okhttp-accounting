package com.github.bric3.accoutable;

import java.util.concurrent.atomic.AtomicBoolean;

class RequestMetadata {
    private String method;
    private String path;
    private AtomicBoolean read = new AtomicBoolean(false);

    public static RequestMetadata of(String method, String path) {
        return new RequestMetadata(method, path);
    }

    private RequestMetadata(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public static RequestMetadata placeHolder() {
        return new RequestMetadata();
    }

    private RequestMetadata() {
    }

    public RequestMetadata setFrom(RequestMetadata other) {
        if (read.get()) {
            throw new IllegalStateException("Can't mutate this class after it has been read");
        }
        this.method = other.method;
        this.path = other.path;
        return this;
    }

    public String method() {
        read.set(true);
        return method;
    }

    public String path() {
        read.set(true);
        return path;
    }

    @Override
    public String toString() {
        read.set(true);
        return method + " " + path;
    }
}
