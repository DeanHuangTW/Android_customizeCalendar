package com.example.customizecalendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEvent extends Activity {
	
	private Button btnOK;
	private Button btnCancel;
	private EditText editTitle;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		setContentView(R.layout.event_dialog);
		
		editTitle = (EditText) findViewById(R.id.editText1);
		
		btnOK = (Button) findViewById(R.id.btn_ok);
		btnOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveAndBack();
			}
		});
		
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AddEvent.this.setResult(RESULT_CANCELED);
				AddEvent.this.finish();
			}
		});
	}
	
	public void saveAndBack() {
		Intent intent = getIntent();
		Bundle bundle = new Bundle();
		String title = editTitle.getText().toString();
		bundle.putString("title", title);		
		
		intent.putExtra("com.example.customizecalendar.AddEvent", bundle);
		AddEvent.this.setResult(RESULT_OK, intent);
		AddEvent.this.finish();
	}
}
