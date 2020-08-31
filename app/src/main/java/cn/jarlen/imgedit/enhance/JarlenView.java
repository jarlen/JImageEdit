package cn.jarlen.imgedit.enhance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import cn.jarlen.imgedit.R;

public class JarlenView extends View implements OnGestureListener
{

	private Context mContext;

	private Bitmap bgBitmap;

	private int mViewWidth;
	private int mViewHeight;

	private int bgWidth, bgHeight;

	private Paint mPointerPaint;
	private float xPosition = 0.0f;

	private int OldX = 0;

	private GestureDetector mGesture;

	public JarlenView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public JarlenView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mContext = context;
		bgBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.scale_img);
		bgWidth = bgBitmap.getWidth();
		bgHeight = bgBitmap.getHeight();

		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(dm);

		mViewWidth = dm.widthPixels;

		mPointerPaint = new Paint();
		mPointerPaint.setStrokeWidth(3);
		mPointerPaint.setColor(Color.parseColor("#FFBA89EB"));

		mGesture = new GestureDetector(context, this);

	}

	public JarlenView(Context context)
	{
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom)
	{
		mViewHeight = getHeight();
		super.onLayout(changed, left, top, right, bottom);
	}

	public void setOnlySide(int type, float factor)
	{
		switch (type)
		{
			case 1 :
				xPosition = (float) (mViewWidth / 2 - (factor * 0.5f / 0.7f + 0.5)
						* bgWidth);
				break;
			case 2 :
				xPosition = mViewWidth / 2 - bgWidth * factor;
				break;

			case 3 :

				xPosition = mViewWidth / 2 - bgWidth * factor;
				break;

			default :
				break;
		}
		invalidate();
	}

	@Override
	public void draw(Canvas canvas)
	{
		super.draw(canvas);

		checkBGBounds();
		canvas.drawBitmap(bgBitmap, xPosition, mViewHeight / 2 - bgHeight / 2,
				null);

		onDrawPointer(canvas);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
				| Paint.FILTER_BITMAP_FLAG));
	}

	/**
	 * 绘制指示器
	 * 
	 * @param canvas
	 */
	private void onDrawPointer(Canvas canvas)
	{
		canvas.save();
		canvas.drawLine(mViewWidth / 2, 25, mViewWidth / 2, mViewHeight - 25,
				mPointerPaint);
		canvas.restore();
		changeMoveAndValue();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		mGesture.onTouchEvent(event);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e)
	{
		OldX = (int) e.getRawX();
		return false;
	}

	/**
	 * 检查底部背景范围
	 */
	private void checkBGBounds()
	{
		if (xPosition >= mViewWidth / 2)
		{
			xPosition = mViewWidth / 2;
		} else if (xPosition < mViewWidth / 2 - bgWidth)
		{
			xPosition = mViewWidth / 2 - bgWidth;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY)
	{
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY)
	{
		xPosition = xPosition + (int) -distanceX;
		invalidate();

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e)
	{

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return false;
	}

	private void changeMoveAndValue()
	{
		if (mListener != null)
		{
			float sum = mViewWidth / 2 - xPosition;
			float lengh = bgWidth;
			float factor = sum / lengh;

			mListener.OnRulerSeekFactorChange(factor);
			mListener.OnRulerSeekValueChange(sum, bgWidth);
		}
	}

	private OnRulerSeekChangeListener mListener;

	public void setOnRulerSeekChangeListener(OnRulerSeekChangeListener listener)
	{
		this.mListener = listener;
	}

	public interface OnRulerSeekChangeListener
	{
		public void OnRulerSeekValueChange(float value, float maxSum);

		/**
		 * 0~~~1
		 * 
		 * @param factor
		 */
		public void OnRulerSeekFactorChange(float factor);

	}

	public void recycle()
	{
		bgBitmap = null;
		mPointerPaint = null;
		mGesture = null;
		mListener = null;
	}
}
