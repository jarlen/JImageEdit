package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;
import cn.jarlen.imgedit.util.FileUtils;

public class ShapeListAdapter extends RvCommonAdapter<String> {

    private OnAdapterItemClickListener<String> adapterItemClickListener;

    public ShapeListAdapter(Context context) {
        super(context);
    }

    public void setAdapterItemClickListener(OnAdapterItemClickListener<String> adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public void onBindView(RvViewHolder viewHolder, final String item) {
        ImageView ivImage = viewHolder.getView(R.id.iv_image);

        String assetPath = FileUtils.buildAssetPath(item);

        Glide.with(mContext).load(assetPath).into(ivImage);

        final int position = viewHolder.getAdapterPosition();
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapterItemClickListener != null) {
                    adapterItemClickListener.onItemClick(item, position);
                }
            }
        });

    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_shape_item;
    }
}
