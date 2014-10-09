package com.cfun.proxy.SocketIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;



import com.cfun.proxy.HttpProxy.HttpConnection;


public class SSLclient2Proxy
{
	//	private HttpClient2Server Brother;
	private byte[] buffer= new byte[40960];
	private HttpConnection conn= null;
	public SSLclient2Proxy(HttpConnection conn,HttpFirstLine hfl) throws IOException
	{
		this.conn= conn;
		conn.getSerrverOUT().write((hfl.Method+" "+hfl.Host+":"+hfl.Port+" HTTP/1.1\r\n").getBytes("iso8859-1"));
	}

	public void doSSL()
	{
		try
		{
			BufferedOutputStream bos= conn.getSerrverOUT();
			BufferedInputStream bis = conn.getClientIN();
			int byteRead;
			while((byteRead= bis.read(buffer)) > 0)
			{
				if(byteRead > 0)
				{
					bos.write(buffer, 0, byteRead);
					bos.flush();
				}
				else
				{
					break;
				}
			}
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}

	}
}
