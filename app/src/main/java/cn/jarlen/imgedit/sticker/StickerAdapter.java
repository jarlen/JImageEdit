package cn.jarlen.imgedit.sticker;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;

public class StickerAdapter extends RvCommonAdapter<Paster> {

    OnStickerPasterListener pasterListener;

    public StickerAdapter(Context context) {
        super(context);
    }


    public void setPasterListener(OnStickerPasterListener pasterListener) {
        this.pasterListener = pasterListener;
    }

    @Override
    public void onBindView(RvViewHolder viewHolder, final Paster item) {
        ImageView ivImage = viewHolder.getView(R.id.iv_item_image);

        Glide.with(mContext).load(item.getPasterPath()).fitCenter().into(ivImage);
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pasterListener != null) {
                    pasterListener.onStickerPaster(item.getPasterName());
                }
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_item_image;
    }

    public static interface OnStickerPasterListener {
        void onStickerPaster(String paster);
    }
}
