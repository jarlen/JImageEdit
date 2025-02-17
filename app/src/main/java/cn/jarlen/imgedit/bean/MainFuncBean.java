package cn.jarlen.imgedit.bean;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MainFuncBean {

    public static final int MAIN_FUNC_TYPE_CROP = 1;
    public static final int MAIN_FUNC_TYPE_ROTATE = 2;
    public static final int MAIN_FUNC_TYPE_PAINT = 3;
    public static final int MAIN_FUNC_TYPE_MOSAIC = 4;
    public static final int MAIN_FUNC_TYPE_FRAME = 5;
    public static final int MAIN_FUNC_TYPE_ARROW = 6;
    public static final int MAIN_FUNC_TYPE_RECT = 7;
    public static final int MAIN_FUNC_TYPE_NINE = 8;
    public static final int MAIN_FUNC_TYPE_COMPRESS = 9;
    public static final int MAIN_FUNC_TYPE_STICKER = 10;
    public static final int MAIN_FUNC_TYPE_ENHANCE = 11;
    public static final int MAIN_FUNC_TYPE_WARP = 12;
    public static final int MAIN_FUNC_TYPE_FILTER = 13;
    public static final int MAIN_FUNC_TYPE_ADD_TEXT = 14;
    public static final int MAIN_FUNC_TYPE_PUZZLE = 15;
    public static final int MAIN_FUNC_TYPE_BG_IMG = 16;
    public static final int MAIN_FUNC_TYPE_BLUR_TOUCH = 17;
    public static final int MAIN_FUNC_TYPE_SHAPE_CUT = 18;

    private String funcName;
    private int funcType;
    private int iconRes;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getFuncType() {
        return funcType;
    }

    public void setFuncType(int funcType) {
        this.funcType = funcType;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @IntDef({MAIN_FUNC_TYPE_CROP,
            MAIN_FUNC_TYPE_ROTATE,
    })

    @Retention(RetentionPolicy.SOURCE)

    public @interface type {
    }

}
