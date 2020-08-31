package cn.jarlen.imgedit.filter.mine;

/**
 * Valencia
 * 
 * valenciaMap.png valenciaGradientMap.png
 * 
 * 
 * 甘醇
 * 
 * songmap.png song2map.png
 * 
 * 
 * @author jarlen
 * 
 */
public class GPUImageFilterMineValencia extends GPUImageFilterMine
{
	private static final String VALENCIA_FRAGMENT_SHADER = ""
			+ "precision lowp float;"
			+ "\n"
			+ "varying highp vec2 textureCoordinate;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture2;//map"
			+ "\n"
			+ "uniform sampler2D inputImageTexture3; //gradMap"
			+ "\n"
			+ "mat3 saturateMatrix = mat3(1.1402,-0.0598,-0.061,-0.1174,1.0826,-0.1186,-0.0228,-0.0228,1.1772);"
			+ "\n"
			+ "vec3 lumaCoeffs = vec3(.3, .59, .11);"
			+ "\n"
			+ "void main()"
			+ "\n"
			+ "{"
			+ "\n"
			+ "vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;"
			+ "\n" + "texel = vec3(" + "\n"
			+ "texture2D(inputImageTexture2, vec2(texel.r, .1666666)).r,"
			+ "\n" + "texture2D(inputImageTexture2, vec2(texel.g, .5)).g,"
			+ "\n" + "texture2D(inputImageTexture2, vec2(texel.b, .8333333)).b"
			+ "\n" + ");" + "\n" + "texel = saturateMatrix * texel;" + "\n"
			+ "float luma = dot(lumaCoeffs, texel);" + "\n" + "texel = vec3("
			+ "\n" + "texture2D(inputImageTexture3, vec2(luma, texel.r)).r,"
			+ "\n" + "texture2D(inputImageTexture3, vec2(luma, texel.g)).g,"
			+ "\n" + "texture2D(inputImageTexture3, vec2(luma, texel.b)).b"
			+ "\n" + ");" + "\n" + "gl_FragColor = vec4(texel, 1.0);" + "\n"
			+ "}";

	public GPUImageFilterMineValencia()
	{
		super(VALENCIA_FRAGMENT_SHADER);
	}
}
