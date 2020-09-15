package cn.jarlen.imgedit;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class ImageEditApplication extends Application {

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        CrashReport.initCrashReport(getApplicationContext(), "9a08c3dd3a", true);
    }

    public static Application getApplication() {
        return application;
    }
}
