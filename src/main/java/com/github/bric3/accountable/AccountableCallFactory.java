package com.github.bric3.accountable;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AccountableCallFactory implements Call.Factory {
    OkHttpClient ok = new OkHttpClient.Builder().eventListener(new EventListener() {
        @Override
        public void callStart(okhttp3.Call call) {
            System.out.printf("Captured start of : %s",
                              call.request().tag(RequestMetadata.class));
        }
    }).build();

    public okhttp3.Call newCall(Request request) {
        return ok.newCall(request.newBuilder()
                                 .tag(RequestMetadata.class, RequestMetadata.placeHolder())
                                 .build());
    }
}
