package com.honliv.su.activity;

import java.util.Calendar;

import com.honliv.su.R;
import com.honliv.su.common.Alarm;
import com.honliv.su.common.AlarmClockManager;
import com.honliv.su.common.AlarmHandle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class NewClockActivity extends Activity implements View.OnClickListener{

	//�򿪶Ի���ı�־
	private final static int SHOW_REPEAT = 1;
	private final static int SHOW_LABEL = 2;
	private final static int DEL_ALARM = 3;
	
	private TimePicker timePicker;
	private TextView tv_repeat;
	private TextView tv_bell;
	private TextView tv_label;
	private CheckBox cb_vibration;
	private Button bt_del;
	
	private Alarm alarm;
	
	private boolean isNew = false;
	
	private Context context;
	
	private String bellPath;
	
	//�Ƿ����
	private int vibration = 1;
	//��¼�ظ���ʽ 0ֻ��һ�Σ�1��һ�����壬2ÿ��
	private int repeat = 0;
	private int repeatOld = 0;
	private int hour;
	private int minute;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//�����ޱ�����
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_clock);
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		context = this;
		timePicker = (TimePicker) findViewById(R.id.clock);
		//����24Сʱ��
		timePicker.setIs24HourView(true);
		//���ý�ֹ��������
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		tv_repeat = (TextView) findViewById(R.id.tv_repeat);
		tv_bell = (TextView) findViewById(R.id.tv_bell);
		tv_label = (TextView) findViewById(R.id.tv_label);
		cb_vibration = (CheckBox) findViewById(R.id.cb_offon);
		bt_del = (Button) findViewById(R.id.bt_del);
		//�ж����½����Ǳ༭
		alarm = (Alarm) getIntent().getSerializableExtra("alarm");
		if(alarm == null ){
			//�½�
			isNew = true;
			((TextView)findViewById(R.id.tv_title)).setText(getResources().getText(R.string.newclock));
			Calendar calendar = Calendar.getInstance();
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
			timePicker.setCurrentHour(hour);
			timePicker.setCurrentMinute(minute);
			tv_repeat.setText(getResources().getText(R.string.ringonece));
			bellPath = getDefaultbell();
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			cb_vibration.setChecked(true);
			tv_label.setText(getResources().getText(R.string.clock));
			//����ɾ����ť
			bt_del.setVisibility(View.GONE);
		}else{
			//�༭
			isNew = false;
			((TextView)findViewById(R.id.tv_title)).setText(getResources().getText(R.string.editclock));
			timePicker.setCurrentHour(alarm.hour);
			timePicker.setCurrentMinute(alarm.minutes);
			hour = alarm.hour;
			minute = alarm.minutes;
			tv_repeat.setText(alarm.repeat);
			repeatOld = alarm.repeat.equals(getResources().getText(R.string.ringonece)) ? 0 :alarm.repeat.equals(getResources().getText(R.string.mondaytofriday)) ? 1 :2;
			repeat = repeatOld;
			bellPath = alarm.bell;
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			cb_vibration.setChecked(alarm.vibrate == 1 ? true : false);
			tv_label.setText(alarm.label);
			//��ʾɾ����ť
			bt_del.setVisibility(View.VISIBLE);
			bt_del.setOnClickListener(this);
		}
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		findViewById(R.id.tv_ok).setOnClickListener(this);
		findViewById(R.id.ll_repeat).setOnClickListener(this);
		findViewById(R.id.ll_bell).setOnClickListener(this);
		findViewById(R.id.ll_label).setOnClickListener(this);
		cb_vibration.setOnClickListener(this);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int mit) {
				// TODO Auto-generated method stub
				hour = hourOfDay;
				minute = mit;
			}
		});
		
	}

	private String getDefaultbell() {
		String ret = "";
		Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
				null, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst()){
				ret = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
			}
			cursor.close();
		}
		return ret;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_ok:
			Intent intent = new Intent();
			if(isNew){
				alarm = new Alarm();
				alarm.hour = hour;
				alarm.minutes = minute;
				alarm.repeat = repeat == 0 ? (String) getResources().getText(R.string.ringonece) : (String) (repeat == 1 ? getResources().getText(R.string.mondaytofriday) : getResources().getText(R.string.everyday));
				alarm.bell = bellPath;
				alarm.vibrate = vibration;
				alarm.label = TextUtils.isEmpty(tv_label.getText()) ? "" : tv_label.getText().toString();
				alarm.enabled = 1;	
				alarm.nextMillis = 0;	
				//����
				AlarmHandle.addAlarm(context, alarm);
				intent.putExtra("alarm", alarm);
			}else{
				ContentValues values = new ContentValues(); 
				if(alarm.hour != hour){
					values.put(Alarm.Columns.HOUR, hour);
					alarm.hour = hour;
				}
				if(alarm.minutes != minute){
					values.put(Alarm.Columns.MINUTES, minute);
					alarm.minutes = minute;
				}
				if(repeatOld != repeat){
					values.put(Alarm.Columns.REPEAT, repeat == 0 ? (String) getResources().getText(R.string.ringonece) : (String) (repeat == 1 ? getResources().getText(R.string.mondaytofriday) : getResources().getText(R.string.everyday)));
					alarm.repeat = repeat == 0 ? (String) getResources().getText(R.string.ringonece) : (String) (repeat == 1 ? getResources().getText(R.string.mondaytofriday) : getResources().getText(R.string.everyday));
				}
				if(!TextUtils.isEmpty(bellPath) && !alarm.bell.equals(bellPath)){
					values.put(Alarm.Columns.BELL, bellPath);
				}
				if(vibration != alarm.vibrate){
					values.put(Alarm.Columns.VIBRATE, vibration);
				}
				if(!TextUtils.isEmpty(tv_label.getText()) && !alarm.label.equals(tv_label.getText())){
					values.put(Alarm.Columns.LABEL, tv_label.getText().toString());
				}
				if(alarm.enabled != 1){
					values.put(Alarm.Columns.ENABLED,1);
					alarm.enabled = 1;
				}
				if(values.size() > 0){
					AlarmHandle.updateAlarm(context, values, alarm.id);
					intent.putExtra("alarm", alarm);
				}
			}
			//���ظ���
			setResult(Alarm.UPDATE_ALARM,intent);
			finish();
			break;
		case R.id.tv_cancel:
			finish();
			break;
		case R.id.ll_repeat:
			showDialog(SHOW_REPEAT);
			break;
		case R.id.ll_bell:
			// TODO Auto-generated method stub  
            Intent selectBell = new Intent(NewClockActivity.this,SelectBellActivity.class);
            selectBell.putExtra("bellPath", bellPath);
            selectBell.putExtra("bellName", tv_bell.getText());
            startActivityForResult(selectBell, 1);  
			break;
		case R.id.cb_offon:
			if(cb_vibration.isChecked()){
				vibration = 1;
			}else{
				vibration = 0;
			}
			break;
		case R.id.ll_label:
			showDialog(SHOW_LABEL);
			break;
		case R.id.bt_del:
			//����ɾ�����ӶԻ���
			showDialog(DEL_ALARM);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		switch(id){
		case SHOW_REPEAT:
			dialog = new AlertDialog.Builder(context)
				.setTitle(getResources().getText(R.string.repeat_text))
				.setSingleChoiceItems(R.array.repeat_item, repeatOld, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv_repeat.setText(getResources().getStringArray(R.array.repeat_item)[which]);
						repeat = which;
						dialog.dismiss();
					}
				})
				.setNegativeButton(getResources().getText(R.string.fordeny), null).create();
			break;
		case SHOW_LABEL:
			final View view = inflater.inflate(R.layout.label_dialog, null);
			final EditText et = (EditText) view.findViewById(R.id.et_label);
			et.setText(tv_label.getText());
			dialog = new AlertDialog.Builder(context)
				.setTitle(getResources().getText(R.string.label_text))
				.setView(view)
				.setNegativeButton(getResources().getText(R.string.fordeny), null)
				.setPositiveButton(getResources().getText(R.string.forsure), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						tv_label.setText(et.getText().toString());						
					}
				}).create();			
			break;
		case DEL_ALARM:
			dialog = new AlertDialog.Builder(context)
			.setTitle(getResources().getText(R.string.del_clock))
			.setMessage(getResources().getText(R.string.ifyoudeleteclock))
			.setNegativeButton(getResources().getText(R.string.fordeny), null)
			.setPositiveButton(getResources().getText(R.string.forsure), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(alarm.enabled == 1){
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}
					AlarmHandle.deleteAlarm(context, alarm.id);
					setResult(Alarm.DELETE_ALARM);
					finish();
				}
			}).create();	
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	//���ѡ�����������ƺ�·��
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == 100){
			tv_bell.setText(data.getStringExtra("name"));
			bellPath = data.getStringExtra("path");
		}
	}

}
