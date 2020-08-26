//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.jarlen.imgedit.arrow;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.view.Display;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;

public class ImageUtils {
    private static final Config BITMAP_CONFIG;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private static final Uri STORAGE_URI;

    public ImageUtils() {
    }

    public static Uri addImage(ContentResolver cr, String path) {
        File file = new File(path);
        String name = file.getName();
        int i = name.lastIndexOf(".");
        String title = name.substring(0, i);
        String filename = title + name.substring(i);
        int[] degree = new int[1];
        return addImage(cr, title, System.currentTimeMillis(), (Location) null, file.getParent(), filename, degree);
    }

    private static Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, int[] degree) {
        File file = new File(directory, filename);
        long size = file.length();
        ContentValues values = new ContentValues(9);
        values.put("title", title);
        values.put("_display_name", filename);
        values.put("datetaken", Long.valueOf(dateTaken));
        values.put("mime_type", "image/jpeg");
        values.put("orientation", Integer.valueOf(degree[0]));
        values.put("_data", file.getAbsolutePath());
        values.put("_size", Long.valueOf(size));
        if (location != null) {
            values.put("latitude", Double.valueOf(location.getLatitude()));
            values.put("longitude", Double.valueOf(location.getLongitude()));
        }

        return cr.insert(STORAGE_URI, values);
    }

    public static Uri addVideo(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename) {
        String filePath = directory + "/" + filename;

        try {
            File size = new File(directory);
            if (!size.exists()) {
                size.mkdirs();
            }

            new File(directory, filename);
        } catch (Exception var11) {
            var11.printStackTrace();
        }

        long size1 = (new File(directory, filename)).length();
        ContentValues values = new ContentValues(9);
        values.put("title", title);
        values.put("_display_name", filename);
        values.put("datetaken", Long.valueOf(dateTaken));
        values.put("mime_type", "video/3gpp");
        values.put("_data", filePath);
        values.put("_size", Long.valueOf(size1));
        if (location != null) {
            values.put("latitude", Double.valueOf(location.getLatitude()));
            values.put("longitude", Double.valueOf(location.getLongitude()));
        }

        return cr.insert(STORAGE_URI, values);
    }

    public static Bitmap rotate(Context context, Bitmap bitmap, int degree, boolean isRecycle) {
        Matrix m = new Matrix();
        m.setRotate((float) degree, (float) bitmap.getWidth() / 2.0F, (float) bitmap.getHeight() / 2.0F);

        try {
            Bitmap ex = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
            if (isRecycle) {
                bitmap.recycle();
            }

            return ex;
        } catch (OutOfMemoryError var6) {
            var6.printStackTrace();
            return null;
        }
    }

    public static int getBitmapExifRotate(String path) {
        short digree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(path);
        } catch (IOException var4) {
            var4.printStackTrace();
            return 0;
        }

        if (exif != null) {
            int ori = exif.getAttributeInt("Orientation", 0);
            switch (ori) {
                case 3:
                    digree = 180;
                    break;
                case 6:
                    digree = 90;
                    break;
                case 8:
                    digree = 270;
                    break;
                default:
                    digree = 0;
            }
        }

        return digree;
    }

    public static Bitmap rotateBitmapByExif(Bitmap bitmap, String path, boolean isRecycle) {
        int digree = getBitmapExifRotate(path);
        if (digree != 0) {
            bitmap = rotate((Context) null, bitmap, digree, isRecycle);
        }

        return bitmap;
    }

    public static final Bitmap createBitmapFromPath(String path, Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int screenW = display.getWidth();
        int screenH = display.getHeight();
        return createBitmapFromPath(path, context, screenW, screenH);
    }

    public static final Bitmap createBitmapFromPath(String path, Context context, int maxResolutionX, int maxResolutionY) {
        Bitmap bitmap = null;
        Options options = null;
        if (path.endsWith(".3gp")) {
            return ThumbnailUtils.createVideoThumbnail(path, 1);
        } else {
            try {
                options = new Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
                int e = options.outWidth;
                int height = options.outHeight;
                options.inSampleSize = computeBitmapSimple(e * height, maxResolutionX * maxResolutionY);
                options.inPurgeable = true;
                options.inPreferredConfig = Config.RGB_565;
                options.inDither = false;
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(path, options);
                return rotateBitmapByExif(bitmap, path, true);
            } catch (OutOfMemoryError var8) {
                options.inSampleSize *= 2;
                bitmap = BitmapFactory.decodeFile(path, options);
                return rotateBitmapByExif(bitmap, path, true);
            } catch (Exception var9) {
                var9.printStackTrace();
                return null;
            }
        }
    }

    public static final Bitmap createBitmapFromPath(byte[] data, Context context) {
        Bitmap bitmap = null;
        Options options = null;

        try {
            WindowManager e = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = e.getDefaultDisplay();
            int screenW = display.getWidth();
            int screenH = display.getHeight();
            options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);
            int width = options.outWidth;
            int height = options.outHeight;
            int maxResolutionX = screenW * 2;
            int maxResolutionY = screenH * 2;
            options.inSampleSize = computeBitmapSimple(width * height, maxResolutionX * maxResolutionY);
            options.inPurgeable = true;
            options.inPreferredConfig = Config.RGB_565;
            options.inDither = false;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            return bitmap;
        } catch (OutOfMemoryError var12) {
            options.inSampleSize *= 2;
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            return bitmap;
        } catch (Exception var13) {
            var13.printStackTrace();
            return null;
        }
    }

    public static int computeBitmapSimple(int realPixels, int maxPixels) {
        try {
            if (realPixels <= maxPixels) {
                return 1;
            } else {
                int e;
                for (e = 2; realPixels / (e * e) > maxPixels; e *= 2) {
                    ;
                }

                return e;
            }
        } catch (Exception var3) {
            return 1;
        }
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        } else if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            try {
                Bitmap e;
                if (drawable instanceof ColorDrawable) {
                    e = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
                } else {
                    e = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
                }

                Canvas canvas = new Canvas(e);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return e;
            } catch (Exception var3) {
                var3.printStackTrace();
                return null;
            }
        }
    }

    static {
        BITMAP_CONFIG = Config.ARGB_8888;
        STORAGE_URI = Media.EXTERNAL_CONTENT_URI;
    }
}
