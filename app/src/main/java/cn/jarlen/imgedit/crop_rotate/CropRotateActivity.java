package cn.jarlen.imgedit.crop_rotate;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;

public class CropRotateActivity extends BaseActivity {


    public static final String IMAGE_SHOW_ONLY_ROTATE = "OnlyRotate";
    private CropImageView cropImageView;

    private boolean showOnlyRotate;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        showOnlyRotate = getIntent().getBooleanExtra(IMAGE_SHOW_ONLY_ROTATE, false);
        setToolbarTitle(showOnlyRotate ? "旋转" : "剪切");
        cropImageView = findViewById(R.id.cropImageView);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = cropImageView.getCroppedImage();
                String suffix = showOnlyRotate ? "rotate_" : "crop_";
                saveImage(bitmap, suffix);
            }
        });

        TextView rotateView = findViewById(R.id.iv_toolbar_right_two);
        rotateView.setText("旋转");
        rotateView.setVisibility(showOnlyRotate ? View.VISIBLE : View.GONE);
        rotateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(90);
            }
        });

        Uri imageUri;
        File photoFile = new File(getImagePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //如果是7.0及以上的系统使用FileProvider的方式创建一个Uri
            imageUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
        } else {
            //7.0以下使用这种方式创建一个Uri
            imageUri = Uri.fromFile(photoFile);
        }
        cropImageView.setImageUriAsync(imageUri);
        cropImageView.setShowCropOverlay(!showOnlyRotate);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_crop;
    }
}