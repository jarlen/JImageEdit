package cn.jarlen.imgedit.compress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.util.FileUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import top.zibin.luban.Luban;

public class CompressActivity extends BaseActivity {

    ImageView ivImage;
    TextView ivImgSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress);

        TextView save = findViewById(R.id.iv_toolbar_save);
        save.setText("压缩");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compress();
            }
        });

        ivImage = findViewById(R.id.iv_image);
        ivImgSize = findViewById(R.id.tv_image_size);

        String imgPath = getImagePath();
        File imgFile = new File(imgPath);

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        ivImage.setImageBitmap(bitmap);
        ivImgSize.setText(FileUtils.formatFromSize(imgFile.length()));
    }

    private void compress() {

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
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        ivImage.setImageBitmap(bitmap);
                        ivImgSize.setText(FileUtils.formatFromSize(file.length()));
                        saveImage(bitmap, "compress_");
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