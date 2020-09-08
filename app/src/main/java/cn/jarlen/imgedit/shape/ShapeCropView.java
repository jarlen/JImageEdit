package cn.jarlen.imgedit.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ShapeCropView extends View {

    protected Context mContext;

    /**
     * 背景部分，也就是上面的图形
     */
    private Bitmap mBGBitmap;


    /**
     * 图片显示的矩阵
     */
    private Matrix mBGgmatrix;

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
        mBGgmatrix = new Matrix();
    }

    public void setBackGroundBitMap(Bitmap bgBitmap) {
        this.mBGBitmap = bgBitmap;
        mBGgmatrix.reset();

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 父容器传过来的宽度方向上的模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 父容器传过来的高度方向上的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 父容器传过来的宽度的值
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                - getPaddingRight();
        // 父容器传过来的高度的值
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingLeft()
                - getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY
                && heightMode != MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件为，宽度模式为Exactly，也就是填充父窗体或者是指定宽度；
            // 且高度模式不是Exaclty，代表设置的既不是fill_parent也不是具体的值，于是需要具体测量
            // 且图片的宽高比已经赋值完毕，不再是0.0f
            // 表示宽度确定，要测量高度
            height = (int) (width / ratio + 0.5f);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
                    MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY
                && heightMode == MeasureSpec.EXACTLY && ratio != 0.0f) {
            // 判断条件跟上面的相反，宽度方向和高度方向的条件互换
            // 表示高度确定，要测量宽度
            width = (int) (height * ratio + 0.5f);

            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawColor(Color.parseColor("#ffFFFFFF"));
        canvas.drawBitmap(mBGBitmap, mBGgmatrix, null);
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
                savedMatrix.set(mBGgmatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                eventMode = EventMode.ZOOM;
                oldDist = spacing(event);
                savedMatrix.set(mBGgmatrix);
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
                mBGgmatrix.set(matrix1);
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
