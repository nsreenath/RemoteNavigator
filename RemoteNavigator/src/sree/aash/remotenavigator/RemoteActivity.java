package sree.aash.remotenavigator;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class RemoteActivity extends Activity {

	protected static final String TAG = "RemoteActivity";
	private DoubleTouchView doubleTouchView;
	private JoystickView joystickView;
	private SpeedControlView speedControlView;
	private Remote remote;
	
	ProgressDialog progressDialog;
	protected String serverAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_remote);
		serverAddress = getIntent().getStringExtra("SERVER_ADDRESS");
		remote = Remote.getInstance();
		initViews();
		connectToServer();
	}

	private void connectToServer() {
		progressDialog = ProgressDialog.show(this, "Connecting...", "Connecting to server", true, true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
//				finish();
			}
		});
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressDialog.dismiss();
			}
		};
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					remote.connectToServer(serverAddress);
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					
				}
			}
		});
		t.start();
	}

	private void initViews() {
		doubleTouchView = (DoubleTouchView) findViewById(R.id.doubleTouchView);
		joystickView = doubleTouchView.getJoystickView();
		speedControlView = doubleTouchView.getSpeedControlView();
		joystickView.setJoystickListener(new JoystickListener() {
			
			@Override
			public void onDirectionChanged(JoystickDirection joystickDirection) {
				Log.d(TAG, "direction changed: "+joystickDirection);
				remote.changeDirection(joystickDirection);
			}
		});
		speedControlView.setSpeedControlListener(new SpeedControlListener() {
			
			@Override
			public void onSpeedChanged(int speed) {
				Log.d(TAG, "speed changed: "+speed);
				remote.changeSpeed(speed);
			}
		});
	}
	
	public void showToast(CharSequence msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void finish() {
		remote.disconnectFromServer();
		super.finish();
	}
}
