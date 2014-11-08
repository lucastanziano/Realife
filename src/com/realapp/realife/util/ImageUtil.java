package com.realapp.realife.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

public class ImageUtil {
	
	/**
	 * This function will catch the image from Assets folder instead of Resources.
	 * This is the only solution to avoid out-of-memory for the drawable cache using large images.
	 * @param context
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getAssetImage(Context context, String filename) throws IOException {
	    AssetManager assets = context.getResources().getAssets();
	    InputStream buffer = new BufferedInputStream((assets.open("drawable/" + filename + ".png")));
	    Bitmap bitmap = BitmapFactory.decodeStream(buffer);
	    return bitmap;
	}

	
	/**
	 * This method converts dp unit to equivalent pixels, depending on device
	 * density.
	 * 
	 * @param dp
	 *            A value in dp (density independent pixels) unit. Which we need
	 *            to convert into pixels
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A int value to represent px equivalent to dp depending on device
	 *         density
	 */
	public static int convertDpToPixel(float dp, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	/**
	 * This method converts device specific pixels to density independent
	 * pixels.
	 * 
	 * @param px
	 *            A value in px (pixels) unit. Which we need to convert into db
	 * @param context
	 *            Context to get resources and device specific display metrics
	 * @return A int value to represent dp equivalent to px value
	 */
	public static int convertPixelsToDp(float px, Context context) {
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		int dp = (int) (px / (metrics.densityDpi / 160f));
		return dp;
	}
	
	
	public static void takeScreenshot(View viewToSave, File imageFile){
		// create bitmap screen capture
		Bitmap bitmap;
		View rootView = viewToSave.getRootView();
		rootView.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
		rootView.setDrawingCacheEnabled(false);

		OutputStream fout = null;

		try {
		    fout = new FileOutputStream(imageFile);
		    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
		    fout.flush();
		    fout.close();

		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	}
	
	public static int dpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
	}

}
