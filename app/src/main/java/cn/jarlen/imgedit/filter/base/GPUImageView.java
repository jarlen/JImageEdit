package cn.jarlen.imgedit.filter.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;

import java.io.File;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

public class GPUImageView extends GLSurfaceView
{

	private Context mContext;

	private GPUImage mGPUImage;
	private GPUImageFilter mFilter;
	public Size mForceSize = null;
	private float mRatio = 0.0f;

	public static class Size
	{
		int width;
		int height;

		public Size(int width, int height)
		{
			this.width = width;
			this.height = height;
		}
	}

	public GPUImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mContext = context;
		initView(context);
	}

	public GPUImageView(Context context)
	{
		super(context);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context context)
	{
		mGPUImage = new GPUImage(context);
		mGPUImage.setGLSurfaceView(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		if (mRatio != 0.0f)
		{
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);

			int newHeight;
			int newWidth;
			if (width / mRatio < height)
			{
				newWidth = width;
				newHeight = Math.round(width / mRatio);
			} else
			{
				newHeight = height;
				newWidth = Math.round(height * mRatio);
			}

			int newWidthSpec = 0, newHeightSpec = 0;

			if (mForceSize != null)
			{
				newWidthSpec = MeasureSpec.makeMeasureSpec(mForceSize.width,
						MeasureSpec.EXACTLY);
				newHeightSpec = MeasureSpec.makeMeasureSpec(mForceSize.height,
						MeasureSpec.EXACTLY);
			} else
			{
				newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth,
						MeasureSpec.EXACTLY);
				newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight,
						MeasureSpec.EXACTLY);

			}
			super.onMeasure(newWidthSpec, newHeightSpec);

		} else
		{
			if (mForceSize != null)
			{
				super.onMeasure(MeasureSpec.makeMeasureSpec(mForceSize.width,
						MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
						mForceSize.height, MeasureSpec.EXACTLY));

			} else
			{
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			}

		}
	}

	public void setRatio(float ratio)
	{
		mRatio = ratio;
		this.updateLayout();
		mGPUImage.deleteImage();
	}

	/**
	 * 设置缩放类型
	 * 
	 * @param
	 * type
	 * 
	 */
	public void setScaleType(GPUImage.ScaleType type)
	{
		mGPUImage.setScaleType(type);
	}

	/**
	 * 设置旋转角度
	 * 
	 * @param rotation
	 * 
	 */
	public void setRotation(Rotation rotation)
	{
		mGPUImage.setRotation(rotation);
		this.updateLayout();
	}

	public void setImage(Bitmap bitmap)
	{

		int Width = bitmap.getWidth();
		int Height = bitmap.getHeight();

		// if(Width > 720)
		// {
		// Width = 720;
		// }
		// if(Height > 1230)
		// {
		// Height = 1230;
		// }
		// mForceSize = new Size(Width, Height);

		mGPUImage.setImage(bitmap);
	}

	public void setImage(Uri uri)
	{
		mGPUImage.setImage(uri);
	}

	/**
	 * 设置图片滤镜
	 *
	 * @param file
	 *
	 */
	public void setImage(File file)
	{
		mGPUImage.setImage(file);
	}

	public void setFilter(GPUImageFilter filter)
	{
		Log.i("jarlen", " setFilter  filter = " + filter);
		mFilter = filter;
		mGPUImage.setFilter(filter);
		this.updateLayout();
	}

	public GPUImage getGPUImage()
	{
		return mGPUImage;
	}

	/**
	 * 获得当前的滤镜
	 * 
	 * @return 滤镜
	 */
	public GPUImageFilter getFilter()
	{
		Log.i("jarlen", " getFilter  mFilter = " + mFilter);
		return mFilter;
	}

	public interface OnPictureSavedListener
	{
		void onPictureSaved(Uri uri);
	}

	public void updateLayout()
	{
		this.requestLayout();
	}

	public void updateRender()
	{
		this.requestRender();
	}

	/******************************************************************************************************/

	public Bitmap captureBitmap() throws InterruptedException
	{
		final Semaphore waiter = new Semaphore(0);

		final int width = this.getMeasuredWidth();
		final int height = this.getMeasuredHeight();

		// Take picture on OpenGL thread
		final int[] pixelMirroredArray = new int[width * height];
		mGPUImage.runOnGLThread(new Runnable()
		{
			@Override
			public void run()
			{
				final IntBuffer pixelBuffer = IntBuffer
						.allocate(width * height);
				GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA,
						GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
				int[] pixelArray = pixelBuffer.array();

				// Convert upside down mirror-reversed image to right-side up
				// normal image.
				for (int i = 0; i < height; i++)
				{
					for (int j = 0; j < width; j++)
					{
						pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i
								* width + j];
					}
				}
				waiter.release();
			}
		});
		requestRender();
		waiter.acquire();

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
		return bitmap;
	}

	/**
	 * Retrieve current image with filter applied and given size as Bitmap.
	 * 
	 * @param width
	 *            requested Bitmap width
	 * @param height
	 *            requested Bitmap height
	 * @return Bitmap of picture with given size
	 * @throws InterruptedException
	 */
	public Bitmap captureBitmap(final int width, final int height)
			throws InterruptedException
	{
		// This method needs to run on a background thread because it will take
		// a longer time
		if (Looper.myLooper() == Looper.getMainLooper())
		{
			throw new IllegalStateException(
					"Do not call this method from the UI thread!");
		}

		mForceSize = new Size(width, height);

		final Semaphore waiter = new Semaphore(0);

		// Layout with new size
		getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener()
				{
					@Override
					public void onGlobalLayout()
					{
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
						{
							getViewTreeObserver().removeGlobalOnLayoutListener(
									this);
						} else
						{
							getViewTreeObserver().removeOnGlobalLayoutListener(
									this);
						}
						waiter.release();
					}
				});
		post(new Runnable()
		{
			@Override
			public void run()
			{
				// Show loading
				// addView(new LoadingView(getContext()));

				updateLayout();
			}
		});
		waiter.acquire();

		// Run one render pass
		mGPUImage.runOnGLThread(new Runnable()
		{
			@Override
			public void run()
			{
				waiter.release();
			}
		});
		requestRender();
		waiter.acquire();
		Bitmap bitmap = captureBitmap();

		mForceSize = null;
		post(new Runnable()
		{
			@Override
			public void run()
			{
				updateLayout();
			}
		});
		updateRender();

		postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				// Remove loading view
				// removeViewAt(1);
			}
		}, 300);

		return bitmap;
	}

	public void recycle()
	{
		if (mGPUImage != null)
		{
			mGPUImage = null;
		}
		if(mFilter != null)
		{
			mFilter = null;
		}
		mForceSize = null;
	}

}
