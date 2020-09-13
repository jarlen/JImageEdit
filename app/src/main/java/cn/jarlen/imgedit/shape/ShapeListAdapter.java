package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;

public class ShapeListAdapter extends RvCommonAdapter<Integer> {

    private OnAdapterItemClickListener<Integer> adapterItemClickListener;

    public ShapeListAdapter(Context context) {
        super(context);
    }

    public void setAdapterItemClickListener(OnAdapterItemClickListener<Integer> adapterItemClickListener) {
        this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public void onBindView(RvViewHolder viewHolder, final Integer item) {
        ImageView ivImage = viewHolder.getView(R.id.iv_image);

        ivImage.setImageResource(item);

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
