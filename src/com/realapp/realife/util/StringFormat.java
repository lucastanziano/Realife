package com.realapp.realife.util;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

public class StringFormat {

	
	/**
	 * @param seconds
	 * @return spanned time, ready to be printed in a textview in the format
	 *         12h34m
	 */
	public static Spanned getFormattedGlobalTimeString(long seconds) {
		int totHour = (int) seconds / 3600;
		int totMin = (int) ((seconds - totHour * 3600) / 60);

		float fontLarge = 2f;
		float fontSmall = 1f;

		String hour = Integer.toString(totHour);
		String min = Integer.toString(totMin);
		int flags = 0;
		int charPosition = 0;
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(hour);
		ssb.setSpan(new RelativeSizeSpan(fontLarge), 0, hour.length(), flags);
		charPosition += hour.length();
		ssb.append("h");
		ssb.setSpan(new RelativeSizeSpan(fontSmall), charPosition,
				charPosition + 1, flags);
		charPosition++;
		ssb.append(min);
		ssb.setSpan(new RelativeSizeSpan(fontLarge), charPosition, charPosition
				+ min.length(), flags);
		charPosition += min.length();
		ssb.append("m");
		ssb.setSpan(new RelativeSizeSpan(fontSmall), charPosition,
				charPosition + 1, flags);

		return ssb;
	}
	
	
	public static String getTimeFormatFromSeconds(long seconds) {

		int totHour = (int) seconds / 3600;
		int totMin = (int) ((seconds - totHour * 3600) / 60);
		// int totSecs = (int) (seconds - totHour * 3600 - totMin * 60);
		if(totHour>0){
		return Integer.toString(totHour) + "h" + Integer.toString(totMin) + "m";
		}
		return Integer.toString(totMin) + "m";
	}
}
