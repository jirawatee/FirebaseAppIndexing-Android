package com.example.indexing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.appindexing.FirebaseAppIndex;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.Indexable;
import com.google.firebase.appindexing.builders.Actions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
	private static final String DOMAIN = "https://mokmoon.com/bangkok";
	private static final String TITLE = "MokMoon";
	private String mAppUri;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTextView = findViewById(R.id.link);
		findViewById(R.id.btn_mokmoon).setOnClickListener(this);
		mAppUri = Uri.parse(DOMAIN).buildUpon().build().toString();
		onNewIntent(getIntent());
	}

	@Override
	protected void onStart() {
		super.onStart();
		Indexable articleToIndex = new Indexable.Builder()
				.setName(TITLE)
				.setUrl(mAppUri)
				.setDescription("MokMoon.com teen center of Thailand.")
				.setImage("http://www.businessofapps.com/wp-content/uploads/2017/09/firebase-app-indexing.png")
				.setKeywords("mokmoon", "app indexing", "firebase")
				.build();

		// If the Task is already complete, a call to the listener will be immediately scheduled
		Task<Void> task = FirebaseAppIndex.getInstance().update(articleToIndex);
		task.addOnSuccessListener(this, new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				mTextView.setText(TITLE + " added.");
			}
		});
		task.addOnFailureListener(this, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				mTextView.setText(exception.getMessage());
			}
		});

		// log the view action
		Task<Void> actionTask = FirebaseUserActions.getInstance().start(Actions.newView(TITLE, mAppUri));
		actionTask.addOnSuccessListener(this, new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				mTextView.setText("Started view action on " + TITLE);
			}
		});
		actionTask.addOnFailureListener(this, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				mTextView.setText(exception.getMessage());
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		Task<Void> actionTask = FirebaseUserActions.getInstance().end(Actions.newView(TITLE, mAppUri));
		actionTask.addOnSuccessListener(this, new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Toast.makeText(MainActivity.this, "Ended view action on " + TITLE, Toast.LENGTH_SHORT).show();
			}
		});

		actionTask.addOnFailureListener(this, new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_mokmoon:
				startActivity(new Intent(this, MokMoonActivity.class));
				break;
		}
	}

	protected void onNewIntent(Intent intent) {
		String action = intent.getAction();
		Uri data = intent.getData();
		if (Intent.ACTION_VIEW.equals(action) && data != null) {
			mTextView.setText(data.toString());
		}
	}
}