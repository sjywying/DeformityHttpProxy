package com.cfun.proxy.SocketIO;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Locale;

import com.cfun.proxy.Config;
import com.cfun.proxy.Exception.ClientReadFirstLineExpection;
import com.cfun.proxy.Exception.FirstLineFormatErrorExpection;
import com.cfun.proxy.Exception.HostNotFoundExpection;
import com.cfun.proxy.Exception.HttpMethodNotSupportExpection;
import com.cfun.proxy.HttpProxy.HttpConnection;


public class Client2Proxy
{
	private HttpConnection conn= null;
	private HttpFirstLine hfl = null;
	private String OldHost="";
	private int      OldPort =80;

	private int content_length= 0;
	private byte[] buffer= new byte[10240];
	private StringBuilder lineBuilder=new StringBuilder();

	private BufferedInputStream iStream= null;
	private BufferedOutputStream oStream= null;
	
//	private int ReadTimes = 1;

	private HttpServer2Proxy S2C=null;
	public Client2Proxy(HttpConnection conn) throws IOException
	{
		this.conn= conn;
		iStream= conn.getClientIN();
	}

	public void doRequest()
	{
		try
		{
			while(true)
			{
				boolean isSSL;
				isSSL= analyseFirstLine();
				if(isSSL)
				{
					new SSLclient2Proxy(conn, hfl).doSSL();
					break;
				}
				else
				{
					writeFirstLine();
					analyseAndWriteHead();
					writeBody();
					oStream.flush();
				}
			}
			
		}
		catch (HttpMethodNotSupportExpection e)
		{
			System.out.println(e.getMessage());
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			if(S2C!=null)
				S2C.died();
		}
	}

	/***
	 * 分析第一行数据 如果没用代理 则建立到服务端的连接 返回值为是否是SSL连接
	 * @throws HttpMethodNotSupportExpection 
	 * @throws HostNotFoundExpection 
	 * @throws IOException 
	 * @throws FirstLineFormatErrorExpection 
	 */
	protected boolean analyseFirstLine() throws HttpMethodNotSupportExpection, HostNotFoundExpection, IOException, FirstLineFormatErrorExpection
	{
		String firstLineString = getLine(iStream);
		if(firstLineString.isEmpty()) throw new ClientReadFirstLineExpection(getLine(iStream));
//		String method = firstLineString.substring(0, 7);
		//判断方法是否被支持
//		if(!isSupport(method)) throw new HttpMethodNotSupportExpection("ReadCount:"+(ReadTimes++));
		//解析第一行数据
		hfl =new  HttpFirstLine(firstLineString.trim());
		//设置HttpConnection的Server连接
		ConnectToServer();
		//判断是否是SSL，对SSL进行特殊处理
		return  hfl.isSSL;
		
	}
	protected void writeFirstLine() throws IOException
	{
		//是否在Http请求前插入数据
		lineBuilder.delete(0, lineBuilder.length());
		if(Config.isBeforeURL)
			oStream.write(("GET http://"+Config.beforeURL+" HTTP/1.1\r\nConnection: Keep-Alive\r\n\r\n").getBytes("iso8859-1"));
		lineBuilder.append(hfl.getMethod());
		lineBuilder.append(' ');
		if(Config.isProxyServer) //在使用代理服务器时才插入Http和请求头
		{
			lineBuilder.append("http://");
			//替换请求行的URL操作
			lineBuilder.append(Config.isReplaceURL?Config.replaceURL:hfl.getHost());
		}
		lineBuilder.append(hfl.Uri);
		//是否加后缀
		if(Config.isSuffix)
		{
			if(hfl.Uri.charAt(hfl.Uri.length()-1)!='?')
				lineBuilder.append(hfl.Uri.indexOf('?')>0?'&':'?');
			lineBuilder.append(Config.suffix);
			lineBuilder.append("=a");
		}
		lineBuilder.append(' ');
		//是否更改HTTP协议版本
		lineBuilder.append(Config.isReplaceProtectVersion?Config.replaceProtectVersion : hfl.Version);
		lineBuilder.append("\r\n");
			
		oStream.write(lineBuilder.toString().getBytes("iso8859-1"));
//		System.out.print(lineBuilder.toString());
		
	}

