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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Second extends Activity{
	
	
	private static final String MSG = "msg";
	private static final String STATUS = "status";
	int success;
    String msg;
	private static String STUDENT_ID="";
	private static String BOOK_ID="";
	JSONParser jsonParser = new JSONParser();
	private String contents;
	private String message;
	private RelativeLayout pLoad;
	private RelativeLayout mainDisp;
	JSONObject json;
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	setContentView(R.layout.second);
	
	Intent intent = getIntent();
	/*if( (message = intent.getExtras().getString("message")) != null){
		
		json = new JSONObject(message);
		
		for(int i=0;i<jArray.length();i++) { 
		     JSONArray  jArrayInner = jArray.optJSONArray(i);
		     if (jArrayInner != null) {
		         for(int j=0;j<jArrayInner.length();j++) {
		                JSONObject json_data = jArrayInner.getJSONObject(j);
		                Log.i("log_tag2","title: "+json_data.optString("price0"));
		                Log.i("log_tag2","title: "+json_data.optString("size0"));
		          }
		      }
		     }                                                     

		}
						
		
	try {
		json = new JSONObject(message);
		String stime = json.getString("name");
		name.setText(stime);

		String slecturename = json.getString("deal");
		deal.setText(slecturename);

		String sroom = json.getString("valid");
		valid.setText(sroom);

		String sfaculty = json.getString("address");
		address.setText(sfaculty);

	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}*/
	
	
	TextView bkname= (TextView) findViewById(R.id.textView2);
	bkname.setText(getIntent().getExtras().getString("bookName"));
	
	TextView author_nm= (TextView) findViewById(R.id.textView1);
	author_nm.setText(getIntent().getExtras().getString("author"));
	
	TextView issdate= (TextView) findViewById(R.id.textView5);
	issdate.setText(getIntent().getExtras().getString("issueDate"));
	
	TextView retdate= (TextView) findViewById(R.id.textView7);
	retdate.setText(getIntent().getExtras().getString("renewDate"));
	
	Button scan=(Button)findViewById(R.id.button1);
	
	pLoad = (RelativeLayout) findViewById(R.id.LoadingProgress);
    mainDisp = (RelativeLayout) findViewById(R.id.RLRenewBookAct);
    
    pLoad.setVisibility(View.GONE);
    
    /*Button b2 = (Button) findViewById(R.id.Button01);
    b2.setOnClickListener(this);*/
    
    
    scan.setOnClickListener(new View.OnClickListener() {
        
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
            /*TextView code= (TextView) findViewById(R.id.code);
            code.setText(contents);*/
            
            /*STUDENT_ID = getIntent().getExtras().getString("student_id");
            BOOK_ID = contents;*/
            
            new RenewBooks().execute();      
            
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(Second.this, "Failure.Please scan again",  Toast.LENGTH_SHORT).show();
        }
    }
}
//@Override
/*public void onClick(View arg0) {
	// TODO Auto-generated method stub
	if(arg0.getId()== R.id.button1)
	{
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
		
	}
	
	if(arg0.getId() == R.id.Button01)
	{
		
		new RenewBooks().execute();
	}
	
	
}*/


class RenewBooks extends AsyncTask<String, String, String> {

	/**
	 * Before starting background thread Show Progress Dialog
	 * */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		STUDENT_ID = getIntent().getExtras().getString("student_id");
        BOOK_ID = contents;
		Toast.makeText(Second.this,"Renewing...", Toast.LENGTH_SHORT).show();
		mainDisp.setVisibility(View.GONE);
		pLoad.setVisibility(View.VISIBLE);
	}

	/**
	 * Renewing books
	 * */
	protected String doInBackground(String... args) {
		
		String mode = "RENEW_BOOK";
		String regid = STUDENT_ID;
		String bookid = BOOK_ID;
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mode", mode));
		params.add(new BasicNameValuePair("regid", regid));
		params.add(new BasicNameValuePair("bookid", bookid));

		// getting JSON string from URL
		JSONObject json = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", params);

		// Check your log cat for JSON reponse
		Log.d("BOOKS JSON: ", "> " + json);

		try {
			
			success = json.getInt(STATUS);
			msg= json.getString(MSG);
			
			
				}
				
			catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		
		Toast.makeText(Second.this, msg, Toast.LENGTH_SHORT).show();
		mainDisp.setVisibility(View.VISIBLE);
		pLoad.setVisibility(View.GONE);
	 	
	 if(success==1){
        	    	//Return to book list on successful renewal of book
					Intent bookList= new Intent(getApplicationContext(), Load_Renew_Books.class);
					bookList.putExtra("yourid",STUDENT_ID);
		            startActivity(bookList);
				    finish();	
	 }
	}

}
}
