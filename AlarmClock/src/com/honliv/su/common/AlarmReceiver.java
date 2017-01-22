package com.honliv.su.common;

import com.honliv.su.activity.AlarmDealActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//����
//			Intent service = new Intent(context, AlarmService.class);
//			context.startService(service);
			AlarmClockManager.setNextAlarm(context);
		}else{
			//ת���������
			Intent deal = new Intent(context, AlarmDealActivity.class);
			deal.putExtra(Alarm.Columns._ID, intent.getIntExtra(Alarm.Columns._ID, 0));
			deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deal);
		}
	}
}
