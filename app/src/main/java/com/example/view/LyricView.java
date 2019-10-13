package com.example.view;

import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LyricView extends View {
	private ArrayList<String[]> lyric;
	private String lyrictemp[];
	private int lineHeight;// 每行预留空间
	private int otherLyricSize = 24;// 普通歌词大小
	private int nowLyricSize = 34;// 当前歌词大小
	private int marginTop = 30;// 上方留白
	private int lyricTime = 80;// 划过整个屏幕跳过的时间
	private int lyricSpeed;// 跳过1秒需要划过的像素
	private int alphaSpan;// 透明度渐变步长
	private int paddingTop;// 剩余高度留白在上下两端
	private int paddingLeft = 70;// 剩余高度留白在左右两端
	private int lineCount = 9;// 歌曲行数
	private int current;// 获取进度
	private int currentTemp;// 临时进度
	private int duration;// 获取歌曲总长
	private int viewWidth;// 视图宽度
	private int viewHeight;// 视图高度
	private int tempy;// 记忆按下时的Y
	private boolean touchFlag = true;// 按下时的绘制
	private Paint p = new Paint();

	Context context;

	public LyricView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		this.context = context;
		setBackgroundResource(R.color.none);
	}

	// 初始化控件参数
	public void initView() 
	{
		viewHeight = getHeight();//获得控件高度
		viewWidth = getWidth();//获得控件宽度
		lineHeight = viewHeight / (lineCount + 2);// 根据行数计算行高，3为了预留上下的空白区域
		paddingTop = (viewHeight - lineCount * lineHeight) / 2;//计算歌词上下预留的空白区域
		lyricSpeed = viewHeight / lyricTime;
		otherLyricSize = (int) (lineHeight * 0.6);
		nowLyricSize = (int) (lineHeight * 0.85);
		paddingTop += lineHeight*1/4;
	}

	// 根据时间计算歌词位置
	public int now() 
	{
		int now = 0;
		for (int i = 0; i < lyric.size(); i++) 
		{
			String lyric1[] = lyric.get(i);
			int time1 = (Integer.parseInt(lyric1[0]) * 60 + Integer.parseInt(lyric1[1])) * 1000;
			String lyric2[] = { "", "", "" };
			int time2;
			if (i != lyric.size() - 1) 
			{
				lyric2 = lyric.get(i + 1);
				time2 = (Integer.parseInt(lyric2[0]) * 60 + Integer.parseInt(lyric2[1])) * 1000;
			}
			else 
			{
				time2 = duration;
			}
			if (current < (Integer.parseInt(lyric.get(0)[0]) * 60 + Integer.parseInt(lyric.get(0)[1])) * 1000) 
			{
				break;
			}
			if (current > time1 && current < time2) {
				now = i;
				break;
			}
		}
		return now;
	}

	// 初始化歌词
	public void initLayout() 
	{
		lyrictemp = new String[lineCount];//建立长度为歌曲行数的数组来存放歌词
		int iTemp = lyrictemp.length;
		if (lyric == null) //如果没有歌词。则剧中显示百纳好音乐
		{
			for (int i = 0; i < iTemp; i++) 
			{
				if (i == iTemp / 2) 
				{
					lyrictemp[i] = "百纳音乐";
				} 
				else 
				{
					lyrictemp[i] = "";
				}
			}
			return;
		}

		int j = now();
		j = j - iTemp / 2;
		for (int i = 0; i < iTemp; i++, j++) 
		{
			try 
			{
				lyrictemp[i] = lyric.get(j)[2];
			} 
			catch (Exception e) //如果找不到数据则返回空值
			{
				lyrictemp[i] = "";
			}
		}
	}

	public void setLyric(int musicid) //设置歌词
	{
		lyric = DBUtil.getLyric(DBUtil.getLyricPath(musicid));//读取歌词数组
	}

	public void setMusictime(int current, int duration) //设置当前播放时间与音乐总时长
	{
		this.current = current;
		this.duration = duration;
		if (touchFlag) 
		{
			invalidate();
		}
	}

	public int getCurrent()  //获取当前播放时间
	{
		return current;
	}

	public int getDuration()  //获取音乐总时长
	{
		return duration;
	}

	public String fromMsToMinuteStr(int ms) //毫秒转为时间字符串
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) //触屏监控
	{
		int y = (int) event.getY();
		switch (event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			tempy = y;
			touchFlag = false;
			currentTemp = current;
			break;
		case MotionEvent.ACTION_MOVE:
			current = currentTemp - 1000 * (y - tempy) / lyricSpeed;
			if (current > duration - 1000) 
			{
				current = duration - 1000;
			}
			if (current < 0) 
			{
				current = 0;
			}
			break;
		case MotionEvent.ACTION_UP:
			tempy = 0;
			touchFlag = true;
			if(duration==0)
			{
				break;
			}
			Intent intent = new Intent(Constant.MUSIC_CONTROL);
			intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
			intent.putExtra("current", current);
			context.sendBroadcast(intent);
			break;
		}
		invalidate();
		return super.onTouchEvent(event);
	}

	@Override
	public void onDraw(Canvas canvas) {
		initView();
		initLayout();
		p.setAntiAlias(true);

		// 绘制文字
		int x = viewWidth / 2;
		int y;
		int iTemp = lyrictemp.length;
		alphaSpan = (240 - 40) / (iTemp / 2);

		// 保存和剪裁画布
		canvas.save();
		canvas.clipRect(paddingLeft, 0, viewWidth - paddingLeft, viewHeight);

		p.setTextAlign(Paint.Align.CENTER);
		p.setStyle(Style.STROKE);
		for (int i = 0; i < iTemp; i++) 
		{
			y = marginTop + paddingTop + i * lineHeight;
			if (i < iTemp / 2) 
			{
				p.setColor(Color.WHITE);
				p.setTextSize(otherLyricSize);
				p.setAlpha(alphaSpan * (i + 1) + 40);//设置透明度渐变效果
				canvas.drawText(lyrictemp[i], x, y, p);
			} 
			else if (i == iTemp / 2) 
			{
				p.setColor(Color.YELLOW);
				p.setTextSize(nowLyricSize);
				p.setAlpha(255);
				canvas.drawText(lyrictemp[i], x, y, p);
			} 
			else 
			{
				p.setColor(Color.WHITE);
				p.setTextSize(otherLyricSize);
				p.setAlpha(alphaSpan * (iTemp - i) + 40);
				canvas.drawText(lyrictemp[i], x, y, p);
			}
		}

		// 恢复画布
		canvas.restore();

		if (!touchFlag) 
		{
			// 绘制时间框
			p.setColor(Color.BLACK);
			p.setAlpha(200);
			p.setStyle(Style.FILL_AND_STROKE);
			canvas.drawRect(paddingLeft / 3, viewHeight / 2 - 40,
					paddingLeft / 3 + 60, viewHeight / 2, p);

			// 绘制时间线
			p.setStyle(Style.STROKE);
			p.setColor(Color.YELLOW);
			p.setStrokeWidth(3);
			p.setAlpha(255);
			canvas.drawLine(paddingLeft / 3, viewHeight / 2, viewWidth
					- paddingLeft / 3, viewHeight / 2, p);

			// 绘制时间
			p.setTextAlign(Paint.Align.CENTER);
			p.setTextSize(20);
			p.setStrokeWidth(0);
			canvas.drawText(fromMsToMinuteStr(current), paddingLeft / 3 + 30,viewHeight / 2 - 10, p);
		}
	}
}