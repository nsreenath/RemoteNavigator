package sree.aash.remotenavigator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class SpeedControlView extends View {
	
	private static String TAG = "SpeedControlView";
	public static int INVALID_POINTER_ID = -1; 
	private int activePointer = INVALID_POINTER_ID;
	
	private Bitmap speedcontrolHandle;
//	private Bitmap speedcontrolTrack;
	private int trackWidth = 10;
	
	private float touchX = 0;
	private float touchY = 0;
	private float drawX = 0;
	private float drawY = 0;
	
	private int range = 100;
	
	private SpeedControlListener speedControlListener;
	private int currentSpeed = -1;

	public SpeedControlView(Context context) {
		this(context, null);
	}
	
	public SpeedControlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = getResources();
		speedcontrolHandle = BitmapFactory.decodeResource(res, R.drawable.speedcontrol_handle);
		speedcontrolHandle = Bitmap.createScaledBitmap(speedcontrolHandle, 50, 50, true);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		placeHandleAtBottom();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setARGB(50, 100, 100, 100);
		int centerX = getWidth()/2;
		canvas.drawRect(centerX-trackWidth/2, 0, centerX+trackWidth/2, getHeight(), paint);
		canvas.drawBitmap(speedcontrolHandle, drawX, drawY, null);
	}

	public int getActivePointer() {
		return activePointer;
	}

	public void setActivePointer(int activePointer) {
		this.activePointer = activePointer;
	}
	
	public Rect getRect() {
		Rect rect = new Rect(getLeft(), getTop(), getRight(), getBottom());
		return rect;
	}
	
	public boolean contains(float x, float y) {
		return getRect().contains((int)x, (int)y);
	}

	public boolean hasActivePointer() {
		if(getActivePointer() != INVALID_POINTER_ID) {
			return true;
		}
		return false;
	}

	public void removeActivePointer() {
		setActivePointer(INVALID_POINTER_ID);
	}
	
	public void viewTouched(MotionEvent event) {
		int index = event.getActionIndex();
		if(hasActivePointer()) {
			index = event.findPointerIndex(getActivePointer());
		}
		touchX = event.getX(index)-getLeft(); // - speedcontrolHandle.getWidth()/2;
		touchY = event.getY(index)-getTop(); // - speedcontrolHandle.getHeight()/2;
		String msg = String.format("%d at %f, %f",System.currentTimeMillis(), 
				touchX, touchY);
		Log.d(TAG, msg);
		float topEnd = speedcontrolHandle.getHeight()/2;
		float bottomEnd = getHeight() - speedcontrolHandle.getHeight()/2;
		if(topEnd < touchY && touchY < bottomEnd) {
			drawY = touchY - speedcontrolHandle.getHeight()/2;
			drawX = getWidth()/2 - speedcontrolHandle.getWidth()/2;
			invalidate();
			reportSpeedChanged();
		}
	}
	
	private void reportSpeedChanged() {
		if(speedControlListener == null) {
			return;
		}
		if(currentSpeed == -1) {
			currentSpeed = getSpeed();
			speedControlListener.onSpeedChanged(currentSpeed);
			return;
		}
		
		int newSpeed = getSpeed();
		if(currentSpeed != newSpeed) {
			currentSpeed = newSpeed;
			speedControlListener.onSpeedChanged(currentSpeed);
		}
	}

	public void placeHandleAtBottom() {
		drawX = getWidth()/2 - speedcontrolHandle.getWidth()/2;
		drawY = getHeight() - speedcontrolHandle.getWidth();
	}
	
	public int getSpeed() {
		int baseY = getHeight();
		int posY = (int) (drawY + speedcontrolHandle.getHeight());
		int diff = baseY - posY;
		float trackLength = getHeight() - speedcontrolHandle.getHeight();
		int speed = (int) ((diff/trackLength) * range);
		return speed;
	}
	
	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public SpeedControlListener getSpeedControlListener() {
		return speedControlListener;
	}

	public void setSpeedControlListener(SpeedControlListener speedControlListener) {
		this.speedControlListener = speedControlListener;
	}
}
