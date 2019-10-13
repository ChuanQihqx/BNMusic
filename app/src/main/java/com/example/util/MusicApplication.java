package com.example.util;

import android.app.Application;

public class MusicApplication extends Application
{
	private static boolean isExit=false;
	public static String socketIp="10.16.189.32";
	
	public void setExit(boolean b)
	{
		isExit=b;
	}
	
	public boolean isExit()
	{
		return isExit;
	}
}