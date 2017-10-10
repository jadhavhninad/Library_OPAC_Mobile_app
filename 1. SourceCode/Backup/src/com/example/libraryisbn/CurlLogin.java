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
public class CurlLogin extends Activity {
	
	private EditText regnum;
	private EditText evarsityPass;
	private EditText mobile;
	private EditText email;
	private EditText appPass;
	private EditText confirmPass;
    
	JSONParser jsonParser = new JSONParser();
	private static final String STATUS = "status";
	private static final String MSG = "msg";
	private static String errorMSG ;
	private int  status1 = 0;
	private int  status2 = 0;
	private int  status3 = 0;
	private String msg = "";
	private ConnectionDetector cd;
	private RelativeLayout pLoad;
	private RelativeLayout mainDisp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		cd = new ConnectionDetector(getApplicationContext());
		  if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
			  Toast.makeText(CurlLogin.this,"Oh Snap!Go Get a Connected first", Toast.LENGTH_SHORT).show();
	            // stop executing code by return
	            return;
	        }
		
		regnum=(EditText)findViewById(R.id.editText1);
		evarsityPass=(EditText)findViewById(R.id.editText2);
		mobile = (EditText)findViewById(R.id.editText5);
		email = (EditText)findViewById(R.id.editText6);
		appPass = (EditText)findViewById(R.id.editText3);
		confirmPass = (EditText)findViewById(R.id.editText4);
		TextView Register=(TextView)findViewById(R.id.textView3);
		status1 = 0;
		pLoad = (RelativeLayout) findViewById(R.id.LoadingProgress);
        mainDisp = (RelativeLayout) findViewById(R.id.RLcurlLogin);
		        
		//Form Validation function
		registerViews();	
		
        Register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    
				new Checkuser().execute();			
			}
		});
	}
		 
	
	
	class Checkuser extends AsyncTask<String, String, String> {
		
		protected void onPreExecute() {
			super.onPreExecute();			
			if(checkValidation() == false){
        		Toast.makeText(getApplicationContext(),errorMSG, Toast.LENGTH_SHORT).show();
        	}
			
			else{
				Toast.makeText(CurlLogin.this,"Checking Credientials...", Toast.LENGTH_SHORT).show();
				mainDisp.setVisibility(View.GONE);
				pLoad.setVisibility(View.VISIBLE);
			}
		}
		
        protected String doInBackground(String... args) {
			
        	String regno = regnum.getText().toString();
			String password = evarsityPass.getText().toString();
            String mode_checkuser = "CHECK_USER";
            
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mode", mode_checkuser));
			params.add(new BasicNameValuePair("regid", regno));
			params.add(new BasicNameValuePair("pass", password));

			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", params);
			
			try {
				
				status1 = json.getInt(STATUS);
				msg = json.getString(MSG);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//If Authentication success then check if student is registerd to the library or not
			if(status1 == 1)
			{
	            mode_checkuser = "CHK_LIB_DB";
	            
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
				
			}
			
			if(status2 == 1){
				
				String mode_sendstuinfo = "sendStudentInfo";
			    String mobileNo = mobile.getText().toString() ;
			    String emailId = email.getText().toString();
			    String pass = appPass.getText().toString();
			    String cpass = confirmPass.getText().toString();
	         
				// Building Parameters
				List<NameValuePair> paramsStu = new ArrayList<NameValuePair>();
				paramsStu.add(new BasicNameValuePair("mode", mode_sendstuinfo));
				paramsStu.add(new BasicNameValuePair("regid", regno));
				paramsStu.add(new BasicNameValuePair("mobile", mobileNo ));
				paramsStu.add(new BasicNameValuePair("email", emailId));
				paramsStu.add(new BasicNameValuePair("pwd", pass));
				paramsStu.add(new BasicNameValuePair("confpwd", cpass));

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
        	
        	if(checkValidation() && (status1 ==0 || status2 ==0 || status3==0)){
        		Toast.makeText(CurlLogin.this, msg, Toast.LENGTH_SHORT).show();
        		pLoad.setVisibility(View.GONE);
        		mainDisp.setVisibility(View.VISIBLE);
        	}
        	else if(status3 == 1){
        		Toast.makeText(CurlLogin.this,"Registration Successful", Toast.LENGTH_SHORT).show();
        		pLoad.setVisibility(View.GONE);
        	}
        	       
        	if(checkValidation() == true){				
        		if(status3 == 1){
        			finish();
        			Intent i = new Intent(getApplicationContext(), AppLoginOrRegister.class);
        			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);                  
        			i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        			startActivity(i);	
        		}	
              }
        	}
}
		
			
	private void registerViews() {
        
        // TextWatcher would let us check validation error on the fly
		
		//Check registration number
        regnum.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.isRegistrationNumber(regnum, true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
        //Check Evarsity password
        evarsityPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(evarsityPass);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        
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
        if ((Validation.isRegistrationNumber(regnum,true)) &&
         (Validation.hasText(evarsityPass))    &&
         (Validation.isPhoneNumber(mobile, true)) &&
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