package com.cfun.proxy.HttpProxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class HttpConnection
{
	private Socket clientSocket;
	private Socket serverSocket;

	private BufferedInputStream clientIn;
	private BufferedInputStream serverIn;
	private BufferedOutputStream clientOut;
	private BufferedOutputStream serverOut;


	public void setNewClient(Socket client) throws IOException
	{
		closeClient();
		this.clientSocket = client;
		this.clientIn = new BufferedInputStream(client.getInputStream());
		this.clientOut = new BufferedOutputStream(client.getOutputStream());
	}
	public void setNewServer(Socket server) throws IOException
	{
		closeServer();
		this.serverSocket = server;
		this.serverIn = new BufferedInputStream(server.getInputStream());
		this.serverOut = new BufferedOutputStream(server.getOutputStream());
	}

	public BufferedInputStream getClientIN()
	{
		return clientIn;
	}

	public BufferedInputStream getServerIN()
	{
		return serverIn;
	}

	public BufferedOutputStream getClientOUT()
	{
		return clientOut;
	}

	public BufferedOutputStream getSerrverOUT()
	{
		return serverOut;
	}

	public void allClose()
	{
		closeClient();
		closeServer();
	}

	public void closeClient()
	{
		try
		{
			if(clientSocket != null && !clientSocket.isClosed())
			{
				clientIn.close();
				clientOut.close();
				clientSocket.close();
			}
		}
		catch(IOException e)
		{}
	}

	public void closeServer()
	{
		try
		{
			if(serverSocket != null && !serverSocket.isClosed())
			{
				serverOut.close();
				serverIn.close();
				serverSocket.close();
			}
		}
		catch(IOException e)
		{}
	}
	
	public boolean isConnectToServer()
	{
		return serverSocket!=null && !serverSocket.isClosed();
	}
}

