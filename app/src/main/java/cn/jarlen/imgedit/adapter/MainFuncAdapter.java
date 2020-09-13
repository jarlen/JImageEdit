package cn.jarlen.imgedit.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.RvCommonAdapter;
import cn.jarlen.imgedit.base.RvViewHolder;
import cn.jarlen.imgedit.bean.MainFuncBean;

public class MainFuncAdapter extends RvCommonAdapter<MainFuncBean> {

    private final OnMainFuncItemListener listener;

    public MainFuncAdapter(Context context, OnMainFuncItemListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    public void onBindView(RvViewHolder viewHolder, final MainFuncBean item) {
        ImageView ivFuncIcon = viewHolder.getView(R.id.iv_main_func_icon);
        TextView tvFuncName = viewHolder.getView(R.id.tv_main_func_name);
        tvFuncName.setText(item.getFuncName());
        ivFuncIcon.setImageResource(item.getIconRes());
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onMainFuncItem(item);
                }
            }
        });
    }

    @Override
    public int getLayoutResId(int viewType) {
        return R.layout.layout_main_func_item;
    }

    public static interface OnMainFuncItemListener {
        void onMainFuncItem(MainFuncBean bean);
    }
}
