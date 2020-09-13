package cn.jarlen.imgedit.filter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;
import cn.jarlen.imgedit.filter.edit.OperateView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class FilterActivity extends BaseActivity implements OnAdapterItemClickListener<FilterItem> {

    private RecyclerView filterList;
    private WMFilterOperateView filterOperateView;
    private FilterAdapter filterAdapter;

    private Bitmap mBitmapSrc;

    private TextView saveBtn;

    private GPUImageFilterManager filterManager = null;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("滤镜");
        filterList = findViewById(R.id.rv_list_filter);
        filterOperateView = findViewById(R.id.view_wm_filter_operate);

        saveBtn = findViewById(R.id.iv_toolbar_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        saveBtn.setClickable(false);

        filterAdapter = new FilterAdapter(this);
        filterAdapter.setClickListener(this);

        LinearLayoutManager filterLinearLayoutManager = new LinearLayoutManager(this);
        filterLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        filterList.setLayoutManager(filterLinearLayoutManager);
        filterList.setAdapter(filterAdapter);

        mBitmapSrc = BitmapFactory.decodeFile(getImagePath());
        filterOperateView.post(new Runnable() {
            @Override
            public void run() {
                if (!filterOperateView.isInvalidating()) {
                    filterOperateView.setImageResource(mBitmapSrc,
                            new OperateView.OnTouchWaterMarkListener() {
                                @Override
                                public void onTouchDown() {

                                }

                                @Override
                                public void OnTouchUp() {

                                }
                            });
                    saveBtn.setClickable(true);
                } else {
                    filterOperateView.post(this);
                }
            }
        });

        initFilterThumbnail();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_filter;
    }

    private void initFilterThumbnail() {
        Observable.fromCallable(new Callable<ArrayList<FilterItem>>() {
            @Override
            public ArrayList<FilterItem> call() throws Exception {

                String[] filterName = ImageEditApplication.getApplication().getResources().getStringArray(
                        R.array.filterName);
                int[] filterType = ImageEditApplication.getApplication().getResources().getIntArray(
                        R.array.filterType);

                ArrayList<FilterItem> filterList = new ArrayList<FilterItem>();
                int width = (int) ImageEditApplication.getApplication().getResources().getDimension(
                        R.dimen.filter_item_width);
                int height = (int) ImageEditApplication.getApplication().getResources().getDimension(
                        R.dimen.filter_item_height);
                Bitmap thumbnailBmp = ThumbnailUtils.extractThumbnail(mBitmapSrc,
                        width, height);
                GPUImageFilterManager filterManaager = null;

                filterManaager = new GPUImageFilterManager(ImageEditApplication.getApplication());
                filterManaager.setImageSrc(thumbnailBmp);

                for (int index = 0; index < filterType.length; index++) {
                    FilterItem filterItem = new FilterItem();

                    /** 设置滤镜类型 **/
                    filterItem.setFilterType(filterType[index]);

                    /** 设置滤镜名字 **/
                    filterItem.setFilterName(filterName[index]);

                    /** 生成滤镜缩略图 **/

                    Bitmap thumbnailFilterBmp = filterManaager
                            .createBitmapWithFilterApplied(filterType[index]);

                    /** 设置滤镜缩略图 **/
                    filterItem.setFilterThumbnail(thumbnailFilterBmp);

                    filterList.add(filterItem);
                }
                return filterList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ArrayList<FilterItem>>() {

                    @Override
                    public void onNext(ArrayList<FilterItem> filterItems) {
                        filterAdapter.addDataList(filterItems);
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
    public void onItemClick(FilterItem item, int position) {
        if (filterManager == null) {
            filterManager = new GPUImageFilterManager(this);
        }

        ArrayList<GPUImageFilter> list = filterManager
                .GPUCreateGPUImageFilter(item.getFilterType());

        filterOperateView.setFilterTypeInManager(item.getFilterType());
        filterOperateView.setOperateFilter(list);
    }

    private void saveImage() {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return filterOperateView.getFilterResult();
            }
        }).flatMap(new Function<Bitmap, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(Bitmap bitmap) throws Exception {
                return saveImage2(bitmap, "filter_");
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
