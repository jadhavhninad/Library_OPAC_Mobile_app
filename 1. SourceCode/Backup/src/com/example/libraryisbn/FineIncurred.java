/**
 * 
 */
package com.example.libraryisbn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class FineIncurred extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fine_incurred);
		
		TextView bkname= (TextView) findViewById(R.id.textView2);
		bkname.setText(getIntent().getExtras().getString("bookName"));
		
		TextView author_nm= (TextView) findViewById(R.id.textView1);
		author_nm.setText(getIntent().getExtras().getString("author"));
		
		TextView issdate= (TextView) findViewById(R.id.textView5);
		issdate.setText(getIntent().getExtras().getString("issueDate"));
		
		TextView retdate= (TextView) findViewById(R.id.textView7);
		retdate.setText(getIntent().getExtras().getString("renewDate"));
	}

}
