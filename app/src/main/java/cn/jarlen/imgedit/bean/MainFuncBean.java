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

    private String funcName;
    private int funcType;

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

    @IntDef({MAIN_FUNC_TYPE_CROP,
            MAIN_FUNC_TYPE_ROTATE,
    })

    @Retention(RetentionPolicy.SOURCE)

    public @interface type {
    }

}
