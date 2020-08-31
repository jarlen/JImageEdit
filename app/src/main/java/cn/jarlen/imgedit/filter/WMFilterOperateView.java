package cn.jarlen.imgedit.filter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cn.jarlen.imgedit.filter.edit.ImageObject;
import cn.jarlen.imgedit.filter.edit.OperateUtils;
import cn.jarlen.imgedit.filter.edit.OperateView;
import cn.jarlen.imgedit.filter.mine.GPUImageFilterMineWhiteBalance;
import cn.jarlen.imgedit.util.FileUtils;
import cn.jarlen.imgedit.util.ViewUtil;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
//import jp.co.cyberagent.android.gpuimage.filter.GPUImageVignetteFilter;


public class WMFilterOperateView extends RelativeLayout {

    private Activity mContext;

    private GPUImageView mFilterOperateView;
    private OperateView mWMOperateView;
    private ImageView mOperateCompareView;

    private OperateUtils operateUtils;
    private Bitmap mBitmapSrc = null;

    private LinearLayout wmOperateContainer;
    private RelativeLayout filterOperateContainer;

    private boolean isInvalidating = false;

    private int wmPadding = 0;

    public WMFilterOperateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WMFilterOperateView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.mContext = (Activity) context;
        operateUtils = new OperateUtils(mContext);
        this.wmPadding = ViewUtil.dipTopx(mContext, 5);
        initView();
        initOperateFilter();
    }

    private void initView() {
        isInvalidating = true;
        /** 滤镜层 **/
        filterOperateContainer = new RelativeLayout(mContext);
        filterOperateContainer.setId(2);
        filterOperateContainer.setBackgroundColor(0x00000000);// 滤镜层
        filterOperateContainer.setGravity(Gravity.CENTER);
        LayoutParams lp1 = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        filterOperateContainer.setLayoutParams(lp1);
        mFilterOperateView = new GPUImageView(mContext);
        mFilterOperateView.setBackgroundColor(0x00000000);//
        mFilterOperateView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
        LayoutParams lp2 = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        mFilterOperateView.setLayoutParams(lp2);

        /* 滤镜对比层 */
        LayoutParams lp4 = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp4.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        mOperateCompareView = new ImageView(mContext);
        mOperateCompareView.setBackgroundColor(0x00000000);
        mOperateCompareView.setLayoutParams(lp4);

        filterOperateContainer.addView(mFilterOperateView);
        filterOperateContainer.addView(mOperateCompareView);
        addView(filterOperateContainer);

        mOperateCompareView.setVisibility(View.VISIBLE);

        filterOperateContainer.post(new Runnable() {

            @Override
            public void run() {
                if (filterOperateContainer.getWidth() > 0) {
                    /* 水印操作容器 */
                    wmOperateContainer = new LinearLayout(mContext);
                    wmOperateContainer.setBackgroundColor(0x00000000);
                    wmOperateContainer.setGravity(Gravity.CENTER);
                    wmOperateContainer.setOrientation(LinearLayout.VERTICAL);
                    LayoutParams lp3 = new LayoutParams(
                            filterOperateContainer.getWidth(),
                            filterOperateContainer.getHeight());
                    lp3.addRule(RelativeLayout.ALIGN_TOP, 2);
                    lp3.addRule(RelativeLayout.ALIGN_BOTTOM, 2);
                    wmOperateContainer.setLayoutParams(lp3);
                    addView(wmOperateContainer);
                    isInvalidating = false;
                }
            }
        });
    }

    /**
     * 设置操作图片
     *
     * @param src
     */
    public void setImageResource(Bitmap src,
                                 final OperateView.OnTouchWaterMarkListener listener) {
        this.mBitmapSrc = src;

        /* 获取imageView中实际绘制的大小 */
        int width = getWidth();
        int height = getHeight();

        float scaleW = width / (float) src.getWidth();
        float scaleH = height / (float) src.getHeight();
        float scale = Math.min(scaleW, scaleH);

        int newWidth = (int) (src.getWidth() * scale);
        int newHeight = (int) (src.getHeight() * scale);

        mOperateCompareView.setImageBitmap(mBitmapSrc);

        /* 重新计算GLSurfaceView的大小，解决了其绘制黑边的问题 */
        LayoutParams lp2 = new LayoutParams(
                newWidth, newHeight);
        lp2.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        mFilterOperateView.setLayoutParams(lp2);
        mFilterOperateView.setImage(mBitmapSrc);

        if (mWMOperateView == null) {
            /* 水印操作层 */
            mWMOperateView = new OperateView(mContext);
            mWMOperateView.setCanvasLimits(newWidth, newHeight);
            mWMOperateView.setBackgroundColor(0x00000000);

            LayoutParams layoutParams = new LayoutParams(
                    newWidth, newHeight);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
            mWMOperateView.setLayoutParams(layoutParams);
            mWMOperateView.setMultiAdd(false);
            mWMOperateView.setOnTouchWaterMarkListener(listener);

            wmOperateContainer.addView(mWMOperateView);

            /* 添加时间延迟 使的滤镜层家在图片ok后，隐藏对比层 */
            wmOperateContainer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOperateCompareView.setVisibility(View.GONE);
                }
            }, 500);
        }

    }

    public boolean isInvalidating() {
        return isInvalidating;
    }

    private Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /*************************************** 水印相关方法 ********************************/

    public OperateView getWMOperateView() {
        return mWMOperateView;
    }

    public void setOnTouchWMOperateView(OperateView.OnTouchWaterMarkListener listener) {
        if (mWMOperateView != null) {
            mWMOperateView.setOnTouchWaterMarkListener(listener);
        }

    }

    /**
     * 增加单个水印
     *
     * @param bit
     */
    public void addWaterMark(Bitmap bit) {

        if (bit == null) {
            return;
        }

        int wmContainerWidth = mWMOperateView.getWidth() * 2 / 3;
        int wmContainerHeight = mWMOperateView.getHeight() * 2 / 3;

        float scaleW1 = wmContainerWidth / (float) bit.getWidth();
        float scaleH1 = wmContainerHeight / (float) bit.getHeight();

        float bitScale = Math.min(scaleW1, scaleH1);

        ImageObject imgObject = operateUtils.getImageObject(bit,
                mWMOperateView, 5, 0, 0, bitScale);
        mWMOperateView.addSingleItem(imgObject);

    }

    /**
     * 增加两个水印，第一个固定
     *
     * @param bit1
     * @param bit2
     */
    public void addWaterMark(Bitmap bit1, Bitmap bit2) {

        if (bit1 == null || bit2 == null) {
            return;
        }

        int wmContainerWidth = mWMOperateView.getWidth() / 2;
        int wmContainerHeight = mWMOperateView.getHeight() / 2;

        int w = bit1.getWidth();
        int h = bit1.getHeight();

        float scaleW = wmContainerWidth / (float) w;
        float scaleH = wmContainerHeight / (float) h;

        float scaleOne = Math.min(scaleW, scaleH);

        ImageObject imgObject1 = operateUtils.getImageObject(bit1,
                mWMOperateView, 2, wmContainerWidth / 2 + wmPadding,
                wmContainerHeight / 2 + wmPadding, scaleOne);

        wmContainerWidth = mWMOperateView.getWidth() * 3 / 4;
        wmContainerHeight = mWMOperateView.getHeight() * 3 / 4;

        w = bit2.getWidth();
        h = bit2.getHeight();

        scaleW = wmContainerWidth / (float) w;
        scaleH = wmContainerHeight / (float) h;

        scaleOne = Math.min(scaleW, scaleH);

        ImageObject imgObject2 = operateUtils.getImageObject(bit2,
                mWMOperateView, 3, wmContainerWidth / 2 + wmPadding,
                wmContainerHeight / 2 + wmPadding, scaleOne);

        mWMOperateView.addDoubleItem(imgObject1, imgObject2);
    }

    /*********************************** 滤镜相关方法 ******************************/
    public GPUImageView getFilterOperateView() {
        return mFilterOperateView;
    }

    private float brightnessValue = 0.0f;
    private float whiteBalanceValue = 0.5f;
    private float vignetteValue = 1.0f;

    private ArrayList<GPUImageFilter> mFilterList = null;

    private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageFilterMineWhiteBalance whiteBalanceFilter;
