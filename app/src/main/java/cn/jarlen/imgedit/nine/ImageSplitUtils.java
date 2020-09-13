package cn.jarlen.imgedit.nine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

import cn.jarlen.imgedit.util.FileUtils;

import static cn.jarlen.imgedit.util.FileUtils.DCIMCamera_PATH;

public class ImageSplitUtils {
    /**
     * 裁剪图片为指定块数
     *
     * @param imageFilePath 要裁剪的图片路径
     * @param piece         切成piece*piece块
     */
    public static List<ImagePiece> splitImage(String imageFilePath, int piece, String suffix) {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);

        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
        int size = 0;// 图片尺寸
        int offsetX = 0, offsetY = 0;// 横纵坐标偏移量（针对非1：1图片）
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        size = Math.min(width, height);
        if (width > height) {
            offsetX = (width - height) / 2;
        } else if (height > width) {
            offsetY = (height - width) / 2;
        }

        int pieceSize = size / piece;

        for (int row = 0; row < piece; row++) {
            for (int col = 0; col < piece; col++) {
                ImagePiece imagePiece = new ImagePiece();

                int x = col * pieceSize;
                int y = row * pieceSize;
                int index = col + row * piece;

                Bitmap bit = Bitmap.createBitmap(bitmap, x + offsetX, y + offsetY,
                        pieceSize, pieceSize);
                String name = suffix + System.currentTimeMillis() + "_" + index + ".jpg";
                String imgPath = FileUtils.saveFile(bit, DCIMCamera_PATH, name);
                imagePiece.setImgPath(imgPath);
                imagePiece.setIndex(index);// 设置裁剪后的图片索引
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;
    }

}
