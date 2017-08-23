package volynskyi.testtask.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import volynskyi.testtask.api.apiModel.ResponseMain;
import volynskyi.testtask.api.authorizationModel.AccessToken;

public interface APIClient {

    @FormUrlEncoded
    @POST("token")
    Call<AccessToken> getNewAccessToken(
            @Field("code") String code,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret,
            @Field("redirect_uri") String redirectUri,
            @Field("grant_type") String grantType);

    @GET("photos")
    Call<List<ResponseMain>> getPhotos(
            @Header("Authorization") String token,
            @Query("order_by") String sort);

    @POST("photos/{id}/like")
    Call<ResponseMain> likePhoto(
            @Header("Authorization") String token,
            @Path("id") String id);

    @DELETE("photos/{id}/like")
    Call<ResponseMain> unlikePhoto(
            @Header("Authorization") String token,
            @Path("id") String id);

    @GET("photos/random")
    Call<ResponseMain> getRandom(
            @Header("Authorization") String token);

}
