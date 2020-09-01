package cn.jarlen.imgedit.filter;

import android.graphics.Bitmap;

public class FilterItem {

	/**
	 * 滤镜缩略图
	 */
	private Bitmap filterThumbnail;

	/**
	 * 滤镜名称
	 */
	private String filterName;

	/**
	 * 滤镜类型
	 */
	private int filterType;

	/**
	 * 滤镜风格
	 */
	private String filterStyle;

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public int getFilterType() {
		return filterType;
	}

	public void setFilterType(int filterType) {
		this.filterType = filterType;
	}

	public String getFilterStyle() {
		return filterStyle;
	}

	public void setFilterStyle(String filterStyle) {
		this.filterStyle = filterStyle;
	}

	public Bitmap getFilterThumbnail() {
		return filterThumbnail;
	}

	public void setFilterThumbnail(Bitmap filterThumbnail) {
		this.filterThumbnail = filterThumbnail;
	}
}
