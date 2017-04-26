package id.co.unila.adi.laundryapp.rest_api;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by japra_awok on 13/03/2017.
 */

public class MyOkHttpInterceptor {
    private OkHttpClient client;
    public HashMap<String, String> headers;

    public MyOkHttpInterceptor() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        client = httpClient.build();
    }

    public MyOkHttpInterceptor(final HashMap<String, String> headers) {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder();

                for (String key : headers.keySet()) {
                    requestBuilder.addHeader(key, headers.get(key));
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        client = httpClient.build();
    }

    public OkHttpClient getOkHttpClient(){
        return client;
    }
}
