package com.realapp.realife.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.realapp.realife.R;
import com.realapp.realife.ReaLifeApplication;
import com.realapp.realife.gui.BetterPopupWindow;
import com.realapp.realife.models.GlobalStatistics;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppID;
import com.realapp.realife.models.apps.AppUsageItem;
import com.realapp.realife.util.Print;
import com.realapp.realife.util.StringFormat;

public class AppUsageListAdapter extends ArrayAdapter<AppUsageItem> {

	public List<AppUsageItem> appUsageList = new ArrayList<AppUsageItem>();

	public static final String POPUP_INFO_TAG = "Popuptaginfo";

	public static boolean openSetCategoryDialog = false;

	public AppUsageListAdapter(Context context) {
		super(context, 0);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(
					getContext().getApplicationContext()).inflate(
					R.layout.expandable_list_item, null);
		}

		ImageView icon = (ImageView) convertView.findViewById(R.id.iconApp);
		icon.setImageDrawable(getItem(position).getIcon());

		final TextView appName = (TextView) convertView.findViewById(R.id.textPName);
		appName.setTag(position);
		appName.setText(getItem(position).getAppID().getAppName());

		if (getItem(position).getAppID().getCategoryID() == AppDBHelper
				.getCategoryIndex(AppDBHelper.UNCATEGORIZED)) {
			appName.setTextColor(Color.RED);
		}

		TextView activeTime = (TextView) convertView
				.findViewById(R.id.activeTimeAppText);
		activeTime.setText(StringFormat.getTimeFormatFromSeconds(getItem(
				position).getActiveTime()));

		ImageView addictionImg = (ImageView) convertView
				.findViewById(R.id.expandable_toggle_button);
		int imgAddiId = R.drawable.dep_soft;
		int addiction = (int) getItem(position).getAddiction();
		if (addiction < 33) {
			imgAddiId = R.drawable.dep_soft;
		}
		if (addiction >= 33 && addiction < 66) {
			imgAddiId = R.drawable.dep_moderate;
		}
		if (addiction >= 66) {
			imgAddiId = R.drawable.dep_high;
		}

		addictionImg.setImageResource(imgAddiId);

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				InfoPopupWindow infoPopup = new InfoPopupWindow(v,
						getItem(position));
				infoPopup.showLikeQuickActionCentered();
				infoPopup.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						if (openSetCategoryDialog) {
							openSelectCategoryDialog(getItem(position), appName);
						}
					}
				});

			}
		});

		return convertView;
	}

	private void openSelectCategoryDialog(final AppUsageItem appItem, final TextView appName) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext()
				);
		builder.setTitle("Select category");

		final CharSequence[] choiceList = new CharSequence[AppDBHelper.categories
				.size()];
		int index = 0;
		for (String cat : AppDBHelper.categories) {
			choiceList[index] = cat;
			index++;
		}

		int selected = 0; // does not select anything

		builder.setSingleChoiceItems(choiceList, selected,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.setCancelable(false);
		builder.setPositiveButton("Assign",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListView lw = ((AlertDialog) dialog).getListView();
						int categoryID = lw.getCheckedItemPosition();
						appItem.getAppID().setCategoryID(categoryID);
						List<AppID> apps = new ArrayList<AppID>();
						apps.add(appItem.getAppID());
						AppDBHelper.getInstance(
								getContext().getApplicationContext())
								.updateCategoryTable(apps);
						appName.setTextColor(Color.BLACK);
						broadcastSignalDatabaseUpdated();

					}
				});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();

					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}
	
	private void broadcastSignalDatabaseUpdated() {
		Print.debug("BROADCASTING ACTION DB UPDATED");
		Intent intent = new Intent();
		intent.setAction(ReaLifeApplication.ACTION_DB_UPDATED);
		getContext().sendBroadcast(intent);
	}

	private static class InfoPopupWindow extends BetterPopupWindow implements
			OnClickListener {

		private TextView compulsivityValue;

		private TextView tirelessValue;

		private TextView compulsivityDescription;

		private TextView tirelessDescription;

		private TextView constancyValue;

		private TextView constancyDescription;

		private TextView addictionValue;

		private AppUsageItem appItem;

		private ImageView appIcon;

		private TextView appName;

		private Button assignCategory;
		
		private LinearLayout assignCategoryLayout;

		public InfoPopupWindow(View anchor, AppUsageItem appUsageItem) {
			super(anchor);
			this.appItem = appUsageItem;
		}

		@Override
		protected void onCreate() {

			// inflate layout
			LayoutInflater inflater = (LayoutInflater) this.anchor.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup root = (ViewGroup) inflater.inflate(
					R.layout.popup_grid_layout, null);

			appIcon = (ImageView) root.findViewById(R.id.appIconInfoPopup);

			appName = (TextView) root.findViewById(R.id.appNameInfoPopup);

			compulsivityValue = (TextView) root
					.findViewById(R.id.compulsivityValue);

			compulsivityDescription = (TextView) root
					.findViewById(R.id.compulsivityDescription);

			tirelessValue = (TextView) root.findViewById(R.id.tirelessValue);

			tirelessDescription = (TextView) root
					.findViewById(R.id.tirelessDescription);

			constancyValue = (TextView) root.findViewById(R.id.constancyValue);

			constancyDescription = (TextView) root
					.findViewById(R.id.constancyDescription);

			addictionValue = (TextView) root.findViewById(R.id.addictionValue);
			
			assignCategoryLayout = (LinearLayout) root.findViewById(R.id.infoPopupAssignCategory);

			assignCategory = (Button) root
					.findViewById(R.id.infoPopupAssignCategoryBtn);

			assignCategory.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openSetCategoryDialog = true;
					dismiss();
				}
			});

			this.setContentView(root);
		}

		@Override
		public void onShow() {
			
			openSetCategoryDialog = false;

			appName.setText(appItem.getAppID().getAppName());

			appIcon.setImageDrawable(appItem.getIcon());

			compulsivityValue.setText(appItem.getCompulsivity() + "%");

			String compDescText = "You opened this app " + appItem.getAppUses()
					+ " times (" + appItem.getFreqUsesPerHour() + " per hr).";
			compulsivityDescription.setText(compDescText);

			tirelessValue.setText(appItem.getTireless() + "%");

			double overallUsage = 100 * (appItem.getActiveTime() / GlobalStatistics
					.getInstance().getGlobalStatistics().getTotalActiveTime());
			String tirelessText = "You used this app for "
					+ StringFormat.getTimeFormatFromSeconds(appItem
							.getActiveTime()) + " (" + overallUsage
					+ "% overall).";
			tirelessDescription.setText(tirelessText);

			constancyValue.setText(appItem.getConstancy() + "%");

			String constDesc = "You used this app during "
					+ appItem.getNumSamples() + " different hours in the last "
					+ appItem.getDays() + " days.";
			constancyDescription.setText(constDesc);

			addictionValue.setText(appItem.getAddiction() + "%");

			if (appItem.getAppID().getCategoryID() == AppDBHelper
					.getCategoryIndex(AppDBHelper.UNCATEGORIZED)) {
				assignCategoryLayout.setVisibility(View.VISIBLE);
			}
			else{
				assignCategoryLayout.setVisibility(View.GONE);
			}
		}

		@Override
		public void onClick(View v) {

			this.dismiss();
		}
	}

}