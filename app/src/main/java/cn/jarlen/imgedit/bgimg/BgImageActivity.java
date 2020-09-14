package cn.jarlen.imgedit.bgimg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.adapter.VerticalDividerItemDecoration;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static cn.jarlen.imgedit.util.FileUtils.getImageFromAssetsFile;

public class BgImageActivity extends BaseActivity implements OnAdapterItemClickListener<String> {

    public static final String BG_IMG_FLODER = "bgImg";

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

        getBgImgData();
    }

    private void getBgImgData() {

        Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                String[] frameArray = ImageEditApplication.getApplication().getAssets().list(BG_IMG_FLODER);
                int count = frameArray.length;
                List<String> frameList = new ArrayList<>();
                for (int index = 0; index < count; index++) {
                    String frameName = BG_IMG_FLODER + File.separator + frameArray[index];
                    frameList.add(frameName);
                }
                return frameList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<String>>() {

                    @Override
                    public void onNext(List<String> strings) {
                        bgImgAdapter.addDataList(strings);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
