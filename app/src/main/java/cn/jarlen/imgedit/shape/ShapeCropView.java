package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class ShapeCropView extends View {

    protected Context mContext;


    /**
     * 图片操作临时矩阵
     */
    public Matrix matrix1 = new Matrix();

    /**
     * 记录拖动前的矩阵
     */
    Matrix savedMatrix = new Matrix();

    /**
     * 最初两点的距离;用来计算缩放比 除改变后的距离就是缩放比
     */
    private PointF dpoint = new PointF(); // 偏移位置

    /**
     * 中心点坐标
     */
    private PointF centerP = new PointF();

    /**
     * aciton_down 记录的点击点
     */
    private PointF startP = new PointF();

    private PointF begMoveP = new PointF();

    /**
     * 拖动中的缩放
     */
    float moveDist = 1f;

    /**
     * 最大缩放值
     */
    private float maxZoom = 2.0f;

    /**
     * 最小缩放值
     */
    private float minZoom = 1f;

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
    private int mMaskBitWidth;

    /**
     * 操作状态
     */
    private final int STATUS_TOUCH_SINGLE = 1; // 单点
    private final int STATUS_TOUCH_MULTI_START = 2; // 多点开始
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3; // 多点拖拽中

    /* 单点触摸的时候 */
    private float oldX = 0;
    private float oldY = 0;

    /**
     * 多点触摸的时候
     */
    private float oldx_0 = 0;
    private float oldy_0 = 0;

    private float oldx_1 = 0;
    private float oldy_1 = 0;


    private int mTouchStatus = STATUS_TOUCH_SINGLE;

    /**
     * 背景部分，也就是上面的图形
     */
    private Bitmap mBgBit;

    private BitmapDrawable mBgDrawable;
    private Rect mBgDrawableRect = new Rect();
    private Rect mBgDrawableTempRect = new Rect();
    private int originalWidth;
    private float mOriginScale;

    /**
     * 图片显示的矩阵
     */
    private Matrix mBgMatrix;
    private float mScale;

    private Bitmap mMaskBit;
    private Matrix mMaskMatrix = new Matrix();
    private Rect mMaskDrawableRect = new Rect();

    private RectF mapRect = new RectF();
    private RectF mapRectTemp = new RectF();


    private PointF midPoint = new PointF();

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
        mBgMatrix = new Matrix();
    }

