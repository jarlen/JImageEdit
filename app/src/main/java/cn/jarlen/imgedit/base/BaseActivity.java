package cn.jarlen.imgedit.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.util.Utils;
import io.reactivex.Observable;

import static cn.jarlen.imgedit.util.Utils.saveImage2Photo;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String IMAGE_PATH = "image_path";
    String imagePath;

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

        onBindView(savedInstanceState);
    }

    protected abstract void onBindView(Bundle savedInstanceState);

    protected abstract int getLayoutResId();

    public String getImagePath() {
        return imagePath;
    }

    public void saveImage(Bitmap bitmap, String suffix) {
        File imageFile = new File(getImagePath());
        saveImage2Photo(bitmap, suffix + imageFile.getName());
    }

    public Observable<Boolean> saveImage2(Bitmap bitmap, String suffix) {
        File imageFile = new File(getImagePath());
        return Utils.saveImg2(bitmap, suffix + imageFile.getName());
    }
}
