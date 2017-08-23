package volynskyi.testtask.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient;

    private static Retrofit.Builder builder;

    public static <S> S createService(Class<S> serviceClass, String baseURL) {
        httpClient = new OkHttpClient.Builder();
        builder = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create());

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = builder.client(client).build();
        return retrofit.create(serviceClass);
    }
}
