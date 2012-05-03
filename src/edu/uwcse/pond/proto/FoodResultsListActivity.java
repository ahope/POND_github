package edu.uwcse.pond.proto;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts.FoodClass;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CursorTreeAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FilterQueryProvider;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FoodResultsListActivity extends Activity {
	
	String mDialogQuery = "";
	
	public static final int DIALOG_GET_FOOD_QUERY = 1;
	public static final int DIALOG_FILTER_RESULTS = 2;
	
	public static final int FOOD_ENTRY_RESULT_CODE = 5; 
	
	public static final String FOOD_QUERY = "query";
	
	NutritionDbHelper mDbHelper; 
	
	private DiaryDbHelper mDiaryHelper;
	
	private SimpleCursorAdapter myListAdapter; 
	
	//private SimpleCursorTreeAdapter myExpandableListAdapter; 
	
	private String mQueryTerm = ""; 
	
	private AlertDialog mFilterDialog; 
	
	private long mStudyConditionStartTime = 0L; 
	
	private List<String> mSearchHistory = new ArrayList<String>(); 

   // private QueryHandler mQueryHandler;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_query_results);
        
       
        Bundle b = this.getIntent().getExtras(); 
        if (b.containsKey(FoodDetailActivity.DATA_DATETIME_EATEN_KEY)){
        	mStudyConditionStartTime = b.getLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY);
        }
        
        Bundle appData = getIntent().getBundleExtra(SearchManager.APP_DATA);
        if (appData != null) {
            mStudyConditionStartTime= appData.getLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY);
            
        }
        
        mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
	 	
        
        /*
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		//FrameLayout f1 = (FrameLayout)alert.findViewById(android.R.id.body);
		//f1.addView(inflater.inflate(R.layout.dialog_view, f1, false));
		View layout = inflater.inflate(R.layout.food_query_dialog, null);
		
		final Spinner food_class_spinner = (Spinner)layout.findViewById(R.id.food_query_category_Spinner);
		food_class_spinner.setAdapter(new ArrayAdapter<FoodClass>(this, android.R.layout.simple_list_item_1, FoodClass.values()));
		final EditText input = (EditText)layout.findViewById(R.id.food_query_EditText);
		final CheckBox useClass = (CheckBox)layout.findViewById(R.id.food_query_CheckBox);
		
		alert.setView(layout);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mDialogQuery = input.getText().toString().trim();
				doMySearch(locid, food_class_spinner, useClass);
			}

		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		*/
	//	alert.show();
        
        
        //setContentView(R.layout.food_query_list);
        mDbHelper = new NutritionDbHelper(this);
        //mDbHelper.openDataBase();
        mDbHelper.open();
        
    /*    if (mDialogQuery.length() > 0){
        	fillData(mDbHelper.getFoodsWithName(mDialogQuery));
        }*/
        
     // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
          mQueryTerm = intent.getStringExtra(SearchManager.QUERY);
          doMySearch(mQueryTerm);
        }
    }
	
	@Override
	protected void onNewIntent(Intent intent) {
		
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	          mQueryTerm = intent.getStringExtra(SearchManager.QUERY);
	          doMySearch(mQueryTerm);
	        }
	};
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		mDiaryHelper.doneWithDb();
		mDbHelper.close();
	};
	
	private void fillData(Cursor c) {
		TextView summary = (TextView)findViewById(R.id.food_query_results_summ_text_view);
		summary.setText(c.getCount() + " results for '" + mQueryTerm + "'");
		
        // Get all of the notes from the database and create the item list
		//Cursor c = mDbHelper.getFoodsWithName(queryTerm, cat);
        startManagingCursor(c);

        String[] from = new String[] { "foodName", "manufacturerName" };
        int[] to = new int[] { R.id.food_name_textView, R.id.manufacturer_name_textView };
        
        // Now create an array adapter and set it to display using our row
        myListAdapter =
            new SimpleCursorAdapter(this, R.layout.food_list_item, c, from, to);
     
     /*   myExpandableListAdapter = new SimpleCursorTreeAdapter(this, c, 
        		android.R.layout.simple_expandable_list_item_1,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { "manufacturerName" }, // Name for group layouts
                new int[] { android.R.id.text1 },
                new String[] { "foodName" }, // Number for child layouts
                new int[] { android.R.id.text1 });
        
        ExpandableListView ex_list_view = (ExpandableListView)findViewById(R.id.food_query_expandable_list_view);
        ex_list_view.setFastScrollEnabled(true);
        ex_list_view.setA
     */   
        ListView list_view = (ListView)findViewById(R.id.food_query_list_view);
        list_view.setFastScrollEnabled(true);
        list_view.setTextFilterEnabled(true);
        
        list_view.setAdapter(myListAdapter);
        
        list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long id) {
				Intent intent = new Intent(FoodResultsListActivity.this, FoodDetailActivity.class);
		    	Bundle extras = intent.getExtras();
		    	if (extras == null){
		    		extras = new Bundle();
		    	}
		    	extras.putLong(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
		    	if (mStudyConditionStartTime > 0L){
		    		extras.putLong(FoodDetailActivity.DATA_DATETIME_EATEN_KEY, new Date().getTime());
		    		intent.putExtra(FoodDetailActivity.DATA_DATETIME_EATEN_KEY, new Date().getTime());
		    	}
		    	
		    	intent.putExtra(FoodDetailActivity.DATA_FOOD_ID_KEY, id);
		    	
		        startActivityForResult(intent, FOOD_ENTRY_RESULT_CODE);
				
			}
		});
        
        
        //c.close();
    }
	
	protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == FOOD_ENTRY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                finish(); 
            }
        }
    }
	
	private AlertDialog buildFilterDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.food_list_filter_dialog, null);
		
		final Spinner manSpinner = (Spinner)layout.findViewById(R.id.listFilterDialog_manufacturer_Spinner);
		Cursor manCursor = mDbHelper.getManfacturersForFoodWithName(mQueryTerm);
		String[] from = new String[]{"manufacturerName"};
		int[] to = new int[] { android.R.id.text1 };
		
		SimpleCursorAdapter manAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, 
				manCursor, from, to);//DailyEntriesListViewAdapter(this, entries_cursor);
		manAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		manSpinner.setAdapter(manAdapter);
		
		//===================
