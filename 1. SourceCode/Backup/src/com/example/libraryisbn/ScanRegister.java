/**
 * 
 */
package com.example.libraryisbn;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Ninad
 *
 */
public class ScanRegister extends Activity {
	
	private ConnectionDetector cd;
	private EditText mobile;
	private EditText email;
	private EditText appPass;
	private EditText confirmPass;
	private TextView Register;
	private static String REGNO;
	private String errorMSG;
	private ProgressDialog pDialog;
	private static final String STATUS = "status";
	private static final String MSG = "msg";
	private int status2;
	private int status3;
	private String msg;
	JSONParser jsonParser = new JSONParser();
	private RelativeLayout pLoad;
	private RelativeLayout mainDisp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scan_and_register_activity);
		
		cd = new ConnectionDetector(getApplicationContext());
		  if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
			  Toast.makeText(ScanRegister.this,"Oh Snap!Go Get Internet Connection first", Toast.LENGTH_SHORT).show();
	            // stop executing code by return
	            return;
	        }
		  
		  Register =(TextView)findViewById(R.id.textView3);
		  REGNO =(getIntent().getExtras().getString("yourid"));
		  
		    TextView Regnumber= (TextView) findViewById(R.id.textView1);
			Regnumber.setText(REGNO);
			
			 pLoad = (RelativeLayout) findViewById(R.id.LoadingProgress);
		     mainDisp = (RelativeLayout) findViewById(R.id.RLScanRegister);
		        
		     pLoad.setVisibility(View.GONE);
		  
		  registerViews();
		  
		  
		  Register.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					new CheckuserLib().execute();
					
				}
			});
		  
	}   
	
	
      class CheckuserLib extends AsyncTask<String, String, String> {
		
		protected void onPreExecute() {
			super.onPreExecute();
			
			pDialog = new ProgressDialog(ScanRegister.this);
			
			if(checkValidation() == false){		
				Toast.makeText(ScanRegister.this,errorMSG, Toast.LENGTH_SHORT).show();	
		    }
		    else{			    	
				Toast.makeText(ScanRegister.this,"Checking credientials...", Toast.LENGTH_SHORT).show();
				mainDisp.setVisibility(View.GONE);
				pLoad.setVisibility(View.VISIBLE);
		    }			
			
		}
		
        protected String doInBackground(String... args) {
			
	        	String regno = REGNO;
	        	String mode_checkuser = "CHK_LIB_DB";
				
			    //Check if student is registered to the library or not

				// Building Parameters
				List<NameValuePair> paramsLib = new ArrayList<NameValuePair>();
				paramsLib.add(new BasicNameValuePair("mode", mode_checkuser));
				paramsLib.add(new BasicNameValuePair("regid", regno ));

				// getting JSON string from URL
				JSONObject jsonLib = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", paramsLib);
				
				try {
					
					status2 = jsonLib.getInt(STATUS);
					Log.d("status2: ", "> " + jsonLib);
					msg = jsonLib.getString(MSG);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//If student is registered to library then send data to library database.
				if(status2 == 1){
	  				
	  				String mode_sendstuinfo = "sendStudentInfo";
	  			    String mobileNo = mobile.getText().toString() ;
	  			    String emailId = email.getText().toString();
	  			    String pwd=appPass.getText().toString();
					String cpwd=confirmPass.getText().toString();
	  	         
	  				// Building Parameters
	  				List<NameValuePair> paramsStu = new ArrayList<NameValuePair>();
	  				paramsStu.add(new BasicNameValuePair("mode", mode_sendstuinfo));
	  				paramsStu.add(new BasicNameValuePair("regid", regno));
	  				paramsStu.add(new BasicNameValuePair("mobile", mobileNo ));
	  				paramsStu.add(new BasicNameValuePair("email", emailId));
	  				paramsStu.add(new BasicNameValuePair("pwd", pwd));
	  				paramsStu.add(new BasicNameValuePair("confpwd", cpwd));

	  				// getting JSON string from URL
	  				JSONObject jsonStu = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", paramsStu);
	  				
	  				try {
	  					
	  					status3 = jsonStu.getInt(STATUS);
	  					msg = jsonStu.getString(MSG);
	  					
	  				} catch (JSONException e) {
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  			}

			return null;
	}
        
        protected void onPostExecute(String file_url) {
			// dismiss the dialog 
        	if(status2==0){
        		Toast.makeText(ScanRegister.this, msg, Toast.LENGTH_SHORT).show();  	
	        	Toast.makeText(ScanRegister.this,"Redirecting to Login page.", Toast.LENGTH_SHORT).show();
				pLoad.setVisibility(View.GONE);
	        	
	    		finish();
	    		Intent i = new Intent(getApplicationContext(), AppLoginOrRegister.class);
	    		startActivity(i);
      	
        	}
        	
        	else if( status3==0) {
        		
        		Toast.makeText(ScanRegister.this, msg, Toast.LENGTH_SHORT).show();
				mainDisp.setVisibility(View.VISIBLE);
				pLoad.setVisibility(View.GONE);
        	}
        	
        	if(status3==1){
        		Toast.makeText(ScanRegister.this, msg, Toast.LENGTH_SHORT).show();
				pLoad.setVisibility(View.GONE);
        		
	    				finish();
	    				Intent i = new Intent(getApplicationContext(),Load_Renew_Books.class);
	    				startActivity(i); 		
        	}
        }
}
      
      
        private void registerViews() {
        
        // TextWatcher would let us check validation error on the fly
        
        //Check Mobile number
        mobile.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isPhoneNumber(mobile, true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
    
        //Check Email ID
        email.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isEmailAddress(email, true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
 
        //Check app password
        appPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(appPass);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        //Check confirm app password
        confirmPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(confirmPass);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        }
  	
	private boolean checkValidation() {
        boolean ret = true;
           
        //validation returns false if field is empty.
        if ((Validation.isPhoneNumber(mobile, true)) &&
         (Validation.isEmailAddress(email, true)) &&
         (Validation.hasText(appPass)) &&
         (Validation.hasText(confirmPass))){
        	
        	if((appPass.getText().toString().equals(confirmPass.getText().toString())))
        			ret = true;
        		
        	else{
        		errorMSG = "APP Passwords do not match";
        	    ret = false;
            	}
        }
           else{
    		errorMSG = "Fields Empty";
    		ret = false;
    	}
    
    return ret;
        	
	}
}
