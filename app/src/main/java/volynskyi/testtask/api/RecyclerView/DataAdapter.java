package volynskyi.testtask.api.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import volynskyi.testtask.R;
import volynskyi.testtask.api.APIClient;
import volynskyi.testtask.api.ServiceGenerator;
import volynskyi.testtask.api.apiModel.ResponseMain;

import static volynskyi.testtask.LoginActivity.BASE_API;
import static volynskyi.testtask.LoginActivity.TOKEN_INCASE;
import static volynskyi.testtask.R.drawable.like;

public class DataAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ResponseMain> responseMainList = new ArrayList<>();
    private Context context;

    public DataAdapter(Context context, List<ResponseMain> response) {
        this.context = context;
        responseMainList = response;
    }

    public DataAdapter(Context context, ResponseMain response) {
        this.context = context;
        responseMainList.add(response);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new VHItem(v);
    }

    private ResponseMain getItem(int position) {
        return responseMainList.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final ResponseMain currentItem = getItem(position);
        final VHItem vhItem = (VHItem) holder;
        vhItem.userName.setText(currentItem.getUser().getName());
        final int likes = currentItem.getLikes();

        String avatarUrl = currentItem.getUser().getProfileImage().getMedium();
        String photoUrl = currentItem.getUrls().getSmall();

        Picasso.with(context)
                .load(avatarUrl)
                .placeholder(R.drawable.avatar_place_holder)
                .into(vhItem.avatar);

        Picasso.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.photo_place_holder)
                .into(vhItem.photo);

        final Drawable likeIcon, unlikeIcon;
        unlikeIcon = context.getResources().getDrawable(like);
        likeIcon = context.getResources().getDrawable(R.drawable.unlike);

        isPhotoLiked(likes, vhItem, likeIcon, unlikeIcon, currentItem.isLikedByUser());

        final int itemPosition = position;

        vhItem.like.setOnClickListener(new View.OnClickListener() {
            boolean isLiked = currentItem.isLikedByUser();
            APIClient client;
            Call<ResponseMain> call;
            String photoId;

            @Override
            public void onClick(View v) {
                if (isLiked) {
                    photoId = getItem(itemPosition).getId();
                    client = ServiceGenerator.createService(APIClient.class, BASE_API);
                    call = client.unlikePhoto(TOKEN_INCASE, photoId);
                } else {
                    photoId = getItem(itemPosition).getId();
                    client = ServiceGenerator.createService(APIClient.class, BASE_API);
                    call = client.likePhoto(TOKEN_INCASE, photoId);
                }

                call.enqueue(new Callback<ResponseMain>() {
                    @Override
                    public void onResponse(Call<ResponseMain> call, Response<ResponseMain> response) {
                        if (response.code() == 200 | response.code() == 201) {

                            ResponseMain responseMain = response.body();
                            currentItem.setLikedByUser(responseMain.getPhoto().isLikedByUser());
                            currentItem.setLikes(responseMain.getPhoto().getLikes());
                            isPhotoLiked(responseMain.getPhoto().getLikes(), vhItem, likeIcon, unlikeIcon,
                                    responseMain.getPhoto().isLikedByUser());
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseMain> call, Throwable t) {
                    }
                });
            }
        });
    }

    private void isPhotoLiked(int likes, VHItem vhItem, Drawable likeIcon, Drawable unlikeIcon, boolean isLiked) {
        if (isLiked) {
            vhItem.like.setCompoundDrawablesWithIntrinsicBounds(likeIcon, null, null, null);
            vhItem.like.setTextColor(Color.WHITE);
            vhItem.like.setBackgroundResource(R.drawable.liked_shape_btn);
        } else {
            vhItem.like.setCompoundDrawablesWithIntrinsicBounds(unlikeIcon, null, null, null);
            vhItem.like.setTextColor(Color.DKGRAY);
            vhItem.like.setBackgroundResource(R.drawable.unliked_shape_btn);
        }
        vhItem.like.setText(String.valueOf(likes));
    }

    @Override
    public int getItemCount() {
        return responseMainList.size();
    }
}
