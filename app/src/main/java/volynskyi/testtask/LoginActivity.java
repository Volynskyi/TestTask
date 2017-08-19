package volynskyi.testtask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import volynskyi.testtask.api.APIClient;
import volynskyi.testtask.api.ServiceGenerator;
import volynskyi.testtask.api.objects.AccessToken;

import static volynskyi.testtask.api.ServiceGenerator.API_OAUTH_REDIRECT;

public class LoginActivity extends AppCompatActivity {

    Map<String, String> map;
    Button loginButton;

    private static String CLIENT_ID = "412a6b16dc47276524aec7a14c1a3336843628e4b891c412f190d642bcca3b02";
    //Use your own client secret
    private static String REDIRECT_URI = "https://com.volynskyi.testtask/unsplash/callback";
    private static String GRANT_TYPE = "authorization_code";
    private static String TOKEN_URL = "https://unsplash.com/oauth/token";
    private static String OAUTH_URL = "https://unsplash.com/oauth/authorize";
    private static String OAUTH_SCOPE = "public+read_photos+write_likes+read_collections";
    private static String RESPONSE_TYPE = "CODE";
    private static String CLIENT_SECRET = "281b7270e0effd97fe04617233c3af5a93a46cde24895042636728cc8518b5fb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        map = new HashMap();
        map.put("client_id", CLIENT_ID);
        map.put("redirect_uri", REDIRECT_URI);
        map.put("response_type", RESPONSE_TYPE);
        map.put("scope", OAUTH_SCOPE);


        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://unsplash.com/oauth/authorize?" +
                                "client_id=412a6b16dc47276524aec7a14c1a3336843628e4b891c412f190d642bcca3b02&" +
                                "redirect_uri=com.volynskyi.testtask://oauth&" +
                                "response_type=code&scope=public+read_photos+write_likes+read_collections"));
                // This flag is set to prevent the browser with the login form from showing in the history stack
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(API_OAUTH_REDIRECT)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {

                final SharedPreferences prefs = this.getSharedPreferences(
                        BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);

                APIClient client = ServiceGenerator.createService(APIClient.class);
                Call<AccessToken> call = client.getNewAccessToken(code, CLIENT_ID,
                        CLIENT_SECRET, API_OAUTH_REDIRECT,
                        "authorization_code");
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        int statusCode = response.code();
                        if (statusCode == 200) {
                            AccessToken token = response.body();
                            prefs.edit().putBoolean("oauth.loggedin", true).apply();
                            prefs.edit().putString("oauth.accesstoken", token.getAccessToken()).apply();
                            prefs.edit().putString("oauth.refreshtoken", token.getRefreshToken()).apply();
                            prefs.edit().putString("oauth.tokentype", token.getTokenType()).apply();


                            Log.d("Result", String.valueOf(prefs.getBoolean("oauth.loggedin", false)));
                            Log.d("Result", prefs.getString("oauth.accesstoken", "nulll"));
                            Log.d("Result", prefs.getString("oauth.refreshtoken", "nulll"));
                            Log.d("Result", prefs.getString("oauth.tokentype", "nulll"));
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                    }
                });
            }
        }
    }

}


