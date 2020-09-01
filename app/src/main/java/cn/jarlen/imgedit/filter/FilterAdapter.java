package cn.jarlen.imgedit.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;

public class FilterAdapter extends RvCommonAdapter<FilterItem> {

    OnFilterItemClickListener<FilterItem> clickListener;

    private int mSelectItem = 0;

    public FilterAdapter(Context context) {
        super(context);
    }

    public void setClickListener(OnFilterItemClickListener<FilterItem> clickListener) {
        this.clickListener = clickListener;
    }

    public void setSelectItem(int position) {
        this.mSelectItem = position;
        notifyDataSetChanged();
    }

    @Override
    public void onBindView(final RvViewHolder viewHolder, final FilterItem item) {
        Bitmap bmp = item.getFilterThumbnail();
        String name = item.getFilterName();
        ((ImageView) viewHolder.getView(R.id.filterThumbnail)).setImageBitmap(bmp);
        ((TextView) viewHolder.getView(R.id.filterName)).setText(name);

        final int position = viewHolder.getLayoutPosition();
        if (position == mSelectItem) {
            viewHolder.getConvertView().setBackgroundDrawable(
                    mContext.getResources().getDrawable(
                            R.drawable.camera_shape_wm_item_selected));
        } else {
            viewHolder.getConvertView().setBackgroundDrawable(
                    mContext.getResources().getDrawable(
                            R.drawable.camera_shape_wm_item));
        }

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onFilterItemClick(item, position);
                }
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_filter_item;
    }
}
