package edu.uwcse.pond.proto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.uwcse.pond.diary.ActionLogDbHelper;
import edu.uwcse.pond.diary.DiaryDbHelper;
import edu.uwcse.pond.diary.DiaryEntry;
import edu.uwcse.pond.diary.LocationDiaryTableHelper;
import edu.uwcse.pond.diary.ActionLogDbHelper.Action;
import edu.uwcse.pond.nutrition.Consts;
import edu.uwcse.pond.nutrition.Consts.PointComponent;
import edu.uwcse.pond.nutrition.DataBaseHelper;
import edu.uwcse.pond.nutrition.NutritionDbHelper;
import android.R.anim;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.SQLException;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class OverviewActivity  extends Activity implements LocationListener{
	
	public static final int FOOD_QUERY_ID = Menu.FIRST;
	
	public static final String INTENT_SEND_DATA_KEY = "SendData"; 
	
	public static final String PREFS_INSITU_STUDY = "PondInSituStudyPrefs"; 
	private static final String PREFS_INSITU_STUDY_PPT_ID_KEY = "inSituPptId";
	public static final String PREFS_INSITU_STUDY_CAN_CHANGE_GOALS = "canChangeGoals";
	private static final String PREFS_INSITU_STUDY_SEND_DATE_1_KEY = "sendDate1"; 
	private static final String PREFS_INSITU_STUDY_SEND_DATE_2_KEY = "sendDate2"; 
	private static final String PREFS_INSITU_STUDY_SEND_DATE_3_KEY = "sendDate3"; 
	private static final String PREFS_INSITU_STUDY_SENT_1_KEY = "sent1"; 
	private static final String PREFS_INSITU_STUDY_SENT_2_KEY = "sent2"; 
	private static final String PREFS_INSITU_STUDY_SENT_3_KEY = "sent3"; 
	
	protected DiaryDbHelper mDiaryHelper;
	
	private TimerTask mStopLocationTask; 
	private Timer mCheckLocationTimer; 
	final Handler handler = new Handler(); 
	
	private DiaryEntry mCurEntry = new DiaryEntry();
	
	private Map<Consts.PointComponent,Integer> mGoalMap;
	private Map<Consts.PointComponent,Double> mEntryMap;
	
	private OverviewDayListViewAdapter mOverviewListViewAdapter; 
	
	private ListView mOverviewListView;
	

	private LocationManager mLocationManager;
	private Spinner mLocationSpinner; 
	private SimpleCursorAdapter mLocationAdapter;
	
	private static final int DIALOG_ID_START_STUDY = 1;
	private static final int DIALOG_ID_SEND_DATA = 2;
    /**
     * Begins the activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_simple);
       
       /*
        // Register a receiver to provide register/unregister notifications
       // registerReceiver(mUpdateUIReceiver, new IntentFilter(Util.UPDATE_UI_INTENT));
    			NutritionDbHelper myDbHelper = new NutritionDbHelper(this);
    	 
    	        try {
    	 
    	        	myDbHelper.createDataBase();
    	 
    	 	} catch (IOException ioe) {
    	 		System.err.print(ioe.getStackTrace());
    	 		throw new Error("Unable to create database");
    	 		
    	 	}
*/
     //   NutritionDbHelper nutDbHelper = new NutritionDbHelper(this);
    	        DataBaseHelper myDbHelper;
    	        myDbHelper = new DataBaseHelper(this);
    	 
    	        try {
    	 
    	        	myDbHelper.createDataBase();
    	 
    	 	} catch (IOException ioe) {
    	 
    	 		throw new Error("Unable to create database");
    	 
    	 	}
    	        myDbHelper.close();
    	 /*
    	 	try {
    	 
    	 		myDbHelper.openDataBase();
    	 
    	 	}catch(SQLException sqle){
    	 
    	 		throw sqle;
    	 
    	 	}*/
    	 	
    	 	mDiaryHelper = DiaryDbHelper.getDiaryDbHelper(getApplicationContext());
    	 	mDiaryHelper.logAction(Action.START_APP, -1, -1, "Starting app", "");

    	 	// Location Spinner
    	 	mLocationSpinner = (Spinner)findViewById(R.id.overview_location_Spinner);
    	 	Cursor entries_cursor = mDiaryHelper.getLocationEntries(); 
    	 	
    	 	String[] from = new String[]{LocationDiaryTableHelper.COL_LOC_NAME};
    	 	int[] to = new int[]{android.R.id.text1};//R.id.location_name_TextView};
            
    	 	mLocationAdapter = new PrettyCursorAdapter(this, android.R.layout.simple_spinner_item, 
    				entries_cursor, from, to);//DailyEntriesListViewAdapter(this, entries_cursor);
    	 	mLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		
    		mLocationSpinner.setAdapter(mLocationAdapter);
    		
    		mLocationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					mDiaryHelper.setNewLocationId(arg3);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
    		
    	 	

    		// Turn on location listening
			// Acquire a reference to the system Location Manager
			mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
			// Register the listener with the Location Manager to receive location updates
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			doLocationScan();
			
			Button addFoodButton = (Button)findViewById(R.id.add_food_button);
			addFoodButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(OverviewActivity.this, FoodResults2ListActivity.class);
			    	
			    	startActivity(intent);
				}
			});
			
			if (savedInstanceState != null && 
					savedInstanceState.containsKey(INTENT_SEND_DATA_KEY)){
				if (savedInstanceState.getBoolean(INTENT_SEND_DATA_KEY)){
					sendDataViaEmail();
				}
			}
			
			checkOnSentData();
    }
    
	public void doLocationScan() {
		mCheckLocationTimer = new Timer();

		mStopLocationTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						// Stop listening for location updates
						stopLocationScan(); 
					}
				});
			}
		};

		mCheckLocationTimer.schedule(mStopLocationTask, 30000, 30000);

	}

	public void stopLocationScan() {

		if (mStopLocationTask != null) {
			mStopLocationTask.cancel();
			mLocationManager.removeUpdates(this);
		}

	}
    
    // Data was just sent; check if it needed to be remembered. 
    private void rememberSentData(){
    	SharedPreferences settings = getSharedPreferences(PREFS_INSITU_STUDY, 0);
    	
    	
    	boolean sent1 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_1_KEY, false);
    	if (!sent1){
    		// Check to see if it should be! 
    		String sendDate1 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_1_KEY, "");
    		if (sendDate1.length() > 0){
        		Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
                if (curDate.compareTo(sendDate1) > 0){
                	// Remember it's sent.  
                	SharedPreferences.Editor editor = settings.edit();
                	editor.putBoolean(PREFS_INSITU_STUDY_SENT_1_KEY, true);
                	editor.commit(); 
                	
                }
                return;
        	}
    	}else{
    		boolean sent2 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_2_KEY, false);
    		if (!sent2){
    			// Check to see if it should be! 
        		String sendDate2 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_2_KEY, "");
        		if (sendDate2.length() > 0){
            		Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
                    if (curDate.compareTo(sendDate2) > 0){
                    	// Remember it's sent.  
                    	SharedPreferences.Editor editor = settings.edit();
                    	editor.putBoolean(PREFS_INSITU_STUDY_SENT_2_KEY, true);
                    	editor.commit(); 
                    }
                    return;
            	}
    		}else{
    			boolean sent3 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_3_KEY, false);
    			if (!sent3){
    				// Check to see if it should be! 
    	    		String sendDate3 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_3_KEY, "");
    	    		if (sendDate3.length() > 0){
    	        		Calendar calendar = Calendar.getInstance();
    	                calendar.setTimeInMillis(System.currentTimeMillis());
    	                String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
    	                if (curDate.compareTo(sendDate3) > 0){
    	                	// Remember it's sent.  
    	                	SharedPreferences.Editor editor = settings.edit();
    	                	editor.clear(); // Clear everything, so the study is no longer "running". 
    	                	editor.commit(); 
    	                	
    	                }
    	                return;
    	        	}
    			}
    		}
    	}
    	
    	
    }
    
    private void checkOnSentData(){
    	SharedPreferences settings = getSharedPreferences(PREFS_INSITU_STUDY, 0);
    	
    	
    	boolean sent1 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_1_KEY, false);
    	if (!sent1){
    		// Check to see if it should be! 
    		String sendDate1 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_1_KEY, "");
    		if (sendDate1.length() > 0){
        		Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
                if (curDate.compareTo(sendDate1) > 0){
                	// Try to send the data; 
                	showDialog(DIALOG_ID_SEND_DATA);
                	
                }
                return;
        	}
    	}else{
    		boolean sent2 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_2_KEY, false);
    		if (!sent2){
    			// Check to see if it should be! 
        		String sendDate2 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_2_KEY, "");
        		if (sendDate2.length() > 0){
            		Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
                    if (curDate.compareTo(sendDate2) > 0){
                    	// Try to send the data; 
                    	
                    	showDialog(DIALOG_ID_SEND_DATA);
                    }
                    return;
            	}
    		}else{
    			boolean sent3 = settings.getBoolean(PREFS_INSITU_STUDY_SENT_3_KEY, false);
    			if (!sent3){
    				// Check to see if it should be! 
    	    		String sendDate3 = settings.getString(PREFS_INSITU_STUDY_SEND_DATE_3_KEY, "");
    	    		if (sendDate3.length() > 0){
    	        		Calendar calendar = Calendar.getInstance();
    	                calendar.setTimeInMillis(System.currentTimeMillis());
    	                String curDate = DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime());
    	                if (curDate.compareTo(sendDate3) > 0){
    	                	// Try to send the data; 
    	                	showDialog(DIALOG_ID_SEND_DATA);
    	                }
    	                return;
    	        	}
    			}
    		}
    	}
    	
    	
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	if (intent.getExtras() != null && 
    			intent.getExtras().containsKey(INTENT_SEND_DATA_KEY)){
			if (intent.getExtras().getBoolean(INTENT_SEND_DATA_KEY)){
				sendDataViaEmail();
			}
		}
    };
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();

	 	populateGoalView();
		
    }
    
    @Override
    protected void onRestart() {
    	// TODO Auto-generated method stub
    	super.onRestart();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }

    
    
	private void populateGoalView() {
		mGoalMap = mDiaryHelper.getMostRecentGoalAsMap();
	 	mEntryMap = getSummedPointsEntry();
	 	mOverviewListView = (ListView)findViewById(R.id.overview_ListView);
		mOverviewListViewAdapter = new OverviewDayListViewAdapter(this, mGoalMap, mEntryMap);
		mOverviewListView.setAdapter(mOverviewListViewAdapter);
	}
	
	protected Map<Consts.PointComponent,Double> getSummedPointsEntry(){
		return mDiaryHelper.getSummedPointsEntryForDayAsMap(new Date());
	}
    
    public void updateComponentPlusOne(PointComponent pc){
    	mDiaryHelper.createNewPointsEntryPlusOne(pc);
    }
    
    public void updateComponentPlusHalf(PointComponent pc){
    	mDiaryHelper.createNewPointsEntryPlusHalf(pc);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	SharedPreferences settings = getSharedPreferences(PREFS_INSITU_STUDY, 0);
    	String id = settings.getString(PREFS_INSITU_STUDY_PPT_ID_KEY, "");
    	if(id.length() > 0){
    		menu.getItem(7).setEnabled(false);
    		
    	}
    	return true; 
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		case R.id.do_food_query_menuItem:
		/*	Intent i = new Intent(OverviewActivity.this, FoodResults2ListActivity.class);
	    	Bundle myExtras = i.getExtras();
	    	if (myExtras == null){
	    		myExtras = new Bundle();
	    	}
	  //  	extras.putLong(LOCATION_ID, mLocationSpinner.getSelectedItemId());
	  //  	intent.putExtra(LOCATION_ID,  mLocationSpinner.getSelectedItemId());
	        startActivity(i);
	    */    
	       
			onSearchRequested();
			
			return true; 
			
		case R.id.show_goals_menuItem:
			Intent i2 = new Intent(OverviewActivity.this, GoalEdityActivity.class);

	        startActivity(i2);
	        
			return true; 
			
		case R.id.show_locations_menuItem:
			Intent i3 = new Intent(OverviewActivity.this, LocationActivity.class);

	        startActivity(i3);
	        
			return true; 
		
		case R.id.show_entry_history_menuItem:
			Intent intent = new Intent(OverviewActivity.this, DailyDetailActivity.class);
	    	Bundle extras = intent.getExtras();
	    	if (extras == null){
	    		extras = new Bundle();
	    	}
	    	//extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY, PointComponent.FATS.getPtDbColName());
	    	intent.putExtra(DailyDetailActivity.DATA_WHICH_COMPONENT_INT_KEY, PointComponent.ALL.getPtDbColName());
	    	startActivity(intent);
	        return true; 
		
		case R.id.history_overview_menuItem: 
			Intent intent2 = new Intent(OverviewActivity.this, WeeklySummaryActivity.class);
	    	Bundle extras2 = intent2.getExtras();
	    	if (extras2 == null){
	    		extras2 = new Bundle();
	    	}
	    	//extras.putString(EntriesListActivity.DATA_WHICH_COMPONENT_KEY, PointComponent.FATS.getPtDbColName());
	    	intent2.putExtra(DailyDetailActivity.DATA_WHICH_COMPONENT_INT_KEY, PointComponent.VEGGIE_GREEN.getPtDbColName());
	    	startActivity(intent2);
	        
			return true; 
	        
	/*	case R.id.copy_data:
			 try {
				copyDataBase("POND_data");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		*/
		case R.id.send_data_menuItem:
			sendDataViaEmail();
			return true; 
		case R.id.start_study_menuItem:
			// Launch a dialog
			showDialog(DIALOG_ID_START_STUDY);
			
			return true; 
		case R.id.create_point_entry_menuItem: 
			Intent intent3 = new Intent(OverviewActivity.this, CreateCustomPointFoodActivity.class);
	    	startActivity(intent3);
	        
			return true; 
		}
		return super.onOptionsItemSelected(item); 
	}

	private void sendDataViaEmail() {
		try {
			SharedPreferences settings = getSharedPreferences(PREFS_INSITU_STUDY, 0);
			String pptId = settings.getString(PREFS_INSITU_STUDY_PPT_ID_KEY, "xx");
			
			SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
			String filename = "POND_"+ pptId + date_format.format(new Date());
			String full_filename = copyDataBase(filename);
			
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
		    sendIntent.setType("application/x-pond-sqlite");
		    sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Data file");
		    sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"aha@cs.washington.edu"});
		    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:" + full_filename));
		    sendIntent.putExtra(Intent.EXTRA_TEXT, "This weeks email. ");
		    startActivity(Intent.createChooser(sendIntent, "Email:"));
		    rememberSentData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	 public static String copyDataBase(String filename) throws IOException{
		    String DB_PATH = "/data/data/edu.uwcse.pond.proto/databases/";
		    
		    String DB_NAME = "PondDiary";
		    
		    //String filename = "Pond_data";
		    
		    String full_filename = ""; 
		    
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		    	File path = Environment.getExternalStoragePublicDirectory(
		                Environment.DIRECTORY_DOWNLOADS);

		    	if (!path.exists()){
		    		path.createNewFile();
		    	}
		    	
		    	
		    	// Path to the just created empty db
		    	String outFileName = DB_PATH + DB_NAME;
		 
		    	//Open the empty db as the output stream
		    	InputStream myInput = new FileInputStream(outFileName);
		    	

		    	//Open the empty db as the output stream
		    	File out = new File(path, filename);
		    	if (!out.exists()){
		    		out.createNewFile();
		    	}
		    	OutputStream myOutput = new FileOutputStream(out);
		    	

		    	//transfer bytes from the inputfile to the outputfile
		    	byte[] buffer = new byte[1024];
		    	int length;
		    	while ((length = myInput.read(buffer))>0){
		    		myOutput.write(buffer, 0, length);
		    	}
		 
		    	//Close the streams
		    	myOutput.flush();
		    	myOutput.close();
		    	myInput.close();
		    	
		    	full_filename = out.getAbsolutePath();
		    	
		    }
		    
		    return full_filename;
		    /*
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 */
	    }
	 
    
	@Override
	public void onLocationChanged(Location location) {

		
		long locId = mDiaryHelper.getClosestLocation(location);
		for(int i=0; i<mLocationAdapter.getCount(); i++){
			if (mLocationAdapter.getItemId(i) == locId){
				mLocationSpinner.setSelection(i);
			}
		}
		//System.out.println("location: " + blah); 
		mLocationManager.removeUpdates(this);
	}



	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mLocationManager.removeUpdates(this);
		mDiaryHelper.doneWithDb();
	}
    
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
	    case DIALOG_ID_START_STUDY:
	    	dialog = buildStartInSituStudyDialog();
	    	break; 
	    case DIALOG_ID_SEND_DATA:
	    	dialog = buildSendInSituDataDialog();
	    	break; 
	    default:
	        dialog = null;
	    }
	    return dialog;
	}
	
	
	
	private AlertDialog buildSendInSituDataDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("It seems you haven't sent your data yet. Send now?  ")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               sendDataViaEmail(); 
		               dialog.cancel();
		           }
		       })
		       .setNegativeButton("Later", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		return alert; 
	}

	
	private AlertDialog buildStartInSituStudyDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(this);
		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.start_study_dialog, null);
		
		final EditText whole_num = (EditText)layout.findViewById(R.id.editText1);
		final CheckBox goals_CheckBox = (CheckBox)layout.findViewById(R.id.checkBox1);
		
		alert.setView(layout);
		
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Get the ppt id
				// Open preference editor
				// Save pptId & alarm time
				SharedPreferences settings = getSharedPreferences(PREFS_INSITU_STUDY, 0);
   			    SharedPreferences.Editor editor = settings.edit();
   			    
				// Start the alarmService
				Intent intent = new Intent(OverviewActivity.this, StudyAlarmReceiver.class);
	            PendingIntent sender1 = PendingIntent.getBroadcast(OverviewActivity.this,
	                    0, intent, 0);
	            PendingIntent sender2 = PendingIntent.getBroadcast(OverviewActivity.this,
	                    1, intent, 0);
	            PendingIntent sender3 = PendingIntent.getBroadcast(OverviewActivity.this,
	                    2, intent, 0);
	            
	         // We want the alarm to go off 30 seconds from now.
	            Calendar calendar = Calendar.getInstance();
	            calendar.setTimeInMillis(System.currentTimeMillis());
	            calendar.add(Calendar.DATE, 8);
