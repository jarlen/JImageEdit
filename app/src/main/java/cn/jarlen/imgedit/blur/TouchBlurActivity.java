package cn.jarlen.imgedit.blur;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import java.util.concurrent.Callable;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.mosaic.DrawMosaicView;
import cn.jarlen.imgedit.mosaic.MosaicUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TouchBlurActivity extends BaseActivity {

    DrawMosaicView mosaicView;

    Bitmap bitmapSrc;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mosaicView = findViewById(R.id.view_mosaic);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bit = mosaicView.getMosaicBitmap();
                saveImage(bit, "blur_");
            }
        });

        mosaicView.setMosaicBackgroundResource(getImagePath());
        bitmapSrc = BitmapFactory.decodeFile(getImagePath());
        mosaicView.setMosaicBrushWidth(32);
        initView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_touch_blur;
    }

    private void initView() {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return MosaicUtil.getBlur(bitmapSrc);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Bitmap>() {

                    @Override
                    public void onNext(Bitmap bit) {
                        mosaicView.setMosaicResource(bit);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
