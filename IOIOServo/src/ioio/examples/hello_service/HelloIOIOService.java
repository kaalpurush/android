package ioio.examples.hello_service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

/**
 * An example IOIO service. While this service is alive, it will attempt to
 * connect to a IOIO and blink the LED. A notification will appear on the
 * notification bar, enabling the user to stop the service.
 */
public class HelloIOIOService extends IOIOService implements
		SensorEventListener {

	float tilt;
	boolean mLed;

	/* sensor data */
	SensorManager m_sensorManager;

	private final IBinder myBinder = new IOIOBinder();

	protected IHelloIOIOService listener;

	public interface IHelloIOIOService {
		void onConnect(Boolean isConnected, String user);
	}

	private void registerListeners() {
		// m_sensorManager.registerListener(this,
		// m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
		// SensorManager.SENSOR_DELAY_GAME);
		m_sensorManager.registerListener(this,
				m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
	}

	private void unregisterListeners() {
		m_sensorManager.unregisterListener(this);
	}

	@Override
	protected IOIOLooper createIOIOLooper() {
		return new BaseIOIOLooper() {
			private AnalogInput input_;
			private PwmOutput pwmOutput_;
			private DigitalOutput led_;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
				input_ = ioio_.openAnalogInput(40);
				pwmOutput_ = ioio_.openPwmOutput(12, 50);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				final float reading = input_.read();
				// setText(Float.toString(reading));

				int roll = (int) (554 + (1836 / 20 * (tilt + 10)));

				//Log.d("roll", String.valueOf(roll));

				if (roll > 554 && roll < 2390)
					pwmOutput_.setPulseWidth(roll);

				led_.write(!mLed);

				Thread.sleep(10);
			}
		};
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (intent != null && intent.getAction() != null
				&& intent.getAction().equals("stop")) {
			// User clicked the notification. Need to stop the service.
			nm.cancel(0);
			stopSelf();
		} else {
			// Service starting. Create a notification.
			Notification notification = new Notification(
					R.drawable.ic_launcher, "IOIO service running",
					System.currentTimeMillis());
			notification
					.setLatestEventInfo(this, "IOIO Service", "Click to stop",
							PendingIntent.getService(this, 0, new Intent(
									"stop", null, this, this.getClass()), 0));
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			nm.notify(0, notification);
		}

		m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		registerListeners();
	}

	public void toggleLed() {
		mLed = !mLed;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			tilt = event.values[0];
		}
	}

	public void attachListener(IHelloIOIOService thelistener) {
		listener = thelistener;
	}

	public void detachListener() {
		listener = null;
	}

	public class IOIOBinder extends Binder {
		public HelloIOIOService getService() {
			return HelloIOIOService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return myBinder;
	}

}
