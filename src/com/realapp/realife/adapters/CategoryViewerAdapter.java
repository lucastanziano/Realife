package com.realapp.realife.adapters;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.realapp.realife.fragments.CategorySelectorFragment;
import com.realapp.realife.gui.viewpager.OutlineContainer;
import com.realapp.realife.models.apps.AppDBHelper;
import com.realapp.realife.models.apps.IAppItem;

public class CategoryViewerAdapter extends PagerAdapter {

	AppCheckListAdapter[] appListAdapters = new AppCheckListAdapter[getCount()];
	private SparseArray<List<IAppItem>> itemsArray = new SparseArray<List<IAppItem>>();

	private WeakReference<CategorySelectorFragment> fragmentRef;

	public CategoryViewerAdapter(CategorySelectorFragment fragment) {
		this.fragmentRef = new WeakReference<CategorySelectorFragment>(fragment);
		clearItemArray();
	}

	public AppCheckListAdapter getListAdapter(int position) {
		CategorySelectorFragment fragment = fragmentRef.get();
		if (fragment != null) {
			if (appListAdapters[position] == null) {
				appListAdapters[position] = new AppCheckListAdapter(
						fragment, itemsArray.get(
								getCategory(position)).size());
			}
			return appListAdapters[position];
		}
		return new AppCheckListAdapter(null, 0);
	}

	private void clearItemArray() {
		itemsArray = new SparseArray<List<IAppItem>>();
		for (int i = 0; i < 8; i++) {
			itemsArray.put(i, new ArrayList<IAppItem>());
		}

		itemsArray.put(AppDBHelper.getCategoryIndex(AppDBHelper.UNCATEGORIZED),
				new ArrayList<IAppItem>());

	}

	public void setAppItems(List<IAppItem> itemList) {
		if (itemList != null) {
			clearItemArray();
			for (IAppItem item : itemList) {
				if (itemsArray.get(item.getAppID().getCategoryID()) == null) {
					itemsArray.put(item.getAppID().getCategoryID(),
							new ArrayList<IAppItem>());
				}
				itemsArray.get(item.getAppID().getCategoryID()).add(item);
			}
		}
	}

	private int getCategory(int position) {
		int category = position - 1;
		if (position == 0) {
			category = AppDBHelper.getCategoryIndex(AppDBHelper.UNCATEGORIZED);
		}
		return category;
	}

	/**
	 * All the objects of the page must be instantiated here and added to the
	 * container
	 * 
	 * @param container
	 *            , the global view where insert the gui elements
	 * @param position
	 *            , the current item showed in the view pager
	 */
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {

		CategorySelectorFragment fragment = fragmentRef.get();
		if (fragment != null) {

			int category = getCategory(position);

			// setting layout
			LinearLayout layout = new LinearLayout(fragment.getActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

			if (itemsArray.get(category).size() > 0) {

				// setting up entry list
				ListView list = new ListView(fragment.getActivity()
						.getApplicationContext());

				appListAdapters[position] = new AppCheckListAdapter(
						fragment, itemsArray.get(category).size());

				for (IAppItem item : itemsArray.get(category)) {
					appListAdapters[position].add(item);
				}
				list.setAdapter(appListAdapters[position]);

				layout.addView(list);

			}

			else {
				layout.setGravity(Gravity.CENTER);
				layout.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				TextView nodataText = new TextView(fragment.getActivity()
						.getApplicationContext());
				LinearLayout.LayoutParams nodataLayout = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				nodataLayout.gravity = Gravity.CENTER;
				nodataText.setLayoutParams(nodataLayout);
				nodataText.setText("No apps in this category");
				nodataText.setTextSize(20);

				layout.addView(nodataText);
			}

			container.addView(layout, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);

			return layout;
		}
		return container;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object obj) {
		container.removeView((View) obj);
	}

	@Override
	public int getCount() {
		return AppDBHelper.categories.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		if (view instanceof OutlineContainer) {
			return ((OutlineContainer) view).getChildAt(0) == obj;
		} else {
			return view == obj;
		}
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
}