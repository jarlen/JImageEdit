package cn.jarlen.imgedit.util;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;
import android.widget.ImageView;

public class ViewUtil
{

	public static int dipTopx(Context context, float dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, int dpValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue / scale);
	}
	
	
	/**
	 * 获取ImageView中实际绘制大小
	 * @param iv
	 * 这个Imageview 必须加载数据完成后才能调用此方法，使用post判断
	 * @return
	 */
	public static Point getDrawSizeFromImageView(ImageView iv){
		
		//获得ImageView中Image的真实宽高，  
        int dw = iv.getDrawable().getBounds().width();
        int dh = iv.getDrawable().getBounds().height();
        Log.d("===", "获得ImageView中Image的真实宽高 dw = " + dw + ", dh = " + dh);
          
        //获得ImageView中Image的变换矩阵  
        Matrix m = iv.getImageMatrix();
        float[] values = new float[10];
        m.getValues(values);
        
        //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数  
        float sx = values[0];
        float sy = values[4];
        Log.d("lxy", "scale_X = " + sx + ", scale_Y = " + sy);
        
        //计算Image在屏幕上实际绘制的宽高
        int cw = (int)(dw * sx);  
        int ch = (int)(dh * sy);
        
        Point size = new Point(cw, ch);
        return size;
	}
}
