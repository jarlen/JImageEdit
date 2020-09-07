package cn.jarlen.imgedit.arrow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;

public class ArrowRectActivity extends BaseActivity {

    public static final String RECTANGLE_ABLE = "rectangleAble";

    FrameLayout mFrameLayout;

    GraffitiView mGraffitiView;

    private boolean rectangleAble = false;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        rectangleAble = getIntent().getBooleanExtra(RECTANGLE_ABLE, false);

        mFrameLayout = findViewById(R.id.graffiti_container);

        Bitmap bitmap = BitmapFactory.decodeFile(getImagePath());

        mGraffitiView = new GraffitiView(this, bitmap, new GraffitiView.GraffitiListener() {

            @Override
            public void onSaved(Bitmap bitmap, Bitmap bitmapEraser) {
                saveImage(bitmap, rectangleAble ? "rect_" : "arrow_");
            }

            @Override
            public void onError(int i, String msg) {

            }

            @Override
            public void onReady() {

            }
        });
        TextView clean = findViewById(R.id.iv_toolbar_right_two);
        clean.setText("撤回");
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraffitiView.clear();
            }
        });
        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraffitiView.save();
            }
        });

        mGraffitiView.setShape(rectangleAble ? GraffitiView.Shape.HOLLOW_RECT : GraffitiView.Shape.ARROW);
        mFrameLayout.addView(mGraffitiView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_arrow;
    }
}
