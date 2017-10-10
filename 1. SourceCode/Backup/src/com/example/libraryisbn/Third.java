package com.example.libraryisbn;
//This was added by jotish

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;




public class Third extends Activity implements OnClickListener {
	
	String yourid ;
	String bookcode;
	String res=null;
	ProgressBar progress ;
	TextView error;
	Button issue;
	Button ret;
	Button renewal;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.third);
progress = (ProgressBar)findViewById(R.id.progressBar1);
progress.setProgress(0);
	yourid=getIntent().getExtras().getString("yourid");
	bookcode=getIntent().getExtras().getString("bookcode");
	
	 issue=(Button) findViewById(R.id.button1);
	//Ninadh //ret=(Button) findViewById(R.id.button2);
	 renewal=(Button) findViewById(R.id.button3);
	error=(TextView) findViewById(R.id.textView1);
	error.setText(bookcode);
	issue.setOnClickListener(this);
	ret.setOnClickListener(this);
	renewal.setOnClickListener(this);
	yourid=getIntent().getExtras().getString("yourid");
	bookcode= getIntent().getExtras().getString("bookcode");
	
}

@Override
public void onClick(View arg0) {
	if(arg0.getId()== R.id.button1)
	{   
	
		if(!isOnline(this))
		{Toast.makeText(Third.this,
		         "No internet connection.", Toast.LENGTH_LONG).show();}
		else{
	try
	{  
		new MyTask().execute("Issue");
		issue.setClickable(false);
		ret.setClickable(false);
		renewal.setClickable(false);
	}
	
	catch(Exception ie)
	{
		
		error.setText(ie.toString());
	}
	
	
	
	}}
	
	else if(arg0.getId()== R.id.button3)
	{   
		if(!isOnline(this))
		{Toast.makeText(Third.this,
		         "No internet connection.", Toast.LENGTH_LONG).show();}
		else{
	try
	{
		new MyTask().execute("Renewal");
		issue.setClickable(false);
		ret.setClickable(false);
		renewal.setClickable(false);
	}
	
	catch(Exception ie)
	{
		
		error.setText(ie.toString());
	}
	}
	}

}
void updatetext()
{
	Intent trd= new Intent(Third.this,Fourth.class);
    trd.putExtra("message",res);
    startActivity(trd);
	
}
public boolean isOnline(Context c) {
	ConnectivityManager cm = (ConnectivityManager) c
	.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo ni = cm.getActiveNetworkInfo();
	 
	if (ni != null && ni.isConnected())
	  return true;
	else
	  return false;
	}
private class MyTask extends AsyncTask<String, Integer, Void>
{
	
	
	int myProgress;
	@Override
	protected Void doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		
		
		
		HttpClient httpClient = new DefaultHttpClient();
    HttpPost httpPost = new HttpPost("http://jotish.3owl.com/mobile.php?q=mobile");
	List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
	
	postParameters.add(new BasicNameValuePair("userid",yourid ));
	postParameters.add(new BasicNameValuePair("bookcode", bookcode));
	postParameters.add(new BasicNameValuePair("type", arg0[0]));
	
	try {

	httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
   
	}
	catch (UnsupportedEncodingException e) {
	    // writing error to Log
		res=e.toString();
		
	   
	}
	try{
	
		    HttpResponse response = httpClient.execute(httpPost);
		     
		     HttpEntity entity = response.getEntity();
		     
		     
		     if(entity != null){


                 //Create new input stream with received data assigned
                 InputStream instream = entity.getContent();

                 //Create new JSON Object. assign converted data as parameter.
                 JSONObject jsonResponse = new JSONObject(convertStreamToString(instream));

                 //assign json responses to local strings
                 //String retUser = jsonResponse.getString("success");//mySQL table field
                 res = jsonResponse.getString("message");
                 
    		     
		     }
		     
		     
	}
 
	

	
		
	catch (ClientProtocolException ie) {
		res=ie.toString();
	
}
	catch (Exception ie) {
		res=ie.toString();
		
		
	}
	while(myProgress<100){
	    myProgress++;
	    publishProgress(myProgress);
	       SystemClock.sleep(30);
	   }
	
		return null;
	}
	@Override
	  protected void onPreExecute() {
	   // TODO Auto-generated method stub
	   Toast.makeText(Third.this,
	         "Sending Request", Toast.LENGTH_LONG).show();
	   myProgress = 0;
	  }
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		   // TODO Auto-generated method stub
		   progress.setProgress(values[0]);
		  }
	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		Toast.makeText(Third.this,
		         "Request Sent", Toast.LENGTH_LONG).show();
		updatetext();
		
		
	}
	private String convertStreamToString(InputStream is) {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }//END convertStreamToString()
}

}
