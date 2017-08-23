package volynskyi.testtask.api.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import volynskyi.testtask.R;

class VHItem extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView userName;
    ImageView photo;
    CircleImageView avatar;
    Button like;
    private Drawable likeIcon, unlikeIcon;
    private boolean likeClicked;

    public VHItem(final View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.tvUserName);
        avatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
        photo = (ImageView) itemView.findViewById(R.id.ivPhoto);
        like = (Button) itemView.findViewById(R.id.btnLike);
        likeClicked = false;
        like.setTextColor(Color.DKGRAY);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLike:
                if (likeClicked) {

                    like.setCompoundDrawablesWithIntrinsicBounds(likeIcon, null, null, null);
                    like.setTextColor(Color.DKGRAY);
                    like.setBackgroundResource(R.drawable.unliked_shape_btn);

                } else {
                    like.setCompoundDrawablesWithIntrinsicBounds(unlikeIcon, null, null, null);
                    like.setTextColor(Color.WHITE);
                    like.setBackgroundResource(R.drawable.liked_shape_btn);
                }
                likeClicked = !likeClicked;
        }
    }
}