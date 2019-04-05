package com.github.bric3;

import com.github.bric3.accountable.AccountableCallAdapterFactory;
import com.github.bric3.accountable.AccountableCallFactory;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.io.IOException;

public class RetroMetrics {

    public static void main(String... args) throws IOException {
        MockWebServer mockWebServer = new MockWebServer();
        mockWebServer.start(1025);

        Retrofit retrofit = new Builder().baseUrl(mockWebServer.url("/"))
                                         .addCallAdapterFactory(new AccountableCallAdapterFactory())
                                         .callFactory(new AccountableCallFactory())
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


}
