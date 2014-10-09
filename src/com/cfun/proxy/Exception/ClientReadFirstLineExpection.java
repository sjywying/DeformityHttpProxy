package com.cfun.proxy.Exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class ClientReadFirstLineExpection extends IOException
{

	public ClientReadFirstLineExpection(String nextLine)
	{
		super("客户端数据读取第一行失败异常"+":"+nextLine);
	}


}
