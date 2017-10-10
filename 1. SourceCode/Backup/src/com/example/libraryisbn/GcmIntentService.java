package com.example.libraryisbn;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.libraryisbn.broadcast.*;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService{
	
	Context context;
	JSONObject json_noti;
	
	String bkID;
	String stuRegno;
	String bkName;
	String authorName;
	String issueDate;
	String renewDate;
	JSONArray regno;
	
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public static final String TAG = "GCM Demo";

	public GcmIntentService() {
		super("GcmIntentService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		Bundle extras = intent.getExtras();
		String msg = intent.getStringExtra("message");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		
		 if (!extras.isEmpty()) {
			 
			 if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
	                sendNotification("Deleted messages on server: " +
	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // This loop represents the service doing some work.
	                for (int i=0; i<5; i++) {
	                    Log.i(TAG, "Working... " + (i+1)
	                            + "/5 @ " + SystemClock.elapsedRealtime());
	                    try {
	                        Thread.sleep(500);
	                    } catch (InterruptedException e) {
	                    }
	                }
	                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
	                // Post notification of received message.
	                //sendNotification("Received: " + extras.toString());
	                sendNotification(msg);
	                Log.i(TAG, "Received: " + extras.toString());
	            }
	        }
		 GcmNBroadcastReceiver.completeWakefulIntent(intent);
	}
	private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        //String regnumber = null;
        
       Intent gcmintent = new Intent(GcmIntentService.this, NotificationMessage.class);
       gcmintent.putExtra("youRegno", msg);
       //startActivity(gcmintent);
       /*try {
			json_noti = new JSONObject(msg);
			regno = json_noti.optJSONArray(msg);
			JSONObject d = regno.getJSONObject(0);
			regnumber = d.getString("REGNO");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       gcmintent.putExtra("yourid",regnumber);*/
       //Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
       //gcmintent.putExtra("youRegno", msg);
       
//       try {
//			json_noti = new JSONObject(msg);
//			bkID = json_noti.getString("ID");
//			stuRegno = json_noti.getString("REGNO");
//			bkName = json_noti.getString("BOOKNAME");
//			authorName = json_noti.getString("AUTHOR");
//			issueDate = json_noti.getString("TIMESTAMP");
//			renewDate = json_noti.getString("RENEWDATE");
//
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//       
//       gcmintent.putExtra("book_list_id", bkID);
//       gcmintent.putExtra("student_id",stuRegno); 
//       gcmintent.putExtra("bookName", bkName);
//       gcmintent.putExtra("issueDate",issueDate); 
//       gcmintent.putExtra("renewDate",renewDate); 
//       gcmintent.putExtra("author",authorName); 
       
       PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		gcmintent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle("Renew Book")
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
