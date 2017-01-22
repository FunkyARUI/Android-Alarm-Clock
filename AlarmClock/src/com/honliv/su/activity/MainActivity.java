package com.honliv.su.activity;

import java.util.List;

import com.honliv.su.R;
import com.honliv.su.common.Alarm;
import com.honliv.su.common.AlarmClockManager;
import com.honliv.su.common.AlarmHandle;
import com.tencent.tauth.Tencent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private final static int DELETE = 1;
	private final static int ABOUT = 2;
	
	private Context context;
	private final static String TAG = "MainActivity";
	private ListView lv_clocks;
	private List<Alarm> alarms;
	private AlarmAdapter adapter;
	//��¼�����ؼ���ʱ��
	private long downTime = 0;
	//��Ѷ
	private Tencent mTencent; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//�����ޱ�����
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		context = this;
		//mTencent = Tencent.createInstance("1104776810", context);
		AlarmClockManager.setNextAlarm(context);
		init();
	}

	//��ʼ��
	private void init() {
		//��ʼ����ť�����onClickʱ��
		findViewById(R.id.ib_add).setOnClickListener(this);
		findViewById(R.id.ib_setting).setOnClickListener(this);
		lv_clocks = (ListView) findViewById(R.id.lv_clocks);
		lv_clocks.setDivider(new ColorDrawable(Color.WHITE));  
		lv_clocks.setDividerHeight(1); 
		//�õ���ǰ��������
		getAlarms(this);
		adapter = new AlarmAdapter();
		lv_clocks.setAdapter(adapter);
	}

	//��õ�ǰ��������
	private void getAlarms(Context context) {
		Log.v(TAG, "��������б�");
		alarms = AlarmHandle.getAlarms(context);
	}

	//�Զ���ListView��������
	class AlarmAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(alarms != null){
				return alarms.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return alarms.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
				holder = new Holder();
				convertView.setTag(holder);
				
				//��ʾ������Ϣ��LinearLayout
				holder.ll_info = (LinearLayout) convertView.findViewById(R.id.ll_info);
				//ʱ��
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				//�ظ�
				holder.tv_repeat = (TextView) convertView.findViewById(R.id.tv_repeat);
				//����
				holder.cb_switch = (CheckBox) convertView.findViewById(R.id.cb_switch);
				
			}else{
				holder = (Holder) convertView.getTag();
			}
			
			final Alarm alarm = alarms.get(position);
			holder.ll_info.setTag(alarm.id);
			//��������༭����
			holder.ll_info.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(context, NewClockActivity.class);
					intent.putExtra("alarm",alarms.get(position));
					startActivityForResult(intent, 10);
				}
			});
			String hourStr = (alarm.hour+"").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
			String minutesStr = (alarm.minutes+"").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
			holder.tv_time.setText(hourStr + ":" + minutesStr);
			holder.tv_repeat.setText(alarm.repeat);
			
			holder.cb_switch.setChecked(alarm.enabled == 1 ? true : false);
			
			//���ؿ���
			holder.cb_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues values = new ContentValues();
					boolean isChecked = true;
					if(((CheckBox)v).isChecked()){
						isChecked = true;
					}else{
						isChecked = false;
					}
					//�������ݿ����е�����
					values.put(Alarm.Columns.ENABLED, isChecked ? 1 : 0);
					AlarmHandle.updateAlarm(context, values, alarm.id);
					alarms.get(position).enabled = isChecked ? 1 : 0;
					if(isChecked){
						//������
						AlarmClockManager.setAlarm(context, alarm);
					}else{
						//�ر�����
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}
					
				}
			});
			return convertView;
		}
		
		class Holder{
			LinearLayout ll_info;
			TextView tv_time;
			TextView tv_repeat;
			CheckBox cb_switch;
		}
	}

	/*
	 * �ϲ�ҳ�淵�غ�����Ӧ���� 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "����adpater");
		switch(resultCode){
		case Alarm.UPDATE_ALARM :
			if(adapter != null){
				getAlarms(context);
				adapter.notifyDataSetChanged();
			}
			Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
			if(alarm != null){
				//������
				AlarmClockManager.setAlarm(context, alarm);
			}
			break;
		case Alarm.DELETE_ALARM:
			if(adapter != null){
				getAlarms(context);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	/*
	 * ������������ť�ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch(v.getId()){
		case R.id.ib_add:
			intent = new Intent(this,NewClockActivity.class);
			startActivityForResult(intent, 10);
			break;
		case R.id.ib_setting:
			intent = new Intent(this,SettingActivity.class);
			startActivity(intent);
			break;
		}
		
	}

	/*
	 * �����ٴλ�ý��� �������� 
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(adapter != null){
			Log.v(TAG, "onResume�и��������б�");
			getAlarms(context);
			adapter.notifyDataSetChanged();
		}
	}
	
	/*
	 * �����˵� 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * �˵������¼�
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.action_add:
			Intent intent = new Intent(this,NewClockActivity.class);
			startActivityForResult(intent, 10);
			break;
		case R.id.action_del:
			showDialog(DELETE);
			break;
		case R.id.action_about:
			showDialog(ABOUT);
			break;
		case R.id.action_suggest:
			/*
			 * ��QQ������档
			 * û��װQQ�Ļ��������ؽ��档
			 * 
			 * �е��ֻ��ᱨ�Ҳ���android.permission.WRITE_APN_SETTINGSȨ��
			 * Android 4.0�Ժ�� google���������Ȩ�ޡ�
			 * 4.0�Ժ����������Ȩ�������ַ�����1��root��2������APK��UID��system��һ����
			 */
			mTencent.startWPAConversation(MainActivity.this,"943689625", "��ã�");
			break;
		case R.id.action_settings:
			intent = new Intent(this,SettingActivity.class);
			startActivity(intent);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/*
	 * �����Ի��� 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id){
		case DELETE:
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.action_del)
			.setMessage("ȷ��Ҫɾ������������")
			.setNegativeButton(getResources().getText(R.string.fordeny), null)
			.setPositiveButton(getResources().getText(R.string.forsure), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AlarmHandle.deleteAllAlarm(context);
					//����adpater
					if(adapter != null){
						getAlarms(context);
						adapter.notifyDataSetChanged();
					}
					dialog.dismiss();
				}
			}).create();
			break;
		case ABOUT:
			dialog = new AlertDialog.Builder(context)
			.setTitle(R.string.action_about)
			.setMessage(R.string.about_content)
			.setNegativeButton(getResources().getText(R.string.close), null)
			.create();
			break;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(System.currentTimeMillis() - downTime > 2000){
				Toast.makeText(context, getResources().getText(R.string.quittext), Toast.LENGTH_SHORT).show();
				downTime = System.currentTimeMillis();
			}else{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
