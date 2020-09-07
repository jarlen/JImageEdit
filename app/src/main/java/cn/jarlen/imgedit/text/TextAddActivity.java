package cn.jarlen.imgedit.text;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.concurrent.Callable;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.imagezoom.ImageViewTouch;
import cn.jarlen.imgedit.imagezoom.ImageViewTouchBase;
import cn.jarlen.imgedit.util.ColorPicker;
import cn.jarlen.imgedit.util.Matrix3;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TextAddActivity extends BaseActivity implements TextWatcher {

    Bitmap bitmapSrc;

    TextStickerView mTextStickerView;
    ImageViewTouch mainImage;

    EditText etInput;
    ImageView btnColor;
    ColorPicker mColorPicker;

    private int mTextColor = Color.WHITE;
    private InputMethodManager imm;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        mainImage = findViewById(R.id.iv_image);
        mTextStickerView = findViewById(R.id.view_sticker_text);
        etInput = findViewById(R.id.et_input);
        btnColor = findViewById(R.id.btn_color);
        findViewById(R.id.btn_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorPicker.show();
                Button okColor = (Button) mColorPicker.findViewById(R.id.okColorButton);
                okColor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeTextColor(mColorPicker.getColor());
                        mColorPicker.dismiss();
                    }
                });
            }
        });

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTextAdd();
            }
        });

        bitmapSrc = BitmapFactory.decodeFile(getImagePath());
        mColorPicker = new ColorPicker(this, 255, 0, 0);

        etInput.addTextChangedListener(this);
        mTextStickerView.setEditText(etInput);
        etInput.clearFocus();

        //统一颜色设置
        btnColor.setBackgroundColor(mColorPicker.getColor());
        mTextStickerView.setTextColor(mColorPicker.getColor());
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mainImage.setDoubleTapEnabled(false);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        mainImage.setScaleEnabled(false);
        mainImage.setImageBitmap(bitmapSrc);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_text_add;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString().trim();
        mTextStickerView.setText(text);
    }

    /**
     * 修改字体颜色
     *
     * @param newColor
     */
    private void changeTextColor(int newColor) {
        this.mTextColor = newColor;
        btnColor.setBackgroundColor(mTextColor);
        mTextStickerView.setTextColor(mTextColor);
    }

    public void hideInput() {
        if (getCurrentFocus() != null && isInputMethodShow()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public boolean isInputMethodShow() {
        return imm.isActive();
    }

    private void saveTextAdd() {
        Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() throws Exception {

                Matrix touchMatrix = mainImage.getImageViewMatrix();
                Bitmap resultBit = Bitmap.createBitmap(bitmapSrc).copy(
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
                //System.out.println("scale = " + scale_x + "       " + scale_y + "     " + dx + "    " + dy);
                mTextStickerView.drawText(canvas, mTextStickerView.layout_x,
                        mTextStickerView.layout_y, mTextStickerView.mScale, mTextStickerView.mRotateAngle);
                canvas.restore();
                return resultBit;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(Bitmap bitmap) {
                        saveImage(bitmap, "text_add_");
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
