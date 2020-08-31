package cn.jarlen.imgedit.filter.base;

public class YUVDecoder
{
	static
	{
		System.loadLibrary("YUVDecoder");
	}

	public static native void YUVtoRBGA(byte[] yuv, int width, int height,
			int[] out);

	public static native void YUVtoARBG(byte[] yuv, int width, int height,
			int[] out);

}
