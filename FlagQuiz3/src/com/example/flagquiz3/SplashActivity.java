package com.example.flagquiz3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class SplashActivity extends Activity {
	protected int SPLASH_SCREEN_DELAY = 1000;

	private Thread splashTread;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_splash_screen);
		if (savedInstanceState == null) {
			splashTread = new Thread() {
				@Override
				public void run() {
					try {
						synchronized (this) {
							wait(SPLASH_SCREEN_DELAY);
						}
					} catch (InterruptedException e) {
					} finally {
						finish();
						Intent intent = new Intent(SplashActivity.this,
								GameScreen.class);
						startActivity(intent);
					}
				}
			};
			splashTread.start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (splashTread) {
				splashTread.notifyAll();
			}
		}
		return true;
	}

}
