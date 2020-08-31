package cn.jarlen.imgedit.base;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import cn.jarlen.imgedit.util.Utils;
import io.reactivex.Observable;

import static cn.jarlen.imgedit.util.Utils.saveImage2Photo;

public class BaseActivity extends AppCompatActivity {
    public static final String IMAGE_PATH = "image_path";
    String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getIntent().getStringExtra(IMAGE_PATH);
    }

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
