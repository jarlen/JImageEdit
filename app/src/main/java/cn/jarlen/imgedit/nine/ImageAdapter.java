package cn.jarlen.imgedit.nine;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;

public class ImageAdapter extends RvCommonAdapter<ImagePiece> {

    public ImageAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindView(RvViewHolder viewHolder, ImagePiece item) {
        ImageView ivImage = viewHolder.getView(R.id.iv_image);
        TextView ivIndex = viewHolder.getView(R.id.iv_index);
        Glide.with(mContext).load(item.getImgPath())
                .into(ivImage);
        ivIndex.setText(item.getIndex() + "");
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_nine_item;
    }
}
