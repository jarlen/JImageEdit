package cn.jarlen.imgedit.draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.adapter.ColorListAdapter;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.imagezoom.ImageViewTouch;
import cn.jarlen.imgedit.imagezoom.ImageViewTouchBase;
import cn.jarlen.imgedit.util.ColorPicker;
import cn.jarlen.imgedit.util.Matrix3;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class DrawActivity extends BaseActivity implements ColorListAdapter.IColorListAction, View.OnClickListener {

    private PaintModeView mPaintModeView;
    private RecyclerView mColorListView;//颜色列表View
    private ColorListAdapter mColorAdapter;
    private View popView;

    private ColorPicker mColorPicker;//颜色选择器

    private PopupWindow setStokenWidthWindow;
    private SeekBar mStokenWidthSeekBar;

    private ImageView mEraserView;

    private CustomPaintView mPaintView;

    public boolean isEraser = false;//是否是擦除模式

    private Bitmap imageBit;
    ImageViewTouch mainImage;

    public int[] mPaintColors = {Color.BLACK,
            Color.DKGRAY, Color.GRAY, Color.LTGRAY, Color.WHITE,
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA};

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mPaintView = findViewById(R.id.custom_paint_view);
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage21(mPaintView.getPaintBit(), "paint_");
            }
        });
        mainImage = findViewById(R.id.iv_image);
        mainImage.setDoubleTapEnabled(false);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        mPaintModeView = findViewById(R.id.paint_thumb);
        mColorListView = findViewById(R.id.paint_color_list);
        mEraserView = findViewById(R.id.paint_eraser);

        imageBit = BitmapFactory.decodeFile(getImagePath());
        mainImage.setImageBitmap(imageBit);

        mColorPicker = new ColorPicker(this, 255, 0, 0);
        initColorListView();
        mPaintModeView.setOnClickListener(this);

        initStokeWidthPopWindow();

        mEraserView.setOnClickListener(this);
        updateEraserView();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_edit_paint;
    }

    /**
     * 初始化颜色列表
     */
    private void initColorListView() {

        mColorListView.setHasFixedSize(false);

        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(this);
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mColorListView.setLayoutManager(stickerListLayoutManager);
        mColorAdapter = new ColorListAdapter(this, mPaintColors, this);
        mColorListView.setAdapter(mColorAdapter);
    }

    private void initStokeWidthPopWindow() {
        popView = LayoutInflater.from(this).
                inflate(R.layout.view_set_stoke_width, null);
        setStokenWidthWindow = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);

        mStokenWidthSeekBar = (SeekBar) popView.findViewById(R.id.stoke_width_seekbar);

        setStokenWidthWindow.setFocusable(true);
        setStokenWidthWindow.setOutsideTouchable(true);
        setStokenWidthWindow.setBackgroundDrawable(new BitmapDrawable());
        setStokenWidthWindow.setAnimationStyle(R.style.popwin_anim_style);


        mPaintModeView.setPaintStrokeColor(Color.RED);
        mPaintModeView.setPaintStrokeWidth(10);

        updatePaintView();
    }

    private void updatePaintView() {
        isEraser = false;
        updateEraserView();
        this.mPaintView.setColor(mPaintModeView.getStokenColor());
        this.mPaintView.setWidth(mPaintModeView.getStokenWidth());
    }

    private void updateEraserView() {
        mEraserView.setImageResource(isEraser ? R.drawable.eraser_seleced : R.drawable.eraser_normal);
        mPaintView.setEraser(isEraser);
    }

    @Override
    public void onColorSelected(int position, int color) {
        setPaintColor(color);
    }

    @Override
    public void onMoreSelected(int position) {
        mColorPicker.show();
        Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPaintColor(mColorPicker.getColor());
                mColorPicker.dismiss();
            }
        });
    }

    /**
     * 设置画笔颜色
     *
     * @param paintColor
     */
    protected void setPaintColor(final int paintColor) {
        mPaintModeView.setPaintStrokeColor(paintColor);
        updatePaintView();
    }

    @Override
    public void onClick(View v) {
        if (v == mPaintModeView) {//设置绘制画笔粗细
            setStokeWidth();
        } else if (v == mEraserView) {
            toggleEraserView();
        }
    }

    private void toggleEraserView() {
        isEraser = !isEraser;
        updateEraserView();
    }

    /**
     * 设置画笔粗细
     * show popwidnow to set paint width
     */
    protected void setStokeWidth() {
        if (popView.getMeasuredHeight() == 0) {
            popView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }

        mStokenWidthSeekBar.setMax(mPaintModeView.getMeasuredHeight());

        mStokenWidthSeekBar.setProgress((int) mPaintModeView.getStokenWidth());

        mStokenWidthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPaintModeView.setPaintStrokeWidth(progress);
                updatePaintView();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void saveImage21(final Bitmap bitmap, final String suffix) {
        Observable.just(bitmap)
                .map(new Function<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap apply(Bitmap bitmap) throws Exception {

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

                        float[] f = new float[9];
                        m.getValues(f);
                        int dx = (int) f[Matrix.MTRANS_X];
                        int dy = (int) f[Matrix.MTRANS_Y];
                        float scale_x = f[Matrix.MSCALE_X];
                        float scale_y = f[Matrix.MSCALE_Y];
                        canvas.save();
                        canvas.translate(dx, dy);
                        canvas.scale(scale_x, scale_y);
                        if (mPaintView.getPaintBit() != null) {
                            canvas.drawBitmap(mPaintView.getPaintBit(), 0, 0, null);
                        }
                        canvas.restore();
                        return resultBit;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap1) {
                        saveImage(bitmap1, suffix);
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
