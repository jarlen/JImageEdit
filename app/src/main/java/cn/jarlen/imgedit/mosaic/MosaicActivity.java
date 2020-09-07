package cn.jarlen.imgedit.mosaic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.util.FileUtils;

public class MosaicActivity extends BaseActivity implements View.OnClickListener {

    private DrawMosaicView mosaic;

    Bitmap srcBitmap;
    private int mWidth, mHeight;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mosaic);


    }

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mosaic = findViewById(R.id.mosaic);
        findViewById(R.id.tv_base).setOnClickListener(this);
        findViewById(R.id.tv_flower).setOnClickListener(this);
        findViewById(R.id.tv_eraser).setOnClickListener(this);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bit = mosaic.getMosaicBitmap();
                saveImage(bit, "mosaic_");
            }
        });

        mosaic.setMosaicBackgroundResource(getImagePath());

        srcBitmap = BitmapFactory.decodeFile(getImagePath());

        mWidth = srcBitmap.getWidth();
        mHeight = srcBitmap.getHeight();
        Bitmap bit = MosaicUtil.getMosaic(srcBitmap);

        mosaic.setMosaicResource(bit);
        mosaic.setMosaicBrushWidth(32);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mosaic;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_eraser:
                mosaic.setMosaicType(MosaicUtil.MosaicType.ERASER);
                break;
            case R.id.tv_base:
                Bitmap bitmapMosaic = MosaicUtil.getMosaic(srcBitmap);
                mosaic.setMosaicResource(bitmapMosaic);
                break;
            case R.id.tv_flower:
                Bitmap bit = BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.hi4);
                bit = FileUtils.ResizeBitmap(bit, mWidth, mHeight);
                mosaic.setMosaicResource(bit);
                break;
            default:
                break;
        }
    }
}
