package cn.jarlen.imgedit.nine;

import android.graphics.Color;
import android.os.Bundle;

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

    RecyclerView recyclerView;
    ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nine);
        recyclerView = findViewById(R.id.rv_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .color(Color.parseColor("#FFFFFF"))
                .size(15).build());
        recyclerView.addItemDecoration(new VerticalDividerItemDecoration.Builder(this)
                .color(Color.parseColor("#FFFFFF"))
                .size(15).build());
        imageAdapter = new ImageAdapter(this);
        recyclerView.setAdapter(imageAdapter);
        splitImage();
    }

    private void splitImage() {
        Observable.just(getImagePath()).map(new Function<String, List<ImagePiece>>() {
            @Override
            public List<ImagePiece> apply(String oriImgPath) throws Exception {
                return ImageSplitUtils.splitImage(oriImgPath, 3);
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
                        imageAdapter.addDataList(imagePieces);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}