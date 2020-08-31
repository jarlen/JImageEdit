package cn.jarlen.imgedit.filter.mine;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.OpenGlUtils;
import jp.co.cyberagent.android.gpuimage.util.Rotation;
import jp.co.cyberagent.android.gpuimage.util.TextureRotationUtil;


public class GPUImageFilterMine extends GPUImageFilter {

    // public int mGLAttribPosition, mGLAttribTextureCoordinate;
    public int mFilterInputTextureUniform2, mFilterInputTextureUniform3,
            mFilterInputTextureUniform4, mFilterInputTextureUniform5,
            mFilterInputTextureUniform6;

    // ByteBuffer filterFramebuffer;

    public int mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
    public int mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
    public int mFilterSourceTexture4 = OpenGlUtils.NO_TEXTURE;
    public int mFilterSourceTexture5 = OpenGlUtils.NO_TEXTURE;
    public int mFilterSourceTexture6 = OpenGlUtils.NO_TEXTURE;

    public Bitmap bitmapTwo, bitmapThree, bitmapFour, bitmapFive, bitmapSix;

    private ByteBuffer mTexture2CoordinatesBuffer;
    public int mGLAttribTextureCoordinate2;

    public GPUImageFilterMine(String fragmentShader) {
        this(NO_FILTER_VERTEX_SHADER, fragmentShader);
    }

    public GPUImageFilterMine(String vertexShader, String fragmentShader) {
        super(vertexShader, fragmentShader);
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onInit() {
        super.onInit();

        mGLAttribTextureCoordinate2 = GLES20.glGetAttribLocation(getProgram(),
                "inputTextureCoordinate2");
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate2);

        // This does assume a name of "inputImageTexture2" for second input
        // texture in the fragment shader
        mFilterInputTextureUniform2 = GLES20.glGetUniformLocation(getProgram(),
                "inputImageTexture2");
        // This does assume a name of "inputImageTexture3" for second input
        // texture in the fragment shader
        mFilterInputTextureUniform3 = GLES20.glGetUniformLocation(getProgram(),
                "inputImageTexture3");
        // This does assume a name of "inputImageTexture4" for second input
        // texture in the fragment shader
        mFilterInputTextureUniform4 = GLES20.glGetUniformLocation(getProgram(),
                "inputImageTexture4");
        // This does assume a name of "inputImageTexture5" for second input
        // texture in the fragment shader
        mFilterInputTextureUniform5 = GLES20.glGetUniformLocation(getProgram(),
                "inputImageTexture5");
        // This does assume a name of "inputImageTexture6" for second input
        // texture in the fragment shader
        mFilterInputTextureUniform6 = GLES20.glGetUniformLocation(getProgram(),
                "inputImageTexture6");
    }

    @Override
    protected void onDrawArraysPre() {
        GLES20.glEnableVertexAttribArray(mGLAttribTextureCoordinate2);

        if (mFilterSourceTexture2 != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture2);
            GLES20.glUniform1i(mFilterInputTextureUniform2, 3);
        }

        if (mFilterSourceTexture3 != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture3);
            GLES20.glUniform1i(mFilterInputTextureUniform3, 4);
        }

        if (mFilterSourceTexture4 != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture4);
            GLES20.glUniform1i(mFilterInputTextureUniform4, 5);
        }

        if (mFilterSourceTexture5 != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE6);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture5);
            GLES20.glUniform1i(mFilterInputTextureUniform5, 6);
        }

        if (mFilterSourceTexture6 != 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE7);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFilterSourceTexture6);
            GLES20.glUniform1i(mFilterInputTextureUniform6, 7);
        }

        mTexture2CoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(mGLAttribTextureCoordinate2, 2,
                GLES20.GL_FLOAT, false, 0, mTexture2CoordinatesBuffer);

    }

    public void setBitmap(final Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            return;
        }

        runOnDraw(new Runnable() {
            @Override
            public void run() {

                if (mFilterSourceTexture2 == OpenGlUtils.NO_TEXTURE) {
                    bitmapTwo = bitmap;
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture2 = OpenGlUtils.loadTexture(bitmap,
                            OpenGlUtils.NO_TEXTURE, false);
                } else if (mFilterSourceTexture3 == OpenGlUtils.NO_TEXTURE) {
                    bitmapThree = bitmap;
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture3 = OpenGlUtils.loadTexture(bitmap,
                            OpenGlUtils.NO_TEXTURE, false);
                } else if (mFilterSourceTexture4 == OpenGlUtils.NO_TEXTURE) {
                    bitmapFour = bitmap;
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture4 = OpenGlUtils.loadTexture(bitmap,
                            OpenGlUtils.NO_TEXTURE, false);
                } else if (mFilterSourceTexture5 == OpenGlUtils.NO_TEXTURE) {
                    bitmapFive = bitmap;
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture5 = OpenGlUtils.loadTexture(bitmap,
                            OpenGlUtils.NO_TEXTURE, false);
                } else if (mFilterSourceTexture6 == OpenGlUtils.NO_TEXTURE) {
                    bitmapSix = bitmap;
                    GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
                    mFilterSourceTexture6 = OpenGlUtils.loadTexture(bitmap,
                            OpenGlUtils.NO_TEXTURE, false);
                }

            }
        });
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        float[] buffer = TextureRotationUtil.getRotation(rotation,
                flipHorizontal, flipVertical);

        ByteBuffer bBuffer = ByteBuffer.allocateDirect(32).order(
                ByteOrder.nativeOrder());
        FloatBuffer fBuffer = bBuffer.asFloatBuffer();
        fBuffer.put(buffer);
        fBuffer.flip();

        mTexture2CoordinatesBuffer = bBuffer;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mFilterSourceTexture2 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mFilterSourceTexture2}, 0);
            mFilterSourceTexture2 = OpenGlUtils.NO_TEXTURE;
        }

        if (mFilterSourceTexture3 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mFilterSourceTexture3}, 0);
            mFilterSourceTexture3 = OpenGlUtils.NO_TEXTURE;
        }

        if (mFilterSourceTexture4 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mFilterSourceTexture4}, 0);
            mFilterSourceTexture4 = OpenGlUtils.NO_TEXTURE;
        }

        if (mFilterSourceTexture5 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mFilterSourceTexture5}, 0);
            mFilterSourceTexture5 = OpenGlUtils.NO_TEXTURE;
        }
        if (mFilterSourceTexture5 != OpenGlUtils.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, new int[]{mFilterSourceTexture5}, 0);
            mFilterSourceTexture5 = OpenGlUtils.NO_TEXTURE;
        }

    }

    @Override
    public void onInitialized() {
        // TODO Auto-generated method stub
        super.onInitialized();
    }
}
