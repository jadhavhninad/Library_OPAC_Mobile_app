package com.example.libraryisbn;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Load_Renew_Books extends ListActivity {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> booklist;
	
	@SuppressLint
	("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
	
	
	private static final String BOOK_ID = "ID";
	private static final String BOOK_NAME = "BOOKNAME";
	private static final String BOOK_RENEW_DATE = "RENEWDATE";
	private static final String BOOK_ISSUE_DATE = "TIMESTAMP";
	private static final String BOOK_AUTHOR = "AUTHOR";
	private static final String SYS_DATE = "SYSDATE"; 
	private static final String MSG = "msg";
	private static final String STATUS = "status";
	private static String COLLEGEID="";
	int success;
	int success_date;
	JSONObject json_noti;
	//private static int sendgcmreq =1;
	String msg_date;
    String msg;
    String renewal_date;
    String system_date;
    Date date_current;
    Date date_renew;
    SessionManagement session;
	JSONArray sysdate = null;
	JSONArray books=null;
	private RelativeLayout pLoad;
	private RelativeLayout mainDisp;
	JSONArray regno;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load__renew__books);
		
		cd = new ConnectionDetector(getApplicationContext());
		  if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
			  Toast.makeText(Load_Renew_Books.this,"Oh Snap!Go Get a Connected first", Toast.LENGTH_SHORT).show();
	            // stop executing code by return
	            return;
	        } 
		  
/*		  
  if((getIntent().getExtras().getString("youRegno")) != null){
		  try {
				json_noti = new JSONObject(msg);
				regno = json_noti.optJSONArray(msg);
				JSONObject d = regno.getJSONObject(0);
				COLLEGEID = d.getString("REGNO");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  else
			  COLLEGEID =(getIntent().getExtras().getString("yourid"));*/
		  
		    TextView Logout = (TextView) findViewById(R.id.textView1);
		    COLLEGEID =(getIntent().getExtras().getString("yourid"));
		    session = new SessionManagement(getApplicationContext());
		  
			booklist = new ArrayList<HashMap<String, String>>();
			pLoad = (RelativeLayout) findViewById(R.id.LoadingProgress);
	        mainDisp = (RelativeLayout) findViewById(R.id.RLBookList);
	        
	        pLoad.setVisibility(View.GONE);
	        
			new LoadRenewBooks().execute();
			
