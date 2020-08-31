package cn.jarlen.imgedit.filter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.filter.base.GPUImage;
import cn.jarlen.imgedit.filter.base.GPUImageFilter;
import cn.jarlen.imgedit.filter.base.GPUImageFilterGroup;
import cn.jarlen.imgedit.filter.blend.GPUImageLookupFilter;
import cn.jarlen.imgedit.filter.blend.GPUImageOverlayBlendFilter;
import cn.jarlen.imgedit.filter.mine.GPUImageFilterMine;
import cn.jarlen.imgedit.filter.mine.GPUImageFilterMineValencia;
import cn.jarlen.imgedit.filter.mine.GPUImageFilterMineWalden;
import cn.jarlen.imgedit.filter.single.GPUImageToneCurveFilter;

public class GPUImageFilterManager
{

	private Context mContext;
	private Bitmap bitmapSrc;
	GPUImage mGpuImage;

	private ArrayList<GPUImageFilter> mFilterList;

	public GPUImageFilterManager(Context context)
	{
		this.mContext = context;
		mGpuImage = new GPUImage(mContext);
	}

	public void setImageSrc(Bitmap bitmap)
	{
		this.bitmapSrc = bitmap;
	}

	public Bitmap createBitmapWithFilterApplied(int filterType)
	{

		Bitmap result = bitmapSrc;

		ArrayList<GPUImageFilter> filters = GPUCreateGPUImageFilter(filterType);

		if (filters == null || filters.size() < 1)
		{
			this.mFilterList = null;
			return result;

		} else
		{
			this.mFilterList = filters;

			ArrayList<GPUImageFilter> filtersList = new ArrayList<GPUImageFilter>();
			filtersList.addAll(filters);

			GPUImageFilterGroup filterGroup11 = new GPUImageFilterGroup(
					filtersList);

			if (filterGroup11 != null)
			{
				mGpuImage.setFilter(filterGroup11);
				result = mGpuImage.getBitmapWithFilterApplied(bitmapSrc);
			}

			return result;
		}
	}

