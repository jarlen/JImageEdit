package cn.jarlen.imgedit.frame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;

import static cn.jarlen.imgedit.util.FileUtils.getImageFromAssetsFile;

public class PhotoFrameActivity extends BaseActivity implements OnAdapterItemClickListener<String> {

    ImageView picture;
    Bitmap mBitmap, mTmpBmp;
    PhotoFrame mImageFrame;

    RecyclerView rvListFrame;
    PhotoFrameAdapter frameAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("相框");
        picture = (ImageView) findViewById(R.id.picture);
        rvListFrame = findViewById(R.id.rv_list_frame);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage(mTmpBmp, "frame_");
            }
        });

        LinearLayoutManager shapeLinearLayoutManager = new LinearLayoutManager(this);
        shapeLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvListFrame.setLayoutManager(shapeLinearLayoutManager);
        frameAdapter = new PhotoFrameAdapter(this);
        frameAdapter.setAdapterItemClickListener(this);
        rvListFrame.setAdapter(frameAdapter);

        mBitmap = BitmapFactory.decodeFile(getImagePath());
        mTmpBmp = mBitmap;

        reset();
        mImageFrame = new PhotoFrame(this, mBitmap);

        frameAdapter.addDataList(getPhotoFrameData());
    }

    private List<String> getPhotoFrameData() {
        List<String> frameList = new ArrayList<>();
        for (int index = 1; index <= 18; index++) {
            String frameName = "PhotoFrame/pf_xg" + index + ".png";
            frameList.add(frameName);
        }
        return frameList;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_photo_frame;
    }

    @Override
    public void onItemClick(String item, int position) {
        mImageFrame.setFrameType(PhotoFrame.FRAME_BIG);
        Bitmap bitmap = getImageFromAssetsFile(item);
        mTmpBmp = mImageFrame.combineFrameRes(bitmap);
        reset();
    }

    /**
     * 重新设置一下图片
     */
    private void reset() {
        picture.setImageBitmap(mTmpBmp);
        picture.invalidate();
    }
}