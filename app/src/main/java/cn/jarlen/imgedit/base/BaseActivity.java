package cn.jarlen.imgedit.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.util.Utils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String IMAGE_PATH = "image_path";
    String imagePath;

    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getIntent().getStringExtra(IMAGE_PATH);
        setContentView(getLayoutResId());

        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = findViewById(R.id.tv_title);
        setToolbarTitle("测试");
        onBindView(savedInstanceState);
    }

    protected void setToolbarTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    protected abstract void onBindView(Bundle savedInstanceState);

    protected abstract int getLayoutResId();

    public String getImagePath() {
        return imagePath;
    }

    public void saveImage(Bitmap bitmap, String suffix) {
        saveImage2(bitmap, suffix)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Boolean>() {

                    @Override
                    public void onNext(Boolean aBoolean) {
                        showSaveSuccessTip();
                    }

                    @Override
                    public void onError(Throwable e) {
                        showSaveFailureTip();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    protected void showSaveSuccessTip() {
        Snackbar.make(getWindow().getDecorView(), "已保存至相册", Snackbar.LENGTH_SHORT).show();
    }

    protected void showSaveFailureTip() {
        Snackbar.make(getWindow().getDecorView(), "保存失败", Snackbar.LENGTH_SHORT).show();
    }

    public Observable<Boolean> saveImage2(Bitmap bitmap, String suffix) {
        File imageFile = new File(getImagePath());
        return Utils.saveImg2(bitmap, suffix + imageFile.getName());
    }
}
