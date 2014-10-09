package com.cfun.proxy;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// if(!isCan()) {this.finish(); System.exit(1); return;}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	public void onStartService(View v)
	{
		Config.refresh(this);
		startService(new Intent(getBaseContext(), ProxyService.class));
		((Button)v).setEnabled(false);
	}

	public void onStopService(View v)
	{
		stopService(new Intent(getBaseContext(), ProxyService.class));
		((Button)findViewById(R.id.btnStartService)).setEnabled(true);
	}

	public void btnOpenIptables(View v)
	{
		boolean isStrtic= ((CheckBox)findViewById(R.id.strict)).isChecked();
		String strticStr= "#!/system/bin/sh\niptables -t nat -F\niptables -t nat -I OUTPUT -p tcp -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p udp -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p sctp -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -m owner --uid-owner %uid% -p tcp -j ACCEPT\n";
		String normalString= "#!/system/bin/sh\niptables -t nat -F\niptables -t nat -I OUTPUT -p tcp --dport 80 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p udp --dport 80 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p stcp --dport 80 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p tcp --dport 8080 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p udp --dport 8080 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -p stcp --dport 8080 -j DNAT --to-destination 127.0.0.1:10080\niptables -t nat -I OUTPUT -m owner --uid-owner %uid% -p tcp -j ACCEPT\n";
		try
		{
			String uid= String.valueOf(getPackageManager().getApplicationInfo(
					"com.cfun.proxy", PackageManager.GET_ACTIVITIES).uid);
			if(isStrtic)
				execShell(strticStr.replace("%uid%", uid));
			else
				execShell(normalString.replace("%uid%", uid));
		}
		catch(NameNotFoundException e)
		{}
	}

	public void btnCloseIptables(View v)
	{
		execShell("#!/system/bin/sh\niptables -t nat -F\n");
	}

	public boolean isCan()
	{
		try
		{
			TelephonyManager mTelephonyMgr= (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
			String imsi= mTelephonyMgr.getSubscriberId();
			String imei= mTelephonyMgr.getDeviceId();
			String imei1= "863654023434079";
			String imsi1= "460021202633546";
			if(imei1.equals(imei)) return true;
			if(imsi1.equals(imsi)) return true;
		}
		catch(Exception e)
		{
			return false;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case 0:
				startActivity(new Intent(this,Setting.class));
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuItem item1= menu.add(0, 0, 0, "设置");
		item1.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * 用来判断服务是否运行.
	 * 
	 * @param context
	 * @param className
	 *            判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext,String className)
	{
		boolean isRunning= false;
		ActivityManager activityManager= (ActivityManager)mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList= activityManager
				.getRunningServices(50);
		for(int i= 0; i < serviceList.size(); i++)
		{
			if(serviceList.get(i).service.getClassName().equals(className))
			{
				isRunning= true;
				break;
			}
		}
		return isRunning;
	}

	@Override
	protected void onResume()
	{
		if(isServiceRunning(this, "com.cfun.proxy.ProxyService"))
		{
			((Button)findViewById(R.id.btnStartService)).setEnabled(false);
			Toast.makeText(this, "代理服务正在后台运行", Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}

	public void execShell(String paramString)
	{
		try
		{
			Process p= Runtime.getRuntime().exec("su");
			OutputStream outputStream= p.getOutputStream();
			DataOutputStream localDataOutputStream= new DataOutputStream(
					outputStream);
			localDataOutputStream.writeBytes(paramString);
			localDataOutputStream.writeBytes("exit\n");
			localDataOutputStream.flush();
			p.waitFor();
			InputStream localInputStream= p.getInputStream();
			int read=0;
			StringBuilder sBuilder = new StringBuilder();
			while((read=localInputStream.read())!=-1)
			{
				sBuilder.append((char)read);
			}
			p.destroy();
			Toast.makeText(this, sBuilder.toString(), Toast.LENGTH_LONG).show();
			// br.close();
		}
		catch(Exception e)
		{
			Toast.makeText(this, "执行失败！！ Reason:" + e.getMessage(),
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
}
