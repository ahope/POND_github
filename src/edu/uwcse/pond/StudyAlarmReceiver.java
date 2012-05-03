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
package edu.uwcse.pond;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.uwcse.pond.activities.OverviewActivity;
import edu.uwcse.pond.proto.R;

public class StudyAlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		// Toast.makeText(context, "Got it!", Toast.LENGTH_SHORT).show();

		final NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Set the icon, scrolling text and timestamp
		final Notification notification = new Notification(R.drawable.circle,
				"Please send in your data", System.currentTimeMillis());

		final Intent intent = new Intent(context, OverviewActivity.class);
		intent.putExtra(OverviewActivity.INTENT_SEND_DATA_KEY, true);
		final Bundle b = new Bundle();
		b.putBoolean(OverviewActivity.INTENT_SEND_DATA_KEY, true);

		final PendingIntent contentIntent = PendingIntent.getActivity(context,
				0, intent.putExtra("moodimg", R.drawable.circle),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(context, "POND: Send data", "dothis",
				contentIntent);

		// Send the notification.
		// We use a layout id because it is a unique number. We use it later to
		// cancel.
		mNotificationManager.notify(R.layout.overview_simple, notification);
	}

}
