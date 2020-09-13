package cn.jarlen.imgedit.nine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.adapter.HorizontalDividerItemDecoration;
import cn.jarlen.imgedit.adapter.VerticalDividerItemDecoration;
import cn.jarlen.imgedit.base.BaseActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class NineActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    private ImageView ivPicture;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("九宫格");
        ivPicture = findViewById(R.id.iv_picture);
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .showLastDivider()
                .size(15).build());
        recyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(Color.TRANSPARENT)
                .showLastDivider()
                .size(15).build());
        imageAdapter = new ImageAdapter(this);
        recyclerView.setAdapter(imageAdapter);

        Bitmap picture = BitmapFactory.decodeFile(getImagePath());
        ivPicture.setImageBitmap(picture);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                splitNineImage();
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_nine;
    }

    private void splitNineImage() {
        imageAdapter.clearDataList();
        Observable.just(getImagePath()).map(new Function<String, List<ImagePiece>>() {
            @Override
            public List<ImagePiece> apply(String oriImgPath) throws Exception {
                return ImageSplitUtils.splitImage(oriImgPath, 3, "nine_");
            }
        }).map(new Function<List<ImagePiece>, List<ImagePiece>>() {
            @Override
            public List<ImagePiece> apply(List<ImagePiece> imagePieces) throws Exception {
                Arrays.sort(imagePieces.toArray());
                return imagePieces;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<ImagePiece>>() {
                    @Override
                    public void onNext(List<ImagePiece> imagePieces) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        sendBroadcast(intent);
                        ivPicture.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        imageAdapter.addDataList(imagePieces);
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