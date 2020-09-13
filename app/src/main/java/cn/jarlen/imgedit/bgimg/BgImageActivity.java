package cn.jarlen.imgedit.bgimg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.adapter.VerticalDividerItemDecoration;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.util.FileUtils;

import static cn.jarlen.imgedit.util.FileUtils.getImageFromAssetsFile;

public class BgImageActivity extends BaseActivity implements OnAdapterItemClickListener<String> {

    ImageView ivBg;
    ImageView ivPreview;
    Bitmap bitmapSrc;

    DragFrameLayout layoutContainer;

    RecyclerView rvBgImgList;
    BgImgAdapter bgImgAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("背景");
        layoutContainer = findViewById(R.id.layout_container);
        ivBg = findViewById(R.id.view_image_bg);
        ivPreview = findViewById(R.id.view_image_preview);

        rvBgImgList = findViewById(R.id.rv_bg_list);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = FileUtils.getViewBitmap(layoutContainer);
                saveImage(bitmap, "bgImg_");
            }
        });

        LinearLayoutManager shapeLinearLayoutManager = new LinearLayoutManager(this);
        shapeLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvBgImgList.setLayoutManager(shapeLinearLayoutManager);
        rvBgImgList.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .showLastDivider()
                .size(15).build());
        bgImgAdapter = new BgImgAdapter(this);
        bgImgAdapter.setAdapterItemClickListener(this);
        rvBgImgList.setAdapter(bgImgAdapter);

        layoutContainer.addDragView(ivPreview);

        bitmapSrc = BitmapFactory.decodeFile(getImagePath());
        ivPreview.setImageBitmap(bitmapSrc);

        bgImgAdapter.addDataList(getBgImgData());
    }

    private List<String> getBgImgData() {
        List<String> bgImgList = new ArrayList<>();
        for (int index = 1; index <= 20; index++) {
            String frameName = "bgImg/gezi_" + index + ".jpg";
            bgImgList.add(frameName);
        }
        return bgImgList;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bg_image;
    }

    @Override
    public void onItemClick(String item, int position) {
        Bitmap bitmap = getImageFromAssetsFile(item);
        ivBg.setImageBitmap(bitmap);
    }

}
