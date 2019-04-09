package com.github.bric3;

import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Invocation;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.HTTP;
import retrofit2.http.OPTIONS;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.io.IOException;
import java.lang.annotation.Annotation;

public class RetroMetrics {

    public static void main(String... args) throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start(1025);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .callFactory(new OkHttpClient.Builder().eventListener(new EventListener() {
                    @Override
                    public void callStart(okhttp3.Call call) {
                        Invocation invocation = call.request().tag(Invocation.class);
                        System.out.printf("Captured start of : %s%n",
                                          readRequestMetadataFrom(invocation.method().getDeclaredAnnotations()));
                    }
                }).build())
                .build();


        FakeRest fakeRest = retrofit.create(FakeRest.class);

        mockWebServer.enqueue(new MockResponse());
        System.out.println(fakeRest.posts().execute());

        mockWebServer.enqueue(new MockResponse());
        System.out.println(fakeRest.post(1).execute());

        mockWebServer.enqueue(new MockResponse());
        System.out.println(fakeRest.postComments(1).execute());

        mockWebServer.close();
    }

    interface FakeRest {
        @GET("/posts")
        Call<ResponseBody> posts();

        @GET("/posts/{post-id}")
        Call<ResponseBody> post(@Path("post-id") int postId);

        @GET("/posts/{post-id}/comments")
        Call<ResponseBody> postComments(@Path("post-id") int postId);
    }

    private static String readRequestMetadataFrom(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof DELETE) {
                return "DELETE" + " " + ((DELETE) annotation).value();
            } else if (annotation instanceof GET) {
                return "GET" + " " + ((GET) annotation).value();
            } else if (annotation instanceof HEAD) {
                return "HEAD" + " " + ((HEAD) annotation).value();
            } else if (annotation instanceof PATCH) {
                return "PATCH" + " " + ((PATCH) annotation).value();
            } else if (annotation instanceof POST) {
                return "POST" + " " + ((POST) annotation).value();
            } else if (annotation instanceof PUT) {
                return "PUT" + " " + ((PUT) annotation).value();
            } else if (annotation instanceof OPTIONS) {
                return "PUT" + " " + ((OPTIONS) annotation).value();
            } else if (annotation instanceof HTTP) {
                return ((HTTP) annotation).method() + " " + ((HTTP) annotation).path();
            }
        }
        throw new IllegalStateException("This method has no retrofit verbs");
    }
}
