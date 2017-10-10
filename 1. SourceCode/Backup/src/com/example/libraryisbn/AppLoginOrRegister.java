/**
 * 
 */
package com.example.libraryisbn;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.jotish.library.R;

/**
 * @author Ninad
 *
 */
public class AppLoginOrRegister extends Activity {

	private EditText regnum;
	private EditText appPass;
	private Button scan;
	private static final String STATUS = "status";
	private static final String MSG = "msg";
	JSONParser jsonParser = new JSONParser();
	private RelativeLayout pLoad;
	private RelativeLayout mainDisp;
	private int status1;
	private String msg;
	private String Regno;
	private String contents;
	private SessionManagement session;
	private ConnectionDetector cd;
	private static InputStream is = null;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.curl_login);
		
        TextView Register=(TextView)findViewById(R.id.textView2);
        TextView Login=(TextView)findViewById(R.id.textView1);
        regnum=(EditText)findViewById(R.id.editText1);
        appPass=(EditText)findViewById(R.id.editText2);
        scan = (Button) findViewById(R.id.button1);
        session = new SessionManagement(getApplicationContext());
        pLoad = (RelativeLayout) findViewById(R.id.LoadingProgress);
        mainDisp = (RelativeLayout) findViewById(R.id.MainScreen);
        
        
        cd = new ConnectionDetector(getApplicationContext());
		  if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
			  Toast.makeText(AppLoginOrRegister.this,"Oh Snap!Go Get a Connected first", Toast.LENGTH_SHORT).show();
	            // stop executing code by return
	            return;
	        }
        
        pLoad.setVisibility(View.GONE);
        
        registerViews();
        status1=0;
		
            //Register a new user.
	        Register.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
						Intent i = new Intent(AppLoginOrRegister.this, CurlLogin.class);
						startActivity(i);				
									}
			});
         
	        //Login for existing user using id and password
        	Login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					if(checkValidation()){
						
						new ChkStuLogin().execute();
					}
					else{
						Toast.makeText(AppLoginOrRegister.this,"Fill all Fields", Toast.LENGTH_SHORT).show();
					}
			}
		});
        	
        	//Login or register by scanning id
        	scan.setOnClickListener(new OnClickListener(){
        		
        		@Override
        		public void onClick(View arg0) {
        			
        				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        		        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
        		        startActivityForResult(intent, 0);
        			
        		}
        	});

	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            contents = intent.getStringExtra("SCAN_RESULT");
	           // String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            	   
	            new ChkStuLoginScan().execute(); 
				
	            /*if(res == true){
	            	//If id in database then start the book_list_activity
		            Intent getBooks= new Intent(AppLoginOrRegister.this, Load_Renew_Books.class);
		            getBooks.putExtra("yourid",contents);
		            startActivity(getBooks);
		            finish();
	            }
	            else{
	            	//If id not in database then start this.
	            	Intent getBooks= new Intent(AppLoginOrRegister.this, ScanRegister.class);
		            getBooks.putExtra("yourid",contents);
		            startActivity(getBooks);           
	            }
	            */
	        } else if (resultCode == RESULT_CANCELED) {
	        	
	           Toast.makeText(AppLoginOrRegister.this, "Scan it again Please!!!", Toast.LENGTH_SHORT).show();
	        }
	    }
	   
	}
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }*/
	 
	 //Check student Id and password from server
		class ChkStuLogin extends AsyncTask<String, String, String> {
		
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(AppLoginOrRegister.this,"Logging in...", Toast.LENGTH_SHORT).show();
			mainDisp.setVisibility(View.GONE);
			pLoad.setVisibility(View.VISIBLE);
		}
		
		protected String doInBackground(String... args) {
			
		    String mode_chkstulogin = "chkStudentLogin";
		    String regno = regnum.getText().toString();
		    String pass = appPass.getText().toString();
	     
			// Building Parameters
			List<NameValuePair> paramsLogin= new ArrayList<NameValuePair>();
			paramsLogin.add(new BasicNameValuePair("mode", mode_chkstulogin));
			paramsLogin.add(new BasicNameValuePair("regid", regno));
			paramsLogin.add(new BasicNameValuePair("pwd", pass ));
	
			// getting JSON string from URL
			JSONObject jsonLogin = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", paramsLogin);
			
			try {
				
				status1 = jsonLogin.getInt(STATUS);
				msg = jsonLogin.getString(MSG);
				Log.d("Status1" , ">" + status1 );
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(status1==1){
				
                // this code will send registration id of a device to our own server.
			String urlgcm = ConnectionDetector.URL;
			String mode_gcmid = "GCM_ID";
			
			List<NameValuePair> paramsGCM = new ArrayList<NameValuePair>();
			paramsGCM.add(new BasicNameValuePair("mode", mode_gcmid));
			paramsGCM.add(new BasicNameValuePair("gcmid", StartSplashScreen.GCMregid));
			paramsGCM.add(new BasicNameValuePair("regid", regno));

		try{
			DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(paramsGCM, "utf-8");
            urlgcm += "?" + paramString;
            HttpGet httpGet = new HttpGet(urlgcm);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();          
        }   
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		    } catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
		        e.printStackTrace();

		} 
			}
			return null;
		}
		
		 protected void onPostExecute(String file_url) {
			 
	        	Toast.makeText(AppLoginOrRegister.this, msg, Toast.LENGTH_SHORT).show();
	        	
			 if(status1==1){
				 //pLoad.setVisibility(View.GONE);	
				//Start activity  to fetch book list from the Library database
		        Regno = regnum.getText().toString();
		        session.createLoginSession(Regno);
				Intent getBooks= new Intent(getApplicationContext(), Load_Renew_Books.class);
				getBooks.putExtra("yourid",Regno);
				startActivity(getBooks);
				finish();	
			 }  
			 
			 else{
				 pLoad.setVisibility(View.GONE);
				 mainDisp.setVisibility(View.VISIBLE);
			 }
		 }
		}
		
		class ChkStuLoginScan extends AsyncTask<String, String, String> {
			
			protected void onPreExecute() {
				super.onPreExecute();
				Toast.makeText(AppLoginOrRegister.this,"Checking...", Toast.LENGTH_SHORT).show();
				mainDisp.setVisibility(View.GONE);
				pLoad.setVisibility(View.VISIBLE);
			}
			
			protected String doInBackground(String... args) {
				
			    String mode_chkstulogin = "chkStudentLoginScan";
			    String regno = contents;
		     
				// Building Parameters
				List<NameValuePair> paramsLoginScan= new ArrayList<NameValuePair>();
				paramsLoginScan.add(new BasicNameValuePair("mode", mode_chkstulogin));
				paramsLoginScan.add(new BasicNameValuePair("regid", regno));
		
				// getting JSON string from URL
				JSONObject jsonLogin = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", paramsLoginScan);
				
				try {
					
					status1 = jsonLogin.getInt(STATUS);
					msg = jsonLogin.getString(MSG);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			 protected void onPostExecute(String file_url) {
				 
		        	Toast.makeText(AppLoginOrRegister.this, msg, Toast.LENGTH_SHORT).show();
		        	pLoad.setVisibility(View.GONE);	
				 	
				 if(status1==1){
					
			        	 session.createLoginSession(contents);
			        	 Intent getBooks= new Intent(getApplicationContext(), Load_Renew_Books.class);
						 getBooks.putExtra("yourid",contents);
						 startActivity(getBooks);
						 finish();	
			        }
		            else
		            {
			        	 Intent scanreg= new Intent(getApplicationContext(), ScanRegister.class);
					     scanreg.putExtra("yourid",contents);
					     startActivity(scanreg);
					     finish();       
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
 
        //Check app password
        appPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Validation.hasText(appPass);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        }
  
    private boolean checkValidation() {
        boolean ret = true;
        if (!Validation.isRegistrationNumber(regnum,true) &&
         !Validation.hasText(appPass)) ret = false;
        return ret;
    }

}
