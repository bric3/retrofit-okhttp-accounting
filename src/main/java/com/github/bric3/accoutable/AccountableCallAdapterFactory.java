package com.github.bric3.accoutable;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.CallAdapter.Factory;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Supplier;

public class AccountableCallAdapterFactory extends Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        @SuppressWarnings("unchecked")
        CallAdapter<Object, Object> callAdapter =
                (CallAdapter<Object, Object>) retrofit.nextCallAdapter(this,
                                                                       returnType,
                                                                       annotations);

        RequestMetadata requestMetadata = readRequestMetadataFrom(annotations);
        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return callAdapter.responseType();
            }

            @Override
            public Object adapt(Call<Object> call) {
                return callAdapter.adapt(new AccountableCall(call, requestMetadata));
            }
        };
    }

    private static RequestMetadata readRequestMetadataFrom(Annotation[] annotations) {
        for (Annotation annotation: annotations) {
            if (annotation instanceof DELETE) {
                return RequestMetadata.of("DELETE", ((DELETE) annotation).value());
            } else if (annotation instanceof GET) {
                return RequestMetadata.of("GET", ((GET) annotation).value());
            } else if (annotation instanceof HEAD) {
                return RequestMetadata.of("HEAD", ((HEAD) annotation).value());
            } else if (annotation instanceof PATCH) {
                return RequestMetadata.of("PATCH", ((PATCH) annotation).value());
            } else if (annotation instanceof POST) {
                return RequestMetadata.of("POST", ((POST) annotation).value());
            } else if (annotation instanceof PUT) {
                return RequestMetadata.of("PUT", ((PUT) annotation).value());
            } else if (annotation instanceof OPTIONS) {
                return RequestMetadata.of("PUT", ((OPTIONS) annotation).value());
            } else if (annotation instanceof HTTP) {
                return RequestMetadata.of(((HTTP) annotation).method(), ((HTTP) annotation).path());
            }
        }
        throw new IllegalStateException("This method has no retrofit verbs");
    }

    private static class AccountableCall implements Call<Object> {
        private final Call<Object> call;
        private RequestMetadata requestMetadata;
        private Supplier<Request> taggedRequest;

        private AccountableCall(Call<Object> call, RequestMetadata requestMetadata) {
            this.call = call;
            this.requestMetadata = requestMetadata;
            taggedRequest = Suppliers.memoize(() -> {
                call.request()
                    .tag(RequestMetadata.class)
                    .setFrom(requestMetadata);
                return call.request();
            });
        }

        @Override
        public Response<Object> execute() throws IOException {
            taggedRequest.get();
            return call.execute();
        }

        @Override
        public void enqueue(Callback<Object> callback) {
            taggedRequest.get();
            call.enqueue(callback);
        }

        @Override
        public boolean isExecuted() {
            return call.isExecuted();
        }

        @Override
        public void cancel() {
            call.cancel();
        }

        @Override
        public boolean isCanceled() {
            return call.isCanceled();
        }

        @Override
        public Call<Object> clone() {
            return new AccountableCall(call.clone(), requestMetadata);
        }

        @Override
        public Request request() {
            return taggedRequest.get();
        }
    }
}
