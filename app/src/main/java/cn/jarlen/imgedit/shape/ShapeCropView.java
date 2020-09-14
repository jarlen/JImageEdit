package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import cn.jarlen.imgedit.util.FileUtils;

public class ShapeCropView extends View {

    protected Context mContext;

    /**
     * 最大缩放值
     */
    private float maxZoom = 2.0f;

    /**
     * 操作事件类型 DRAG：拖动 ZOOM：缩放
     *
     * @author jarlen
     */
    private enum EventMode {
        NONE, DRAG, ZOOM
    }

    private EventMode eventMode = EventMode.NONE;

    private float x_down = 0;
    private float y_down = 0;

    private int mViewWidth;
    private int mViewHeight;

    private BitmapDrawable mBgDrawable;
    private Rect mBgDrawableRect = new Rect();
    private Rect mBgDrawableTempRect = new Rect();
    private int originalWidth;
    private float mOriginScale;
    private float mRadioWH;

    private BitmapDrawable maskDrawable;

    private Rect mMaskDrawableRect = new Rect();

    private RectF mapRect = new RectF();
    private RectF mapRectTemp = new RectF();

    private PointF midPoint = new PointF();

    float oldDist = 1f;

    private float centerX, centerY;

    public ShapeCropView(Context context) {
        this(context, null);
    }

    public ShapeCropView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShapeCropView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        mViewWidth = point.x;
        mViewHeight = mViewWidth;
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setMaskResource(int resId) {
        Bitmap mMaskBit = BitmapFactory.decodeResource(getResources(), resId);
        if (mMaskBit == null) {
            return;
        }
        maskDrawable = new BitmapDrawable(mContext.getResources(), mMaskBit);

        int left = 0;
        int top = 0;
        int right = mViewWidth;
        int bottom = mViewHeight;
        mMaskDrawableRect.set(left, top, right, bottom);
        maskDrawable.setBounds(mMaskDrawableRect);
        invalidate();
    }

    public void setMaskBitmap(Bitmap mMaskBit) {
        if (mMaskBit == null) {
            return;
        }
        maskDrawable = new BitmapDrawable(mContext.getResources(), mMaskBit);

        int left = 0;
        int top = 0;
        int right = mViewWidth;
        int bottom = mViewHeight;
        mMaskDrawableRect.set(left, top, right, bottom);
        maskDrawable.setBounds(mMaskDrawableRect);
        invalidate();
    }

    public void setMaskPath(String maskPath) {
        Bitmap mMaskBit = BitmapFactory.decodeFile(maskPath);
        if (mMaskBit == null) {
            return;
        }
        maskDrawable = new BitmapDrawable(mContext.getResources(), mMaskBit);

        int left = 0;
        int top = 0;
        int right = mViewWidth;
        int bottom = mViewHeight;
        mMaskDrawableRect.set(left, top, right, bottom);
        maskDrawable.setBounds(mMaskDrawableRect);
        invalidate();
    }

    public void setBackGroundBitMap(Bitmap bgBit) {
        /**
         * 背景部分，也就是上面的图形
         */
        if (bgBit == null) {
            return;
        }

        float scaleWidth = mViewWidth * 1.0f / bgBit.getWidth();
        float scaleHeight = mViewHeight * 1.0f / bgBit.getHeight();
        float mScale = Math.max(scaleWidth, scaleHeight);

        int width = (int) (bgBit.getWidth() * mScale);
        int height = (int) (bgBit.getHeight() * mScale);
        originalWidth = bgBit.getWidth();
        mRadioWH = bgBit.getWidth() * 1.0f / bgBit.getHeight();
        mOriginScale = mScale;

        maxZoom = mOriginScale + 5;

        int left = (int) (mViewWidth * 1.f / 2 - width * 1.0f / 2);
        int top = (int) (mViewHeight * 1.f / 2 - height * 1.0f / 2);
        int right = left + width;
        int bottom = top + height;

        mBgDrawableRect.set(left, top, right, bottom);
        mBgDrawableTempRect.set(mBgDrawableRect);
        mBgDrawable = new BitmapDrawable(mContext.getResources(), bgBit);
        mBgDrawable.setBounds(mBgDrawableRect);
        centerX = mBgDrawableRect.centerX();
        centerY = mBgDrawableRect.centerY();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mViewHeight,
                MeasureSpec.EXACTLY);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(mViewWidth,
                MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBgDrawable != null) {
            mBgDrawable.draw(canvas);
        }