/*			// Set a listener to be invoked when the list should be refreshed.
	        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
	            @Override
	            public void onRefresh() {
	                // Do work to refresh the list here.
	                new LoadRenewBooks().execute();
	            }
	        })*/;
			

			
			 ListView lv = getListView();
	         
		        /**
		         * Listview item click listener

		         * */
		        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
		                    long arg3) {
		                // on selecting a single book in the list
		                // Second activity will be launched which has scanner.
		            	
		            	//get renew date of the selected book
		            	renewal_date = ((TextView) view.findViewById(R.id.renew_date)).getText().toString();
		            	
		            	//convert it into specific format dd-MMM-yy
		            	try {
							date_renew = formatter.parse(renewal_date);
							date_current = formatter.parse(system_date);
						} 
		            	catch (ParseException e) 
		            	{
							e.printStackTrace();
						}
		            	catch (java.text.ParseException e) 
		            	{
							e.printStackTrace();
						}
		            	
		            	//check if the current date is after the renew date or not
		            	if(date_current.after(date_renew))
		            	{
		            		Intent i = new Intent(getApplicationContext(), FineIncurred.class);
			                 
			                // send student id to next activity for renewal
			                String book_list_id = ((TextView) view.findViewById(R.id.book_id)).getText().toString();
			                String book_name = ((TextView) view.findViewById(R.id.book_name)).getText().toString();
			                String book_issue_date = ((TextView) view.findViewById(R.id.issue_date)).getText().toString();
			                String book_author_name = ((TextView) view.findViewById(R.id.book_author)).getText().toString();
			                
			                i.putExtra("book_list_id", book_list_id);
			                i.putExtra("student_id",COLLEGEID); 
			                i.putExtra("bookName", book_name);
			                i.putExtra("issueDate",book_issue_date); 
			                i.putExtra("renewDate",renewal_date); 
			                i.putExtra("author",book_author_name); 
			                
			                startActivity(i);
			                
		            	}
		            	else
		            	{
		            	Intent i = new Intent(getApplicationContext(), Second.class);
		                 
		                // send student id to next activity for renewal
		                String book_list_id = ((TextView) view.findViewById(R.id.book_id)).getText().toString();
		                String book_name = ((TextView) view.findViewById(R.id.book_name)).getText().toString();
		                String book_issue_date = ((TextView) view.findViewById(R.id.issue_date)).getText().toString();
		                String book_author_name = ((TextView) view.findViewById(R.id.book_author)).getText().toString();
		                
		                i.putExtra("book_list_id", book_list_id);
		                i.putExtra("student_id",COLLEGEID); 
		                i.putExtra("bookName", book_name);
		                i.putExtra("issueDate",book_issue_date); 
		                i.putExtra("renewDate",renewal_date); 
		                i.putExtra("author",book_author_name); 
		                 
		                startActivity(i);
		            	}
		            }
		        });
		        
		        Logout.setOnClickListener(new View.OnClickListener() {
		             
		            @Override
		            public void onClick(View arg0) {
		                // Clear the session data
		                // This will clear all session data and 
		                // redirect user to LoginActivity
		                new Logout().execute();
		            }
		        });
			
	}
	
	class Logout extends AsyncTask<String, String, String>{
		
			protected String doInBackground(String... args) {
			
			String mode_logout = "GCM_LOGOUT";
			String regid = COLLEGEID;
			String gcm_id = StartSplashScreen.GCMregid;
            	
			// Building Parameters
			List<NameValuePair> paramsGcmLogout = new ArrayList<NameValuePair>();
			paramsGcmLogout.add(new BasicNameValuePair("mode", mode_logout));
			paramsGcmLogout.add(new BasicNameValuePair("gcmid", gcm_id ));
			paramsGcmLogout.add(new BasicNameValuePair("regid", regid));
			
			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", paramsGcmLogout);

			// Check your log cat for JSON reponse
			Log.d("GCM Logout JSON: ", "> " + json);
			
						
			try {					
				success = json.getInt(STATUS);
				msg= json.getString(MSG);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
			}
			
			protected void onPostExecute(String file_url) {
	        	
			 if(success==1){
				Toast.makeText(Load_Renew_Books.this, msg, Toast.LENGTH_SHORT).show();
			 }  
			 
			 else{
				 Toast.makeText(Load_Renew_Books.this, "Error in GCM registration. please restart the app", Toast.LENGTH_SHORT).show();
			 }
			 session.logoutUser();
             finish();	
		 }
			
	}

	
	class LoadRenewBooks extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(Load_Renew_Books.this,"Listing Books ...", Toast.LENGTH_SHORT).show();
			mainDisp.setVisibility(View.GONE);
			pLoad.setVisibility(View.VISIBLE);
		}

		/**
		 * Retrieving book list
		 * */
		protected String doInBackground(String... args) {
			
			String mode_renew = "GET_RENEW_BOOKS";
			String mode_sysdate = "GET_SYSDATE";
			String regid = COLLEGEID;
			//String mode_gcmid = "GCM_ID";
			//String gcmid = MainActivity.GCMID;
            	
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("mode", mode_renew));
			params.add(new BasicNameValuePair("regid", regid));
			
			List<NameValuePair> param_sysdate = new ArrayList<NameValuePair>();
			param_sysdate.add(new BasicNameValuePair("mode", mode_sysdate));

			// getting JSON string from URL
			JSONObject json = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", params);
			JSONObject json_date = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", param_sysdate); //getting system date

			/*if(sendgcmreq == 1){
			List<NameValuePair> params_gcmid = new ArrayList<NameValuePair>();
			params_gcmid.add(new BasicNameValuePair("mode", mode_gcmid));
			params_gcmid.add(new BasicNameValuePair("regid", regid));
			params_gcmid.add(new BasicNameValuePair("gcmid", gcmid));	
			
			JSONObject json_gcmid = jsonParser.makeHttpRequest(ConnectionDetector.URL, "GET", params_gcmid);
			Log.d("GCM JSON: ", "> " + json_gcmid);
			sendgcmreq++;
			}*/

			// Check your log cat for JSON reponse
			Log.d("BOOKS JSON: ", "> " + json);
			Log.d("DATE JSON: ", "> " + json_date);
			
						
			try {
				
				success_date = json_date.getInt(STATUS);
				if(success_date ==1)
				{
					sysdate = json_date.optJSONArray(MSG);		
					JSONObject dt = sysdate.getJSONObject(0);
					system_date = dt.getString(SYS_DATE);					
				}
			
					
				success = json.getInt(STATUS);
				msg= json.getString(MSG);
				
				if(success==1)
				{
					books = json.optJSONArray(MSG);
					
					for(int j=0;j<books.length();j++)
					{
						JSONObject d = books.getJSONObject(j);

						// Storing each json item values in variable
						String id = d.getString(BOOK_ID);
						String name=d.getString(BOOK_NAME);
						String issue_date = d.getString(BOOK_ISSUE_DATE);;
						String renew_date=d.getString(BOOK_RENEW_DATE);
						String author=d.getString(BOOK_AUTHOR);
					
						HashMap<String, String> map = new HashMap<String, String>();

					// adding each child node to HashMap key => value
					map.put(BOOK_ID, id);
					map.put(BOOK_NAME, name);
					map.put(BOOK_ISSUE_DATE, issue_date);
					map.put(BOOK_RENEW_DATE, renew_date);
					map.put(BOOK_AUTHOR, author);

					// adding HashList to ArrayList
					booklist.add(map);
					
				}
				}
				
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
			// dismiss the dialog 
			mainDisp.setVisibility(View.VISIBLE);
			pLoad.setVisibility(View.GONE);
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					
					if(success == 0)
					{
						
						Toast.makeText(Load_Renew_Books.this,msg,  Toast.LENGTH_SHORT).show();	
						Log.d("Library: ", "null");
					}
					
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							Load_Renew_Books.this, booklist,
							R.layout.list_for_book, new String[] { BOOK_ID,
									BOOK_NAME, BOOK_ISSUE_DATE, BOOK_RENEW_DATE, BOOK_AUTHOR }, new int[] {
									R.id.book_id, R.id.book_name, R.id.issue_date, R.id.renew_date, R.id.book_author });
					
					// updating listview
					setListAdapter(adapter);
				}
			});
			
			/*// Call onRefreshComplete when the list has been refreshed.
            ((PullToRefreshListView) getListView()).onRefreshComplete();*/

		}

	}
		
}
