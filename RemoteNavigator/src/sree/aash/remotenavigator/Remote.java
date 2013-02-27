package sree.aash.remotenavigator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

public class Remote {

	private static final String TAG = "Remote";
	private static String serverAddress = "192.168.3.14"; // default address
	private static int port = 8888;
	private Socket socket;
	private PrintWriter out;
	
	private static Remote singletonInstance = null;

	private JoystickDirection lastDirection = JoystickDirection.NEUTRAL;
	private int lastSpeed = 0;

	public void connectToServer(String serverAddress) throws UnknownHostException, IOException {
		if(socket == null || (socket != null && socket.isClosed())) {
			this.serverAddress = serverAddress;
			socket = new Socket(serverAddress, port);
			out = new PrintWriter(socket.getOutputStream(), true);
		} else if(out == null) {
			out = new PrintWriter(socket.getOutputStream(), true);
		}
	}
	
	public static Remote getInstance() {
		if(singletonInstance == null) {
			singletonInstance = new Remote();
		}
		return singletonInstance;
	}
	
	public void disconnectFromServer() {
		if(out != null) {
			out.close();
		}
		if(socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void changeDirection(JoystickDirection newDirection) {
		if(out == null) {
			return;
		}
		lastDirection = newDirection;
		String data = String.format("%s %d", lastDirection.toString(), lastSpeed);
		out.println(data);
		Log.d(TAG, data);
	}

	public void changeSpeed(int newSpeed) {
		if(out == null) {
			return;
		}
		lastSpeed = newSpeed;
		String data = String.format("%s %d", lastDirection.toString(), lastSpeed);
		out.println(data);
		Log.d(TAG, data);
	}

}
