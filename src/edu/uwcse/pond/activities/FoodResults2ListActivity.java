/*
 * Copyright (C) 2012 University of Washington
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package edu.uwcse.pond.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.MyFoodsTableHelper;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import edu.uwcse.pond.proto.R;

public class FoodResults2ListActivity extends TabActivity implements
		ExpandableListView.OnChildClickListener,
		ExpandableListView.OnGroupCollapseListener,
		ExpandableListView.OnGroupExpandListener, OnItemClickListener {

	private class PondGenericSectionAdapter extends PondRecentSectionAdapter {

		public PondGenericSectionAdapter(String query) {
			super(query);

		}

		@Override
		protected void doFirstListItems(String query) {
			addItemsToLists(
					mFoodDatabaseHelper.getGenericCommonFoodsWithNameFTS(query),
					1, 0);
		}

		@Override
		protected String getContentDisplayStringFromCursor(Cursor c) {
			return c.getString(1);
		}

		@Override
		protected int getContentIdFromCursor(Cursor c) {
			return c.getInt(0);
		}

		@Override
		protected Cursor getSectionContentCursor(String query,
				Cursor curSectionHeadingCursor) {
			final int foodClassId = curSectionHeadingCursor.getInt(2);
			return mFoodDatabaseHelper.getGenericFoodsInFoodClassWithNameFTS(
					query, foodClassId);
		}

		@Override
		protected Cursor getSectionHeadingCursor(String query) {
			return mFoodDatabaseHelper
					.getFoodClassesForGenericFoodsWithNameFTS(query);
		}

		@Override
		protected String getSectionHeadingFromCursor(
				Cursor curSectionHeadingCursor) {
			return curSectionHeadingCursor.getString(1) + " ("
					+ curSectionHeadingCursor.getInt(3) + " results)";
		}

		@Override
		protected int getSourceIdFromCursor(Cursor c) {
			return 1;
		}

	}

	private class PondMineSectionAdapter extends PondRecentSectionAdapter {

		public PondMineSectionAdapter(String query) {
			super(query);
		}

		@Override
		protected String getContentDisplayStringFromCursor(Cursor c) {
			return c.getString(3);// 3
		}

		@Override
		protected int getContentIdFromCursor(Cursor c) {
			return c.getInt(2);
		}

		@Override
		protected Cursor getSectionContentCursor(String query,
				Cursor curSectionHeadingCursor) {
			final String foodClassId = curSectionHeadingCursor.getString(0);
			return mDiaryHelper.getMyFoodsThatStartWith(foodClassId);
		}

		@Override
		protected Cursor getSectionHeadingCursor(String query) {
			return mDiaryHelper.getMyFoodHeadings();
		}

		@Override
		protected String getSectionHeadingFromCursor(
				Cursor curSectionHeadingCursor) {
			return curSectionHeadingCursor.getString(0);// + " ("+
														// curSectionHeadingCursor.getInt(3)
														// + " results)";
		}

	}

	private class PondRecentSectionAdapter extends BaseAdapter {

		List<String> displayStrings = new ArrayList<String>();
		List<Integer> itemIds = new ArrayList<Integer>();
		List<Integer> itemSource = new ArrayList<Integer>();

		public PondRecentSectionAdapter(String query) {

			doFirstListItems(query);

			final Cursor sectionHeadingCursor = getSectionHeadingCursor(query);
			sectionHeadingCursor.moveToFirst();
			while (!sectionHeadingCursor.isAfterLast()) {
				displayStrings
						.add(getSectionHeadingFromCursor(sectionHeadingCursor));
				itemIds.add(-1);
				itemSource.add(-1);
				addItemsToLists(
						getSectionContentCursor(query, sectionHeadingCursor),
						1, 0);

				sectionHeadingCursor.moveToNext();
			}
			sectionHeadingCursor.close();
		}

		protected void addItemsToLists(Cursor c, int nameColId, int idColId) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				displayStrings.add(getContentDisplayStringFromCursor(c));
				itemIds.add(getContentIdFromCursor(c));
				itemSource.add(getSourceIdFromCursor(c));
				c.moveToNext();
			}
			c.close();
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		protected void doFirstListItems(String query) {

		}

		protected String getContentDisplayStringFromCursor(Cursor c) {
			return c.getString(3);
		}

		protected int getContentIdFromCursor(Cursor c) {
			return c.getInt(2);
		}

		@Override
		public int getCount() {
			return itemIds.size();
		}

		@Override
		public Object getItem(int arg0) {
			return itemSource.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return itemIds.get(arg0);
		}

		@Override
		public int getItemViewType(int arg0) {

			return (itemIds.get(arg0) == -1) ? 0 : 1;
		}

		protected Cursor getSectionContentCursor(String query,
				Cursor curSectionHeadingCursor) {
			// int foodClassId = curSectionHeadingCursor.getInt(2);
			return mDiaryHelper.getRecentFoodsFromDay(curSectionHeadingCursor
					.getString(0));// groupCursor.getColumnIndex("timestamp")));
		}

		protected Cursor getSectionHeadingCursor(String query) {
			return mDiaryHelper.getRecentFoodDates();
		}

		protected String getSectionHeadingFromCursor(
				Cursor curSectionHeadingCursor) {
			return curSectionHeadingCursor.getString(0);// + "("+
														// curSectionHeadingCursor.getInt(3)
														// + " results)";
		}

		protected int getSourceIdFromCursor(Cursor c) {
			return c.getInt(c.getColumnIndex(MyFoodsTableHelper.COL_SOURCE));
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			if (convertView == null) {

				convertView = inflateCorrectViewForItem(position);

			} else {
				// Check that it's the right view for either section heading or
				// content
				if (itemIds.get(position) == -1) {

					if (convertView.getId() == R.layout.section_list_item_view) {
						// The view needs to be a header but is an item ==> fix
						// it
						convertView = inflateCorrectViewForItem(position);
					}
				} else {
					if (convertView.getId() == R.layout.section_list_header_view) {
						// The view needs to be an item but is a header ==> fix
						// it
						convertView = inflateCorrectViewForItem(position);
					}
				}

			}

			final TextView tv = (TextView) convertView
					.findViewById(R.id.textView1);
			tv.setText(displayStrings.get(position));

			if (position % 2 == 0) {
				convertView.setBackgroundColor(Color.rgb(33, 33, 33));
			}

			return convertView;
		}

		@Override
		public int getViewTypeCount() {

			return 2;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		private View inflateCorrectViewForItem(int position) {
			final LayoutInflater inflater = FoodResults2ListActivity.this
					.getLayoutInflater();

			if (itemIds.get(position) == -1) {
				return inflater
						.inflate(R.layout.section_list_header_view, null);
			} else {
				return inflater.inflate(R.layout.section_list_item_view, null);
			}
		}

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isEnabled(int arg0) {
			return itemIds.get(arg0) != -1; // !displayStrings.get(arg0).startsWith("+");
		}

	}

	String mDialogQuery = "";

	public static final int DIALOG_GET_FOOD_QUERY = 1;

	public static final int DIALOG_FILTER_RESULTS = 2;

	public static final int FOOD_ENTRY_RESULT_CODE = 5;

	public static final String FOOD_QUERY = "query";

	// private SimpleCursorTreeAdapter myExpandableListAdapter;

	NutritionDbHelper mFoodDatabaseHelper;

	private DiaryDbHelper mDiaryHelper;

	private SimpleCursorAdapter myListAdapter;

	// private ListView mGenericListView;

	private String mQueryTerm = "";

	private long mStudyConditionStartTime = 0L;

	private final List<String> mSearchHistory = new ArrayList<String>();

	private ExpandableListView mBrandsListView;

	private ListView mRecentListView;

	private ListView mMineListView;

	private TabHost mTabHost;

	private View mGenericViewHolder;

	private TabSpec mGenericTabSpec;

	private TabSpec mBrandsTabSpec;

	private AlertDialog buildFilterDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);

		final LayoutInflater inflater = getLayoutInflater();
		final View layout = inflater.inflate(R.layout.food_list_filter_dialog,
				null);

		final Spinner manSpinner = (Spinner) layout
				.findViewById(R.id.listFilterDialog_manufacturer_Spinner);
		final Cursor manCursor = mFoodDatabaseHelper
				.getManfacturersForFoodWithName(mQueryTerm);
		final String[] from = new String[] { "manufacturerName" };
		final int[] to = new int[] { android.R.id.text1 };

		final SimpleCursorAdapter manAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, manCursor, from, to);// DailyEntriesListViewAdapter(this,
																			// entries_cursor);
		manAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		manSpinner.setAdapter(manAdapter);

		// ===================
		/*
		 * Spinner food_class_spinner =
		 * (Spinner)layout.findViewById(R.id.listFilterDialog_foodClass_Spinner
		 * ); ArrayAdapter<FoodClass> class_adapter = new
		 * ArrayAdapter<FoodClass>(this, android.R.layout.simple_spinner_item,
		 * FoodClass.values());
		 * class_adapter.setDropDownViewResource(android.R.layout
		 * .simple_spinner_dropdown_item);
		 * food_class_spinner.setAdapter(class_adapter);
		 */
		alert.setView(layout);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				doFilter(manSpinner.getSelectedItemId());
				dialog.dismiss();
			}
		});

		alert.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		/*
		 * alert.setNegativeButton("Cancel", new
		 * DialogInterface.OnClickListener() { public void
		 * onClick(DialogInterface dialog, int whichButton) { dialog.cancel(); }
		 * });
		 */

		alert.setCancelable(true);
		return alert.create();

	}

	private void doFilter(long manId) {
		fillData(mFoodDatabaseHelper
				.getFoodsWithNameAndManId(mQueryTerm, manId));
	}
	/*
	 * private void doMySearch(String query) {
	 * 
	 * //mDbHelper.logAction(Action.FOOD_SEARCH, -1, -1, "food query", query);
	 * 
	 * 
	 * 
	 * Toast.makeText(getApplicationContext(), query,
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * mSearchHistory.add(query);
	 * 
	 * Cursor c = mFoodDatabaseHelper.getFoodsWithName(query);
	 * 
	 * mDiaryHelper.logAction(Action.FOOD_SEARCH, -1, c.getCount(),
	 * "Food query; numResults", query);
	 * 
	 * fillData(c); }
	 */;

	private void doMySearch(final long locid, final Spinner food_class_spinner,
			final CheckBox useClass) {
		Toast.makeText(getApplicationContext(), mDialogQuery,
				Toast.LENGTH_SHORT).show();
		// long id = food_class_spinner.getSelectedItemId();
		if (useClass.isChecked()) {
			final Object obj = food_class_spinner.getSelectedItem();
			// System.out.println(obj);
			// FoodClass cat = FoodClass.get((int)id);
			final Cursor c = mFoodDatabaseHelper.getFoodsWithName(mDialogQuery,
					(FoodClass) obj);
			fillData(c);
		} else {
			final Cursor c = mFoodDatabaseHelper.getFoodsWithName(mDialogQuery);
			fillData(c);
		}
	};

	private void fillBrandResults(final String string, int numResults) {
		// mBrandsTabSpec.setIndicator("brands (" + numResults + ")");
		final TextView title = (TextView) mTabHost.getTabWidget().getChildAt(1)
				.findViewById(android.R.id.title);
		title.setText("Brands (" + numResults + ")");

		if (string.length() > 0) {
			mDiaryHelper.logAction(Action.FOOD_SEARCH, 2, numResults,
					"Food query; numResults (brands)", string);
		}
		final Cursor c = mFoodDatabaseHelper
				.getManfacturersForFoodWithName(string);
		final String[] from = new String[] { "manufacturerName" };
		;
		final int[] to = new int[] { R.id.textView1 };
		final String[] childFrom = new String[] { "foodName" };
		final int[] childTo = new int[] { R.id.textView1 };

		final SimpleCursorTreeAdapter brandTreeAdapter = new SimpleCursorTreeAdapter(
				this, c, R.layout.expandable_list_header_view, from, to,
				R.layout.section_list_item_view, childFrom, childTo) {

			@Override
			protected Cursor getChildrenCursor(Cursor arg0) {
				return mFoodDatabaseHelper.getFoodsWithNameAndManId(string,
						arg0.getInt(arg0.getColumnIndex("_id")));
			}
		};

		mBrandsListView.setAdapter(brandTreeAdapter);
		mBrandsListView.setOnItemClickListener(this);
		mBrandsListView.setOnChildClickListener(this);

	}

	private void fillData(Cursor c) {
		final TextView summary = (TextView) findViewById(R.id.food_query_results_summ_text_view);
		summary.setText(c.getCount() + " results for '" + mQueryTerm + "'");

		// Get all of the notes from the database and create the item list
		// Cursor c = mDbHelper.getFoodsWithName(queryTerm, cat);
		startManagingCursor(c);

		final String[] from = new String[] { "foodName", "manufacturerName" };
		final int[] to = new int[] { R.id.food_name_textView,
				R.id.manufacturer_name_textView };

		// Now create an array adapter and set it to display using our row
		myListAdapter = new SimpleCursorAdapter(this, R.layout.food_list_item,
				c, from, to);

		/*
		 * myExpandableListAdapter = new SimpleCursorTreeAdapter(this, c,
		 * android.R.layout.simple_expandable_list_item_1,
		 * android.R.layout.simple_expandable_list_item_1, new String[] {
		 * "manufacturerName" }, // Name for group layouts new int[] {
		 * android.R.id.text1 }, new String[] { "foodName" }, // Number for
		 * child layouts new int[] { android.R.id.text1 });
		 * 
		 * ExpandableListView ex_list_view =
		 * (ExpandableListView)findViewById(R.id
		 * .food_query_expandable_list_view);
		 * ex_list_view.setFastScrollEnabled(true); ex_list_view.setA
		 */
		final ListView list_view = (ListView) findViewById(R.id.food_query_list_view);
		list_view.setFastScrollEnabled(true);
		list_view.setTextFilterEnabled(true);

		list_view.setAdapter(myListAdapter);

		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				final Intent intent = new Intent(FoodResults2ListActivity.this,
						FoodDetailActivity.class);
				Bundle extras = intent.getExtras();
				if (extras == null) {
					extras = new Bundle();
				}
				extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
				if (mStudyConditionStartTime > 0L) {
					extras.putLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
							new Date().getTime());
					intent.putExtra(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
							new Date().getTime());
				}

				intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, id);

				startActivityForResult(intent, FOOD_ENTRY_RESULT_CODE);

			}
		});

		// c.close();
	}

	private void fillGenericResults(final String query, int numResults,
			View parentView) {
		final LinearLayout parent_layout = (LinearLayout) parentView;// parentView.findViewById(R.id.linearLayout1);
		parent_layout.removeAllViews();
		final TextView title = (TextView) mTabHost.getTabWidget().getChildAt(0)
				.findViewById(android.R.id.title);
		title.setText("xyz");
		// mGenericTabSpec.setIndicator("Generic (" + numResults +")");
		title.setText("Generic (" + numResults + ")");
		if (query.length() > 0) {
			mDiaryHelper.logAction(Action.FOOD_SEARCH, 1, numResults,
					"Food query; numResults (generic)", query);
		}
		if (numResults < 50) {
			final ListView generic_list_view = new ListView(this);
			generic_list_view.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			parent_layout.addView(generic_list_view);

			final PondGenericSectionAdapter generic_adapter = new PondGenericSectionAdapter(
					query);
			generic_list_view.setAdapter(generic_adapter);
			generic_list_view.setOnItemClickListener(this);
		} else {
			/*
			 * ListView common_list_view = new ListView(this);
			 * common_list_view.setLayoutParams(new LinearLayout.LayoutParams(
			 * LinearLayout.LayoutParams.FILL_PARENT, 100));
			 * 
			 * // parent_layout.addView(common_list_view);
			 * 
			 * Cursor common_cursor =
			 * mFoodDatabaseHelper.getGenericCommonFoodsWithNameFTS(query);
			 * String[] common_from = new String[]{"foodNameText"}; int[]
			 * common_to = new int[]{android.R.id.text1}; SimpleCursorAdapter
			 * common_adapter = new SimpleCursorAdapter(this,
			 * android.R.layout.simple_list_item_1, common_cursor, common_from,
			 * common_to); common_list_view.setAdapter(common_adapter);
			 */

			final ExpandableListView expand_list_view = new ExpandableListView(
					this);
			expand_list_view.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

			parent_layout.addView(expand_list_view);

			// Make it an expandable list view
			final Cursor c = mFoodDatabaseHelper
					.getFoodClassesForGenericAndCommonFoodsWithNameFTS(query);
			final String[] from = new String[] { "foodClassFullName" };
			;
			final int[] to = new int[] { R.id.textView1 };
			final String[] childFrom = new String[] { "foodNameText" };
			final int[] childTo = new int[] { R.id.textView1 };

			final SimpleCursorTreeAdapter brandTreeAdapter = new SimpleCursorTreeAdapter(
					this, c, R.layout.expandable_list_header_view, from, to,
					R.layout.section_list_item_view, childFrom, childTo) {

				@Override
				protected Cursor getChildrenCursor(Cursor arg0) {
					return mFoodDatabaseHelper
							.getGenericFoodsInFoodClassWithNameFTS(query,
									arg0.getInt(2));// getFoodsWithNameAndManId(string,
													// arg0.getInt(arg0.getColumnIndex("_id")));
				}
			};

			expand_list_view.setAdapter(brandTreeAdapter);
			expand_list_view.setOnItemClickListener(this);
			expand_list_view.setOnChildClickListener(this);
		}

	}

	private void fillMineListView(String string) {
		mDiaryHelper.getMyFoods();

		// Now create an array adapter and set it to display using our row
		// ListAdapter generic_adapter =
		// new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c,
		// from, to);

		final PondMineSectionAdapter generic_adapter = new PondMineSectionAdapter(
				string);
		mMineListView.setAdapter(generic_adapter);
		mMineListView.setOnItemClickListener(this);
	}

	private void fillRecentListView(String string) {
		// Right now, this fills the expandableListView with the last 10 days of
		// food items.
		// In the future, add a ListView above it that includes items that have
		// the given query word

		final PondRecentSectionAdapter generic_adapter = new PondRecentSectionAdapter(
				string);
		mRecentListView.setAdapter(generic_adapter); // recentTreeAdapter);
		mRecentListView.setOnItemClickListener(this);

	}

	private void fillRecentSearchData(Cursor c) {
		final ListView summary = (ListView) findViewById(R.id.recent_search_listview);
		// summary.setText(c.getCount() + " results for '" + mQueryTerm + "'");

		// Get all of the notes from the database and create the item list
		// Cursor c = mDbHelper.getFoodsWithName(queryTerm, cat);
		startManagingCursor(c);

		final String[] from = new String[] { "description", "typeId" };
		final int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		// Now create an array adapter and set it to display using our row
		myListAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, c, from, to);

		summary.setAdapter(myListAdapter);

		// c.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FOOD_ENTRY_RESULT_CODE) {
			if (resultCode == RESULT_OK) {
				finish();
			}
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(android.view.Menu menu) {
	 * MenuInflater inflater = getMenuInflater();
	 * inflater.inflate(R.menu.food_list_context_menu, menu); // Invoke the
	 * Register activity //menu.getItem(0).setIntent(new Intent(this,
	 * AccountsActivity.class));
	 * 
	 * return true; }
	 */

	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1,
			int groupPos, int childPos, long id) {

		final Intent intent = new Intent(FoodResults2ListActivity.this,
				FoodDetailActivity.class);
		Bundle extras = intent.getExtras();
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
		if (mStudyConditionStartTime > 0L) {
			extras.putLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
					new Date().getTime());
			intent.putExtra(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
					new Date().getTime());
		}

		intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, id);

		startActivityForResult(intent, FOOD_ENTRY_RESULT_CODE);

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_query_results_tabbed);

		final Bundle b = this.getIntent().getExtras();

		if ((b != null)
				&& b.containsKey(FoodDetailActivity.DATA_DATETIME_EATEN_KEY)) {
			mStudyConditionStartTime = b
					.getLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY);
		}

		final Bundle appData = getIntent().getBundleExtra(
				SearchManager.APP_DATA);
		if (appData != null) {
			mStudyConditionStartTime = appData
					.getLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY);
		}

		mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());

		// setContentView(R.layout.food_query_list);
		mFoodDatabaseHelper = new NutritionDbHelper(this);
		// mDbHelper.openDataBase();
		mFoodDatabaseHelper.open();

		/*
		 * if (mDialogQuery.length() > 0){
		 * fillData(mDbHelper.getFoodsWithName(mDialogQuery)); }
		 */

		// Get the intent, verify the action and get the query
		final Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mQueryTerm = intent.getStringExtra(SearchManager.QUERY);

		}

		getResources();
		mTabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		mGenericViewHolder = LayoutInflater.from(this).inflate(
				R.layout.food_query_results_generic_view, null);
		final int num_generic_items = mFoodDatabaseHelper
				.getNumGenericFoodsWithNameFTS(mQueryTerm);
		mGenericTabSpec = mTabHost.newTabSpec("generic")
				.setIndicator("generic (" + num_generic_items + ")")
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return mGenericViewHolder;
					}
				});
		mTabHost.addTab(mGenericTabSpec);

		// Do the same for the other tabs
		// tab_intent = new Intent().setClass(this, AlbumsActivity.class);
		final View brand_view = LayoutInflater.from(this).inflate(
				R.layout.food_query_results_brand_view, null);
		final int num_brand_items = mFoodDatabaseHelper
				.getNumBrandFoodsWithNameFTS(mQueryTerm);
		mBrandsTabSpec = mTabHost.newTabSpec("brands")
				.setIndicator("brands (" + num_brand_items + ")")
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return brand_view;
					}
				});
		mTabHost.addTab(mBrandsTabSpec);
		mBrandsListView = (ExpandableListView) brand_view
				.findViewById(R.id.expandableListView1);

		// tab_intent = new Intent().setClass(this, SongsActivity.class);
		final View mine_view = LayoutInflater.from(this).inflate(
				R.layout.food_query_results_mine_view, null);
		spec = mTabHost.newTabSpec("mine").setIndicator("mine")
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return mine_view;
					}
				});
		mTabHost.addTab(spec);
		mMineListView = (ListView) mine_view.findViewById(R.id.listView1);

		final View recent_view = LayoutInflater.from(this).inflate(
				R.layout.food_query_results_recent_view, null);
		spec = mTabHost.newTabSpec("recent").setIndicator("recent")
				.setContent(new TabHost.TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return recent_view;
					}
				});
		mTabHost.addTab(spec);
		mRecentListView = (ListView) recent_view.findViewById(R.id.listView1);

		if (mQueryTerm == null || mQueryTerm.length() <= 0) {
			mTabHost.setCurrentTab(2);
			setTitle("Recent Foods");
		} else {
			setTitle("Search Results: " + mQueryTerm);
		}

		fillGenericResults(mQueryTerm, num_generic_items, mGenericViewHolder);
		fillBrandResults(mQueryTerm, num_brand_items);
		fillRecentListView(mQueryTerm);
		fillMineListView(mQueryTerm);
	}

	/**
	 * This method is called when the user context-clicks a note in the list.
	 * NotesList registers itself as the handler for context menus in its
	 * ListView (this is done in onCreate()).
	 * 
	 * The only available options are COPY and DELETE.
	 * 
	 * Context-click is equivalent to long-press.
	 * 
	 * @param menu
	 *            A ContexMenu object to which items should be added.
	 * @param view
	 *            The View for which the context menu is being constructed.
	 * @param menuInfo
	 *            Data associated with view.
	 * @throws ClassCastException
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {

		// The data from the menu item.
		AdapterView.AdapterContextMenuInfo info;

		// Tries to get the position of the item in the ListView that was
		// long-pressed.
		try {
			// Casts the incoming data object into the type for AdapterView
			// objects.
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (final ClassCastException e) {
			// If the menu object can't be cast, logs an error.
			// Log.e(TAG, "bad menuInfo", e);
			return;
		}

		/*
		 * Gets the data associated with the item at the selected position.
		 * getItem() returns whatever the backing adapter of the ListView has
		 * associated with the item. In NotesList, the adapter associated all of
		 * the data for a note with its list item. As a result, getItem()
		 * returns that data as a Cursor.
		 */
		final Cursor cursor = (Cursor) myListAdapter.getItem(info.position);

		// If the cursor is empty, then for some reason the adapter can't get
		// the data from the
		// provider, so returns null to the caller.
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		// Inflate menu from XML resource
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food_list_context_menu, menu);
		/*
		 * // Append to the // menu items for any other activities that can do
		 * stuff with it // as well. This does a query on the system for any
		 * activities that // implement the ALTERNATIVE_ACTION for our data,
		 * adding a menu item // for each one that is found. Intent intent = new
		 * Intent(null, Uri.withAppendedPath(getIntent().getData(),
		 * Integer.toString((int) info.id) ));
		 * intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		 * menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, new
		 * ComponentName(this, NotesList.class), null, intent, 0, null);
		 */
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_FILTER_RESULTS:
			dialog = buildFilterDialog();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		mDiaryHelper.doneWithDb();
		mFoodDatabaseHelper.close();
	}

	@Override
	public void onGroupCollapse(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent;

		final Integer o = (Integer) arg0.getAdapter().getItem(arg2);
		if (o.intValue() == 1) {
			intent = new Intent(FoodResults2ListActivity.this,
					FoodDetailActivity.class);
		} else {
			intent = new Intent(FoodResults2ListActivity.this,
					CustomFoodDetailActivity.class);
		}
		// Temp: until I figure this out...
		// intent = new Intent(FoodResults2ListActivity.this,
		// FoodDetailActivity.class);

		Bundle extras = intent.getExtras();
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, arg3);
		if (mStudyConditionStartTime > 0L) {
			extras.putLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
					new Date().getTime());
			intent.putExtra(FoodDetailActivity.DATA_DATETIME_EATEN_KEY,
					new Date().getTime());
		}

		intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, arg3);

		startActivityForResult(intent, FOOD_ENTRY_RESULT_CODE);

	}

	@Override
	protected void onNewIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			mQueryTerm = intent.getStringExtra(SearchManager.QUERY);

			setTitle("Search Results: " + mQueryTerm);

			fillGenericResults(mQueryTerm,
					mFoodDatabaseHelper
							.getNumGenericFoodsWithNameFTS(mQueryTerm),
					mGenericViewHolder);
			fillBrandResults(mQueryTerm,
					mFoodDatabaseHelper.getNumBrandFoodsWithNameFTS(mQueryTerm));
			mTabHost.setCurrentTab(0);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.filter_results_menu_item:
			showDialog(DIALOG_FILTER_RESULTS);
			// mFilterDialog.show();
			return true;
			// break;
		case R.id.new_query_menu_item:
			startSearch(mQueryTerm, true, null, false);
			return true;
		case R.id.revise_query_menu_item:
			startSearch(mQueryTerm, false, null, false);
			return true;
		}

		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_FILTER_RESULTS:
			// dialog = buildFilterDialog();
			// If query has changed, change the spinner.
			break;
		// case DIALOG_GET_FOOD_QUERY:
		// do the work to define the game over Dialog

		// break;
		default:
			dialog = null;
		}

	}

	private void setUpOtherOptions() {
		final ListView summary = (ListView) findViewById(R.id.options_listview);

		final String[] options = new String[] { "My Foods", "Recent Entries",
				"Brand Name Foods" };

		// Now create an array adapter and set it to display using our row
		final ListAdapter adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, options);

		// new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c,
		// from, to);

		summary.setAdapter(adapter);
	}

	/*
	 * private static final int TOKEN_GROUP = 0; private static final int
	 * TOKEN_CHILD = 1;
	 * 
	 * private static final class QueryHandler extends AsyncQueryHandler {
	 * private CursorTreeAdapter mAdapter;
	 * 
	 * public QueryHandler(Context context, CursorTreeAdapter adapter) {
	 * super(context.getContentResolver()); this.mAdapter = adapter; }
	 * 
	 * @Override protected void onQueryComplete(int token, Object cookie, Cursor
	 * cursor) { switch (token) { case TOKEN_GROUP:
	 * mAdapter.setGroupCursor(cursor); break;
	 * 
	 * case TOKEN_CHILD: int groupPosition = (Integer) cookie;
	 * mAdapter.setChildrenCursor(groupPosition, cursor); break; } } }
	 * 
	 * 
	 * public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {
	 * 
	 * // Note that the constructor does not take a Cursor. This is done to
	 * avoid querying the // database on the main thread. public
	 * MyExpandableListAdapter(Context context, int groupLayout, int
	 * childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
	 * int[] childrenTo) {
	 * 
	 * super(context, null, groupLayout, groupFrom, groupTo, childLayout,
	 * childrenFrom, childrenTo); }
	 * 
	 * @Override protected Cursor getChildrenCursor(Cursor groupCursor) { //
	 * Given the group, we return a cursor for all the children within that
	 * group
	 * 
	 * // Return a cursor that points to this contact's phone numbers
	 * Uri.Builder builder = Contacts.CONTENT_URI.buildUpon();
	 * ContentUris.appendId(builder,
	 * groupCursor.getLong(GROUP_ID_COLUMN_INDEX));
	 * builder.appendEncodedPath(Contacts.Data.CONTENT_DIRECTORY); Uri
	 * phoneNumbersUri = builder.build();
	 * 
	 * mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(),
	 * phoneNumbersUri, PHONE_NUMBER_PROJECTION, Phone.MIMETYPE + "=?", new
	 * String[] { Phone.CONTENT_ITEM_TYPE }, null);
	 * 
	 * mQueryHandler.startQuery(TOKEN_CHILD, cookie, uri, projection, selection,
	 * selectionArgs, orderBy) return null; } }
	 */
}
