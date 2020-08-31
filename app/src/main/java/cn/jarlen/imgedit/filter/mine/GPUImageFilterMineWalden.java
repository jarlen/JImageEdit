package cn.jarlen.imgedit.filter.mine;

public class GPUImageFilterMineWalden extends GPUImageFilterMine
{

	/**
	 * walden
	 * 
	 * waldenMap.png vignetteMap.png
	 * 
	 */
	private static final String WALDEN_FRAGMENT_SHADER = ""
			+ "precision lowp float;"
			+ "\n"
			+ "varying highp vec2 textureCoordinate;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture;"
			+ "uniform sampler2D inputImageTexture2; //map"
			+ "\n"
			+ "uniform sampler2D inputImageTexture3; //vigMap"
			+ "\n"
			+ "void main()"
			+ "\n"
			+ "{"
			+ "\n"
			+ "vec3 texel = texture2D(inputImageTexture, textureCoordinate).rgb;"
			+ "\n" + "texel = vec3(" + "\n"
			+ "texture2D(inputImageTexture2, vec2(texel.r, .16666)).r," + "\n"
			+ "texture2D(inputImageTexture2, vec2(texel.g, .5)).g," + "\n"
			+ "texture2D(inputImageTexture2, vec2(texel.b, .83333)).b);" + "\n"
			+ "vec2 tc = (2.0 * textureCoordinate) - 1.0;" + "\n"
			+ "float d = dot(tc, tc);" + "\n"
			+ "vec2 lookup = vec2(d, texel.r);" + "\n"
			+ "texel.r = texture2D(inputImageTexture3, lookup).r;" + "\n"
			+ "lookup.y = texel.g;" + "\n"
			+ "texel.g = texture2D(inputImageTexture3, lookup).g;" + "\n"
			+ "lookup.y = texel.b;" + "\n"
			+ "texel.b	= texture2D(inputImageTexture3, lookup).b;" + "\n"
			+ "gl_FragColor = vec4(texel, 1.0);" + "\n" + "}";

	public GPUImageFilterMineWalden()
	{
		super(WALDEN_FRAGMENT_SHADER);
	}

}