//    private GPUImageVignetteFilter vignetteFilter;

    /* 滤镜类型 */
    private int filterTypeInFilterManager = 0;

    public void setFilterTypeInManager(int type) {
        this.filterTypeInFilterManager = type;
    }

    public int getFilterTypeInFilterManager() {
        return filterTypeInFilterManager;
    }

    private void initOperateFilter() {
        ArrayList<GPUImageFilter> listTemp = new ArrayList<GPUImageFilter>();

        if (mFilterList != null && mFilterList.size() > 0) {
            listTemp.addAll(mFilterList);
        }

        brightnessFilter = new GPUImageBrightnessFilter();
        brightnessFilter.setBrightness(brightnessValue);
        listTemp.add(brightnessFilter);

        whiteBalanceFilter = new GPUImageFilterMineWhiteBalance();
        whiteBalanceFilter.setTemperatureProportion(whiteBalanceValue);
        listTemp.add(whiteBalanceFilter);

//        vignetteFilter = new GPUImageVignetteFilter();
//        vignetteFilter.setVignetteStart(vignetteValue);
//        listTemp.add(vignetteFilter);

        GPUImageFilterGroup mGroupFilter = new GPUImageFilterGroup(listTemp);
        mFilterOperateView.setFilter(mGroupFilter);
    }

    public void setCompareViewHide() {
        mOperateCompareView.setVisibility(View.GONE);
    }

    public void setCompareViewVISIBLE() {
        mOperateCompareView.setVisibility(View.VISIBLE);
    }

    /**
     * 渲染滤镜
     */
    public void requestOperateFilter() {
        ArrayList<GPUImageFilter> listTemp = new ArrayList<GPUImageFilter>();

        if (mFilterList != null && mFilterList.size() > 0) {
            listTemp.addAll(mFilterList);
        }

        brightnessFilter.setBrightness(brightnessValue);
        listTemp.add(brightnessFilter);

        whiteBalanceFilter.setTemperatureProportion(whiteBalanceValue);
        listTemp.add(whiteBalanceFilter);

//        vignetteFilter.setVignetteStart(vignetteValue);
//        listTemp.add(vignetteFilter);
        GPUImageFilterGroup mGroupFilter = new GPUImageFilterGroup(listTemp);

        mFilterOperateView.setFilter(mGroupFilter);
    }

    public void adjustWhiteBalanceFilter(float factor) {
        this.whiteBalanceValue = factor;
        whiteBalanceFilter.setTemperatureProportion(factor);
        mFilterOperateView.requestRender();
    }

    public void adjustBrightnessFilter(float factor) {
        this.brightnessValue = factor;
        brightnessFilter.setBrightness(factor);
        mFilterOperateView.requestRender();
    }

