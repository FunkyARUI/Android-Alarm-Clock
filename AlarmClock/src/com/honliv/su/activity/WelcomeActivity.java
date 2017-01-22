package com.honliv.su.activity;

import com.honliv.su.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class WelcomeActivity extends Activity {
	
	private LinearLayout launch;
	private Animation fadeIn;
	private Animation fadeInScale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// ������title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		init();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		fadeIn.setAnimationListener(new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				launch.setAnimation(fadeInScale);
			}
		});
		fadeInScale.setAnimationListener(new AnimationListener() {			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//������ɺ���ת��������
				Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	private void init() {
		// TODO Auto-generated method stub
		launch = (LinearLayout) findViewById(R.id.launch);
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
		fadeIn.setDuration(500);
		fadeIn.setFillAfter(true);
		fadeInScale = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in_scale);
		fadeInScale.setDuration(3000);
		fadeInScale.setFillAfter(true);
		launch.startAnimation(fadeIn);
	}

	//���η��ؼ�
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
