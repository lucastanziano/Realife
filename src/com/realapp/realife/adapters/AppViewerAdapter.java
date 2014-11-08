package com.realapp.realife.adapters;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.realapp.realife.fragments.AddictionMeasurerFragment;
import com.realapp.realife.gui.viewpager.OutlineContainer;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.AppUsageItem;
import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.util.ImageUtil;
import com.realapp.realife.util.Print;

public class AppViewerAdapter extends PagerAdapter {

	private static final int MIN_APP_USAGE_SECONDS = 60; //don't show an app if it has been used for less than 1 minute

	private static final String LIST = "LIST";
	private static final String IMAGE = "Image";

	private SparseArray<List<AppUsageItem>> itemsArray = new SparseArray<List<AppUsageItem>>();

	private final int uncategorized_idx = AppDBHelper
			.getCategoryIndex(AppDBHelper.UNCATEGORIZED);

	private WeakReference<AddictionMeasurerFragment> fragmentRef;

	public AppViewerAdapter(AddictionMeasurerFragment fragment) {
		this.fragmentRef = new WeakReference<AddictionMeasurerFragment>(
				fragment);
		cleanItemsArray();
	}

	private Activity getActivity() {
		if (fragmentRef.get() == null) {
			return null;
		}

		return fragmentRef.get().getActivity();
	}

	private void cleanItemsArray() {
		itemsArray = new SparseArray<List<AppUsageItem>>();
		for (int i = 0; i < getCount(); i++) {
			itemsArray.put(i, new ArrayList<AppUsageItem>());
		}

		itemsArray.put(uncategorized_idx, new ArrayList<AppUsageItem>());
	}

	public void setAppItems(List<IAppItem> itemList) {

		cleanItemsArray();

		for (IAppItem item : itemList) {
			if (item instanceof AppUsageItem) {
				if (item.getAppID().getCategoryID() < getCount()
						|| item.getAppID().getCategoryID() == uncategorized_idx) {
					if (((AppUsageItem) item).getActiveTime() > MIN_APP_USAGE_SECONDS) {
						itemsArray.get(item.getAppID().getCategoryID()).add(
								(AppUsageItem) item);
					}
				}
			}
		}

	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		if (getActivity() != null) {

			// setting layout
			LinearLayout layout = new LinearLayout(getActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			ListView list = new ListView(getActivity());

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			params.gravity = Gravity.TOP;
			list.setLayoutParams(params);

			list.setTag(getListViewTagByPosition(position));

			final AppUsageListAdapter listAdapter = new AppUsageListAdapter(
					getActivity());

			if (getItemCount(position) > 0) {

				for (AppUsageItem item : itemsArray.get(position)) {
					listAdapter.add(item);
				}

				if (position == uncategorized_idx) {
					for (AppUsageItem item : itemsArray.get(uncategorized_idx)) {
						listAdapter.add(item);
					}
				}

			}

			list.setAdapter(listAdapter);
			layout.addView(list);

			final ImageView nodataImage = new ImageView(getActivity());

			nodataImage.setTag(getImageViewTagByPosition(position));
			final String imgFileName;
			switch (position) {
			case 0:
				imgFileName = "texting_data_empty";
				break;
			case 1:
				imgFileName = "social_data_empty";
				break;
			case 2:
				imgFileName = "apps_data_empty";
				break;
			case 3:
				imgFileName = "www_data_empty";

				break;
			case 4:
				imgFileName = "game_data_empty";
				break;
			default:
				imgFileName = "apps_data_empty";
				break;
			}

			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					try {

						Bitmap nodataBmp = ImageUtil.getAssetImage(
								getActivity(), imgFileName);

						Drawable nodataDraw = new BitmapDrawable(getActivity()
								.getResources(), nodataBmp);
						nodataImage.setImageDrawable(nodataDraw);

					} catch (IOException e) {
						Print.error(new Throwable(
								"error loading no data image", e));
					}

				}
			});

			LinearLayout.LayoutParams imglParams = new LinearLayout.LayoutParams(
					400, 400);
			nodataImage.setLayoutParams(imglParams);
			layout.setGravity(Gravity.CENTER);

			layout.addView(nodataImage);

			// if there were no items in this page, show a no data image
			if (getItemCount(position) > 0) {
				nodataImage.setVisibility(View.GONE);
			} else {
				list.setVisibility(View.GONE);
			}

			container.addView(layout, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return layout;
		}

		return container;
	}

	public int getItemCount(int position) {
		if (position == AppDBHelper.getCategoryIndex(AppDBHelper.APPS)) {
			return itemsArray.get(position).size()
					+ itemsArray.get(uncategorized_idx).size();
		}
		return itemsArray.get(position).size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		container.removeView((View) obj);
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		if (view instanceof OutlineContainer) {
			return ((OutlineContainer) view).getChildAt(0) == obj;
		} else {
			return view == obj;
		}
	}

	public void updateDataList(ListView listV, ImageView imageV, int position) {

		AppUsageListAdapter listAdapter = (AppUsageListAdapter) listV
				.getAdapter();

		if (getItemCount(position) <= 0) {
			clearPage(listV, imageV);
		}

		if (getItemCount(position) > 0) {
			listV.setVisibility(View.VISIBLE);
			imageV.setVisibility(View.GONE);
			List<AppUsageItem> items = new ArrayList<AppUsageItem>();
			items.addAll(itemsArray.get(position));
			if (position == AppDBHelper.getCategoryIndex(AppDBHelper.APPS)) {
				items.addAll(itemsArray.get(uncategorized_idx));
			}

			listAdapter.clear();
//			listAdapter.addAll(items);  not available for API < 11 on adapters
            for(int i=0; i<items.size(); i++){
            	listAdapter.add(items.get(i));
            }

			listAdapter.sort(new Comparator<AppUsageItem>() {

				@Override
				public int compare(AppUsageItem firstItem,
						AppUsageItem secondItem) {

					return secondItem.getActiveTime()
							- firstItem.getActiveTime();
				}
			});

		}

		listAdapter.notifyDataSetChanged();
	}

	public void clearPage(ListView listV, ImageView imageV) {
		listV.setAdapter(new AppUsageListAdapter(getActivity()));
		listV.setVisibility(View.GONE);
		imageV.setVisibility(View.VISIBLE);
	}

	public static Object getListViewTagByPosition(int position) {
		return LIST + position;
	}

	public static Object getImageViewTagByPosition(int position) {

		return IMAGE + position;
	}
}