package com.realapp.realife.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.realapp.realife.R;
import com.realapp.realife.activities.MainActivity;

public class GlobalMenuFragment extends ListFragment {

	private MenuAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list, null);
		adapter = new MenuAdapter(getActivity());
		setList();

		setListAdapter(adapter);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	protected void setList() {

		adapter.add(new MenuItem("Today", 0, android.R.drawable.ic_menu_search));
		adapter.add(new MenuItem("Categories", 0,
				android.R.drawable.ic_dialog_info));

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent;
		switch (position) {
		case 0:
			newContent = new AddictionMeasurerFragment();
			break;
		case 1:
			newContent = new CategorySelectorFragment();
			break;
		default:
			newContent = new AddictionMeasurerFragment();
			break;
		}

		if (newContent != null)
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof MainActivity) {
			MainActivity ma = (MainActivity) getActivity();
			ma.switchContent(fragment);
		}
	}

	private class MenuItem {
		public String counter;
		public String tag;
		public int iconRes;
		public Bitmap icon;
		private boolean hasIconBitmap;

		public MenuItem(String tag, int counter) {
			this.tag = tag;
			if (counter == 0) {
				this.counter = "";
			} else {
				this.counter = String.valueOf(counter);
			}
			this.iconRes = -1;
			this.hasIconBitmap = false;
		}

		public MenuItem(String tag, int counter, int iconRes) {
			this(tag, counter);
			this.iconRes = iconRes;
		}

		public MenuItem(String tag, int counter, Bitmap icon) {
			this(tag, counter);
			this.icon = icon;
			this.hasIconBitmap = true;
		}

		public boolean hasIconBitmap() {
			return hasIconBitmap;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.menu_item, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.menu_item_icon);
			if (getItem(position).hasIconBitmap()) {
				icon.setImageBitmap(getItem(position).icon);
			} else {
				icon.setImageResource(getItem(position).iconRes);
			}

			TextView title = (TextView) convertView
					.findViewById(R.id.menu_item_title);
			title.setText(getItem(position).tag);

			TextView counter = (TextView) convertView
					.findViewById(R.id.menu_item_counter);
			counter.setText(getItem(position).counter);

			return convertView;
		}

	}

}
