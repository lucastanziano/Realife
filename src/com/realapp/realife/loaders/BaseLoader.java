package com.realapp.realife.loaders;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.AsyncTaskLoader;

import com.realapp.realife.models.apps.IAppItem;
import com.realapp.realife.observers.DataEntryObserver;

public class BaseLoader extends AsyncTaskLoader<List<IAppItem>> {

	// We hold a reference to the Loader data here.
	private List<IAppItem> mEntries;
	
	protected IntentFilter filter;
	
	public BaseLoader(Context ctx) {
		// Loaders may be used across multiple Activitys (assuming they aren't
		// bound to the LoaderManager), so NEVER hold a reference to the context
		// directly. Doing so will cause you to leak an entire Activity's
		// context.
		// The superclass constructor will store a reference to the Application
		// Context instead, and can be retrieved with a call to getContext().
		super(ctx);

		startLoading();

	}

	/****************************************************/
	/** (1) A task that performs the asynchronous load **/
	/****************************************************/

	/**
	 * This method is called on a background thread and generates a List of
	 * {@link AppItemSample} AppItems. Each entry corresponds to a single
	 * installed application on the device.
	 */
	@Override
	public List<IAppItem> loadInBackground() {
		
		return new ArrayList<IAppItem>();
	}

	/*******************************************/
	/** (2) Deliver the results to the client **/
	/**
	 * @param db
	 *****************************************/

	/**
	 * Called when there is new data to deliver to the client. The superclass
	 * will deliver it to the registered listener (i.e. the LoaderManager),
	 * which will forward the results to the client through a call to
	 * onLoadFinished.
	 */
	@Override
	public void deliverResult(List<IAppItem> entries) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the
			// data.
			// This can happen when the Loader is reset while an asynchronous
			// query
			// is working in the background. That is, when the background thread
			// finishes its work and attempts to deliver the results to the
			// client,
			// it will see here that the Loader has been reset and discard any
			// resources associated with the new data as necessary.
			if (entries != null) {
				releaseResources(entries);
				return;
			}
		}

		// Hold a reference to the old data so it doesn't get garbage collected.
		// We must protect it until the new data has been delivered.
		List<IAppItem> oldEntries = mEntries;
		mEntries = entries;

		if (isStarted()) {
			// If the Loader is in a started state, have the superclass deliver
			// the
			// results to the client.

			super.deliverResult(entries);
		}

		// Invalidate the old data as we don't need it any more.
		if (oldEntries != null && oldEntries != entries) {
			releaseResources(oldEntries);
		}
	}

	/*********************************************************/
	/** (3) Implement the Loader�s state-dependent behavior **/
	/*********************************************************/

	@Override
	protected void onStartLoading() {

		if (mEntries != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(mEntries);
		}

		// Register the observers that will notify the Loader when changes are
		// made.
		if (dEntryObserver == null && filter != null) {
			dEntryObserver = new DataEntryObserver();
			getContext().registerReceiver(dEntryObserver, filter);
			dEntryObserver.setAppItemLoader(this);
		}

		if (takeContentChanged()) {

			forceLoad();
		} else if (mEntries == null) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {

		// The Loader has been put in a stopped state, so we should attempt to
		// cancel the current load (if there is one).
		cancelLoad();

		// Note that we leave the observer as is; Loaders in a stopped state
		// should still monitor the data source for changes so that the Loader
		// will know to force a new load if it is ever started again.
	}

	@Override
	protected void onReset() {

		// Ensure the loader is stopped.
		onStopLoading();

		if (mEntries != null) {
			releaseResources(mEntries);
			mEntries = null;
		}

		// The Loader is being reset, so we should stop monitoring for changes.
		if (dEntryObserver != null) {
			getContext().unregisterReceiver(dEntryObserver);
			dEntryObserver = null;
		}

	}

	@Override
	public void onCanceled(List<IAppItem> apps) {

		// Attempt to cancel the current asynchronous load.
		super.onCanceled(apps);

		releaseResources(apps);
	}

	@Override
	public void forceLoad() {
		super.forceLoad();
	}

	/**
	 * Helper method to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	private void releaseResources(List<IAppItem> apps) {
		// For a simple List, there is nothing to do. For something like a
		// Cursor,
		// we would close it in this method. All resources associated with the
		// Loader should be released here.
	}

	/*********************************************************************/
	/** (4) Observer which receives notifications when the data changes **/
	/*********************************************************************/

	// An observer to notify the Loader when new apps are installed/updated.
	private DataEntryObserver dEntryObserver;

}
