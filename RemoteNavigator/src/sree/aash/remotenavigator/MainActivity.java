package sree.aash.remotenavigator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	EditText etServerAddress;
	Button connectButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		etServerAddress = (EditText) findViewById(R.id.etServerAddress);
		connectButton = (Button) findViewById(R.id.connectButton);
		
		connectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String serverAddress = etServerAddress.getText().toString();
				Intent intent = new Intent(MainActivity.this, RemoteActivity.class);
				intent.putExtra("SERVER_ADDRESS", serverAddress);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
