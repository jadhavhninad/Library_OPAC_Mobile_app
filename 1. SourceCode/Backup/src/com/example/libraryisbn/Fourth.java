package com.example.libraryisbn;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Fourth extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fourth);
		TextView message = (TextView) findViewById(R.id.message);
		message.setText(getIntent().getExtras().getString("message"));
	}

}
