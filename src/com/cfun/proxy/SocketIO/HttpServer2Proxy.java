package com.cfun.proxy.SocketIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;



import com.cfun.proxy.HttpProxy.HttpConnection;


public class HttpServer2Proxy extends Thread
{
	//	private HttpClient2Server Brother;
	private byte[] buffer= new byte[40960];
	private HttpConnection conn= null;
	private boolean die= false;
	private BufferedOutputStream oStream;
	private BufferedInputStream iStream;

	public void died()
	{
		die= true;
	}
	public HttpServer2Proxy(HttpConnection conn)
	{
		this.conn= conn;
		setPriority(Thread.MIN_PRIORITY);
		oStream = conn.getClientOUT();
		iStream = conn.getServerIN();
	}

	public void run()
	{
		try
		{
			int byteRead;
			while((byteRead= iStream.read(buffer)) > 0)
			{
				if(byteRead > 0)
				{
					oStream.write(buffer, 0, byteRead);
					oStream.flush();
				}
				else
				{
					break;
				}
				if(die)
				{
					break;
				}
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
		finally
		{
			conn.closeClient();
		}

	}
}
