package com.example.receiver;

import com.example.activity.MusicActivityPlay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicUpdateVisualizer extends BroadcastReceiver
{
	MusicActivityPlay pa;
	public boolean visualzerFlag;
	public boolean visualzerMode;
	byte bytes[];
	
	public MusicUpdateVisualizer(MusicActivityPlay pa)
	{
		this.pa=pa;
		visualzerFlag=false;
		visualzerMode=false;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(visualzerFlag)
		{
			if(visualzerMode)
			{
				bytes=intent.getByteArrayExtra("visualizerwave");
				if(bytes!=null)
				{
					pa.mVisualizerView.updateVisualizer(bytes,visualzerMode);
				}
			}
			else
			{
				bytes=intent.getByteArrayExtra("visualizerfft");
				if(bytes!=null)
				{
					pa.mVisualizerView.updateVisualizer(bytes,visualzerMode);
				}
			}
		}
	}
}