package sree.aash.remotenavigator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DoubleTouchView extends RelativeLayout {
	
	private static final String TAG = "DoubleTouch View";
	private JoystickView joystickView;
	private SpeedControlView speedControlView;

	public DoubleTouchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		View v = View.inflate(context, R.layout.template_double_touch_view, this);
		joystickView = (JoystickView) v.findViewById(R.id.joystickView);
		speedControlView = (SpeedControlView) v.findViewById(R.id.speedControlView);
	}
	
	public DoubleTouchView(Context context) {
		this(context, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getActionMasked();
		int id, index;
		float x, y;
		int nPointer=0;
		switch(action) {
		case MotionEvent.ACTION_DOWN:
			x = event.getX(0);
			y = event.getY(0);
			id = event.getPointerId(0);
			if(joystickView.contains(x, y)) {
				joystickView.setActivePointer(id); joystickView.viewTouched(event);
				Log.d(TAG, "Left down");
			} else if(speedControlView.contains(x, y)) {
				speedControlView.setActivePointer(id); speedControlView.viewTouched(event);
				Log.d(TAG, "Right down");
			}
//			Log.d(TAG, String.format("action down %d at %f, %f", id, ));
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			index = event.getActionIndex();
			id = event.getPointerId(index);
			x = event.getX(index);
			y = event.getY(index);
			if(!joystickView.hasActivePointer() && joystickView.contains(x, y)) {
				joystickView.setActivePointer(id);  joystickView.viewTouched(event);
				Log.d(TAG, "Left down");
			} else if(!speedControlView.hasActivePointer() && speedControlView.contains(x, y)) {
				speedControlView.setActivePointer(id); speedControlView.viewTouched(event);
				Log.d(TAG, "Right down");
			}
//			Log.d(TAG, "action pointer down "+id);
//			Log.d(TAG, String.format("action pointer down %d at %f, %f", id, event.getX(index), event.getY(index)));
			break;
		case MotionEvent.ACTION_UP:
			id = event.getPointerId(0);
			if(id == joystickView.getActivePointer()) {
				joystickView.removeActivePointer();  joystickView.viewTouched(event);
				Log.d(TAG, "Left up");
			} else if(id == speedControlView.getActivePointer()) {
				speedControlView.removeActivePointer();  speedControlView.viewTouched(event);
				Log.d(TAG, "Right up");
			}
//			Log.d(TAG, String.format("action up %d at %f, %f", id, event.getX(0), event.getY(0)));
			break;
		case MotionEvent.ACTION_POINTER_UP:
			index = event.getActionIndex();
			id = event.getPointerId(index);
			if(id == joystickView.getActivePointer()) {
				joystickView.removeActivePointer(); joystickView.viewTouched(event);
				Log.d(TAG, "Left up");
			} else if(id == speedControlView.getActivePointer()) {
				speedControlView.removeActivePointer(); speedControlView.viewTouched(event);
				Log.d(TAG, "Right up");
			}
//			Log.d(TAG, "action pointer up "+id);
//			Log.d(TAG, String.format("action pointer up %d at %f, %f", id, event.getX(index), event.getY(index)));
			break;
		case MotionEvent.ACTION_MOVE:
			nPointer = event.getPointerCount();
			for(int i=0; i<nPointer; i++) {
				id = event.getPointerId(i);
				if(id == joystickView.getActivePointer()) {
					Log.d(TAG, "Left moved");  joystickView.viewTouched(event);
				} else if(id == speedControlView.getActivePointer()) {
					Log.d(TAG, "Right moved");  speedControlView.viewTouched(event);
				}
//				Log.d(TAG, String.format("action move %d at %f, %f", id, event.getX(i), event.getY(i)));
//				Log.d(TAG, "action move");
			}
			break;
		}
		return true;
	}

	public JoystickView getJoystickView() {
		return joystickView;
	}
	
	public SpeedControlView getSpeedControlView() {
		return speedControlView;
	}
}
