package edu.uwcse.pond.proto;

import java.util.Calendar;

import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.FoodDiaryTableHelper;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class RecentEntriesListActivity extends ListActivity {

	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		DiaryDbHelper myDiaryDbHelper = DiaryDbHelper.getDiaryDbHelper(this); 
		
		Calendar today = Calendar.getInstance();
		Calendar before = Calendar.getInstance(); 
		before.add(Calendar.DATE, -10);
		
		Cursor c = myDiaryDbHelper.getFoodEntriesForTimePeriod(before, today);
		
		startManagingCursor(c);

        String[] from = new String[] { FoodDiaryTableHelper.COL_FOODID, FoodDiaryTableHelper.COL_TIME_ENTERED };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        
        // Now create an array adapter and set it to display using our row
        ListAdapter myListAdapter =
            new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, c, from, to);
     
		setListAdapter(myListAdapter);
	}
	
}
