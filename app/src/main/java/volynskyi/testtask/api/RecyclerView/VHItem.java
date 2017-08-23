package volynskyi.testtask.api.RecyclerView;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import volynskyi.testtask.R;

class VHItem extends RecyclerView.ViewHolder {
    TextView userName;
    ImageView photo;
    CircleImageView avatar;
    Button like;

    public VHItem(final View itemView) {
        super(itemView);
        userName = (TextView) itemView.findViewById(R.id.tvUserName);
        avatar = (CircleImageView) itemView.findViewById(R.id.ivAvatar);
        photo = (ImageView) itemView.findViewById(R.id.ivPhoto);
        like = (Button) itemView.findViewById(R.id.btnLike);
        like.setTextColor(Color.DKGRAY);
    }
}