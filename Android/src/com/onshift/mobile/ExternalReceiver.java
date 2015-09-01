/*
 * Copyright 2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.onshift.mobile;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.*;
import com.onshift.mobile.models.PushNotificationMessage;
import com.onshift.mobile.models.PushNotificationMessage_v1;
import com.onshift.mobile.universal_login.UniversalLoginActivity;

@SuppressLint("NewApi")
public class ExternalReceiver extends BroadcastReceiver {

	public static NotificationManager myNotificationManager;
	public static final int NOTIFICATION_ID = 1;
	// old format message
	private PushNotificationMessage_v1 msgv1;
	// new format message
	private PushNotificationMessage msg;
	private static final String MESSAGETEMPLATE = " new messages";

	@Override
	public void onReceive(Context context, Intent intent) {
		myNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		int notificationCount = 1;

		Bundle extras = intent.getExtras();
		StringBuilder payload = new StringBuilder();
		StringBuilder messagePayload = new StringBuilder();
		StringBuilder message = new StringBuilder();

		Gson gson = new Gson();
		msgv1 = new PushNotificationMessage_v1();
		msg = new PushNotificationMessage();

		SharedPreferences settings = context.getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		String UserID = settings.getString("UserID", "");

		Boolean registrationmsg = false;
		Boolean displayNotification = false;

		if (extras != null) {
			for (String key : extras.keySet()) {
				if (key.equals("registration_id")) {
					Log.i("key: ", key);
					Log.i("value: ", extras.getString(key));
					payload.append(String.format("%s=%s", key,
							extras.getString(key)) + '\n');
					MainActivity.sendRegistrationToWebview(extras
							.getString(key));
					registrationmsg = true;
				}

				if (key.equals("error")) {
					registrationmsg = true;
				}

				if (extras.containsKey("message")) {
					if ((key.equals("message"))
							|| (key.equals("message_type_id"))
							|| (key.equals("shift_id")) || (key.equals("id"))
							|| (key.equals("recipient_id"))
							|| (key.equals("badge"))) {
						if (key.equals("message")) {
							message.append(String.format("%s",
									extras.getString(key)));
							msgv1.set_message(extras.getString(key));
						}

						if (key.equals("message_type_id")) {
							msgv1.set_messagetypeid(extras.getString(key));
						}

						if (key.equals("id")) {
							msgv1.set_messageid(extras.getString(key));
						}

						if (key.equals("shift_id")) {
							msgv1.set_shiftid(extras.getString(key));
						}

						if (key.equals("badge")) {
							msgv1.set_badge(extras.getString(key));
							try {
								notificationCount = Integer.parseInt(extras
										.getString(key));
							} catch (NumberFormatException nfe) {

							}
						}

						if (key.equals("recipient_id")) {
							if (extras.getString(key).equals(UserID)) {
								displayNotification = true;
							}
						}
					}
				}

				if (extras.containsKey("msg")) {
					if ((key.equals("msg")) || (key.equals("msg_type"))
							|| (key.equals("id")) || (key.equals("rid"))
							|| (key.equals("badge"))) {
						if (key.equals("msg")) {
							message.append(String.format("%s",
									extras.getString(key)));
							msg.set_message(extras.getString(key));
						}

						if (key.equals("msg_type")) {
							msg.set_msgtype(extras.getString(key));
						}

						if (key.equals("id")) {
							msg.set_messageid(extras.getString(key));
						}

						if (key.equals("badge")) {
							msg.set_badge(extras.getString(key));
							try {
								notificationCount = Integer.parseInt(extras
										.getString(key));
							} catch (NumberFormatException nfe) {

							}
						}

						if (key.equals("rid")) {
							if (extras.getString(key).equals(UserID)) {
								displayNotification = true;
							}
						}
					}
				}
			}

			if (msgv1.get_message() != null) {
				messagePayload.append(gson.toJson(msgv1));
			}

			if (msg.get_message() != null) {
				messagePayload.append(gson.toJson(msg));
			}

			try {
				if (messagePayload.toString() != null) {
					if (!registrationmsg) {
						if (displayNotification) {
							MainActivity
							.sendForegroundNotificationToWebview(messagePayload
									.toString());
						}
					}
				}
			} catch (Exception e) {
			}
		}

		if (notificationCount > 1) {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("OnShift Mobile")
			.setContentText(
					String.valueOf(notificationCount) + MESSAGETEMPLATE)
					.setDefaults(Notification.DEFAULT_VIBRATE);

			// Intent resultIntent = new Intent(context, MainActivity.class);
			Intent resultIntent = new Intent(context,
					UniversalLoginActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if (messagePayload.toString() != null) {
				resultIntent.putExtra("message", messagePayload.toString());
				editor.putString("PendingMessage", messagePayload.toString());
				editor.commit();
			}

			// The stack builder object will contain an artificial back stack
			// for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out
			// of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(UniversalLoginActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			if (displayNotification) {
				mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
			}
		} else {
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					context).setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("OnShift Mobile").setContentText(message)
					.setDefaults(Notification.DEFAULT_VIBRATE);

			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context,
					UniversalLoginActivity.class);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			if (messagePayload.toString() != null) {
				resultIntent.putExtra("message", messagePayload.toString());
				editor.putString("PendingMessage", messagePayload.toString());
				editor.commit();
			}
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(UniversalLoginActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			if (!registrationmsg) {
				if (displayNotification) {
					mNotificationManager.notify(NOTIFICATION_ID,
							mBuilder.build());
				}
			}
		}
	}
}
