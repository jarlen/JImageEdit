package cn.jarlen.imgedit.frame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;

public class PhotoFrameActivity extends BaseActivity {

    ImageView picture;
    Bitmap mBitmap, mTmpBmp;

    PhotoFrame mImageFrame;

    PhotoFrameOnClickListener frameOnClickListener;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        BitmapFactory.Options mOption = new BitmapFactory.Options();
        mOption.inSampleSize = 1;

        mBitmap = BitmapFactory.decodeFile(getImagePath(), mOption);
        mTmpBmp = mBitmap;
        picture = (ImageView) findViewById(R.id.picture);
        frameOnClickListener = new PhotoFrameOnClickListener();

        findViewById(R.id.photoRes_one).setOnClickListener(frameOnClickListener);
        findViewById(R.id.photoRes_two).setOnClickListener(frameOnClickListener);
        findViewById(R.id.photoRes_three).setOnClickListener(frameOnClickListener);
        reset();
        mImageFrame = new PhotoFrame(this, mBitmap);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(mTmpBmp, "frame_");
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_photo_frame;
    }


    private class PhotoFrameOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.photoRes_one:
                    mImageFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                    mImageFrame.setFrameResources(
                            R.drawable.frame_around1_left_top,
                            R.drawable.frame_around1_left,
                            R.drawable.frame_around1_left_bottom,
                            R.drawable.frame_around1_bottom,
                            R.drawable.frame_around1_right_bottom,
                            R.drawable.frame_around1_right,
                            R.drawable.frame_around1_right_top,
                            R.drawable.frame_around1_top);
                    mTmpBmp = mImageFrame.combineFrameRes();
                    break;
                case R.id.photoRes_two:
                    mImageFrame.setFrameType(PhotoFrame.FRAME_SMALL);
                    mImageFrame.setFrameResources(
                            R.drawable.frame_around2_left_top,
                            R.drawable.frame_around2_left,
                            R.drawable.frame_around2_left_bottom,
                            R.drawable.frame_around2_bottom,
                            R.drawable.frame_around2_right_bottom,
                            R.drawable.frame_around2_right,
                            R.drawable.frame_around2_right_top,
                            R.drawable.frame_around2_top);
                    mTmpBmp = mImageFrame.combineFrameRes();
                    break;
                case R.id.photoRes_three:
                    mImageFrame.setFrameType(PhotoFrame.FRAME_BIG);
                    mImageFrame.setFrameResources(R.drawable.frame_big1);
                    mTmpBmp = mImageFrame.combineFrameRes();
                    break;
                default:
                    break;
            }
            reset();
        }
    }

    /**
     * 重新设置一下图片
     */
    private void reset() {
        picture.setImageBitmap(mTmpBmp);
        picture.invalidate();
    }
}