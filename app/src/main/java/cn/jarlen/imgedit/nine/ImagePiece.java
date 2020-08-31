package cn.jarlen.imgedit.nine;

import java.io.Serializable;

public class ImagePiece implements Serializable, Comparable {
    private int index;
    private String imgPath;

    public ImagePiece() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ImagePiece(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof ImagePiece) {
            return ((ImagePiece) o).index - index;
        }
        return 0;
    }
}