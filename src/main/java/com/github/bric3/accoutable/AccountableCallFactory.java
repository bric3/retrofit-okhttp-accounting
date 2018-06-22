package com.github.bric3.accoutable;

import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AccountableCallFactory {
    OkHttpClient ok = new OkHttpClient.Builder().eventListener(new EventListener() {
        @Override
        public void callStart(okhttp3.Call call) {
            RequestMetadata tag = (RequestMetadata) call.request().tag();
            System.out.printf("Captured start of : %s", tag);
        }
    }).build();

    public okhttp3.Call newCall(Request request) {
        return ok.newCall(request.newBuilder()
                                 .tag(RequestMetadata.placeHolder())
                                 .build());
    }
}
