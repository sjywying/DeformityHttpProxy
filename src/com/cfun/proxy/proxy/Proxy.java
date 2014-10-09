package com.cfun.proxy.proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.cfun.proxy.HttpProxy.HttpSession;

public class Proxy extends Thread
{
	private ServerSocket serverSocket;

	@Override
	public void run()
	{
		try
		{
			while(true)
			{
				Socket socket= null;
				socket= serverSocket.accept();
				new HttpSession(socket);
			}
		}
		catch(Exception e)
		{}
	}

	public void BindPort() throws IOException
	{
		serverSocket= new ServerSocket(10080);
	}

	public void RelasePort()
	{
		if(serverSocket != null && !serverSocket.isClosed())
		{
			try
			{
				serverSocket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}