package com.honliv.su.activity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.honliv.su.R;
import com.honliv.su.common.Alarm;
import com.honliv.su.common.AlarmClockManager;
import com.honliv.su.common.AlarmHandle;
import com.honliv.su.common.AlarmPreference;
import com.honliv.su.common.AlarmService;
import com.honliv.su.common.ShakeDetector;
import com.honliv.su.common.ShakeDetector.OnShakeListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmDealActivity extends Activity {

	private Context context;
	private LinearLayout ll_num;
	private LinearLayout ll_shake;
	private TextView tv_info;
	private TextView tv_tip;
	private TextView tv_num;
	private EditText et_result;
	
	private TextView tv_test;
	
	//���ּ��̲���
	private GridView gv_nums;
	//���ּ������ݴ��
	private List<Map<String, Integer>> lists;
	//���̶�Ӧ��ͼƬ
	private int nums[] = {R.drawable.num_1,R.drawable.num_2,R.drawable.num_3,
			R.drawable.num_4,R.drawable.num_5,R.drawable.num_6,R.drawable.num_7,
			R.drawable.num_8,R.drawable.num_9,R.drawable.num_del,R.drawable.num_0,R.drawable.num_ok};
	//ͼƬ��Ӧ��ֵ
	private String [] tags = {"1","2","3","4","5","6","7","8","9","del","0","ok"};
	//��������
	private Alarm alarm;
	//�����
	private Random random;
	//���
	private int result;
	//��������
	private int times;
	private String numberStr = "";
	
	//ҡ��ֵ
	private int shakeValue = 0 ;
	
	//ȡ������ģʽ
	private int cancelAlaemMode;
	
	private float y1 = 0;
	private float y2 = 0;
	private float y3 = 0;
	
	//����
	private AlarmService alarmService;
	//��������
	private ServiceConnection SConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			alarmService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {

			alarmService = ((AlarmService.MyBinder)service).getService();

//			vibrator = alarmService.mVibrator;
//			if(vibrator != null && alarm != null && alarm.vibrate == 1){
//				vibrator.vibrate(new long[]{500,500}, 0);
//			}
		}
	}; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//�����ޱ�����
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		final Window win = getWindow();
		//�ĸ�������������ʾ��������������Ļ����������Ļ
		win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		setContentView(R.layout.alarm_deal);
		init();
	}

	/*
	 * ��ʼ��
	 */
	private void init() {
		// TODO Auto-generated method stub
		context = this;
		ll_num = (LinearLayout) findViewById(R.id.ll_num);
		ll_shake = (LinearLayout) findViewById(R.id.ll_shake);
		int id = getIntent().getIntExtra(Alarm.Columns._ID, 0);
		if(id != 0){
			//����ID������ӵ���ϸ��Ϣ
			alarm = AlarmHandle.getAlarm(context, id);
			//�������񣬼����绰״̬�����ֲ���
			Intent intent = new Intent(this, AlarmService.class);
			intent.putExtra("alarm", alarm);
			bindService(intent, SConn, Context.BIND_AUTO_CREATE);
			//��ù�������
			cancelAlaemMode = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.CANCEL_MODE_KEY);
			times = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.NUM_TIMES_KEY);
			//�ж�ȡ�����ӵ�ģʽ
			switch(cancelAlaemMode){
			//����ģʽ
			case Alarm.CANCEL_NUM_MODE:
				ll_num.setVisibility(View.VISIBLE);
				ll_shake.setVisibility(View.GONE);
				break;
			//ҡ��ģʽ
			case Alarm.CANCEL_SHAKE_MODE:
				ll_num.setVisibility(View.GONE);
				ll_shake.setVisibility(View.VISIBLE);
				//������õ�ҡ�η�ֵ
				final int shakeThreshold = (Integer) AlarmPreference.getSettingValue(context, AlarmPreference.SHAKE_ITEM_KEY);
				System.out.println("shakeThreshold:"+shakeThreshold);
				final ShakeDetector shakeDetector = new ShakeDetector(context);
				shakeDetector.registerOnShakeListener(new OnShakeListener() {
					@Override
					public void onShake() {
						shakeValue = shakeDetector.shakeValue;
						if(shakeValue - shakeThreshold >= 0){
							release();
							tv_test.setText(getResources().getText(R.string.yaohuangzhe));
							shakeDetector.stop();
							alarmFinish(alarm);
							finish();
						}else{
							NumberFormat nt = NumberFormat.getPercentInstance();
							nt.setMinimumFractionDigits(2);
							tv_test.setText(getResources().getText(R.string.almostchenggong)+ nt.format((double)shakeValue / shakeThreshold));
						}
						
					}
				});
				shakeDetector.start();
				break;
			}
		}else{
			finish();
		}
		tv_test = (TextView) findViewById(R.id.tv_test);
		
		tv_info = (TextView) findViewById(R.id.tv_info);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_num = (TextView) findViewById(R.id.tv_num);
		et_result = (EditText) findViewById(R.id.et_result);
		tv_info.setText((String)getResources().getText(R.string.stillneedunlock) + times + getResources().getText(R.string.cishu));
		random = new Random();
		showNextNumber();

		//��ʼ��adapter������list
		lists = new ArrayList<Map<String,Integer>>();
		for(int i=0;i<nums.length; i++){
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("num", nums[i]);
			lists.add(map);
		}
		gv_nums = (GridView) findViewById(R.id.gv_nums);
		//����adapter
		gv_nums.setAdapter(new SimpleAdapter(context, lists,
				R.layout.grid_item, new String[]{"num"}, new int[]{R.id.iv_item}));
		//���ð�������¼�
		gv_nums.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(tags[position]){
				case "0":
					numberStr += tags[position];
					break;
				case "1":
				case "2":
				case "3":
				case "4":
				case "5":
				case "6":
				case "7":
				case "8":
				case "9":
					if("0".equals(numberStr)){
						numberStr = "";
					}
					numberStr += tags[position];
					break;
				case "del":
					if(!TextUtils.isEmpty(numberStr) && numberStr.length() > 0){
						numberStr = numberStr.substring(0, numberStr.length() - 1);
					}
					break;
				case "ok":
					if(!TextUtils.isEmpty(et_result.getText()) 
							&& Integer.parseInt(et_result.getText().toString()) == result){
						numberStr = "";
						tv_tip.setText(getResources().getText(R.string.huidazhengque));
						times--;
					}else{
						numberStr = "";
						tv_tip.setText(getResources().getText(R.string.huidacuowu));
					}
					if(times == 0){
						release();
						alarmFinish(alarm);
						finish();
					}
					showNextNumber();
					tv_info.setText((String)getResources().getText(R.string.stillneedunlock) + times + getResources().getText(R.string.cishu));
					break;
				}
				et_result.setText(numberStr);
				et_result.setSelection(numberStr.length());
			}
		});
	}

	/*
	 * ������������������ӡ����������� 
	 */
	private void showNextNumber() {
		int a = random.nextInt(100);
		int b = random.nextInt(100);
		int c = random.nextInt(3);
		switch(c){
		case 0:
			//�ӷ�
			tv_num.setText(a + " + " + b + " =");
			result = a + b;
			break;
		case 1:
			//����
			if(a > b){
				tv_num.setText(a + " - " + b + " =");
				result = a - b;
			}else{
				tv_num.setText(b + " - " + a + " =");
				result = b - a;
			}
			break;
		case 2:
			//�˷�
			b = random.nextInt(11);
			tv_num.setText(a + " * " + b + " =");
			result = a * b;
			break;
		}
		
	}

	//�ͷ�����������Դ
	private void release(){
		//�ر���������
		if(alarmService != null){
			alarmService.stop();
		}
	}
	
	private void alarmFinish(Alarm alarm){
		//��ֻ��һ��������enabledΪ0
		String[] repeats = context.getResources().getStringArray(R.array.repeat_item);
		if(alarm.repeat.equals(repeats[Alarm.ALARM_ONCE])){
			ContentValues values = new ContentValues();
			values.put(Alarm.Columns.ENABLED, 0);
			//�������ݿ�
			AlarmHandle.updateAlarm(context, values, alarm.id);
		}else{
			long timeMillis = AlarmClockManager.time2Millis(alarm.hour , alarm.minutes , alarm.repeat , repeats);
			//���´�����ʱ��ĺ������浽���ݿ�
			ContentValues values = new ContentValues();
			values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
			AlarmHandle.updateAlarm(context, values, alarm.id);
		}
		AlarmClockManager.setNextAlarm(context);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		release();
		//����ָ������
		if(alarmService != null){
			unbindService(SConn);
		}
	}
	
	/*
	 * ���η��ؼ����˵�����home����������
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_HOME:
		case KeyEvent.KEYCODE_MENU:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			return true;
		default:
			return false;
		}
	}

	/*
	 * �����¼�,��������ָͬʱ�»��������200��ȡ������
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()&MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			System.out.println(event.getPointerCount());
			if(event.getPointerCount() == 3){
				System.out.println("�»�ȡ������");
				y1 = event.getY(0);
				y2 = event.getY(1);
				y3 = event.getY(2);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if(event.getPointerCount() == 3){
				System.out.println("�ͷ�");
				if(event.getY(0) - y1 > 200 && event.getY(1) - y2 > 200 
						&& event.getY(2) - y3 > 200){
					//ȡ������
					alarmFinish(alarm);
					System.out.println("ȡ������");
					finish();
				}
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
}
