package cn.jarlen.imgedit.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;

public class FilterAdapter extends RvCommonAdapter<FilterItem> {

    OnAdapterItemClickListener<FilterItem> clickListener;
    
    public FilterAdapter(Context context) {
        super(context);
    }

    public void setClickListener(OnAdapterItemClickListener<FilterItem> clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindView(final RvViewHolder viewHolder, final FilterItem item) {
        Bitmap bmp = item.getFilterThumbnail();
        String name = item.getFilterName();
        ((ImageView) viewHolder.getView(R.id.filterThumbnail)).setImageBitmap(bmp);
        ((TextView) viewHolder.getView(R.id.filterName)).setText(name);

        final int position = viewHolder.getLayoutPosition();
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onItemClick(item, position);
                }
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_filter_item;
    }
}
