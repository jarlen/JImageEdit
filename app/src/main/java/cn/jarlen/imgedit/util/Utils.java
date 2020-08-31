package cn.jarlen.imgedit.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.jarlen.imgedit.ImageEditApplication;
import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.arrow.ArrowRectActivity;
import cn.jarlen.imgedit.bean.MainFuncBean;
import cn.jarlen.imgedit.compress.CompressActivity;
import cn.jarlen.imgedit.crop_rotate.CropRotateActivity;
import cn.jarlen.imgedit.draw.DrawActivity;
import cn.jarlen.imgedit.frame.PhotoFrameActivity;
import cn.jarlen.imgedit.mosaic.MosaicActivity;
import cn.jarlen.imgedit.nine.NineActivity;
import cn.jarlen.imgedit.sticker.StickerActivity;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Utils {


    public static List<MainFuncBean> buildMainData(Context context) {
        List<MainFuncBean> funcBeanList = new ArrayList<>();
        int[] mainFuncTypeArray = context.getResources().getIntArray(R.array.main_func_type);
        for (int index = 0; index < mainFuncTypeArray.length; index++) {
            MainFuncBean funcBean = new MainFuncBean();
            int type = mainFuncTypeArray[index];
            funcBean.setFuncType(type);
            funcBean.setFuncName(convertFuncName(type));
            funcBeanList.add(funcBean);
        }
        return funcBeanList;
    }

    private static String convertFuncName(int funcType) {
        String funcName;
        switch (funcType) {
            case MainFuncBean.MAIN_FUNC_TYPE_CROP:
                funcName = "剪切";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ROTATE:
                funcName = "旋转";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_PAINT:
                funcName = "涂鸦";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_MOSAIC:
                funcName = "马赛克";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_FRAME:
                funcName = "相框";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ARROW:
                funcName = "箭头";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_RECT:
                funcName = "框选";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_NINE:
                funcName = "九宫格";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_COMPRESS:
                funcName = "压缩";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_STICKER:
                funcName = "贴纸";
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ENHANCE:
                funcName = "增强";
                break;
            default:
                funcName = "测试";
                break;
        }
        return funcName;
    }

    public static void navigate2Func(Activity activity, MainFuncBean funcBean, String image) {
        Intent funcIntent = null;

        switch (funcBean.getFuncType()) {
            case MainFuncBean.MAIN_FUNC_TYPE_CROP:
                funcIntent = new Intent(activity, CropRotateActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_SHOW_ONLY_ROTATE, false);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ROTATE:
                funcIntent = new Intent(activity, CropRotateActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_SHOW_ONLY_ROTATE, true);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;

            case MainFuncBean.MAIN_FUNC_TYPE_PAINT:
                funcIntent = new Intent(activity, DrawActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;

            case MainFuncBean.MAIN_FUNC_TYPE_MOSAIC:
                funcIntent = new Intent(activity, MosaicActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;

            case MainFuncBean.MAIN_FUNC_TYPE_FRAME:
                funcIntent = new Intent(activity, PhotoFrameActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ARROW:
                funcIntent = new Intent(activity, ArrowRectActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_RECT:
                funcIntent = new Intent(activity, ArrowRectActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(ArrowRectActivity.RECTANGLE_ABLE, true);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_NINE:
                funcIntent = new Intent(activity, NineActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_COMPRESS:
                funcIntent = new Intent(activity, CompressActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_STICKER:
                funcIntent = new Intent(activity, StickerActivity.class);
                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            case MainFuncBean.MAIN_FUNC_TYPE_ENHANCE:
//                funcIntent = new Intent(activity, EnhanceActivity.class);
//                funcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                funcIntent.putExtra(CropRotateActivity.IMAGE_PATH, image);
                break;
            default:
                break;
        }
        if (funcIntent == null) {
            Toast.makeText(activity, "正在开发中", Toast.LENGTH_SHORT).show();
            return;
        }
        activity.startActivity(funcIntent);
    }

    public static void saveImage2Photo(Bitmap bitmap, final String targetImageName) {
        Observable.just(bitmap)
                .map(new Function<Bitmap, Boolean>() {
                    @Override
                    public Boolean apply(Bitmap bitmap) throws Exception {
                        return FileUtils.saveBitmapToCamera(ImageEditApplication.getApplication(), bitmap, targetImageName);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean s) {
                        Toast.makeText(ImageEditApplication.getApplication(), "已保存至相册", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static Observable<Boolean> saveImg2(Bitmap bitmap, final String targetImageName) {
        return Observable.just(bitmap)
                .map(new Function<Bitmap, Boolean>() {
                    @Override
                    public Boolean apply(Bitmap bitmap) throws Exception {
                        return FileUtils.saveBitmapToCamera(ImageEditApplication.getApplication(), bitmap, targetImageName);
                    }
                });
    }

}
