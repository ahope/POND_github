package edu.uwcse.pond.proto;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uwcse.pond.nutrition.Consts.PointComponent;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class StudyAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		//Toast.makeText(context, "Got it!", Toast.LENGTH_SHORT).show();
		
		NotificationManager mNotificationManager = 
				(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		
		// Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.circle, "Please send in your data",
                System.currentTimeMillis());
        
        Intent intent = new Intent(context, OverviewActivity.class);
        intent.putExtra(OverviewActivity.INTENT_SEND_DATA_KEY, true);
        Bundle b = new Bundle(); 
        b.putBoolean(OverviewActivity.INTENT_SEND_DATA_KEY, true);
        
        
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
               intent.putExtra("moodimg", R.drawable.circle),
                PendingIntent.FLAG_UPDATE_CURRENT);
        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, "POND: Send data",
                       "dothis", contentIntent);

        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        mNotificationManager.notify(R.layout.overview_simple, notification);
	}

}
