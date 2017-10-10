package com.example.libraryisbn;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
//import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity implements OnClickListener  {
	
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
   // private static final String PROPERTY_APP_VERSION = "appVersion";
   // private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    
   // please enter your sender id
    String SENDER_ID = "140303152088";
    
    static final String TAG = "GCMDemo";
    //GoogleCloudMessaging gcm;
    //Context context;
    //public static String GCMID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
     	this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        
        Button b=(Button) findViewById(R.id.button1);
        b.setOnClickListener(this);
        
        /*context = getApplicationContext();
		if(checkPlayServices()){
			gcm = GoogleCloudMessaging.getInstance(this);
			GCMID = getRegistrationId(context);
			if(GCMID.isEmpty()){
				new RegisterBackground().execute();
			}
		}*/
        
        
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = intent.getStringExtra("SCAN_RESULT");
	           // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            Intent getBooks= new Intent(MainActivity.this, Load_Renew_Books.class);
	            getBooks.putExtra("yourid",contents);
	            startActivity(getBooks);
	        } else if (resultCode == RESULT_CANCELED) {
	        	TextView code= (TextView) findViewById(R.id.textView1);
	            code.setText("Scan it again Please!!!");
	        }
	    }
	   
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void onClick(View arg0) {
		if(arg0.getId()== R.id.button1)
		{
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
	        startActivityForResult(intent, 0);
			
		}
		
	}
	
	/*@Override
	protected void onResume(){
		super.onResume();
		checkPlayServices();
	}*/
	
	
/*class RegisterBackground extends AsyncTask<String,String,String>{
		
		protected void onPreExecute() {
	        super.onPreExecute();
	        pDialog = new ProgressDialog(MainActivity.this);
	        pDialog.setMessage("Registering. Please wait...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
	    }


		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			String msg = "";
			try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                GCMID = gcm.register(SENDER_ID);
                msg = "Dvice registered, registration ID=" + GCMID;
                Log.d("111", msg);
                // Persist the regID - no need to register again.
               storeRegistrationId(context, GCMID);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }
		
		@Override
        protected void onPostExecute(String msg) {
            pDialog.dismiss();
            
        }
		
		private void storeRegistrationId(Context context, String regId) {
		    final SharedPreferences prefs = getGCMPreferences(context);
		    int appVersion = getAppVersion(context);
		    Log.i(TAG, "Saving regId on app version " + appVersion);
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putString(PROPERTY_REG_ID, regId);
		    editor.putInt(PROPERTY_APP_VERSION, appVersion);
		    editor.commit();
		}
		
		}
		
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}*/
	
}