//    public void adjustVignetteFilter(float factor) {
//        vignetteFilter.setVignetteStart(factor);
//        mFilterOperateView.requestRender();
//    }

    /**
     * 设置滤镜
     *
     * @param list
     */
    public void setOperateFilter(ArrayList<GPUImageFilter> list) {
        this.mFilterList = list;
        requestOperateFilter();
    }

    public void setBrightnessValue(float brightnessValue) {
        this.brightnessValue = brightnessValue;

    }

    public float getBrightnessValue() {
        return brightnessValue;
    }

    public void setWhiteBalanceValue(float whiteBalanceValue) {
        this.whiteBalanceValue = whiteBalanceValue;
    }

    public float getWhiteBalanceValue() {
        return whiteBalanceValue;
    }

    public void setVignetteValue(float vignetteValue) {
        this.vignetteValue = vignetteValue;
    }

    public float getVignetteValue() {
        return vignetteValue;
    }

    public Bitmap getFilterResult() {

        GPUImage mGPImageTool = new GPUImage(getContext());

        Bitmap filterBitmap = null;
        try {
            ArrayList<GPUImageFilter> listTemp = new ArrayList<GPUImageFilter>();

            GPUImageFilterManager mFilterManager = new GPUImageFilterManager(
                    getContext());
            ArrayList<GPUImageFilter> mlist = mFilterManager
                    .GPUCreateGPUImageFilter(getFilterTypeInFilterManager());

            if (mlist != null && mlist.size() > 0) {
                listTemp.addAll(mlist);
            }

            GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
            brightnessFilter.setBrightness(brightnessValue);
            listTemp.add(brightnessFilter);

            GPUImageFilterMineWhiteBalance whiteBalanceFilter = new GPUImageFilterMineWhiteBalance();
            whiteBalanceFilter.setTemperatureProportion(whiteBalanceValue);
            listTemp.add(whiteBalanceFilter);

//            GPUImageVignetteFilter vignetteFilter = new GPUImageVignetteFilter();
//            vignetteFilter.setVignetteStart(vignetteValue);
//            listTemp.add(vignetteFilter);
            GPUImageFilterGroup mGroupFilter = new GPUImageFilterGroup(
                    listTemp);

            mGPImageTool.setFilter(mGroupFilter);
            filterBitmap = mGPImageTool.getBitmapWithFilterApplied(mBitmapSrc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filterBitmap;
    }

    /**
     * 保存图片线程
     *
     * @author jarlen
     */
    public class WMFilterSaveAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context context;
        private final int mWidth;
        private final int mHeight;
        private OnPictureSavedListener mListener;
        private Handler mHandler;

        private GPUImage mGPImageTool;
        private Bitmap mBitmap = null;

        public WMFilterSaveAsyncTask(Context context,
                                     final OnPictureSavedListener listener) {
            this(context, 0, 0, listener);
        }

        public WMFilterSaveAsyncTask(Context context, int width, int height,
                                     final OnPictureSavedListener listener) {
            this.context = context;
            this.mWidth = width;
            this.mHeight = height;
            this.mListener = listener;
            this.mHandler = new Handler();
            this.mGPImageTool = new GPUImage(context);
            mBitmap = Bitmap.createBitmap(mBitmapSrc.getWidth(),
                    mBitmapSrc.getHeight(), Config.RGB_565);
            Canvas canvas = new Canvas(mBitmap);
            canvas.drawBitmap(mBitmapSrc, 0, 0, null);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                    Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            canvas.save();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Bitmap filterBitmap = null;
            try {
                ArrayList<GPUImageFilter> listTemp = new ArrayList<GPUImageFilter>();

                GPUImageFilterManager mFilterManaager = new GPUImageFilterManager(
                        context);
                ArrayList<GPUImageFilter> mlist = mFilterManaager
                        .GPUCreateGPUImageFilter(getFilterTypeInFilterManager());

                if (mlist != null && mlist.size() > 0) {
                    listTemp.addAll(mlist);
                }

                GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
                brightnessFilter.setBrightness(brightnessValue);
                listTemp.add(brightnessFilter);

                GPUImageFilterMineWhiteBalance whiteBalanceFilter = new GPUImageFilterMineWhiteBalance();
                whiteBalanceFilter.setTemperatureProportion(whiteBalanceValue);
                listTemp.add(whiteBalanceFilter);

//                GPUImageVignetteFilter vignetteFilter = new GPUImageVignetteFilter();
//                vignetteFilter.setVignetteStart(vignetteValue);
//                listTemp.add(vignetteFilter);
                GPUImageFilterGroup mGroupFilter = new GPUImageFilterGroup(
                        listTemp);

                mGPImageTool.setFilter(mGroupFilter);
                filterBitmap = mGPImageTool.getBitmapWithFilterApplied(mBitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }

            int width = filterBitmap.getWidth();
            int height = filterBitmap.getHeight();

            Log.i("===", "filterBitmap  width = " + width + "  height = "
                    + height);

            Bitmap resultBitmap = Bitmap.createBitmap(width, height,
                    Config.RGB_565);
            Canvas canvas = new Canvas(resultBitmap);
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0,
                    Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);// 去掉边缘锯齿
            paint.setStrokeWidth(2);// 设置线宽

            int filterSc = canvas.save();
            if (filterBitmap != null) {
                canvas.drawBitmap(filterBitmap, 0, 0, paint);
            }

            // 画完回收filterBitmap

            if (filterBitmap != null) {
                filterBitmap.recycle();
                filterBitmap = null;
            }

            ArrayList<ImageObject> imageList = null;

            if (mWMOperateView != null) {
                imageList = mWMOperateView.getImageObjectList();
            }

            if (imageList != null) {
                Log.i("===", "  imageList = " + imageList.size());

                float mWMScaleW = width / (float) mWMOperateView.getWidth();
                float mWMScaleH = height / (float) mWMOperateView.getHeight();

                Point newImagePoint = null;
                for (ImageObject image : imageList) {
                    if (image != null) {
                        Point imagePoint = image.getPosition();

                        if (newImagePoint == null) {
                            newImagePoint = new Point();
                        }

                        newImagePoint.set((int) (imagePoint.x * mWMScaleW),
                                (int) (imagePoint.y * mWMScaleH));

                        float newImageScaleW = image.getScale() * mWMScaleW;
                        float newImageScaleH = image.getScale() * mWMScaleH;

                        float newImageRotation = image.getRotation();

                        int sc = canvas.save();
                        canvas.translate(newImagePoint.x, newImagePoint.y);
                        canvas.scale(newImageScaleW, newImageScaleH);

                        canvas.rotate(newImageRotation);
                        canvas.drawBitmap(image.getSrcBm(), -image.getSrcBm()
                                        .getWidth() / 2,
                                -image.getSrcBm().getHeight() / 2, paint);

                        canvas.restoreToCount(sc);

                    }
                }
            }

            canvas.restoreToCount(filterSc);

            saveImage(resultBitmap);

            return null;
        }

        private void saveImage(Bitmap image) {

            final String resultPath = FileUtils
                    .writeImageToCustomerCamera(image);

            if (image != null) {
                image.recycle();
                image = null;
            }

            if (mHandler != null) {
                mHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (mListener != null)
                            mListener.onPictureSaved(resultPath);

                        /* 释放资源 */
                        recycle();
                    }
                });
            }
        }

        private void recycle() {
            mListener = null;
            mHandler = null;

            if (mGPImageTool != null) {
                //mGPImageTool.deleteImage();
                mGPImageTool = null;
            }

            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }

        }

    }

    /**
     * 生成图片
     *
     * @param listener
     */
    public void WMFilterSaveToPictures(OnPictureSavedListener listener) {
        if (mBitmapSrc != null) {
            new WMFilterSaveAsyncTask(mContext, listener).execute();
        }
    }

    public interface OnPictureSavedListener {
        void onPictureSaved(String result);
    }

    public void recycle() {
        /* 滤镜 */
        if (mFilterOperateView != null) {
            mFilterOperateView = null;
        }

        mOperateCompareView = null;

        if (mWMOperateView != null) {
            mWMOperateView.recycle();
            mWMOperateView = null;
        }
        operateUtils = null;
        mBitmapSrc = null;
        wmOperateContainer = null;
        filterOperateContainer = null;

        if (mFilterList != null) {
            mFilterList.clear();
            mFilterList = null;
        }

        if (brightnessFilter != null) {
            brightnessFilter = null;
        }

        if (whiteBalanceFilter != null) {
            whiteBalanceFilter = null;
        }

//        if (vignetteFilter != null) {
//            vignetteFilter = null;
//        }
    }

}