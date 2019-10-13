package com.example.view;

import com.example.bnmusic.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

public class VisualizerView extends View
{
	private int viewWidth;// 视图宽度
	private int viewHeight;// 视图高度
	private float contentWidth;
	private float contentHeight;
	private int paddingLeft=70;//左右留白
	private int paddingLeftRect=2;//柱子左右留白
	private int paddingTop;//上下留白
	private byte[] myBytesWave;//时域频谱数组
	private byte[] myBytesFft;//频域频谱数组
	private byte[] myOldBytesTop;
	private byte[] myOldBytesBottom;
    private float[] myPoints;//时域频谱绘制点
    private float[] myFftPoints=new float[4];//频域频谱绘制点
    private boolean visualizerMode;//频域时域标志位
    private Paint paint = new Paint();//时域画笔
    private Paint paintRect = new Paint();//频域画笔

	public VisualizerView(Context context) 
	{
		super(context);
		setBackgroundResource(R.color.none);
	}
	
	public VisualizerView(Context context,AttributeSet attrs) 
	{
		super(context, attrs);
		myBytesWave=null;
		myBytesFft=null;
		myOldBytesTop=null;
		myOldBytesBottom=null;
	}
	
	public void initView()
	{
		viewHeight=getHeight();//
		viewWidth=getWidth();
		contentWidth=viewWidth-2*paddingLeft;
		contentHeight=3*contentWidth/8;
		paddingTop=(int) ((viewHeight-contentHeight)/2);
		setBackgroundResource(R.color.none);
		paintRect.setStyle(Style.FILL_AND_STROKE);//设置画笔格式为绘制边框并填充
		paintRect.setStrokeWidth(2f);//设置边框大小
		paintRect.setAntiAlias(true);//设置抗锯齿
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setStrokeWidth(2f);
		paint.setAntiAlias(true);
		int colorFrom=Color.RED;
		int colorTo=Color.YELLOW;
		LinearGradient lg=new LinearGradient(paddingLeft,paddingTop,
				contentWidth+paddingLeft,contentHeight+paddingTop,
				colorFrom,colorTo,TileMode.CLAMP);
		paint.setShader(lg);
	}
	
	public void updateVisualizer(byte[] bytes,boolean visualizerMode)
	{
		this.visualizerMode=visualizerMode;
		if(visualizerMode)
		{
			myBytesWave=bytes;
		}
		else
		{
			myBytesFft=bytes;
		}
		
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		initView();
		if(visualizerMode)
		{
			if(myBytesWave==null)
			{
				return;
			}
			
			if(myPoints==null||myPoints.length!=myBytesWave.length*4);
			{
				myPoints=new float[myBytesWave.length*4];
			}
			for(int i=0;i<myBytesWave.length-1;i++)
			{
				myPoints[i*4]=paddingLeft+contentWidth*i/(myBytesWave.length-1);
				myPoints[i*4+1]=viewHeight/2+((byte)(myBytesWave[i]+128))*
						(contentHeight/2)/128;
				myPoints[i*4+2]=paddingLeft+contentWidth*(i+1)/(myBytesWave.length-1);
				myPoints[i*4+3]=viewHeight/2+((byte)(myBytesWave[i+1]+128))*
						(contentHeight/2)/128;
			}
			canvas.drawLines(myPoints, paint);
		}
		else
		{
			if(myBytesFft==null)
			{
				return;
			}
			if(myOldBytesTop==null)
			{
				myOldBytesTop=new byte[myBytesFft.length];
			}
			if(myOldBytesBottom==null)
			{
				myOldBytesBottom=new byte[myBytesFft.length];
			}
			for(int i=0;i<myBytesFft.length;i++)
			{
				if(myBytesFft[i]>myOldBytesTop[i])
				{
					myOldBytesTop[i] = (byte) ((myBytesFft[i] / 3) * 3 + 3);
					myOldBytesBottom[i] = (byte) ((myBytesFft[i] / 3) * 3);
				}else
				{
					if(myOldBytesTop[i]>9)
					{
						myOldBytesTop[i]-=3;
					}
					else
					{
						myOldBytesTop[i]=6;
					}
					if(myOldBytesBottom[i]>6)
					{
						myOldBytesBottom[i]-=6;
					}
					else
					{
						myOldBytesBottom[i]=3;
					}
				}
				paintRect.setARGB(200, 255, 255*i/myBytesFft.length, 0);
				myFftPoints[0]=paddingLeft+paddingLeftRect+contentWidth*i/(myBytesFft.length);
				myFftPoints[1]=viewHeight-paddingTop-(myOldBytesTop[i]*2-2)*contentHeight/256;
				myFftPoints[2]=paddingLeft-paddingLeftRect+contentWidth*(i+1)/(myBytesFft.length);
				myFftPoints[3]=viewHeight-paddingTop-(myOldBytesTop[i]*2-4)*contentHeight/256;
				canvas.drawRect(myFftPoints[0], myFftPoints[1], myFftPoints[2], myFftPoints[3], paintRect);
				for(int j=1;j<=myOldBytesBottom[i]*2/3;j+=2)
				{
					myFftPoints[0]=paddingLeft+paddingLeftRect+contentWidth*i/(myBytesFft.length);
					myFftPoints[1]=viewHeight-paddingTop-j*3*contentHeight/256;
					myFftPoints[2]=paddingLeft-paddingLeftRect+contentWidth*(i+1)/(myBytesFft.length);
					myFftPoints[3]=viewHeight-paddingTop-(j*3-2)*contentHeight/256;
					canvas.drawRect(myFftPoints[0], myFftPoints[1], myFftPoints[2], myFftPoints[3], paintRect);
				}
			}
		}
	}
}