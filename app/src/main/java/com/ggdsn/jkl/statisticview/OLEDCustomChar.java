package com.ggdsn.jkl.statisticview;

/**
 * Created by liubo on 4/21/16.
 */
public class OLEDCustomChar {
	public String string;
	public byte gb2312Low;
	public byte gb2312High;
	public byte customLow;
	public byte customHigh;

	public OLEDCustomChar(String string, int gb2312Low, int gb2312High, int customLow, int customHigh) {
		this.string = string;
		this.gb2312Low = (byte) gb2312Low;
		this.gb2312High = (byte) gb2312High;
		this.customLow = (byte) customLow;
		this.customHigh = (byte) customHigh;
	}
}
