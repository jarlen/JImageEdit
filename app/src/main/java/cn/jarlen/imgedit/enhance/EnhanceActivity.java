package cn.jarlen.imgedit.enhance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Callable;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.filter.WMFilterOperateView;
import cn.jarlen.imgedit.filter.edit.OperateView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class EnhanceActivity extends BaseActivity implements JarlenView.OnRulerSeekChangeListener {

    WMFilterOperateView mWMFilterOperateView;
    Bitmap mBitmapSrc;

    /**
     * 微调选择区
     **/
    private RelativeLayout adjustBar_rl;
    private JarlenView mAdjustBar;
    private TextView adjustBar_tv;
    private TextView adjustBarResetBtn;

    View layoutMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enhance);
        mWMFilterOperateView = findViewById(R.id.view_wm_filter_operate);
        mBitmapSrc = BitmapFactory.decodeFile(getImagePath());

        /** 微调选择区 **/
        findViewById(R.id.adjust_brightness).setOnClickListener(
                onAdjustClickListener);
        findViewById(R.id.adjust_color).setOnClickListener(
                onAdjustClickListener);
        adjustBar_rl = (RelativeLayout) findViewById(R.id.adjustBar_rl);
        mAdjustBar = (JarlenView) findViewById(R.id.adjustBar);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

        layoutMenu = findViewById(R.id.layout_menu);

        mAdjustBar.setOnRulerSeekChangeListener(this);
        adjustBar_tv = (TextView) findViewById(R.id.adjustBar_tv);
        adjustBarResetBtn = (TextView) findViewById(R.id.adjustBar_reset_btn);
        adjustBarResetBtn.setOnClickListener(onAdjustClickListener);
        mWMFilterOperateView.post(new Runnable() {
            @Override
            public void run() {

                if (!mWMFilterOperateView.isInvalidating()) {

                    mWMFilterOperateView.setImageResource(mBitmapSrc,
                            new OperateView.OnTouchWaterMarkListener() {
                                @Override
                                public void onTouchDown() {

                                }

                                @Override
                                public void OnTouchUp() {
                                    showMainMenu();
                                }
                            });
                } else {
                    mWMFilterOperateView.post(this);
                }
            }
        });
    }

    /************************ 微调 Begin ****************************************/

    private OnAdjustClickListener onAdjustClickListener = new OnAdjustClickListener();

    private float brightnessValue = 0.0f;
    private float whiteBalanceValue = 0.5f;
    private int adjustType = 0;

    @Override
    public void OnRulerSeekValueChange(float value, float maxSum) {

    }

    @Override
    public void OnRulerSeekFactorChange(float factor) {
        switch (adjustType) {
            case 1:
                float brightDegree = (factor - 0.5f) / 0.5f * 0.7f;
                final int tv = (int) ((factor - 0.5f) / 0.5f * 12);
                brightnessValue = brightDegree;
                /** 范围 －1.0f ～ 1.0f 0.0 为原图 **/
                mWMFilterOperateView.adjustBrightnessFilter(brightDegree);
                adjustBar_tv.setText(tv + "");
                break;
            case 2:
                float whiteBalanceDegree = factor;
                int whiteBalance_tv = (int) (12 * (factor - 0.5f) / 0.5f);
                whiteBalanceValue = whiteBalanceDegree;
                mWMFilterOperateView.adjustWhiteBalanceFilter(whiteBalanceDegree);
                adjustBar_tv.setText(whiteBalance_tv + "");
                break;
            default:
                break;
        }
    }

    private class OnAdjustClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int id = view.getId();

            if (id == R.id.adjust_brightness) {
                // 微调－亮度
                layoutMenu.setVisibility(View.GONE);
                adjustType = 1;
                mAdjustBar.setOnlySide(1, brightnessValue);
                adjustBar_rl.setVisibility(View.VISIBLE);
                /** 范围 －1.0f ～ 1.0f 0.0 为原图 **/
                mWMFilterOperateView.setBrightnessValue(brightnessValue);
            } else if (id == R.id.adjust_color) {
                // 微调－－色温
                layoutMenu.setVisibility(View.GONE);
                adjustType = 2;
                mAdjustBar.setOnlySide(2, whiteBalanceValue);
                adjustBar_rl.setVisibility(View.VISIBLE);
                /** 范围 0 ～ 1，从冷到原图，再到温 **/
                mWMFilterOperateView.setWhiteBalanceValue(whiteBalanceValue);
            } else if (id == R.id.adjustBar_reset_btn) {
                //复位
                switch (adjustType) {
                    case 1:
                        brightnessValue = 0.0f;
                        mAdjustBar.setOnlySide(1, brightnessValue);
                        mWMFilterOperateView.setBrightnessValue(brightnessValue);
                        break;
                    case 2:
                        whiteBalanceValue = 0.5f;
                        mAdjustBar.setOnlySide(2, whiteBalanceValue);
                        mWMFilterOperateView.setWhiteBalanceValue(whiteBalanceValue);
                        break;
                    default:
                        break;
                }
            } else {
                adjustType = 0;
            }
        }
    }

    private void showMainMenu() {
        // 微调－确定
        adjustBar_rl.setVisibility(View.GONE);
        layoutMenu.setVisibility(View.VISIBLE);
    }

    private void saveImage() {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {
                return mWMFilterOperateView.getFilterResult();
            }
        }).flatMap(new Function<Bitmap, ObservableSource<Boolean>>() {
            @Override
            public ObservableSource<Boolean> apply(Bitmap bitmap) throws Exception {
                return saveImage2(bitmap, "enhance_");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Boolean>() {

                    @Override
                    public void onNext(Boolean ret) {
                        Toast.makeText(ImageEditApplication.getApplication(), "已保存至相册", Toast.LENGTH_SHORT).show();
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