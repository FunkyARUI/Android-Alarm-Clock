package com.honliv.su.common;


import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class AlarmService extends Service{

    public Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;
    private Alarm mCurrentAlarm;
    private TelephonyManager mTelephonyManager;
	
	private IBinder binder = new MyBinder();
//	
//	/**
//	 * ����start������߼�
//	 */	
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		AlarmClockManager.setNextAlarm(new MainActivity().context);
//		return super.onStartCommand(intent, flags, startId);
//	}

	/**
	 * ����Activity�󶨷�����߼�
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if(intent != null){
			mCurrentAlarm = (Alarm) intent.getSerializableExtra("alarm");
			if(mCurrentAlarm != null){
				
				AudioManager volMgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				//��ȡϵͳ���õ�����ģʽ
				switch (volMgr.getRingerMode()) {
				//����ģʽ��ֵΪ0����ʱ������������� �������𶯡�����
				case AudioManager.RINGER_MODE_SILENT:
					boolean isBell = (boolean) AlarmPreference
					.getSettingValue(AlarmService.this, AlarmPreference.BELL_MODE_KEY);
					//�������˾���ʱ�����򲥷��������𶯣�����ֻ��
					if(isBell){
						play(mCurrentAlarm);						
					}else{
						openVibrator();
					}
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					//ϵͳ����ģʽ ��ֻ�� ������
					openVibrator();
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					//ϵͳ������ģʽ
					play(mCurrentAlarm);
					break;
				}
			}else{
				stopSelf();
			}
		}
		
		return binder;
		
	}
	
	//������
	private void openVibrator(){
		if(mCurrentAlarm != null && mVibrator != null && mCurrentAlarm.vibrate == 1){
			mVibrator.vibrate(new long[]{500,500}, 0);
		}
	}
	
	//��ʼ����
	private void play(Alarm alarm) {
		//����ǰ��ֹͣ
		stop();
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(alarm.bell);
			mMediaPlayer.prepare();
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
			//��
			openVibrator();
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.stop();
				mp.reset();
				mp.release();
				mMediaPlayer = null;
				return true;
			}
		});
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//��ʼ������
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // ��������
        mTelephonyManager =
                (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(
                mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	//ֹͣ����
	public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        // ֹͣ��
        mVibrator.cancel();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stop();
		mTelephonyManager.listen(mPhoneStateListener,0);
	}
	
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String ignored) {
        	switch(state){
        	case TelephonyManager.CALL_STATE_IDLE://���κ�״̬
        		System.out.println("û�绰");
        		if(mCurrentAlarm != null && mMediaPlayer != null && ! mMediaPlayer.isPlaying()){
        			play(mCurrentAlarm);
        		}
        		break;
        	case TelephonyManager.CALL_STATE_OFFHOOK://����绰ʱ
        		System.out.println("�ӵ绰");
        		stop();
        		break;
        	case TelephonyManager.CALL_STATE_RINGING://�绰����ʱ
        		System.out.println("�绰"+ignored);
        		stop();
        		break;
        	}
        }
    };
	
	public class MyBinder extends Binder{
		public AlarmService getService(){
			return AlarmService.this;
		}
	}

}
