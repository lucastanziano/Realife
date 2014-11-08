package com.realapp.realife.adapters;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.realapp.realife.R;
import com.realapp.realife.fragments.CategorySelectorFragment;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.Print;

public class AppCheckListAdapter extends ArrayAdapter<IAppItem> {

	private Typeface regularFont;
	private Typeface boldFont;

	private int checkBoxBackgroundId;

	private boolean[] checkedElements; // store which elements have been checked
	private int checkedCounter;

	private WeakReference<CategorySelectorFragment> fragmentRef;

	public AppCheckListAdapter(CategorySelectorFragment fragment, int numItems) {
		super(fragment.getActivity().getApplicationContext(), 0);

		fragmentRef = new WeakReference<CategorySelectorFragment>(fragment);

		checkBoxBackgroundId = Resources.getSystem().getIdentifier(
				"btn_check_holo_light", "drawable", "android");

		loadTypefaceFonts(getContext());

		checkedElements = new boolean[numItems];
		checkedCounter = 0;
	}

	private void loadTypefaceFonts(Context context) {
		this.regularFont = Typeface.createFromAsset(
				context.getAssets(),
				context.getApplicationContext().getResources()
						.getString(R.string.HelveticaNeue));
		this.boldFont = Typeface.createFromAsset(
				context.getAssets(),
				context.getApplicationContext().getResources()
						.getString(R.string.HelveticaNeueBold));
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(
					getContext().getApplicationContext()).inflate(
					R.layout.checkable_list_item, null);
		}

		boolean isChecked = checkedElements[position];

		final ImageView appIcon = (ImageView) convertView
				.findViewById(R.id.iconApp);
		appIcon.setImageDrawable(getItem(position).getIcon());

		final TextView appName = (TextView) convertView
				.findViewById(R.id.textPName);
		if (isChecked) {
			appName.setTypeface(boldFont);
		} else {
			appName.setTypeface(regularFont);
		}
		appName.setText(getItem(position).getAppID().getAppName());

		final CheckBox check = (CheckBox) convertView
				.findViewById(R.id.check_item);
		check.setWidth(30);
		check.setHeight(30);
		check.setButtonDrawable(checkBoxBackgroundId);
		check.setChecked(checkedElements[position]);

		check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isChecked = check.isChecked();
				getItem(position).setChecked(isChecked);
				Print.debug(getItem(position).getAppID().getPackageName()
						+ " is checked " + isChecked);
				checkedElements[position] = isChecked;
				if (isChecked) {
					appName.setTypeface(boldFont);
					checkedCounter++;
				} else {
					appName.setTypeface(regularFont);
					checkedCounter--;
				}
				CategorySelectorFragment fragment = fragmentRef.get();
				if (fragment != null) {
					if (checkedCounter > 0) {
						fragment.setCategoryBtnEnabled(true);
					} else {
						fragment.setCategoryBtnEnabled(false);
					}
				}

			}
		});

		check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				isChecked = check.isChecked();
			}
		});

		return convertView;
	}

	public void resetCheckboxes() {
		int size = checkedElements.length;
		checkedElements = new boolean[size];
		notifyDataSetChanged();
	}

}
