package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import cn.jarlen.imgedit.R;

public class CropFloatView extends View {

    private Context mContext;

    /**
     * 中间区域浮层
     */
    private Bitmap mCropDrawable;

    /**
     * 中间浮层范围
     */
    private Rect mFloatRect;

    /**
     * 周围区域绘制笔
     */
    private Paint mEdgePaint;

    /**
     * 中间区域绘制笔
     */
    private Paint mFloatPaint;

    /**
     * 浮层透明度
     */
    private int mAlpha = 128;

    /**
     * 五块浮层范围
     */
    private Rect leftRect, topRect, rightRect, bottomRect;

    /**
     * 屏幕高宽度
     */
    private int mScrrenWidth, mScrrenHeight;

    public CropFloatView(Context context) {
        this(context, null);
    }

    public CropFloatView(Context context, int width, int height) {
        this(context, null);
        this.mScrrenWidth = width;
        this.mScrrenHeight = height;
    }

    public CropFloatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropFloatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;

        mCropDrawable = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.shape_apple);

        initRectPaint();
        initRectData();

    }

    /**
     * 初始化绘制笔
     */
    private void initRectPaint() {
        mFloatPaint = new Paint();
        mFloatPaint.setAlpha(mAlpha);
        mFloatPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));

        mEdgePaint = new Paint();
        mEdgePaint.setColor(Color.parseColor("#ff000000"));
        mEdgePaint.setAlpha(mAlpha);
    }

    /**
     * 初始化剪切背景浮层范围数据
     */
    private void initRectData() {
        int left = mScrrenWidth / 2 - mCropDrawable.getWidth() / 2;
        int top = mScrrenHeight / 2 - mCropDrawable.getHeight() / 2;
        int right = left + mCropDrawable.getWidth();
        int bottom = top + mCropDrawable.getHeight();

        /** 创建中间浮层区域范围 **/
        mFloatRect = new Rect(left, top, right, bottom);

        /** 创建周围浮层区域范围 **/
        initEdgeData();
    }

    /**
     * 设置剪切浮层
     *
     * @param bitmap
     */
    public void setCropDrawable(Bitmap bitmap) {
        mCropDrawable = bitmap;
        initRectData();
        invalidate();
    }

    /**
     * 获得剪切浮层区域
     *
     * @return
     */
    public Rect getCropDrawableRect() {
        return mFloatRect;
    }

    @Override
    public void draw(Canvas canvas) {
        /** 绘制周围的背景色 **/
        super.draw(canvas);
        drawEdge(canvas);

        /** 绘制中间浮层 **/
        canvas.drawBitmap(mCropDrawable, mFloatRect.left, mFloatRect.top,
                mFloatPaint);

    }

    /**
     * 初始化浮层周围背景数据
     * <p>
     * ------------------------------------- | top |
     * ------------------------------------- | | | | | | | | | left | | right |
     * | | | | | | | | ------------------------------------- | bottom |
     * -------------------------------------
     */
    private void initEdgeData() {
        leftRect = new Rect(0, mFloatRect.top, mFloatRect.left,
                mFloatRect.bottom);
        topRect = new Rect(0, 0, mScrrenWidth, mFloatRect.top);
        rightRect = new Rect(mFloatRect.right, mFloatRect.top, mScrrenWidth,
                mFloatRect.bottom);
        bottomRect = new Rect(0, mFloatRect.bottom, mScrrenWidth, mScrrenHeight);
    }

    /**
     * 绘制周围背景色
     *
     * @param canvas
     */
    private void drawEdge(Canvas canvas) {
        canvas.drawRect(leftRect, mEdgePaint);
        canvas.drawRect(topRect, mEdgePaint);
        canvas.drawRect(rightRect, mEdgePaint);
        canvas.drawRect(bottomRect, mEdgePaint);
    }
}
