package com.honliv.su.common;

import com.honliv.su.R;
import com.honliv.su.activity.MainActivity;
import com.honliv.su.activity.WelcomeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmNotificationManager {

	private static NotificationManager notificationManager;
	
	/*
	 * ��ʾ״̬��֪ͨͼ��
	 */
	public static void showNotification(Context context, Alarm alarm){
		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		Notification notification = new Notification();
		//����ͼ��
		notification.icon = R.drawable.icon;
		// �����ڵ����֪ͨ���е�"���֪ͨ"�󣬴�֪ͨ������� ������FLAG_ONGOING_EVENTһ��ʹ�� 
		notification.flags |= Notification.FLAG_NO_CLEAR;
		// ����֪ͨ�ŵ�֪ͨ����"Ongoing"��"��������"����  
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		Intent intent = new Intent(context, MainActivity.class);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		String title = context.getResources().getString(R.string.app_name);
		String hourStr = (alarm.hour+"").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
		String minutesStr = (alarm.minutes+"").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
		String str = hourStr + ":" + minutesStr + "\t" + alarm.label + "\t" + alarm.repeat;
		notification.setLatestEventInfo(context, title, str, pi);
		notificationManager.notify(0, notification);
	}
	
	
	/*
	 * ȡ��״̬��֪ͨͼ��
	 */
	public static void cancelNotification(Context context){
//		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		if(notificationManager != null){
			notificationManager.cancelAll();
		}
	}
	
}
