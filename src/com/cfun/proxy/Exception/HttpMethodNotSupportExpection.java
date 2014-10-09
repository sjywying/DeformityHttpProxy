package com.cfun.proxy.Exception;

@SuppressWarnings("serial")
public class HttpMethodNotSupportExpection extends Exception
{
	public HttpMethodNotSupportExpection(String method)
	{
		super("Http方法不被支持异常"+":"+method);
	}
}