        if (maskDrawable != null) {
            maskDrawable.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() > 1) {
                    eventMode = EventMode.ZOOM;
                    oldDist = spacing(event);
                    midPoint(midPoint, event);
                    mBgDrawableTempRect.set(mBgDrawableRect);
                } else {
                    x_down = event.getX();
                    y_down = event.getY();
                    eventMode = EventMode.DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (eventMode == EventMode.DRAG) {
                    float dx = event.getX() - x_down;
                    float dy = event.getY() - y_down;
                    int left = (int) (mBgDrawableTempRect.left + dx);
                    int right = (int) (mBgDrawableTempRect.right + dx);
                    if (left > 0) {
                        left = 0;
                        right = left + mBgDrawableTempRect.width();
                    }

                    if (right < mViewWidth) {
                        right = mViewHeight;
                        left = mViewHeight - mBgDrawableTempRect.width();
                    }
                    int top = (int) (mBgDrawableTempRect.top + dy);
                    int bottom = (int) (mBgDrawableTempRect.bottom + dy);
                    if (top > 0) {
                        top = 0;
                        bottom = top + mBgDrawableTempRect.height();
                    }

                    if (bottom < mViewHeight) {
                        bottom = mViewHeight;
                        top = bottom - mBgDrawableTempRect.height();
                    }

                    mBgDrawableRect.set(left, top, right, bottom);
                    mBgDrawable.setBounds(mBgDrawableRect);
                    centerX = mBgDrawableRect.centerX();
                    centerY = mBgDrawableRect.centerY();
                    invalidate();
                } else if (eventMode == EventMode.ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;

                    int newWidth = (int) (mBgDrawableTempRect.width() * scale);
                    float scaleTemp = newWidth * 1.0f / originalWidth;

                    scaleTemp = Math.max(Math.min(scaleTemp, maxZoom), mOriginScale);
                    newWidth = (int) (originalWidth * scaleTemp);
                    int newHeight = (int) (newWidth / mRadioWH);

                    int left = (int) (centerX - newWidth / 2);
                    int right = (int) (centerX + newWidth / 2);

                    if (left > 0) {
                        left = 0;
                        right = newWidth;
                    }

                    if (right < mViewWidth) {
                        right = mViewHeight;
                        left = mViewHeight - newWidth;
                    }

                    int top = (int) (centerY - newHeight / 2);
                    int bottom = (int) (centerY + newHeight / 2);


                    if (top > 0) {
                        top = 0;
                        bottom = top + newHeight;
                    }

                    if (bottom < mViewHeight) {
                        bottom = mViewHeight;
                        top = bottom - newHeight;
                    }
                    mBgDrawableRect.set(left, top, right, bottom);
                    mBgDrawable.setBounds(mBgDrawableRect);
                    centerX = mBgDrawableRect.centerX();
                    centerY = mBgDrawableRect.centerY();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mBgDrawableTempRect.set(mBgDrawableRect);
                eventMode = EventMode.NONE;
                break;
            default:
                break;
        }
        return true;
    }

    private void checkRect(RectF dst, RectF sr) {
        if (mapRect.right > 0) {
            mapRectTemp.right = 0;
        }

    }

    // 取手势中心点
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // 触碰两点间距离
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        }
        return 0;

    }

    public Bitmap getBitmap() {
        Bitmap bitmap = FileUtils.getViewBitmap(this);
        return bitmap;
    }
}