	protected void analyseAndWriteHead() throws IOException
	{
		if(Config.isCustom) 
		{//要插入自定义信息
		String insert = Config.custom.replace("%HOST%", hfl.Host).replace("%PORT%", String.valueOf(hfl.Port)).replace("%URI%", hfl.Uri);
		int index = insert.indexOf("URLEncode(");
		int index2 = insert.indexOf(')');
		if(index>-1 && index2>=index+10)
		{
			String s1 = insert.substring(0, index);
			String s2 =insert.substring(index+10,index2);
			String s3 = insert.substring(index2+1);
			insert = s1+URLEncoder.encode(s2,"iso8859-1")+s3;
		}
		oStream.write(( insert+ "\r\n").getBytes("iso8859-1"));
		}
		content_length= 0;
		byte[] CLCR= {'\r', '\n'};
		for(String line= getLine(iStream); line.length() > 2; line= getLine(iStream))
		{
			String littleLine = null;
			littleLine = line.length()>14 ? line.substring(0, 14).toLowerCase(Locale.ENGLISH)  :line.toLowerCase(Locale.ENGLISH);
			if(littleLine.startsWith("content-length"))
			{
				content_length= Integer.parseInt(line.substring(15).trim());
			}
			else if(littleLine.startsWith("host"))
			{
				if(Config.isReplaceHost)
					line = "Host: "+Config.replaceHost+"\r\n";
				if(Config.isInsertHost)
					line = ("Host: "+Config.insertHost+"\r\n"+line);
			}
			else if(littleLine.startsWith("accept"))
			{
				if(Config.isReplaceAccept)
					line = Config.replaceAccept+"\r\n";
			}
			else if(littleLine.startsWith("connection"))
			{
				if(Config.isReplaceConnection) line ="Connection: "+Config.replaceConnection+"\r\n";
			}
			else if(littleLine.startsWith("x-online-host"))
			{
				if(Config.isXOnlineHost)
					line ="X-Online-Host: "+Config.xOnlineHost+"\r\n";
				if(Config.isInsertX)
					line= "X-Online-Host: "+Config.insertX+"\r\n"+line;
			}
			oStream.write(line.getBytes("iso8859-1"));
//			System.out.print(line);
		}
		oStream.write(CLCR);
//		System.out.println();
		}

	protected void writeBody() throws IOException
	{
		while(content_length > 0)
		{
			int len= iStream.read(buffer, 0, content_length);
			content_length-= len;
			oStream.write(buffer,0,len);
		}
	}

	public String getLine(BufferedInputStream iStream) throws IOException
	{
		int l= 0;
		StringBuilder line= new StringBuilder(128);
		while(l != '\n')
		{
			l= iStream.read();
			if(l != -1)
			{
				line.append((char)l);
			}
			else
				break;
		}
		return line.toString();
	}

	protected boolean isSupport(String methord)
	{
		String temp = methord.toLowerCase(Locale.ENGLISH);
		if(temp.startsWith("get"))
			return true;
		if(temp.startsWith("post"))
			return true;
		if(temp.startsWith("connect"))
			return true;
		if(temp.startsWith("head"))
			return true;
		if(temp.startsWith("options"))
			return true;
		if(temp.startsWith("debug"))
			return true;
		return false;
	}
	/***
	 * 根据请求的HttpFirstLine解析结果 以及Config中的配置信息，判断是否需要连接到Server
	 * @throws HostNotFoundExpection
	 * @throws NumberFormatException
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	protected void ConnectToServer() throws HostNotFoundExpection, NumberFormatException, UnknownHostException, IOException
	{
		if(Config.isProxyServer)
		{
			if(!conn.isConnectToServer())
			{
				int index = Config.proxyServer.indexOf(':');
				conn.setNewServer(
						new Socket(
								Config.proxyServer.substring(0, index),Integer.parseInt(Config.proxyServer.substring(index+1))
								));
				S2C = new HttpServer2Proxy(conn);
				S2C.start();
				oStream = conn.getSerrverOUT();//更新输出流
			}
		}
		else
		{
			//如果不使用代理服务器，即直连模式
			if(hfl.getHost().isEmpty()) throw new HostNotFoundExpection();
			
			//通过对比新旧主机名与IP 判断是否已经建立了连接
			if((!OldHost.equals(hfl.Host)) || OldPort != hfl.Port )
			{
				conn.closeServer();
				if(S2C!=null)
				{
					S2C.died();
					S2C.interrupt();
				}
				
				Socket serverSocket = new Socket(InetAddress.getByName(hfl.getHost()), hfl.getPort());
				conn.setNewServer(serverSocket);
				S2C = new HttpServer2Proxy(conn);
				S2C.start();			//服务端线程启动的时机，新的服务端Socket被建立的时候
				oStream = conn.getSerrverOUT();//更新输出流
				OldHost = hfl.getHost();
				OldPort = hfl.getPort();
			}
		}
	}
	
}
