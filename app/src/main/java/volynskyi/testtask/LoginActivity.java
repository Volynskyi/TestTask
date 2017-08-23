package volynskyi.testtask;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import volynskyi.testtask.api.APIClient;
import volynskyi.testtask.api.RecyclerView.DataAdapter;
import volynskyi.testtask.api.ServiceGenerator;
import volynskyi.testtask.api.apiModel.ResponseMain;
import volynskyi.testtask.api.authorizationModel.AccessToken;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button latest, oldest, popular, random;
    private DataAdapter dataAdapter;
    private boolean latestClicked, oldestClicked, popularClicked, randomClicked;

    private static final String CLIENT_ID = "412a6b16dc47276524aec7a14c1a3336843628e4b891c412f190d642bcca3b02";
    private static final String REDIRECT_URI = "com.volynskyi.testtask://oauth";
    private static final String GRANT_TYPE = "authorization_code";
    private static final String CLIENT_SECRET = "281b7270e0effd97fe04617233c3af5a93a46cde24895042636728cc8518b5fb";
    public static final String TOKEN_INCASE = "Bearer 5d4ce91fef524fda56f0a8dc1e03debb0c81355799e0da4f3bd75898ad22986b";
    private static final String OAUTH_BASE_URL = "https://unsplash.com/oauth/";
    public static final String BASE_API = "https://api.unsplash.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        prefs = this.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        while (prefs.getString("oauth.accesstoken", null) == null) {
            authenticateUser();
            getToken(prefs);
        }

        initButtons();
        initRecyclerView();
        oldestClicked = popularClicked = randomClicked = false;
        latestClicked = true;
        retrofitGetPhoto("latest");
    }

    private void initButtons() {
        latest = (Button) findViewById(R.id.latest);
        oldest = (Button) findViewById(R.id.oldest);
        popular = (Button) findViewById(R.id.popular);
        random = (Button) findViewById(R.id.random);
        latest.setOnClickListener(this);
        oldest.setOnClickListener(this);
        popular.setOnClickListener(this);
        random.setOnClickListener(this);
    }

    private void retrofitGetPhoto(String order_by) {
        APIClient client = ServiceGenerator.createService(APIClient.class, BASE_API);
        Call<List<ResponseMain>> call = client.getPhotos(TOKEN_INCASE, order_by);
        responseListResult(call);
    }

    private void retrofitGetPhoto() {
        APIClient client = ServiceGenerator.createService(APIClient.class, BASE_API);
        Call<ResponseMain> call = client.getRandom(TOKEN_INCASE);
        responseRandomResult(call);
    }

    private void responseListResult(Call<List<ResponseMain>> call) {
        call.enqueue(new Callback<List<ResponseMain>>() {
            @Override
            public void onResponse(Call<List<ResponseMain>> call, Response<List<ResponseMain>> response) {
                Log.d("test1", "onResponse worked");
                if (response.code() == 200) {
                    List<ResponseMain> responseMainList = response.body();
                    dataAdapter = new DataAdapter(LoginActivity.this, responseMainList);
                    recyclerView.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ResponseMain>> call, Throwable t) {
                t.printStackTrace();
                Log.d("test1", "onFailure worked");
            }
        });
    }

    private void responseRandomResult(Call<ResponseMain> call) {
        call.enqueue(new Callback<ResponseMain>() {
            @Override
            public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                if (response.code() == 200) {
                    ResponseMain responseMainList = response.body();
                    dataAdapter = new DataAdapter(LoginActivity.this, responseMainList);
                    recyclerView.setAdapter(dataAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseMain> call, Throwable t) {
            }
        });
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void authenticateUser() {
        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://unsplash.com/oauth/authorize?" +
                        "client_id=412a6b16dc47276524aec7a14c1a3336843628e4b891c412f190d642bcca3b02" +
                        "&redirect_uri=com.volynskyi.testtask://oauth" +
                        "&response_type=code&scope=public+read_photos+write_likes+read_collections"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    private void getToken(SharedPreferences sharedPreferences) {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            String code = uri.getQueryParameter("code");
            if (code != null) {

                final SharedPreferences prefs = sharedPreferences;
                APIClient client = ServiceGenerator.createService(APIClient.class, OAUTH_BASE_URL);
                Call<AccessToken> call = client.getNewAccessToken(code, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI, GRANT_TYPE);
                call.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        if (response.code() == 200) {
                            AccessToken token = response.body();
                            prefs.edit().putString("oauth.accesstoken", token.getAccessToken()).apply();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.latest:
                if (!latestClicked) {
                    latestClicked = !latestClicked;
                    if (oldestClicked) {
                        oldestClicked = !oldestClicked;
                        oldest.setTextColor(Color.WHITE);
                        oldest.setBackgroundColor(Color.BLACK);
                        latest.setTextColor(Color.BLACK);
                        latest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("latest");
                        break;
                    }
                    if (popularClicked) {
                        popularClicked = !popularClicked;
                        popular.setTextColor(Color.WHITE);
                        popular.setBackgroundColor(Color.BLACK);
                        latest.setTextColor(Color.BLACK);
                        latest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("latest");
                        break;
                    } else {
                        randomClicked = !randomClicked;
                        latest.setTextColor(Color.BLACK);
                        latest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("latest");
                        break;
                    }
                }

            case R.id.oldest:
                if (!oldestClicked) {
                    oldestClicked = !oldestClicked;
                    if (latestClicked) {
                        latestClicked = !latestClicked;
                        latest.setTextColor(Color.WHITE);
                        latest.setBackgroundColor(Color.BLACK);
                        oldest.setTextColor(Color.BLACK);
                        oldest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("oldest");
                        break;
                    }
                    if (popularClicked) {
                        popularClicked = !popularClicked;
                        popular.setTextColor(Color.WHITE);
                        popular.setBackgroundColor(Color.BLACK);
                        oldest.setTextColor(Color.BLACK);
                        oldest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("oldest");
                        break;
                    } else {
                        randomClicked = !randomClicked;
                        oldest.setTextColor(Color.BLACK);
                        oldest.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("oldest");
                        break;
                    }
                }

            case R.id.popular:
                if (!popularClicked) {
                    popularClicked = !popularClicked;
                    if (latestClicked) {
                        latestClicked = !latestClicked;
                        latest.setTextColor(Color.WHITE);
                        latest.setBackgroundColor(Color.BLACK);
                        popular.setTextColor(Color.BLACK);
                        popular.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("popular");
                        break;
                    }
                    if (oldestClicked) {
                        oldestClicked = !oldestClicked;
                        oldest.setTextColor(Color.WHITE);
                        oldest.setBackgroundColor(Color.BLACK);
                        popular.setTextColor(Color.BLACK);
                        popular.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("popular");
                        break;
                    } else {
                        randomClicked = !randomClicked;
                        popular.setTextColor(Color.BLACK);
                        popular.setBackgroundColor(Color.WHITE);
                        retrofitGetPhoto("popular");
                        break;
                    }
                }

            case R.id.random:
                if (!randomClicked) {
                    randomClicked = !randomClicked;
                    if (latestClicked) {
                        latestClicked = !latestClicked;
                        latest.setTextColor(Color.WHITE);
                        latest.setBackgroundColor(Color.BLACK);
                        retrofitGetPhoto();
                        break;
                    }
                    if (oldestClicked) {
                        oldestClicked = !oldestClicked;
                        oldest.setTextColor(Color.WHITE);
                        oldest.setBackgroundColor(Color.BLACK);
                        retrofitGetPhoto();
                        break;
                    } else {
                        popularClicked = !popularClicked;
                        popular.setTextColor(Color.WHITE);
                        popular.setBackgroundColor(Color.BLACK);
                        retrofitGetPhoto();
                        break;
                    }
                }
        }
    }
}


