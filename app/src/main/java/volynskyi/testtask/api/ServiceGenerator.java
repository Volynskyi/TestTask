package volynskyi.testtask.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import volynskyi.testtask.api.objects.AccessToken;

/**
 * Created by vova on 19.08.2017.
 */

public class ServiceGenerator {

    public static final String API_BASE_URL = "https://unsplash.com/oauth/";
    public static final String API_OAUTH_REDIRECT = "com.volynskyi.testtask://oauth";

    private static OkHttpClient.Builder httpClient;

    private static Retrofit.Builder builder;

    public static <S> S createService(Class<S> serviceClass) {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }

}
