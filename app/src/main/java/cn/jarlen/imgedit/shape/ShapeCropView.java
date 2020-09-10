package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
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
     * 背景部分，也就是上面的图形
     */
    private Bitmap mBgBit;
    /**
     * 图片显示的矩阵
     */
    private Matrix mBgMatrix;
    private float mScale;

    private Bitmap mMaskBit;
    private Matrix mMaskMatrix = new Matrix();


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

    public void setMaskBitMap(Bitmap mask) {
        this.mMaskBit = mask;
        if (mMaskBit == null) {
            return;
        }

        float scale = mViewWidth * 1.0f / mMaskBit.getWidth();
        mMaskMatrix.setScale(scale, scale);
    }

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
        float scaleWidth = mBgBit.getWidth() * 1.0f / mViewWidth;
        float scaleHeight = mBgBit.getHeight() * 1.0f / mViewHeight;
        mScale = Math.min(scaleWidth, scaleHeight);

        mBgMatrix.setScale(mScale, mScale);
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
        canvas.save();
        canvas.drawColor(Color.parseColor("#ffFFFFFF"));
        canvas.drawBitmap(mBgBit, mBgMatrix, null);

        canvas.drawBitmap(mMaskBit, mMaskMatrix, null);

        canvas.restore();
    }

    float oldDist = 1f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                eventMode = EventMode.DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(mBgMatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                eventMode = EventMode.ZOOM;
                oldDist = spacing(event);
                savedMatrix.set(mBgMatrix);
                midPoint(midPoint, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (eventMode == EventMode.DRAG) {
                    matrix1.set(savedMatrix);
                    matrix1.postTranslate(event.getX() - x_down, event.getY()
                            - y_down);


                } else if (eventMode == EventMode.ZOOM) {
                    float newDist = spacing(event);
                    float scale = newDist / oldDist;
                    Log.e("jarlen", "scale--->" + scale);

                    /** 缩放 **/
                    matrix1.set(savedMatrix);
                    matrix1.postScale(scale, scale, midPoint.x, midPoint.y);
                }


                mBgMatrix.set(matrix1);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                eventMode = EventMode.NONE;
                break;
            default:
                break;
        }
        return true;
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
