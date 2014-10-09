package com.cfun.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.cfun.proxy.proxy.Proxy;

public class ProxyService extends Service
{
	private Thread service;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onDestroy()
	{
		if(service != null)
		{
			((Proxy)service).RelasePort();
			((Proxy)service).interrupt();
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		try
		{			
			((Proxy)service).BindPort();
		}
		catch(Exception e)
		{
			Toast.makeText(this.getBaseContext(), "启动失败，原因："+e.getMessage(), Toast.LENGTH_LONG).show();
			return START_NOT_STICKY;
		}
		if(service != null && !service.isAlive())
		{
			service.start();
			Toast.makeText(this.getBaseContext(), "启动成功", Toast.LENGTH_LONG).show();
		}
		else
		{
			if(service==null)
				Toast.makeText(this.getBaseContext(), "服务异常，无法启动", Toast.LENGTH_LONG).show();
			else if(service.isAlive())
			Toast.makeText(this.getBaseContext(), "服务正在运行", Toast.LENGTH_LONG).show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		service= new Proxy();
		super.onCreate();
	}

}
