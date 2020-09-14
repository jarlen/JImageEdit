package cn.jarlen.imgedit.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static cn.jarlen.imgedit.util.FileUtils.getImageFromAssetsFile;

public class ShapeCutActivity extends BaseActivity implements OnAdapterItemClickListener<String> {

    public static final String SHAPE_FLODER = "shape";

    private ShapeCropView shapeCropView;

    private RecyclerView rvListShape;
    private ShapeListAdapter shapeListAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("形状");
        shapeCropView = findViewById(R.id.view_shape_cut);
        rvListShape = findViewById(R.id.rv_list_shape);
        LinearLayoutManager shapeLinearLayoutManager = new LinearLayoutManager(this);
        shapeLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvListShape.setLayoutManager(shapeLinearLayoutManager);
        shapeListAdapter = new ShapeListAdapter(this);
        shapeListAdapter.setAdapterItemClickListener(this);
        rvListShape.setAdapter(shapeListAdapter);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = shapeCropView.getBitmap();
                saveImage(bitmap, "shape_");
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(getImagePath());
        shapeCropView.setBackGroundBitMap(bitmap);
        getShapeData();
    }

    private void getShapeData() {

        Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                String[] frameArray = ImageEditApplication.getApplication().getAssets().list(SHAPE_FLODER);
                int count = frameArray.length;
                List<String> frameList = new ArrayList<>();
                for (int index = 0; index < count; index++) {
                    String frameName = SHAPE_FLODER + File.separator + frameArray[index];
                    frameList.add(frameName);
                }
                return frameList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<String>>() {

                    @Override
                    public void onNext(List<String> strings) {
                        shapeListAdapter.addDataList(strings);
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
    protected int getLayoutResId() {
        return R.layout.activity_shape_cut;
    }

    @Override
    public void onItemClick(String item, int position) {
        Bitmap bitmap = getImageFromAssetsFile(item);
        shapeCropView.setMaskBitmap(bitmap);
    }
}