/*		Spinner food_class_spinner = (Spinner)layout.findViewById(R.id.listFilterDialog_foodClass_Spinner);
		ArrayAdapter<FoodClass> class_adapter = new ArrayAdapter<FoodClass>(this, android.R.layout.simple_spinner_item, FoodClass.values());
		class_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		food_class_spinner.setAdapter(class_adapter);
	*/	
		alert.setView(layout);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});*/
		
		alert.setCancelable(true);
		return alert.create();
		
	}
    
    /**
     * This method is called when the user context-clicks a note in the list. NotesList registers
     * itself as the handler for context menus in its ListView (this is done in onCreate()).
     *
     * The only available options are COPY and DELETE.
     *
     * Context-click is equivalent to long-press.
     *
     * @param menu A ContexMenu object to which items should be added.
     * @param view The View for which the context menu is being constructed.
     * @param menuInfo Data associated with view.
     * @throws ClassCastException
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {

        // The data from the menu item.
        AdapterView.AdapterContextMenuInfo info;

        // Tries to get the position of the item in the ListView that was long-pressed.
        try {
            // Casts the incoming data object into the type for AdapterView objects.
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            // If the menu object can't be cast, logs an error.
            //Log.e(TAG, "bad menuInfo", e);
            return;
        }

        /*
         * Gets the data associated with the item at the selected position. getItem() returns
         * whatever the backing adapter of the ListView has associated with the item. In NotesList,
         * the adapter associated all of the data for a note with its list item. As a result,
         * getItem() returns that data as a Cursor.
         */
        Cursor cursor = (Cursor) myListAdapter.getItem(info.position);

        // If the cursor is empty, then for some reason the adapter can't get the data from the
        // provider, so returns null to the caller.
        if (cursor == null) {
            // For some reason the requested item isn't available, do nothing
            return;
        }

        // Inflate menu from XML resource
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.food_list_context_menu, menu);
/*
        // Append to the
        // menu items for any other activities that can do stuff with it
        // as well.  This does a query on the system for any activities that
        // implement the ALTERNATIVE_ACTION for our data, adding a menu item
        // for each one that is found.
        Intent intent = new Intent(null, Uri.withAppendedPath(getIntent().getData(), 
                                        Integer.toString((int) info.id) ));
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
                new ComponentName(this, NotesList.class), null, intent, 0, null);
                */
    }
    

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.food_list_context_menu, menu);
	        // Invoke the Register activity
	        //menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
	        
	        return true;	
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.filter_results_menu_item:
			showDialog(DIALOG_FILTER_RESULTS);
			//mFilterDialog.show();
			return true; 
			//break; 
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

	
	
	private void doFilter(long manId){
		fillData(mDbHelper.getFoodsWithNameAndManId(mQueryTerm, manId));
	}
    
	private void doMySearch(String query) {

		//mDbHelper.logAction(Action.FOOD_SEARCH, -1, -1, "food query", query);
		
		
	 	
		Toast.makeText(getApplicationContext(), query,
				Toast.LENGTH_SHORT).show();

		mSearchHistory.add(query);
		
		Cursor c = mDbHelper.getFoodsWithName(query);
		
		mDiaryHelper.logAction(Action.FOOD_SEARCH, -1, c.getCount(), "Food query; numResults", query);
		
		fillData(c);
	}
	
	private void doMySearch(final long locid,
			final Spinner food_class_spinner, final CheckBox useClass) {
		Toast.makeText(getApplicationContext(), mDialogQuery,
				Toast.LENGTH_SHORT).show();
	//	long id = food_class_spinner.getSelectedItemId();
		if (useClass.isChecked()){
			Object obj = food_class_spinner.getSelectedItem(); 
		//	System.out.println(obj);
		//	FoodClass cat = FoodClass.get((int)id);
			Cursor c = mDbHelper.getFoodsWithName(mDialogQuery, (FoodClass)obj);
			fillData(c);
		}
		else{
			Cursor c = mDbHelper.getFoodsWithName(mDialogQuery);
			fillData(c);
		}
	}
	
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_FILTER_RESULTS:
	    	dialog = buildFilterDialog();
	    	break; 
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	protected void onPrepareDialog(int id, Dialog dialog) {
	    
	    switch(id) {
	    case DIALOG_FILTER_RESULTS:
	    	// dialog = buildFilterDialog();
	    	// If query has changed, change the spinner. 
	    	break; 
	    //case DIALOG_GET_FOOD_QUERY:
	        // do the work to define the game over Dialog
	    	
	        //break;
	    default:
	        dialog = null;
	    }
	    
	}
	
	/*
	private static final int TOKEN_GROUP = 0;
    private static final int TOKEN_CHILD = 1;
    
    private static final class QueryHandler extends AsyncQueryHandler {
        private CursorTreeAdapter mAdapter;

        public QueryHandler(Context context, CursorTreeAdapter adapter) {
            super(context.getContentResolver());
            this.mAdapter = adapter;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            switch (token) {
            case TOKEN_GROUP:
                mAdapter.setGroupCursor(cursor);
                break;

            case TOKEN_CHILD:
                int groupPosition = (Integer) cookie;
                mAdapter.setChildrenCursor(groupPosition, cursor);
                break;
            }
        }
    }
    
    
    public class MyExpandableListAdapter extends SimpleCursorTreeAdapter {

        // Note that the constructor does not take a Cursor. This is done to avoid querying the 
        // database on the main thread.
        public MyExpandableListAdapter(Context context, int groupLayout,
                int childLayout, String[] groupFrom, int[] groupTo, String[] childrenFrom,
                int[] childrenTo) {

            super(context, null, groupLayout, groupFrom, groupTo, childLayout, childrenFrom,
                    childrenTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            // Given the group, we return a cursor for all the children within that group 

            // Return a cursor that points to this contact's phone numbers
            Uri.Builder builder = Contacts.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, groupCursor.getLong(GROUP_ID_COLUMN_INDEX));
            builder.appendEncodedPath(Contacts.Data.CONTENT_DIRECTORY);
            Uri phoneNumbersUri = builder.build();

            mQueryHandler.startQuery(TOKEN_CHILD, groupCursor.getPosition(), phoneNumbersUri, 
                    PHONE_NUMBER_PROJECTION, Phone.MIMETYPE + "=?", 
                    new String[] { Phone.CONTENT_ITEM_TYPE }, null);

            mQueryHandler.startQuery(TOKEN_CHILD, cookie, uri, projection, selection, selectionArgs, orderBy)
            return null;
        }
    }
	*/
}
