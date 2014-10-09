package com.cfun.proxy.Exception;

@SuppressWarnings("serial")
public class FirstLineFormatErrorExpection extends Exception
{

	public FirstLineFormatErrorExpection(String firstLine)
	{
		super("Http请求头首行格式化错误异常"+":"+firstLine);
	}

}
