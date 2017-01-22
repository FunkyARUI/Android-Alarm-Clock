package com.honliv.su.common;

import java.util.Calendar;

import com.honliv.su.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmClockManager {

	private final static String TAG = "AlarmClockManager";
	//����
	private static Calendar calendar = Calendar.getInstance();
	//�������
	private static AlarmManager am;
	
	/**
	 * ������ʾ��Ϣ
	 * @param context ������
	 * @param hour Сʱ
	 * @param minute ����
	 */
	public static void setAlarm(Context context , Alarm alarm){
		String[] repeats = context.getResources().getStringArray(R.array.repeat_item);
		long timeMillis = time2Millis(alarm.hour , alarm.minutes , alarm.repeat , repeats);
		//���´�����ʱ��ĺ������浽���ݿ�
		ContentValues values = new ContentValues();
		values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
		AlarmHandle.updateAlarm(context, values, alarm.id);
		Toast.makeText(context, fomatTip(timeMillis) , Toast.LENGTH_SHORT).show();
		System.out.println(fomatTip(timeMillis));
		//��������
		setNextAlarm(context);
	}
	
	/**
	 * ��������
	 * @param context ������
	 */
	public static void setNextAlarm(Context context){
		Log.v(TAG, "��������Ч������");
		Alarm alarm = AlarmHandle.getNextAlarm(context);
		if(alarm != null){
			Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
			intent.putExtra(Alarm.Columns._ID, alarm.id);
			PendingIntent pi = PendingIntent.getBroadcast(context, alarm.id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis, pi);
			//��ʾ֪ͨ
			AlarmNotificationManager.showNotification(context,alarm);
		}else{
			AlarmNotificationManager.cancelNotification(context);
		}
	}
	
	public static void cancelAlarm(Context context , int id ){
		Log.v(TAG, "cancelAlarm");
		Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
		PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		setNextAlarm(context);
	}
	
	private static String fomatTip(long timeMillis) {
	    long delta = timeMillis - System.currentTimeMillis();
        long hours = delta / (1000 * 60 * 60);
        long minutes = delta / (1000 * 60) % 60;
        long days = hours / 24;
        hours = hours % 24;

        String daySeq = (days == 0) ? "" : days+" days and";

        String hourSeq = (hours == 0) ? "" : hours + " hours and";
        	
        String minSeq = (minutes == 0) ? "1 minute" : minutes + " minutes";
        
		return "After"+daySeq+" "+hourSeq+" "+minSeq+", the clock will work";
	}

	public static Long time2Millis(int hour , int minute , String repeat , String[] repeats){
		calendar.setTimeInMillis(System.currentTimeMillis()); 
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		//�����ظ�ģʽΪ ֻ��һ�λ�ÿ��
		if(repeat.equals(repeats[Alarm.ALARM_ONCE]) || repeat.equals(repeats[Alarm.ALARM_EVERYDAY])){
			//��ʱ���Ѿ���ȥ�����Ƴ�һ��
			if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
				System.out.println("��ʱ�ӳ�һ��");
				calendar.roll(Calendar.DATE, 1);
			}
		}else if(repeat.equals(repeats[Alarm.ALARM_MON_FRI])){
			//�����ظ�ģʽΪ ��һ������
			if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
				//������ʱ���Ѿ���ȥ�����Ƴ�3��
				if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
					calendar.roll(Calendar.DATE, 3);
				}
			}else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
				//����
				calendar.roll(Calendar.DATE, 2);
			} else if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
				//����
				calendar.roll(Calendar.DATE, 1);
			}else{
				//��ʱ���Ѿ���ȥ�����Ƴ�һ��
				if(calendar.getTimeInMillis() - System.currentTimeMillis() < 0){
					System.out.println("��ʱ�ӳ�һ��");
					calendar.roll(Calendar.DATE, 1);
				}
			}
		}
		return calendar.getTimeInMillis();
	}
	
}
