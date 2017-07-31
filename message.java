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
	//���ն������趨��ı���
	private TextView sender;
	private TextView content;
	
	private IntentFilter intentFilter;
	private MessageReceiver messageReceiver;
	//���Ͷ������趨��ı���
	private EditText to;
	private EditText sendMes;
	private Button send;
	
	private IntentFilter sendFilter;
	private SmsStatusReceiver SmsStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//��ȡ�ؼ������ն��ţ� 
		sender=(TextView)findViewById(R.id.sender);
		content=(TextView)findViewById(R.id.content);
		//��ȡ�ؼ������Ͷ��ţ�
		to=(EditText)findViewById(R.id.to);
		sendMes=(EditText)findViewById(R.id.sendMes);
		send=(Button)findViewById(R.id.send);
		send.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO �Զ����ɵķ������
				SmsManager manager= SmsManager.getDefault();//��ȡһ��SmsManagerʵ��
				Intent sendIntent =new Intent("SENT_SMS_SERVICE");
				PendingIntent pi =PendingIntent.getBroadcast(MainActivity.this,0, sendIntent,0);
				
				 manager.sendTextMessage(to.getText().toString(), null, sendMes.getText().toString(),pi, null);
			}
		
		});
		//ע��������ŷ���״̬�Ĺ㲥������
		sendFilter = new IntentFilter();
		sendFilter.addAction("SENT_SMS_SERVICE");
		SmsStatus =new SmsStatusReceiver();
		registerReceiver(SmsStatus,sendFilter);
		//ע����ն��ŵĹ㲥������
		intentFilter=new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		messageReceiver=new MessageReceiver();
		registerReceiver(messageReceiver,intentFilter);
	}
	@Override
	protected void onDestroy() {
		// TODO �Զ����ɵķ������
		super.onDestroy();
		unregisterReceiver(messageReceiver);
		unregisterReceiver(SmsStatus);
	}	
		//����һ���ڲ��� �����ڻ�ȡ��������
		class  MessageReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO �Զ����ɵķ������
				Bundle bundle= intent.getExtras();  //��intent�л�ȡһ��Bundle����
				Object[] pdus = (Object[]) bundle.get("pdus");  //ͨ��pdu��Կ��ȡ��������
				SmsMessage [] messages= new SmsMessage[pdus.length];//����һ��pdus��С��SmsMessage���͵�����
				for(int i=0;i<messages.length;i++){
					messages[i]=SmsMessage.createFromPdu((byte[])pdus[i]);//��pdus�����е�ÿһ��byte�ֽ�ת��ΪSmsMessage�������message������
				}
				String address=messages[0].getOriginatingAddress();//��ȡ����
				String fullMessage="";
				for(SmsMessage message:messages){
					fullMessage+=message.getMessageBody();//��ȡ�������ݲ���ÿ���ֽ�������������Ϣ
				}
				sender.setText(address);
				content.setText(fullMessage);
		}
	}
		//����һ���ڲ��࣬���ڼ������ŷ������
		class SmsStatusReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO �Զ����ɵķ������
				if(getResultCode()==RESULT_OK){
					Toast.makeText(context, "�����ѷ���", Toast.LENGTH_LONG).show();

				}
				else{
					Toast.makeText(context, "���ŷ���ʧ��", Toast.LENGTH_LONG).show();
				}
			}
				
		}
		
}