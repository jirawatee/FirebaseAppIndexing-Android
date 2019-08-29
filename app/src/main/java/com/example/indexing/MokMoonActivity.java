package com.example.indexing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.JobIntentService;

import com.google.firebase.appindexing.FirebaseUserActions;

public class MokMoonActivity extends AppCompatActivity implements View.OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mokmoon);

		findViewById(R.id.btn_add).setOnClickListener(this);
		findViewById(R.id.btn_remove).setOnClickListener(this);
		findViewById(R.id.btn_clear).setOnClickListener(this);

		onNewIntent(getIntent());
	}

	@Override
	protected void onStop() {
		FirebaseUserActions.getInstance().end(AppIndexingService.getCustomAction());
		super.onStop();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_add:
				JobIntentService.enqueueWork(this, AppIndexingService.class, AppIndexingService.UNIQUE_JOB_ID, new Intent());
				break;
			case R.id.btn_remove:
				AppIndexingService.removePersonalContent();
				break;
			case R.id.btn_clear:
				AppIndexingService.clearPersonalContent();
				break;
		}
	}

	protected void onNewIntent(Intent intent) {
		String action = intent.getAction();
		Uri data = intent.getData();
		if (Intent.ACTION_VIEW.equals(action) && data != null) {
			TextView linkText = findViewById(R.id.link);
			linkText.setText(data.toString());
		}
	}
}