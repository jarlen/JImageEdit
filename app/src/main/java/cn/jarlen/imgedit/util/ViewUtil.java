package cn.jarlen.imgedit.util;

import android.content.Context;

public class ViewUtil {

    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue / scale);
    }


}
