package com.example.receiver;

import java.util.ArrayList;
import java.util.List;

import com.example.activity.MusicActivityMain;
import com.example.service.MusicService;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class MusicUpdateUtil extends BroadcastReceiver
{
	//此类辅助桌面widget更新MediaPlayer。为播放、暂停、上一首、下一首的操作。
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		this.context=context;
		String controlTemp=intent.getStringExtra("control");
		System.out.println(controlTemp);
		ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE); //获取系统服务管理其    
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(100);//获取正在运行的系统服务
		if(!MusicActivityMain.MusicServiceIsStart(mServiceList, Constant.SERVICE_CLASS))//检索是否存在本程序的进程，没有则创建
		{
			context.startService(new Intent(context, MusicService.class));
			// 获得 歌曲，歌手
			SharedPreferences sp = context.getSharedPreferences("music",Context.MODE_PRIVATE);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			int seek = sp.getInt("current", 0);
			
			//发送播放请求
			Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
			intent_start.putExtra("cmd", Constant.COMMAND_START);
			intent_start.putExtra("flag", false);
			intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
			intent_start.putExtra("current", seek);
			context.sendBroadcast(intent_start);
			return;
		}
		if(controlTemp.equals(Constant.WIDGET_NEXT))
		{
			//获取下一首歌曲ID
			int musicid=getShared(Constant.SHARED_ID);
			ArrayList<Integer> musicList = DBUtil.getMusicList(getShared(Constant.SHARED_LIST));
			musicid = DBUtil.getNextMusic(musicList, musicid);

			//记录歌曲ID
			setShared(Constant.SHARED_ID,musicid);

			//发送播放请求
			String path = DBUtil.getMusicPath(musicid);
			Intent intentTemp = new Intent(Constant.MUSIC_CONTROL);
			intentTemp.putExtra("cmd", Constant.COMMAND_PLAY);
			intentTemp.putExtra("path", path);
			context.sendBroadcast(intentTemp);
		}
		else if(controlTemp.equals(Constant.WIDGET_PREVIOUS))
		{
			//获取上一首歌曲ID
			int musicid=getShared(Constant.SHARED_ID);
			ArrayList<Integer> musicList = DBUtil.getMusicList(getShared(Constant.SHARED_LIST));
			musicid = DBUtil.getPreviousMusic(musicList, musicid);

			//记录歌曲ID
			setShared(Constant.SHARED_ID,musicid);

			//发送播放请求
			String path = DBUtil.getMusicPath(musicid);
			Intent intentTemp = new Intent(Constant.MUSIC_CONTROL);
			intentTemp.putExtra("cmd", Constant.COMMAND_PLAY);
			intentTemp.putExtra("path", path);
			context.sendBroadcast(intentTemp);
		}
		else if(controlTemp.equals(Constant.WIDGET_PLAY))
		{
			int status=intent.getIntExtra("status", Constant.STATUS_STOP);
			System.out.println(status);
			if (status == Constant.STATUS_PLAY) 
			{
				Intent intentTemp = new Intent(Constant.MUSIC_CONTROL);
				intentTemp.putExtra("cmd", Constant.COMMAND_PAUSE);
				context.sendBroadcast(intentTemp);
			} 
			else if (status == Constant.STATUS_PAUSE)
			{
				Intent intentTemp = new Intent(Constant.MUSIC_CONTROL);
				intentTemp.putExtra("cmd", Constant.COMMAND_PLAY);
				context.sendBroadcast(intentTemp);
			} 
			else 
			{
				Intent intentTemp = new Intent(Constant.MUSIC_CONTROL);
				intentTemp.putExtra("cmd", Constant.COMMAND_PLAY);
				intentTemp.putExtra("path", DBUtil.getMusicPath(getShared(Constant.SHARED_ID)));
				context.sendBroadcast(intentTemp);
			}
		}
	}
	
	public void setShared(String key, int value)
	{
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor=sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}
	
	public int getShared(String key)
	{
		SharedPreferences sp = context.getSharedPreferences("music",
				Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}
}