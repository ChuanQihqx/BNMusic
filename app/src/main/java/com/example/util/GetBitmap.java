package com.example.util;


import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

//
public class GetBitmap 
{
	static Bitmap bitmap;                   //获得一张图片Bitmap
	static byte[] bb;                       //图片数据
	static String CACHE="/css";             //存到SD卡的文件名称
	static String filePath;					//文件夹路径
	static String picFilePath;				//图片所在文件夹及图片名称
	static File file;
	
	//得到单张图片
	public static Bitmap getOneBitmap(String aid,String picStr)
	{
		//判断SD卡是否存在
		if(Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			filePath=Environment.getExternalStorageDirectory().toString()+CACHE;
			file=new File(filePath);              //获取缓存文件夹目A录，如果不存在则创建
			if(!file.exists())
			{
				file.mkdirs();
			}
			
			picStr+=".jpg";
			picFilePath=Environment.getExternalStorageDirectory().toString()+CACHE+"/"+picStr;   //获取SD卡文件
			file=new File(picFilePath);
			if(file.exists())
			{
				bitmap=BitmapFactory.decodeFile(picFilePath);
				return bitmap;
			}
			bb=NetInfoUtil.getPicture(aid);             //从网络上获取  返回数据并添加到SD卡中	
			bitmap=BitmapFactory.decodeByteArray(bb, 0,bb.length);
    	    FileOutputStream fos=null;
            file=new File(filePath,picStr);
            try
   	        {
   	        	fos = new FileOutputStream(file);      //读到SD卡中
   	        	if(fos!=null)
   	        	{
	        		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);	    	        		
	        		fos.flush();
    	            fos.close();
    	        }
    	    }
    	    catch(Exception e)
            {
            	e.printStackTrace();
  	        }
    	    return bitmap;
	    }
	    return null;
	}
}
