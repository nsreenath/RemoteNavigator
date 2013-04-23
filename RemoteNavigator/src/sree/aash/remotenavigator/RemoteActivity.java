package sree.aash.remotenavigator;


import de.mjpegsample.MjpegView.MjpegInputStream;
import de.mjpegsample.MjpegView.MjpegView;
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
	private MjpegView cameraView;
	private Remote remote;
	
	private ProgressDialog progressDialog;
	protected String serverAddress;
	protected int cameraPort = 8089;
	
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
				finish();
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
					Log.d(TAG, "Could not connect to server\n"+e);
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
		cameraView = (MjpegView) findViewById(R.id.cameraView);
		String cameraUrl = "http://"+serverAddress+":"+cameraPort;
		cameraView.setSource(MjpegInputStream.read(cameraUrl));
	}
	
	public void showToast(CharSequence msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	@Override
	public void finish() {
		cameraView.stopPlayback();
		remote.disconnectFromServer();
		super.finish();
	}
}
