package cn.jarlen.imgedit.filter.single;

import android.opengl.GLES20;

/**
 * 没效果
 * 
 * @author jarlen
 * 
 */

public class GPUImageGaussianBlurPositionFilter
		extends
			GPUImageTwoPassTextureSamplingFilter
{

	private static final String VERTEX_SHADER = ""
			+ "attribute vec4 position;"
			+ "\n"
			+ "attribute vec4 inputTextureCoordinate;"
			+ "\n"
			+ "const int GAUSSIAN_SAMPLES = 9;"
			+ "\n"
			+ "uniform float texelWidthOffset;"
			+ "\n"
			+ "uniform float texelHeightOffset;"
			+ "\n"
			+ "varying vec2 textureCoordinate;"
			+ "\n"
			+ "varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];"
			+ "\n"
			+ "void main()"
			+ "\n"
			+ "{"
			+ "\n"
			+ "gl_Position = position;"
			+ "\n"
			+ "textureCoordinate = inputTextureCoordinate.xy;"
			+ "\n"
			+ "int multiplier = 0;"
			+ "\n"
			+ "vec2 blurStep;"
			+ "\n"
			+ "vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);"
			+ "\n" + "for (int i = 0; i < GAUSSIAN_SAMPLES; i++) {" + "\n"
			+ "multiplier = (i - ((GAUSSIAN_SAMPLES - 1) / 2));" + "\n"
			+ "blurStep = float(multiplier) * singleStepOffset;" + "\n"
			+ "blurCoordinates[i] = inputTextureCoordinate.xy + blurStep;"
			+ "\n" + "}" + "\n" + "}";

	private static final String FRAGMENT_SHADER = ""
			+ "uniform sampler2D inputImageTexture;"
			+ "\n"
			+ "const int GAUSSIAN_SAMPLES = 9;"
			+ "\n"
			+ "varying vec2 textureCoordinate;"
			+ "\n"
			+ "varying vec2 blurCoordinates[GAUSSIAN_SAMPLES];"
			+ "\n"
			+ "uniform float aspectRatio;"
			+ "\n"
			+ "uniform vec2 blurCenter;"
			+ "\n"
			+ "uniform float blurRadius;"
			+ "\n"
			+ "void main()"
			+ "\n"
			+ "{"
			+ "\n"
			+ "vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));"
			+ "\n"
			+ "float dist = distance(blurCenter, textureCoordinateToUse);"
			+ "\n" + "if (dist < blurRadius)" + "\n" + "{" + "\n"
			+ "vec4 sum = vec4(0.0);" + "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[0]) * 0.05;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[1]) * 0.09;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[2]) * 0.12;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[3]) * 0.15;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[4]) * 0.18;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[5]) * 0.15;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[6]) * 0.12;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[7]) * 0.09;"
			+ "\n"
			+ "sum += texture2D(inputImageTexture, blurCoordinates[8]) * 0.05;"
			+ "\n" + "gl_FragColor = sum;" + "\n" + "}else" + "\n" + "{" + "\n"
			+ "gl_FragColor = texture2D(inputImageTexture, textureCoordinate);"
			+ "\n" + "}" + "}";

	/**
	 * 范围从0.0以上，默认是1.0;
	 */
	private float mBlurSize = 1f;

	/**
	 * 模糊中心，默认(0.5,0.5)
	 */
	private float[] mBlurCenter;

	/**
	 * 模糊半径 默认1.0
	 */
	private float mBlurRadius;

	private float aspectRatio = 1;

	private int blurCenterUniform, blurRadiusUniform, aspectRatioUniform;

	public GPUImageGaussianBlurPositionFilter()
	{
		super(VERTEX_SHADER, FRAGMENT_SHADER, VERTEX_SHADER, FRAGMENT_SHADER);

		this.mBlurSize = 1.0f;
		this.mBlurRadius = 50.0f;
		this.mBlurCenter = new float[]{0.5f, 0.5f};

	}

	@Override
	public void onInit()
	{
		super.onInit();

		blurCenterUniform = GLES20.glGetUniformLocation(getProgram(),
				"blurCenter");

		blurRadiusUniform = GLES20.glGetUniformLocation(getProgram(),
				"blurRadius");

		aspectRatioUniform = GLES20.glGetUniformLocation(getProgram(),
				"aspectRatio");
	}

	public void setBlurSize(float blurSize)
	{
		this.mBlurSize = blurSize;

		runOnDraw(new Runnable()
		{

			@Override
			public void run()
			{
				initTexelOffsets();
			}
		});
	}

	public void setBlurCenter(float[] blurCenter)
	{
		this.mBlurCenter = blurCenter;
		runOnDraw(new Runnable()
		{

			@Override
			public void run()
			{
				initTexelOffsets();
			}
		});
	}

	public void setmBlurRadius(float mBlurRadius)
	{
		this.mBlurRadius = mBlurRadius;
		runOnDraw(new Runnable()
		{

			@Override
			public void run()
			{
				initTexelOffsets();
			}
		});
	}

	public void setAspectRatio(float aspectRatio)
	{
		this.aspectRatio = aspectRatio;
		runOnDraw(new Runnable()
		{

			@Override
			public void run()
			{
				initTexelOffsets();

			}
		});
	}

	@Override
	public float getVerticalTexelOffsetRatio()
	{
		return mBlurSize;
	}

	@Override
	public float getHorizontalTexelOffsetRatio()
	{
		return mBlurSize;
	}

	public void setBlurRadius(float blurRadius)
	{
		this.mBlurRadius = blurRadius;
	}

	@Override
	protected void initTexelOffsets()
	{
		super.initTexelOffsets();

		if (mBlurCenter != null)
		{
			setFloatVec2(blurCenterUniform, mBlurCenter);
		}
		
		setFloat(blurRadiusUniform, mBlurRadius);

		setFloat(aspectRatioUniform, aspectRatio);
	}

}
