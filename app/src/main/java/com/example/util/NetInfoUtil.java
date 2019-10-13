package com.example.util;

import java.io.*;
import java.net.*;
import java.util.*;

public class NetInfoUtil 
{
	public static Socket ss = null;
	public static DataInputStream din = null;
	public static DataOutputStream dos = null;
	public static String message="";
	public static byte[] data;
	
	//通信建立
	public static void connect() throws Exception
	{
		ss = new Socket();//创建一个ServerSocket对象
		SocketAddress socketAddress = new InetSocketAddress(MusicApplication.socketIp, 8888); //绑定到指定IP和端口
		ss.connect(socketAddress, 5000);//设置连接超时时间
		din = new DataInputStream(ss.getInputStream());//创建新数据输入流
		dos = new DataOutputStream(ss.getOutputStream());//创建新数据输出流
	}
	
	//搜索歌曲
	public static List<String[]> searchSong(String info) 
	{
		try 
		{
			connect();
			dos.writeUTF("<#SEARCH_SONG#>" + info);
			message = din.readUTF();
		} catch (Exception e) 
		{
			e.printStackTrace();
		} finally 
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//通信关闭
	public static void disConnect()
	{
		if(dos!=null)
		{
			try{dos.flush();}catch(Exception e){e.printStackTrace();}
		}
		if(din!=null)
		{
			try{din.close();}catch(Exception e){e.printStackTrace();}
		}
		if(ss!=null)
		{
			try{ss.close();}catch(Exception e){e.printStackTrace();}
		}
	}
	
	//获取图片（图片名）
	public static byte[] getPicture(String aid)
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_PICTURE#>"+aid);
			data=IOUtil.readBytes(din);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return data;
	}
	
	//获得歌曲目录
	public static List<String[]> getSongList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SONGLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//获得歌手目录
	public static List<String[]> getSingerList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SINGERLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//获得专辑目录
	public static List<String[]> getAlbumsList()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_ALBUMLIST#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//获得前三名歌手
	public static List<String[]> getSingerListTop()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_SINGERLISTTOP#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
	
	//获得前三名专辑
	public static List<String[]> getAlbumsListTop()
	{
		try
		{
			connect();
			dos.writeUTF("<#GET_ALBUMLISTTOP#>");
			message=din.readUTF();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disConnect();
		}
		return StrListChange.StrToList(message);
	}
}