//    public void setMaskBitMap(Bitmap mask) {
//        this.mMaskBit = mask;
//        if (mMaskBit == null) {
//            return;
//        }
//
//        float scale = mViewWidth * 1.0f / mMaskBit.getWidth();
//
//        mBgDrawableRect.set(0,0,);
//
//
//        mBgDrawable.setBounds(mBgDrawableRect);
//
//
//        mMaskMatrix.setScale(scale, scale);
//    }

    public void setMaskResource(int resId) {
        this.mMaskBit = BitmapFactory.decodeResource(getResources(), resId);
        if (mMaskBit == null) {
            return;
        }

        float scale = mViewWidth * 1.0f / mMaskBit.getWidth();


        mMaskMatrix.setScale(scale, scale);
    }

    public void setBackGroundBitMap(Bitmap bgBit) {
        this.mBgBit = bgBit;
        if (mBgBit == null) {
            return;
        }

        float scaleWidth = mViewWidth * 1.0f / mBgBit.getWidth();
        float scaleHeight = mViewHeight * 1.0f / mBgBit.getHeight();
        mScale = Math.max(scaleWidth, scaleHeight);

        int width = (int) (mBgBit.getWidth() * mScale);
        int height = (int) (mBgBit.getHeight() * mScale);
        originalWidth = width;
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
        mBgDrawable.draw(canvas);
    }

    float oldDist = 1f;

    float moveX = 0, moveY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        if (event.getPointerCount() > 1) {
//            if (mTouchStatus == STATUS_TOUCH_SINGLE) {
//                mTouchStatus = STATUS_TOUCH_MULTI_START;
//                oldx_0 = event.getX(0);
//                oldy_0 = event.getY(0);
//
//                oldx_1 = event.getX(1);
//                oldy_1 = event.getY(1);
//            } else if (mTouchStatus == STATUS_TOUCH_MULTI_START) {
//                mTouchStatus = STATUS_TOUCH_MULTI_TOUCHING;
//            }
//        } else {
//            if (mTouchStatus == STATUS_TOUCH_MULTI_START
//                    || mTouchStatus == STATUS_TOUCH_MULTI_TOUCHING) {
//                oldx_0 = 0;
//                oldy_0 = 0;
//
//                oldx_1 = 0;
//                oldy_1 = 0;
//
//                oldX = event.getX();
//                oldY = event.getY();
//            }
//            mTouchStatus = STATUS_TOUCH_SINGLE;
//        }
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//
//                oldX = event.getX();
//                oldY = event.getY();
//                break;
//
//            case MotionEvent.ACTION_UP:
//
////                checkBounds();
//                break;
//
//            case MotionEvent.ACTION_POINTER_UP:
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (mTouchStatus == STATUS_TOUCH_MULTI_TOUCHING){
//                    float newx_0 = event.getX(0);
//                    float newy_0 = event.getY(0);
//
//                    float newx_1 = event.getX(1);
//                    float newy_1 = event.getY(1);
//
//                    float oldWidth = Math.abs(oldx_1 - oldx_0);
//                    float oldHeight = Math.abs(oldy_1 - oldy_0);
//
//                    float newWidth = Math.abs(newx_1 - newx_0);
//                    float newHeight = Math.abs(newy_1 - newy_0);
//
//                    boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math
//                            .abs(newWidth - oldWidth);
//
//                    float ration = isDependHeight
//                            ? (newHeight / oldHeight)
//                            : (newWidth / oldWidth);
//                    int centerX = mBgDrawableRect.centerX();
//                    int centerY = mBgDrawableRect.centerY();
//                    int _newWidth = (int) (mBgDrawableRect.width() * ration);
//                    int _newHeight = (int) (_newWidth / oriRationWH);
//
//
//                }
//
//                break;
//
//            default:
//                break;
//        }


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mBgDrawableTempRect.set(mBgDrawableRect);
                moveX = 0;
                moveY = 0;
                x_down = event.getX();
                y_down = event.getY();
                eventMode = EventMode.DRAG;
                Log.e("jarlen", "ACTION_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mBgDrawableTempRect.set(mBgDrawableRect);
                moveX = 0;
                moveY = 0;
                eventMode = EventMode.ZOOM;
                oldDist = spacing(event);
                midPoint(midPoint, event);
                Log.e("jarlen", "ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                if (eventMode == EventMode.DRAG) {
                    float dx = event.getX() - x_down;
                    float dy = event.getY() - y_down;
                    Log.e("jarlen", "ACTION_MOVE--->DRAG");
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
                    invalidate();
                } else if (eventMode == EventMode.ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    Log.e("jarlen", "ACTION_MOVE--->ZOOM scale--->" + scale);

                    int centerX = mBgDrawableTempRect.centerX();
                    int centerY = mBgDrawableTempRect.centerY();

                    int newWidth = (int) (mBgDrawableTempRect.width() * scale);
                    float scaleTemp = newWidth * 1.0f / originalWidth;
                    scaleTemp = Math.max(Math.min(scaleTemp, maxZoom), mOriginScale);
                    newWidth = (int) (mBgDrawableTempRect.width() * scaleTemp);
                    int newHeight = (int) (mBgDrawableTempRect.height() * scaleTemp);

                    int left = (int) (centerX - newWidth / 2.0f);
                    int right = (int) (centerX + newWidth / 2.0f);

                    if (left > 0) {
                        left = 0;
                        right = left + mBgDrawableTempRect.width();
                    }

                    if (right < mViewWidth) {
                        right = mViewHeight;
                        left = mViewHeight - mBgDrawableTempRect.width();
                    }
                    int top = (int) (centerY - newHeight / 2.0f);
                    int bottom = (int) (centerY + newHeight / 2.0f);
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
//        Bitmap tmpBitmap = Bitmap.createBitmap(mScrrenWidth, mScrrenHeight,
//                Bitmap.Config.ARGB_8888); // 背景图片
//        Canvas canvas = new Canvas(tmpBitmap); // 新建画布
//        canvas.drawBitmap(mBGBitmap, mBGgmatrix, null); // 画图片
//        canvas.save(); // 保存画布
//        canvas.restore();
//
//        Bitmap ret = Bitmap.createBitmap(tmpBitmap, mFloatRect.left,
//                mFloatRect.top, mFloatRect.width(), mFloatRect.height(), null,
//                true);
//        tmpBitmap.recycle();
//        tmpBitmap = null;
//
        Bitmap newRet = null;
//        = Bitmap.createBitmap(mFloatRect.width(),
//                mFloatRect.height(), Bitmap.Config.ARGB_8888);
//
//        Canvas canvasHead = new Canvas(newRet);
//        canvasHead.drawBitmap(ret, 0, 0, null);
//        Paint paintHead = new Paint();
//        paintHead.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
//        Bitmap crop = BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.shape_apple);
//
//        canvasHead.drawBitmap(crop, 0, 0, paintHead);

        return newRet;
    }
}
