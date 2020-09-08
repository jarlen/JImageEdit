package cn.jarlen.imgedit.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;

public class ShapeCutActivity extends BaseActivity {

    ShapeCropView shapeCropView;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        shapeCropView = findViewById(R.id.view_shape_cut);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = shapeCropView.getBitmap();
                saveImage(bitmap, "shape_");
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(getImagePath());
        shapeCropView.setBackGroundBitMap(bitmap);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_shape_cut;
    }
}
