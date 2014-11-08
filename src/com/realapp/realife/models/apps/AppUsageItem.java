package com.realapp.realife.models.apps;

import java.util.List;

import android.graphics.drawable.Drawable;

import com.realapp.realife.fragments.AddictionMeasurerFragment;

public class AppUsageItem extends AppIDItem{

	public static final int HOURS = 16; // 16hr per day..supposing people sleep
										// for 8 hours
	public static final int MAX_FREQ_PER_HOUR = 12;
	public static final int MAX_ACTIVE_TIME_PER_DAY = 4 * 60 * 60;
	public static final int MAX_SAMPLES_PER_DAY = 16;

	private int activeTime;
	private int uses;
	private List<AppUsageSample> samples;
	private int longestUsageInOneHour;
	private long peakHourForActiveTime;
	private int maxUsesInOneHour;
	private long peakHourForUses;
	private double freqUsesPerHour;
	private int numSamples;
	private int days;
	private int compulsivity;
	private int tireless;
	private int constancy;
	private int addiction;
	


	public AppUsageItem(Drawable icon, AppID appID, List<AppUsageSample> samples) {
		super(icon, appID);
		setSamples(samples);

		int activeTime = 0;
		int appUses = 0;
		longestUsageInOneHour = 0;
		maxUsesInOneHour = 0;

		for (AppUsageSample sample : samples) {
			activeTime += sample.getActiveTime();
			appUses += sample.getAppUses();
			if(sample.getActiveTime()>longestUsageInOneHour){
				longestUsageInOneHour = sample.getActiveTime();
				peakHourForActiveTime = sample.getHourDayTimeMillis();
			}
			if(sample.getAppUses()> maxUsesInOneHour){
				maxUsesInOneHour = sample.getAppUses();
				peakHourForUses = sample.getHourDayTimeMillis();
			}
			
		}
		this.activeTime = activeTime;

		this.uses = appUses;
		
		this.days = 1;
		if(AddictionMeasurerFragment.NAV_DATA_PERIOD==AddictionMeasurerFragment.WEEK){
			this.days = 7;
		}
		
		this.numSamples = samples.size();
		if(numSamples>0){
		this.freqUsesPerHour = uses/numSamples; 
		}
		else{
			this.freqUsesPerHour = 0;
		}
		this.compulsivity = (int) ((freqUsesPerHour/MAX_FREQ_PER_HOUR) *100);
        this.tireless =  (((this.activeTime/days)*100)/MAX_ACTIVE_TIME_PER_DAY) ;
        this.constancy =  (((numSamples/days)*100)/MAX_SAMPLES_PER_DAY) ;
        this.addiction = (compulsivity + tireless + constancy)/3;
        
	}

	/*** GETTERS AND SETTERS ***/

	public int getActiveTime() {
		return activeTime;
	}

	public int getAppUses() {
		return uses;
	}

	public List<AppUsageSample> getSamples() {
		return samples;
	}

	public void setSamples(List<AppUsageSample> samples) {
		this.samples = samples;
	}


	public int getLongestUsageInOneHour() {
		return longestUsageInOneHour;
	}

	public void setLongestUsageInOneHour(int longestUsageInOneHour) {
		this.longestUsageInOneHour = longestUsageInOneHour;
	}

	public long getPeakHourForActiveTime() {
		return peakHourForActiveTime;
	}

	public void setPeakHourForActiveTime(long peakHourForActiveTime) {
		this.peakHourForActiveTime = peakHourForActiveTime;
	}

	public long getPeakHourForUses() {
		return peakHourForUses;
	}

	public void setPeakHourForUses(long peakHourForUses) {
		this.peakHourForUses = peakHourForUses;
	}

	public int getMaxUsesInOneHour() {
		return maxUsesInOneHour;
	}

	public void setMaxUsesInOneHour(int maxUsesInOneHour) {
		this.maxUsesInOneHour = maxUsesInOneHour;
	}

	public double getFreqUsesPerHour() {
		return freqUsesPerHour;
	}

	public void setFreqUsesPerHour(double freqUsesPerHour) {
		this.freqUsesPerHour = freqUsesPerHour;
	}

	public int getNumSamples() {
		return numSamples;
	}

	public void setNumSamples(int numSamples) {
		this.numSamples = numSamples;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public double getCompulsivity() {
		return compulsivity;
	}

	public void setCompulsivity(int compulsivity) {
		this.compulsivity = compulsivity;
	}

	public double getTireless() {
		return tireless;
	}

	public void setTireless(int tireless) {
		this.tireless = tireless;
	}

	public double getConstancy() {
		return constancy;
	}

	public void setConstancy(int constancy) {
		this.constancy = constancy;
	}

	public double getAddiction() {
		return addiction;
	}

	public void setAddiction(int addiction) {
		this.addiction = addiction;
	}

}
