package cn.jarlen.imgedit.bgimg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vansuita.gaussianblur.GaussianBlur;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class BgImageActivity extends BaseActivity implements View.OnClickListener {

    ImageView ivBg;
    ImageView ivPreview;
    Bitmap bitmapSrc;

    DragFrameLayout layoutContainer;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("背景");
        layoutContainer = findViewById(R.id.layout_container);
        ivBg = findViewById(R.id.view_image_bg);
        ivPreview = findViewById(R.id.view_image_preview);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = FileUtils.getViewBitmap(layoutContainer);
                saveImage(bitmap, "bgImg_");
            }
        });

        findViewById(R.id.btn_blur).setOnClickListener(this);
        findViewById(R.id.btn_gezi_1).setOnClickListener(this);
        findViewById(R.id.btn_gezi_2).setOnClickListener(this);
        findViewById(R.id.btn_gezi_3).setOnClickListener(this);
        layoutContainer.addDragView(ivPreview);

        bitmapSrc = BitmapFactory.decodeFile(getImagePath());
        ivPreview.setImageBitmap(bitmapSrc);
        initBgImage(2);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_bg_image;
    }

    private void initBgImage(final int blur) {
        Observable.just(bitmapSrc)
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {
                        Bitmap blurredBitmap = null;
                        if (blur == 1) {
                            blurredBitmap = GaussianBlur.with(ImageEditApplication.getApplication())
                                    .radius(10)
                                    .render(bitmap);
                        } else {
                            blurredBitmap = GaussianBlur.with(ImageEditApplication.getApplication())
                                    .render(bitmap);
                        }
                        return blurredBitmap;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        ivBg.setImageBitmap(bitmap);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_blur:
                initBgImage(1);
                break;
            case R.id.btn_gezi_1:
                ivBg.setImageResource(R.drawable.gezi_1);
                break;
            case R.id.btn_gezi_2:
                ivBg.setImageResource(R.drawable.gezi_2);
                break;
            case R.id.btn_gezi_3:
                ivBg.setImageResource(R.drawable.gezi_3);
                break;
            default:
                break;
        }
    }
}
