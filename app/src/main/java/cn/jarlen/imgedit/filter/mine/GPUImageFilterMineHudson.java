package cn.jarlen.imgedit.filter.mine;

/**
 * Hudson
 * 
 * hudsonBackground.png overlayMap.png hudsonMap.png
 * 
 * @author jarlen
 * 
 */
public class GPUImageFilterMineHudson extends GPUImageFilterMine
{

	private static final String HUDSON_FRAGMENT_SHADER = ""
			+ "precision lowp float;"
			+ "\n"
			+ "varying highp vec2 textureCoordinate;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture2; //blowout;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture3; //overlay;"
			+ "\n"
			+ "uniform sampler2D inputImageTexture4; //map"
			+ "\n"
			+ "void main()"
			+ "\n"
			+ "{"
			+ "\n"
			+ "vec4 texel = texture2D(inputImageTexture, textureCoordinate);"
			+ "\n"
			+ "vec3 bbTexel = texture2D(inputImageTexture2, textureCoordinate).rgb;"
			+ "\n"
			+ "texel.r = texture2D(inputImageTexture3, vec2(bbTexel.r, texel.r)).r;"
			+ "\n"
			+ "texel.g = texture2D(inputImageTexture3, vec2(bbTexel.g, texel.g)).g;"
			+ "\n"
			+ "texel.b = texture2D(inputImageTexture3, vec2(bbTexel.b, texel.b)).b;"
			+ "\n"
			+ "vec4 mapped;"
			+ "\n"
			+ "mapped.r = texture2D(inputImageTexture4, vec2(texel.r, .16666)).r;"
			+ "\n"
			+ "mapped.g = texture2D(inputImageTexture4, vec2(texel.g, .5)).g;"
			+ "\n"
			+ "mapped.b = texture2D(inputImageTexture4, vec2(texel.b, .83333)).b;"
			+ "\n" + "mapped.a = 1.0;" + "\n" + "gl_FragColor = mapped;" + "\n"
			+ "}";

	public GPUImageFilterMineHudson()
	{
		super(HUDSON_FRAGMENT_SHADER);
	}
}
