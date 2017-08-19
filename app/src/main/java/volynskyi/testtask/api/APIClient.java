package volynskyi.testtask.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import volynskyi.testtask.api.objects.AccessToken;

/**
 * Created by vova on 19.08.2017.
 */

public interface APIClient {

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> getNewAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);
}
