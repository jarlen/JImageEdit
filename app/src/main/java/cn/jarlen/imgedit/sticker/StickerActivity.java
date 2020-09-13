package cn.jarlen.imgedit.sticker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Callable;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.imagezoom.ImageViewTouch;
import cn.jarlen.imgedit.imagezoom.ImageViewTouchBase;
import cn.jarlen.imgedit.util.FileUtils;
import cn.jarlen.imgedit.util.Matrix3;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class StickerActivity extends BaseActivity implements StickerAdapter.OnStickerPasterListener {

    public static final String STICKER_PASTER_FLODER = "stickers";

    ImageViewTouch mainImage;
    Bitmap imageBit;

    StickerView mStickerView;
    RecyclerView pasterList;
    StickerAdapter stickerAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("贴纸");
        mainImage = findViewById(R.id.iv_image);
        mainImage.setDoubleTapEnabled(false);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        imageBit = BitmapFactory.decodeFile(getImagePath());
        mainImage.setImageBitmap(imageBit);
        mainImage.setScaleEnabled(false);

        mStickerView = findViewById(R.id.view_sticker);
        pasterList = findViewById(R.id.rv_paster_list);

        pasterList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        stickerAdapter = new StickerAdapter(this);
        stickerAdapter.setPasterListener(this);
        pasterList.setAdapter(stickerAdapter);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        initPaster();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_sticker;
    }

    private void initPaster() {
        Observable.fromCallable(new Callable<List<Paster>>() {
            @Override
            public List<Paster> call() throws Exception {
                String[] pasterArray = ImageEditApplication.getApplication().getAssets().list(STICKER_PASTER_FLODER);
                int count = pasterArray.length;
                List<Paster> pasterList = new ArrayList<>();
                for (int index = 0; index < count; index++) {
                    Paster paster = new Paster();
                    String pasterFileName = STICKER_PASTER_FLODER + File.separator + pasterArray[index];
                    paster.setPasterPath(FileUtils.buildAssetPath(pasterFileName));
                    paster.setPasterName(pasterFileName);
                    pasterList.add(paster);
                }
                return pasterList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<Paster>>() {

                    @Override
                    public void onNext(List<Paster> pasterList) {
                        stickerAdapter.addDataList(pasterList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onStickerPaster(String paster) {
        mStickerView.addBitImage(FileUtils.getImageFromAssetsFile(paster));
    }

    private void saveImage() {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {

                Matrix touchMatrix = mainImage.getImageViewMatrix();

                Bitmap resultBit = Bitmap.createBitmap(imageBit).copy(
                        Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(resultBit);

                float[] data = new float[9];
                touchMatrix.getValues(data);// 底部图片变化记录矩阵原始数据
                Matrix3 cal = new Matrix3(data);// 辅助矩阵计算类
                Matrix3 inverseMatrix = cal.inverseMatrix();// 计算逆矩阵
                Matrix m = new Matrix();
                m.setValues(inverseMatrix.getValues());

                LinkedHashMap<Integer, StickerItem> addItems = mStickerView.getBank();
                for (Integer id : addItems.keySet()) {
                    StickerItem item = addItems.get(id);
                    item.matrix.postConcat(m);// 乘以底部图片变化矩阵
                    canvas.drawBitmap(item.bitmap, item.matrix, null);
                }// end for
                return resultBit;
            }
        }).flatMap(new Function<Bitmap, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(Bitmap bitmap) throws Exception {
                return saveImage2(bitmap, "sticker_");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Boolean>() {

                    @Override
                    public void onNext(Boolean ret) {
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
}