	public ArrayList<GPUImageFilter> GPUCreateGPUImageFilter(int type)
	{
		ArrayList<GPUImageFilter> filtersList = new ArrayList<GPUImageFilter>();

		switch (type)
		{
			case GPUImageFilterType.CANCER :
				GPUImageFilterMine filterCancer = new GPUImageFilterMineValencia();
				filterCancer.setBitmap(getImageFromAssetsFile("Cancer/"
						+ "valenciamap.png"));
				filterCancer.setBitmap(getImageFromAssetsFile("Cancer/"
						+ "valenciagradientmap.png"));

				filtersList.add(filterCancer);
				break;
			case GPUImageFilterType.LEO :
				GPUImageFilterMine filterLeo = new GPUImageFilterMineWalden();
				filterLeo.setBitmap(getImageFromAssetsFile("Leo/"
						+ "waldenmap.png"));
				filterLeo.setBitmap(getImageFromAssetsFile("Leo/"
						+ "vignettemap.png"));
				filtersList.add(filterLeo);
				break;

			case GPUImageFilterType.LIBRA :

				GPUImageLookupFilter filterLookUp_libra = new GPUImageLookupFilter();
				filterLookUp_libra.setBitmap(getImageFromAssetsFile("Libra/"
						+ "libra.png"));
				filtersList.add(filterLookUp_libra);

				GPUImageOverlayBlendFilter filterOverlay_libra = new GPUImageOverlayBlendFilter();
				filterOverlay_libra.setBitmap(getImageFromAssetsFile("Libra/"
						+ "libra_overlay.jpg"));
				filtersList.add(filterOverlay_libra);

				break;

			case GPUImageFilterType.Aquarius :

				GPUImageLookupFilter filterLookUp_Aquarius = new GPUImageLookupFilter();
				filterLookUp_Aquarius
						.setBitmap(getImageFromAssetsFile("Aquarius/"
								+ "aquarius.png"));
				filtersList.add(filterLookUp_Aquarius);

				GPUImageOverlayBlendFilter filterOverlay_Aquarius = new GPUImageOverlayBlendFilter();
				filterOverlay_Aquarius
						.setBitmap(getImageFromAssetsFile("Aquarius/"
								+ "aquarius_overlay.jpg"));
				filtersList.add(filterOverlay_Aquarius);

				break;
			case GPUImageFilterType.Gemini :

				GPUImageLookupFilter filterLookUp_Gemini = new GPUImageLookupFilter();
				filterLookUp_Gemini.setBitmap(getImageFromAssetsFile("Gemini/"
						+ "gemini.png"));
				filtersList.add(filterLookUp_Gemini);

				break;
			case GPUImageFilterType.Pisces :

				GPUImageLookupFilter filterLookUp_Pisces = new GPUImageLookupFilter();
				filterLookUp_Pisces.setBitmap(getImageFromAssetsFile("Pisces/"
						+ "pisces.png"));
				filtersList.add(filterLookUp_Pisces);

				GPUImageOverlayBlendFilter filterOverlay_Pisces = new GPUImageOverlayBlendFilter();
				filterOverlay_Pisces.setBitmap(getImageFromAssetsFile("Pisces/"
						+ "pisces_overlay.jpg"));
				filtersList.add(filterOverlay_Pisces);

				break;

			case GPUImageFilterType.Scorpio :

				GPUImageLookupFilter filterLookUp_Scorpio = new GPUImageLookupFilter();
				filterLookUp_Scorpio
						.setBitmap(getImageFromAssetsFile("Scorpio/"
								+ "scorpio.png"));
				filtersList.add(filterLookUp_Scorpio);

				GPUImageOverlayBlendFilter filterOverlay_Scorpio = new GPUImageOverlayBlendFilter();
				filterOverlay_Scorpio
						.setBitmap(getImageFromAssetsFile("Scorpio/"
								+ "scorpio_overlay.jpg"));
				filtersList.add(filterOverlay_Scorpio);

				break;

			case GPUImageFilterType.Virgo :

				GPUImageLookupFilter filterLookUp_Virgo = new GPUImageLookupFilter();
				filterLookUp_Virgo.setBitmap(getImageFromAssetsFile("Virgo/"
						+ "virgo.png"));
				filtersList.add(filterLookUp_Virgo);
				break;

			case GPUImageFilterType.Taurus :

				GPUImageToneCurveFilter filterTaurus = new GPUImageToneCurveFilter();
				filterTaurus.setFromCurveFileInputStream(mContext
						.getResources().openRawResource(R.raw.taurus));
				filtersList.add(filterTaurus);

				break;

			case GPUImageFilterType.Sagittarius :

				GPUImageToneCurveFilter filterSagittarius = new GPUImageToneCurveFilter();
				filterSagittarius.setFromCurveFileInputStream(mContext
						.getResources().openRawResource(R.raw.sagittarius));
				filtersList.add(filterSagittarius);

				break;

			case GPUImageFilterType.Capricorn :

				GPUImageLookupFilter filterLookUpCapricorn = new GPUImageLookupFilter();
				filterLookUpCapricorn
						.setBitmap(getImageFromAssetsFile("Capricorn/"
								+ "capricorn.png"));
				filtersList.add(filterLookUpCapricorn);

				break;

			case GPUImageFilterType.Aries :

				GPUImageLookupFilter filterLookUpAries = new GPUImageLookupFilter();
				filterLookUpAries.setBitmap(getImageFromAssetsFile("Aries/"
						+ "aries.png"));
				filtersList.add(filterLookUpAries);

				GPUImageOverlayBlendFilter filterOverlayAries = new GPUImageOverlayBlendFilter();
				filterOverlayAries.setBitmap(getImageFromAssetsFile("Aries/"
						+ "aries_overlay.jpg"));
				filtersList.add(filterOverlayAries);

				break;

			default :

				Log.e("===", "No mine filter of that type!");

				break;
		}

		return filtersList;
	}

	// //////////////////////////
	/**
	 * 从Assets中读取图片
	 * 
	 * @param fileName
	 * @return
	 */
	private Bitmap getImageFromAssetsFile(String fileName)
	{
		Bitmap image = null;
		AssetManager am = mContext.getResources().getAssets();
		try
		{
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return image;

	}

	public ArrayList<GPUImageFilter> getFilterList()
	{
		return mFilterList;
	}

	public void setFilterList(ArrayList<GPUImageFilter> mFilterList)
	{
		this.mFilterList = mFilterList;
	}
}
