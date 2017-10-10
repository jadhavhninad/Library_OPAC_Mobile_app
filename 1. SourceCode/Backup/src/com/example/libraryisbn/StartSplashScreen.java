/**
 * 
 */
package com.example.libraryisbn;

import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
/**
 * @author Ninad
 *
 */
public class StartSplashScreen extends Activity {

    private Thread mSplashThread;
    private SessionManagement session;
    public static final String EXTRA_MESSAGE = "message"; 
	public static final String PROPERTY_REG_ID = "registration_id"; 
	private String SENDER_ID = "140303152088";
	static final String TAG = "GCMDemo";
    GoogleCloudMessaging gcm;
    TextView mDisplay;
    public static String GCMregid;
    Context context;
    JSONParser jsonParser = new JSONParser();
    int success;
	
	public void onAttachedToWindow() {
			super.onAttachedToWindow();
			Window window = getWindow();
			window.setFormat(PixelFormat.RGBA_8888);
		}
    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
        StartAnimations();
        
        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(StartSplashScreen.this);
        
        
        //getting gcmid from shared preferences
        GCMregid = getRegistrationId(context);
        
		if(GCMregid.isEmpty()){
			new RegisterBackground().execute();
		}
		
        mSplashThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        wait(4000);
                    }
                } catch (InterruptedException ex) {
                }
                
                session = new SessionManagement(context);
                
                //session.checkLogin();
                if(!session.isLoggedIn()){
                	Intent i = new Intent(getApplicationContext(), AppLoginOrRegister.class);
                	startActivity(i);
                	finish();
                }
                
                else{
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();
                 
                // name
                String Regno = user.get(SessionManagement.KEY_REGNO);
                finish();
                Intent getBooks = new Intent(StartSplashScreen.this, Load_Renew_Books.class);
                getBooks.putExtra("yourid",Regno);
	            startActivity(getBooks);
                }
            }
        };
        
        mSplashThread.start();
    }
	
	@Override
	protected void onResume(){
		super.onResume();
	}
    
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        
        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);
       
    }
    
    class RegisterBackground extends AsyncTask<String,String,String>{
		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String msg = "";
			try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
       		
                GCMregid = gcm.register(SENDER_ID);
                msg = "Device registered, registration ID=" + GCMregid;
                storeRegistrationId(context, GCMregid);
                Log.d("111", msg);
            } 
			catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }
		
		private void storeRegistrationId(Context context, String regId) {
		    final SharedPreferences prefs = getGCMPreferences(context);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString(PROPERTY_REG_ID, regId);
		    editor.commit();
		}
		
    } 

    private String getRegistrationId(Context context) {
    	final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    return registrationId;
    }
    
    	private SharedPreferences getGCMPreferences(Context context) {
	    return getSharedPreferences(StartSplashScreen.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
}

