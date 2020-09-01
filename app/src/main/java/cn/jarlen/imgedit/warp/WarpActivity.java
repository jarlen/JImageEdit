package cn.jarlen.imgedit.warp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

public class WarpActivity extends BaseActivity {

    SmallFaceView warpView;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warp);
        warpView = findViewById(R.id.view_warp);

        TextView resetBtn = findViewById(R.id.iv_toolbar_right_two);
        resetBtn.setVisibility(View.VISIBLE);
        resetBtn.setText("重置");
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap == null) {
                    return;
                }
                warpView.resetView();
            }
        });

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap retBitmap = warpView.getBitmap();
                saveImage(retBitmap, "warp_");
            }
        });
        warpView.setLevel(20);
        warpView.setSmllBody(false);
        init();
    }

    private void init() {
        Observable.just(getImagePath())
                .map(new Function<String, File>() {
                    @Override
                    public File apply(String s) throws Exception {
                        List<File> imgCompressFileList = Luban.with(ImageEditApplication.getApplication())
                                .ignoreBy(500)
                                .load(s)
                                .get();
                        return imgCompressFileList.get(0);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<File>() {
                    @Override
                    public void onNext(File file) {
                        bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        warpView.setBitmap(bitmap);
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
