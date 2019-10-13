package com.example.receiver;

import com.example.activity.MusicActivityMain;
import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class MusicUpdateWidget extends AppWidgetProvider
{
	/* 本文件中发送的Intent命令被MusicUpdateUtil接收
	 * MusicUpdateUtil接收后经过进一步处理发送Intent给MusicUpdateMedia，使播放器状态改变
	 */
	MusicUpdateUtil muu;
	RemoteViews rv;//获得widget界面的引用，widget只能通过RemoteView改变界面，不能单独获得各个控件的引用
	int status;
	
	@Override
	public void onEnabled(Context context)
	{
		status=Constant.STATUS_PAUSE;
	}
	
	@Override
	public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds)
	{
		rv=new RemoteViews(context.getPackageName(),R.layout.widget);//获取widget界面的引用
		Intent intent = new Intent(context,MusicActivityMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity//给按钮绑定Intent，点击使其发送
		(
			context, 
			0, 
			intent, 
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		rv.setOnClickPendingIntent(R.id.widget_iv_image, pendingIntent);//点击图片进入Activity
		
		Intent playIntent = new Intent(Constant.UPDATE_WIDGET);
		playIntent.putExtra("control", Constant.WIDGET_PLAY);
		playIntent.putExtra("status", status);
		PendingIntent playPending = PendingIntent.getBroadcast//点击播放按钮。发送播放命令
		(
			context, 
			1, 
			playIntent, 
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		rv.setOnClickPendingIntent(R.id.widget_iv_play, playPending);
		
		Intent nextIntent = new Intent(Constant.UPDATE_WIDGET);
		nextIntent.putExtra("control", Constant.WIDGET_NEXT);
		PendingIntent nextPending = PendingIntent.getBroadcast//设置下一首按钮
		(
			context, 
			3, 
			nextIntent, 
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		rv.setOnClickPendingIntent(R.id.widget_iv_next, nextPending);
		
		Intent previousIntent = new Intent(Constant.UPDATE_WIDGET);
		previousIntent.putExtra("control", Constant.WIDGET_PREVIOUS);
		PendingIntent proviousPending = PendingIntent.getBroadcast//设置上一首按钮
		(
			context, 
			2, 
			previousIntent, 
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		rv.setOnClickPendingIntent(R.id.widget_iv_pre, proviousPending);

		appWidgetManager.updateAppWidget(appWidgetIds, rv);//创建桌面widget
	}
	
	@Override
	public void onReceive(Context context,Intent intent)
	{
		super.onReceive(context, intent);
		if(rv==null)
		{
			rv=new RemoteViews(context.getPackageName(),R.layout.widget);
		}
		if(intent.getAction().equals(Constant.WIDGET_STATUS))
		{
			setStatus(context, intent);
		}
		else if(intent.getAction().equals(Constant.WIDGET_SEEK))
		{
			if(status!=intent.getIntExtra("status", status))
			{
				setStatus(context, intent);
			}
			int duration = intent.getIntExtra("duration", 0);
			int current = intent.getIntExtra("current", 0);
			rv.setProgressBar(R.id.widget_progressbar_progress, duration, current, false);
		}
		
		//接受广播后，刷新桌面widget
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		int[] appIds = appWidgetManager.getAppWidgetIds
		(
			new ComponentName
			(
				context,
				MusicUpdateWidget.class
			)
		);
		appWidgetManager.updateAppWidget(appIds, rv);
	}
	
	private void setStatus(Context context, Intent intent) //改变widget播放状态，以及播放与暂停图标的切换
	{
		status=intent.getIntExtra("status", Constant.STATUS_STOP);	//获取当前播放状态
		if (status == Constant.STATUS_PLAY) {//改变widget播放与暂停的图标
			rv.setImageViewResource(R.id.widget_iv_play, R.drawable.player_pause_w);
		}
		else
		{
			rv.setImageViewResource(R.id.widget_iv_play, R.drawable.player_play_w);
		}
		SharedPreferences sp=context.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int musicid = sp.getInt(Constant.SHARED_ID, -1);
		if(musicid!=-1)
		{
			rv.setTextViewText(R.id.widget_textview_gequ, DBUtil.getMusicInfo(musicid).get(1));
			rv.setTextViewText(R.id.widget_textview_geshou, DBUtil.getMusicInfo(musicid).get(2));
		}
		
		Intent playIntent = new Intent(Constant.UPDATE_WIDGET);
		playIntent.putExtra("control", Constant.WIDGET_PLAY);
		playIntent.putExtra("status", status);
		PendingIntent playPending = PendingIntent.getBroadcast
		(
			context, 
			1, 
			playIntent, 
			PendingIntent.FLAG_UPDATE_CURRENT
		);
		rv.setOnClickPendingIntent(R.id.widget_iv_play, playPending);
	}
}