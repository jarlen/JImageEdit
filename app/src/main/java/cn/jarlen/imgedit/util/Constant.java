package cn.jarlen.imgedit.util;

import android.os.Environment;

public class Constant {

    public static final String WM_Local = "WaterMarks/";

    public static final String WM_NET_Local = "/constellation/" + WM_Local;

    public static final String WM_NET_Temp = "/constellation/" + "tempZip/";

    public static final String CONTENT_URL = "content://downloads/my_downloads";

    public static final String Camera_Customer_Path = Environment
            .getExternalStorageDirectory() + "/星座胶卷/";
}
