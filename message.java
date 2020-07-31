package com.example.duanxin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	//接收短信所需定义的变量
	private TextView sender;
	private TextView content;
	
	private IntentFilter intentFilter;
	private MessageReceiver messageReceiver;
	//发送短信所需定义的变量
	private EditText to;
	private EditText sendMes;
	private Button send;
	
	private IntentFilter sendFilter;
	private SmsStatusReceiver SmsStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//获取控件（接收短信） 
		sender=(TextView)findViewById(R.id.sender);
		content=(TextView)findViewById(R.id.content);
		//获取控件（发送短信）
		to=(EditText)findViewById(R.id.to);
		sendMes=(EditText)findViewById(R.id.sendMes);
		send=(Button)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				SmsManager manager= SmsManager.getDefault();//获取一个SmsManager实例
				Intent sendIntent =new Intent("SENT_SMS_SERVICE");
				PendingIntent pi =PendingIntent.getBroadcast(MainActivity.this,0, sendIntent,0);
				
				 manager.sendTextMessage(to.getText().toString(), null, sendMes.getText().toString(),pi, null);
			}
		
		});
		//注册监听短信发送状态的广播接收器
		sendFilter = new IntentFilter();
		sendFilter.addAction("SENT_SMS_SERVICE");
		SmsStatus =new SmsStatusReceiver();
		registerReceiver(SmsStatus,sendFilter);
		//注册接收短信的广播接收器
		intentFilter=new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		messageReceiver=new MessageReceiver();
		registerReceiver(messageReceiver,intentFilter);
	}
	@Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
		unregisterReceiver(messageReceiver);
		unregisterReceiver(SmsStatus);
	}	
		//创建一个内部类 ，用于获取短信内容
		class  MessageReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO 自动生成的方法存根
				Bundle bundle= intent.getExtras();  //从intent中获取一个Bundle对象
				Object[] pdus = (Object[]) bundle.get("pdus");  //通过pdu密钥获取短信内容
				SmsMessage [] messages= new SmsMessage[pdus.length];//创建一个pdus大小的SmsMessage类型的数组
				for(int i=0;i<messages.length;i++){
					messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);//将pdus数组中的每一个byte字节转换为SmsMessage对象放在message数组中
				}
				String address=messages[0].getOriginatingAddress();//获取号码
				String fullMessage="";
				for(SmsMessage message:messages){
					fullMessage+=message.getMessageBody();//获取短信内容并将每个字节连成完整的信息
				}
				sender.setText(address);
				content.setText(fullMessage);
		}
	}
		//创建一个内部类，用于监听短信发送情况
		class SmsStatusReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO 自动生成的方法存根
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "短信已发送", Toast.LENGTH_LONG).show();

				}
				else{
					Toast.makeText(context, "短信发送失败", Toast.LENGTH_LONG).show();
				}
			}
				
		}
		
}