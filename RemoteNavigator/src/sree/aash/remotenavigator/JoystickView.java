package sree.aash.remotenavigator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class JoystickView extends View {

	public static int INVALID_POINTER_ID = -1;
	public static String TAG = "JoystickView";
	private int activePointer = INVALID_POINTER_ID;

	private Bitmap joystickHandle;
	private Bitmap joystickTrack;
	private float touchX = 0;
	private float touchY = 0;
	private float drawX = 0;
	private float drawY = 0;
	private float handleSize = 0.5f;

	private float threshold = 0.6f;

	private JoystickListener joystickListener;
	private JoystickDirection currentDirection;

	public JoystickView(Context context) {
		this(context, null);
	}

	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Resources res = getResources();
		joystickHandle = BitmapFactory.decodeResource(res,
				R.drawable.joystick_handle);
//		joystickTrack = BitmapFactory.decodeResource(res,
//				R.drawable.joystick_track);
		setBackgroundResource(R.drawable.joystick_track);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int size = (int) (getWidth() * handleSize);
		joystickHandle = Bitmap
				.createScaledBitmap(joystickHandle, size, size, true);
		placeHandleAtCenter();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setARGB(50, 100, 100, 100);
		canvas.drawCircle(getWidth()/2, getHeight()/2, getWidth()/2, paint);
		canvas.drawBitmap(joystickHandle, drawX, drawY, null);
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
		return getRect().contains((int) x, (int) y);
	}

	public boolean hasActivePointer() {
		if (getActivePointer() != INVALID_POINTER_ID) {
			return true;
		}
		return false;
	}

	public void removeActivePointer() {
		setActivePointer(INVALID_POINTER_ID);
	}

	public void viewTouched(MotionEvent event) {
		int action = event.getActionMasked();
		int index = event.getActionIndex();
		if (hasActivePointer()) {
			index = event.findPointerIndex(getActivePointer());
		}
		switch (action) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			placeHandleAtCenter();
			reportDirectionChanged();
			return;
		}
		touchX = event.getX(index) - getLeft(); // -
												// joystickHandle.getWidth()/2;
		touchY = event.getY(index) - getTop(); // -
												// joystickHandle.getHeight()/2;
		String msg = String.format("%d at %f, %f", System.currentTimeMillis(),
				event.getX(index) - getLeft(), event.getY(index) - getTop());
		Log.d(TAG, msg);
		float topEnd = joystickHandle.getHeight() / 2;
		float bottomEnd = getHeight() - joystickHandle.getHeight() / 2;
		float leftEnd = joystickHandle.getWidth() / 2;
		float rightEnd = getWidth() - joystickHandle.getWidth() / 2;

		if (topEnd < touchY && touchY < bottomEnd) {
			drawY = touchY - joystickHandle.getHeight() / 2;
		} else if (touchY < topEnd) {
			drawY = 0;
		} else if (bottomEnd < touchY) {
			drawY = getHeight() - joystickHandle.getHeight();
		}
		if (leftEnd < touchX && touchX < rightEnd) {
			drawX = touchX - joystickHandle.getWidth() / 2;
		} else if (touchX < leftEnd) {
			drawX = 0;
		} else if (rightEnd < touchX) {
			drawX = getWidth() - joystickHandle.getWidth();
		}
		invalidate();
		reportDirectionChanged();
	}

	private void reportDirectionChanged() {
		if (joystickListener == null) {
			return;
		}
		if (currentDirection == null) {
			currentDirection = getDirection();
			joystickListener.onDirectionChanged(currentDirection);
			return;
		}
		
		JoystickDirection newDirection = getDirection();
		if(!currentDirection.equals(newDirection)) {
			currentDirection = newDirection;
			joystickListener.onDirectionChanged(currentDirection);
		}
	}

	public void placeHandleAtCenter() {
		drawX = getWidth() / 2 - joystickHandle.getWidth() / 2;
		drawY = getHeight() / 2 - joystickHandle.getHeight() / 2;
		invalidate();
	}

	public int getPositionX() {
		int centerX = getWidth() / 2;
		int posX = (int) (drawX + joystickHandle.getWidth() / 2);
		int cartesianX = posX - centerX;
		return cartesianX;
	}

	public int getPositionY() {
		int centerY = getHeight() / 2;
		int posY = (int) (drawY + joystickHandle.getHeight() / 2);
		int cartesianY = centerY - posY;
		return cartesianY;
	}

	public int getPostionXMax() {
		return getWidth() / 2 - joystickHandle.getWidth() / 2;
	}

	public int getPostionYMax() {
		return getHeight() / 2 - joystickHandle.getHeight() / 2;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public JoystickDirection getDirection() {
		int x, y;
		x = getPositionX();
		y = getPositionY();

		int limit = (int) (threshold * getPostionXMax());

		if (x >= -limit && x <= limit && y >= limit) {
			return JoystickDirection.FORWARD;
		} else if (x >= -limit && x <= limit && y <= -limit) {
			return JoystickDirection.REVERSE;
		} else if (x <= -limit && y >= -limit && y <= limit) {
			return JoystickDirection.LEFT;
		} else if (x >= limit && y >= -limit && y <= limit) {
			return JoystickDirection.RIGHT;
		} else if (x >= limit && y >= limit) {
			return JoystickDirection.RIGHT_FORWARD;
		} else if (x >= limit && y <= -limit) {
			return JoystickDirection.RIGHT_REVERSE;
		} else if (x <= -limit && y <= -limit) {
			return JoystickDirection.LEFT_REVERSE;
		} else if (x <= -limit && y >= limit) {
			return JoystickDirection.LEFT_FORWARD;
		}

		return JoystickDirection.NEUTRAL;
	}

	public JoystickListener getJoystickListener() {
		return joystickListener;
	}

	public void setJoystickListener(JoystickListener joystickListener) {
		this.joystickListener = joystickListener;
	}

	public JoystickDirection getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(JoystickDirection currentDirection) {
		this.currentDirection = currentDirection;
	}

}
