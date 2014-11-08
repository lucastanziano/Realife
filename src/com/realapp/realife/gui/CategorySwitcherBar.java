package com.realapp.realife.gui;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.realapp.realife.R;
import com.realapp.realife.fragments.AddictionMeasurerFragment;
import com.realapp.realife.listeners.CategorySwitchBarOnClickListener;

public class CategorySwitcherBar {

	private ImageView socialImageView;
	private ImageView gamingImageView;
	private ImageView communicationImageView;
	private ImageView toolsImageView;
	private ImageView internetImageView;
	private Context context;

	public CategorySwitcherBar(View rootView) {
		
		this.context = rootView.getContext().getApplicationContext();

		this.setSocialImageView((ImageView) rootView
				.findViewById(R.id.imageSocial));

		this.setGamingImageView((ImageView) rootView
				.findViewById(R.id.imageGaming));

		this.setCommunicationImageView((ImageView) rootView
				.findViewById(R.id.imageTexting));

		this.setToolsImageView((ImageView) rootView.findViewById(R.id.imageApps));

		this.setInternetImageView((ImageView) rootView.findViewById(R.id.imageWWW));

	}



	public void setOnClickListeners(Handler handler) {
		getSocialImageView()
				.setOnClickListener(new CategorySwitchBarOnClickListener(
						handler, AddictionMeasurerFragment.SOCIAL_FRAG));
		getGamingImageView()
				.setOnClickListener(new CategorySwitchBarOnClickListener(
						handler, AddictionMeasurerFragment.GAME_FRAG));
		getCommunicationImageView()
				.setOnClickListener(new CategorySwitchBarOnClickListener(
						handler, AddictionMeasurerFragment.TEXTING_FRAG));
		getToolsImageView().setOnClickListener(new CategorySwitchBarOnClickListener(
				handler, AddictionMeasurerFragment.APPS_FRAG));
		getInternetImageView().setOnClickListener(new CategorySwitchBarOnClickListener(
				handler, AddictionMeasurerFragment.WWW_FRAG));
	}

	public void animateButton(Context context, int button) {

		if (context != null) {
			Animation pulse = AnimationUtils.loadAnimation(
					context.getApplicationContext(), R.anim.pulse);

			switch (button) {
			case AddictionMeasurerFragment.TEXTING_FRAG:
				getCommunicationImageView().setImageDrawable(context.getResources()
						.getDrawable(R.drawable.texting_active));
				getCommunicationImageView().startAnimation(pulse);
				break;
			case AddictionMeasurerFragment.SOCIAL_FRAG:
				getSocialImageView().setImageDrawable(context.getResources()
						.getDrawable(R.drawable.social_active));
				getSocialImageView().startAnimation(pulse);
				break;
			case AddictionMeasurerFragment.APPS_FRAG:
				getToolsImageView().setImageDrawable(context.getResources()
						.getDrawable(R.drawable.apps_active));
				getToolsImageView().startAnimation(pulse);
				break;
			case AddictionMeasurerFragment.WWW_FRAG:
				getInternetImageView().setImageDrawable(context.getResources()
						.getDrawable(R.drawable.www_active));
				getInternetImageView().startAnimation(pulse);
				break;
			case AddictionMeasurerFragment.GAME_FRAG:
				getGamingImageView().setImageDrawable(context.getResources()
						.getDrawable(R.drawable.game_active));
				getGamingImageView().startAnimation(pulse);
				break;
			default:
				break;
			}
		}

	}

	public void switchOffPreviousButton(Context context, int previousButton) {
		switch (previousButton) {
		case AddictionMeasurerFragment.TEXTING_FRAG:
			getCommunicationImageView().setImageDrawable(context.getResources()
					.getDrawable(R.drawable.texting));
			break;
		case AddictionMeasurerFragment.SOCIAL_FRAG:
			getSocialImageView().setImageDrawable(context.getResources()
					.getDrawable(R.drawable.social));
			break;
		case AddictionMeasurerFragment.APPS_FRAG:
			getToolsImageView().setImageDrawable(context.getResources().getDrawable(
					R.drawable.apps));
			break;
		case AddictionMeasurerFragment.WWW_FRAG:
			getInternetImageView().setImageDrawable(context.getResources().getDrawable(
					R.drawable.www));
			break;
		case AddictionMeasurerFragment.GAME_FRAG:
			getGamingImageView().setImageDrawable(context.getResources()
					.getDrawable(R.drawable.game));
			break;
		default:
			break;
		}

	}
	
	public ImageView getSocialImageView() {
		if(socialImageView==null){
			socialImageView = new ImageView(context);
		}
		return socialImageView;
	}

	public void setSocialImageView(ImageView socialImageView) {
		this.socialImageView = socialImageView;
	}

	public ImageView getGamingImageView() {
		if(gamingImageView==null){
			gamingImageView = new ImageView(context);
		}
		return gamingImageView;
	}

	public void setGamingImageView(ImageView gamingImageView) {
		this.gamingImageView = gamingImageView;
	}

	public ImageView getToolsImageView() {
		if(toolsImageView == null){
			toolsImageView = new ImageView(context);
		}
		return toolsImageView;
	}

	public void setToolsImageView(ImageView toolsImageView) {
		this.toolsImageView = toolsImageView;
	}

	public ImageView getInternetImageView() {
		if(internetImageView==null){
			internetImageView = new ImageView(context);
		}
		return internetImageView;
	}

	public void setInternetImageView(ImageView internetImageView) {
		this.internetImageView = internetImageView;
	}

	public ImageView getCommunicationImageView() {
		if(communicationImageView == null){
			communicationImageView = new ImageView(context);
		}
		return communicationImageView;
	}

	public void setCommunicationImageView(ImageView communicationImageView) {
		this.communicationImageView = communicationImageView;
	}

}