//	            calendar.add(Calendar.MINUTE, 5);
	            // Schedule the alarm!
	            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender1);
   			    editor.putString(PREFS_INSITU_STUDY_SEND_DATE_1_KEY, 
   			    		DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime())); 
   			    editor.putBoolean(PREFS_INSITU_STUDY_SENT_1_KEY, false);

	            calendar.add(Calendar.DATE, 15);
//	            calendar.add(Calendar.MINUTE, 5);
	            // Schedule the alarm!
	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender2);
	            editor.putString(PREFS_INSITU_STUDY_SEND_DATE_2_KEY, 
   			    		DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime())); 
	            editor.putBoolean(PREFS_INSITU_STUDY_SENT_2_KEY, false);

	            calendar.add(Calendar.DATE, 22);
//	            calendar.add(Calendar.MINUTE, 5);
	            // Schedule the alarm!
	            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender3);
	            editor.putString(PREFS_INSITU_STUDY_SEND_DATE_3_KEY, 
   			    		DiaryDbHelper.DB_DATE_STORE_FORMAT.format(calendar.getTime())); 
	            editor.putBoolean(PREFS_INSITU_STUDY_SENT_3_KEY, false);
	            
   			    editor.putString(PREFS_INSITU_STUDY_PPT_ID_KEY, whole_num.getText().toString()); 
   			    editor.putBoolean(PREFS_INSITU_STUDY_CAN_CHANGE_GOALS, goals_CheckBox.isChecked());

   			    editor.commit();
   			    
   			    mDiaryHelper.logAction(Action.STUDY, -1, 15, "Starting inSitu study", whole_num.getText().toString());
			}
		});
		
		alert.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		
		alert.setCancelable(true);
		return alert.create();
		
	}

	
	
}
