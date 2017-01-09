package com.nextjoy.game.util;

import android.graphics.Bitmap;

public class NativeImageUtils
{
	static
	{
		System.loadLibrary("ImageTool");
	}

	public static native int[] ImageToGray(int[] buf, int w, int h);


	public static Bitmap toGrayscaleNative(Bitmap bmpOriginal)
	{
		try
		{
			int width, height;
			height = bmpOriginal.getHeight();
			width = bmpOriginal.getWidth();
			int[] pix = new int[width * height];
			bmpOriginal.getPixels(pix, 0, width, 0, 0, width, height);
			int[] resultInt = NativeImageUtils.ImageToGray(pix, width, height);
			Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
			bmpGrayscale.setPixels(resultInt, 0, width, 0, 0, width, height);
			return bmpGrayscale;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
