package com.example.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
	
	//创建表
	public static void createTable()
	{
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase
		(
			"/data/data/com.example.bnmusic/musicdata", null,
			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
		);
		String sqlcreate = "create table if not exists musicdata(";
		sqlcreate += "id integer PRIMARY KEY,file varchar(100),music varchar(50),singer varchar(50),";
		sqlcreate += "path varchar(200),lyric varchar(100),lpath varchar(200),";
		sqlcreate += "ilike integer);";
		musicData.execSQL(sqlcreate);
		String sqllastplay="create table if not exists lastplay(id integer PRIMARY KEY)";
		musicData.execSQL(sqllastplay);
		String sqldownload="create table if not exists download(id integer PRIMARY KEY)";
		musicData.execSQL(sqldownload);
		String sqlplaylist="create table if not exists playlist(id integer PRIMARY KEY,name varchar(20))";
		musicData.execSQL(sqlplaylist);
		String sqllistinfo="create table if not exists listinfo(id integer ,musicid integer,";
		sqllistinfo+="FOREIGN KEY(id) REFERENCES playlist(id), FOREIGN KEY(musicid) REFERENCES musicdata(id));";
		musicData.execSQL(sqllistinfo);
		musicData.close();
		
	}
	
	//创建新歌单
	public static void createPlayList(String name)
	{
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		musicData = SQLiteDatabase.openDatabase(
				"/data/data/com.example.bnmusic/musicdata", null,
				SQLiteDatabase.OPEN_READWRITE
						| SQLiteDatabase.CREATE_IF_NECESSARY);
		int listId=0;
		try {
			String sql = "select max(id) from playlist;";
			cur = musicData.rawQuery(sql, null);
			if(cur.moveToFirst())
			{
				listId=cur.getInt(0)+1;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		try{
			String sqlinsert = "insert into playlist values("+listId+",'"+name+"');";
			musicData.execSQL(sqlinsert);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(cur!=null){cur.close();}
			musicData.close();
		}
	}
	
	//删除表
	public static void deleteTable()
	{
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase
		(
			"/data/data/com.example.bnmusic/musicdata", null,
			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
		);
		try
		{
			musicData.delete("listinfo", null, null);
			musicData.delete("playlist", null, null);
			musicData.delete("download", null, null);
			musicData.delete("lastplay", null, null);
			musicData.delete("musicdata", null, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			musicData.close();
		}
	}
	
	//删除歌单
	public static void deletePlayList(int id)
	{
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase
		(
			"/data/data/com.example.bnmusic/musicdata", null,
			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
		);
		String sql="delete from playlist where id="+id+";";
		musicData.execSQL(sql);
		musicData.close();
	}
	
	public static void deleteMusic(int id)
	{
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase
		(
			"/data/data/com.example.bnmusic/musicdata", null,
			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
		);
		String sql="delete from listinfo where musicid="+id+";";
		musicData.execSQL(sql);
		String sql2="delete from download where id="+id+";";
		musicData.execSQL(sql2);
		String sql3="delete from lastplay where id="+id+";";
		musicData.execSQL(sql3);
		String sql4="delete from musicdata where id="+id+";";
		musicData.execSQL(sql4);
		musicData.close();
	}
	
	public static void deleteMusicInList(int musicid,int listid)
	{
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase
		(
			"/data/data/com.example.bnmusic/musicdata", null,
			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY
		);
		String sql="delete from listinfo where musicid="+musicid+" and id="+listid+";";
		musicData.execSQL(sql);
		musicData.close();
	}
	
	//添加歌曲
	public static int setMusic(String[] musicInfo) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		musicData = SQLiteDatabase.openDatabase(
				"/data/data/com.example.bnmusic/musicdata", null,
				SQLiteDatabase.OPEN_READWRITE
						| SQLiteDatabase.CREATE_IF_NECESSARY);
		int musicId=-1;
		try {
			String sql = "select max(id) from musicdata;";
			cur = musicData.rawQuery(sql, null);
			if(cur.moveToFirst())
			{
				musicId=cur.getInt(0)+1;
			}else
			{
				musicId=1;
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		try 
		{
			String sql2 = "insert into musicdata values (";
			sql2+=musicId+",'";
			sql2+=musicInfo[0]+"','";
			sql2+=musicInfo[1]+"','";
			sql2+=musicInfo[2]+"','";
			sql2+=musicInfo[3]+"','";
			sql2+=musicInfo[4]+"','";
			sql2+=musicInfo[5]+"',";
			sql2+=0+");";
			musicData.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return musicId;
	}
	
	//添加歌曲
	public static void setMusicInDownload(int id) {
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase(
				"/data/data/com.example.bnmusic/musicdata", null,
				SQLiteDatabase.OPEN_READWRITE);
		try {
			String sql2="insert into download values("+id+");";
			musicData.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			musicData.close();
		}
	}
	
	//添加歌曲到我的歌单
	public static void setMusicInPlaylist(int musicid,int listid) {
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase(
				"/data/data/com.example.bnmusic/musicdata", null,
				SQLiteDatabase.OPEN_READWRITE);
		try {
			String sql2="insert into listinfo values("+listid+","+musicid+");";
			musicData.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		}finally
		{
			musicData.close();
		}
	}
	
	//设置我的最爱
	public static boolean setILikeStatus(int id)
	{
		boolean flag=false;
		SQLiteDatabase musicData = null;
		Cursor cur=null;
		String iLikeStr="0";
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql = "select ilike from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				iLikeStr = (cur.getInt(0)==0)?"1":"0";
				String sql2 = "update musicdata set ilike="+iLikeStr;
				sql2+=" where id="+id+";";
				musicData.execSQL(sql2);
				flag=true;
			}
			else
			{
				flag=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return flag;
	}
	
	//设置最近播放
	public static void setLastPlay(int id)
	{
		if (id == -1) 
		{
			return;
		}
		SQLiteDatabase musicData = null;
		ArrayList<Integer> alTemp=new ArrayList<Integer>();
		Cursor cur=null;
		alTemp.add(id);
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READWRITE);
			String sql1 = "select id from lastplay";
			cur = musicData.rawQuery(sql1, null);
			while (cur.moveToNext())
			{
				if(cur.getInt(0)!=id)
				{
					alTemp.add(cur.getInt(0));
				}
			}
			musicData.delete("lastplay", null, null);
			if(alTemp.size()<10)
			{
				for(int i=0;i<alTemp.size();i++)
				{
					String sql2="insert into lastplay values("+alTemp.get(i)+");";
					musicData.execSQL(sql2);
				}
			}
			else
			{
				for(int i=0;i<10;i++)
				{
					String sql2="insert into lastplay values("+alTemp.get(i)+");";
					musicData.execSQL(sql2);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
	}
	
	//获取歌单列表
	public static ArrayList<String[]> getPlayList()
	{
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<String[]> file=new ArrayList<String[]>();
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select id,name from playlist;";
			cur = musicData.rawQuery(sql, null);
			while (cur.moveToNext()) 
			{
				String []tempStr={cur.getInt(0)+"",cur.getString(1)};
				file.add(tempStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return file;
	}
	
	//设置我的最爱
	public static boolean getILikeStatus(int id)
	{
		boolean flag=false;
		SQLiteDatabase musicData = null;
		Cursor cur=null;
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select ilike from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				flag = (cur.getInt(0)==0)?false:true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return flag;
	}
	
	// 获取歌曲路径
	public static String getMusicPath(int id) {
		if (id == -1) {
			return null;
		}
		setLastPlay(id);
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		String path = null;
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select path from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				path = cur.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return path;
	}

	// 获取歌曲数量
	public static int getMusicCount() {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		int musicCount = 0;
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select count(*) from musicdata;";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				musicCount = cur.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return musicCount;
	}

	// 获取歌词路径
	public static String getLyricPath(int id) {
		if (id == -1) {
			return "";
		}
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		String lyricpath = null;
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select lpath from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				lyricpath = cur.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return lyricpath;
	}

	public static ArrayList<String[]> getLyric(String lyricPath) {
		if(lyricPath==null)
		{
			return null;
		}
		ArrayList<String[]> mp3Lyric = new ArrayList<String[]>();
		File file = new File(lyricPath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedReader reader = null;
		if (file.exists()) {
			try {
				if (file.exists()) {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bis.mark(4);
					byte[] first3bytes = new byte[3];
					// System.out.println("");
					// 找到文档的前三个字节并自动判断文档类型。
					bis.read(first3bytes);
					bis.reset();
					if (first3bytes[0] == (byte) 0xEF
							&& first3bytes[1] == (byte) 0xBB
							&& first3bytes[2] == (byte) 0xBF) {// utf-8

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-8"));

					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFE) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"unicode"));
					} else if (first3bytes[0] == (byte) 0xFE
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16be"));
					} else if (first3bytes[0] == (byte) 0xFF
							&& first3bytes[1] == (byte) 0xFF) {

						reader = new BufferedReader(new InputStreamReader(bis,
								"utf-16le"));
					} else {

						reader = new BufferedReader(new InputStreamReader(bis,
								"GBK"));
					}

					String strTemp = "";
					while ((strTemp = reader.readLine()) != null) {
						String patternStr = "(.)*([0-9]{2}):([0-9]{2}).([0-9]{2})(.)*";
						Pattern p = Pattern.compile(patternStr);
						Matcher m = p.matcher(strTemp);
						if (m.matches()) {
							String lyric[] = { strTemp.substring(2, 3),
									strTemp.substring(4, 6),
									strTemp.substring(10, strTemp.length()) };
							mp3Lyric.add(lyric);
						}
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (bis != null) {
					try {
						bis.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return mp3Lyric;
		} else {
			return null;
		}
	}
	
	//获得其他歌曲清单
	public static ArrayList<Integer> getMusicList(int listNumber) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<Integer> file = null;
		
		if(listNumber==Constant.LIST_ALLMUSIC)
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = SQLiteDatabase.openDatabase(
						"/data/data/com.example.bnmusic/musicdata", null,
						SQLiteDatabase.OPEN_READONLY);
				String sql = "select id from musicdata order by singer DESC;";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext()) 
				{
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cur!=null){cur.close();}
				musicData.close();
			}
		}
		
		else if(listNumber==Constant.LIST_ILIKE)
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = SQLiteDatabase.openDatabase(
						"/data/data/com.example.bnmusic/musicdata", null,
						SQLiteDatabase.OPEN_READONLY);
				String sql = "select id from musicdata ";
				sql+="where ilike="+1;
				sql+=" order by music ASC";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext())
				{
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cur!=null){cur.close();}
				musicData.close();
			}
		}
		
		else if(listNumber==Constant.LIST_LASTPLAY)
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = SQLiteDatabase.openDatabase(
						"/data/data/com.example.bnmusic/musicdata", null,
						SQLiteDatabase.OPEN_READONLY);
				String sql = "select id from lastplay;";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext())
				{
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cur!=null){cur.close();}
				musicData.close();
			}
		}
		
		else if(listNumber==Constant.LIST_DOWNLOAD)
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = SQLiteDatabase.openDatabase(
						"/data/data/com.example.bnmusic/musicdata", null,
						SQLiteDatabase.OPEN_READONLY);
				String sql = "select id from download;";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext())
				{
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cur!=null){cur.close();}
				musicData.close();
			}
		}
		
		else
		{
			file=new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = SQLiteDatabase.openDatabase(
						"/data/data/com.example.bnmusic/musicdata", null,
						SQLiteDatabase.OPEN_READONLY);
				String sql = "select musicid from listinfo where id="+listNumber+";";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext())
				{
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(cur!=null){cur.close();}
				musicData.close();
			}
		}
		
		return file;
	}

	// 获取歌曲信息
	public static ArrayList<String> getMusicInfo(int id) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<String> musicInfo = new ArrayList<String>();
		try {
			musicData = SQLiteDatabase.openDatabase(
					"/data/data/com.example.bnmusic/musicdata", null,
					SQLiteDatabase.OPEN_READONLY);
			String sql = "select file,music,singer,path,lyric,lpath,ilike ";
			sql += "from musicdata where id='" + id + "';";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				for (int i = 0; i < cur.getColumnCount(); i++) {
					musicInfo.add(cur.getString(i));
					// Log.v("musicinfo",cur.getString(i));
				}
			} else {
				musicInfo.add("中国好音乐-传播好声音");
				musicInfo.add("中国好音乐");
				musicInfo.add("传播好声音");
				musicInfo.add("0");
				musicInfo.add("0");
				musicInfo.add("0");
				musicInfo.add("0");
				musicInfo.add("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			musicInfo.add("中国好音乐-传播好声音");
			musicInfo.add("中国好音乐");
			musicInfo.add("传播好声音");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			musicInfo.add("0");
			return musicInfo;
		} finally {
			if(cur!=null){cur.close();}
			musicData.close();
		}
		return musicInfo;
	}

	// 获取下一首歌曲
	public static int getNextMusic(ArrayList<Integer> musicList, int id) 
	{
		if(id==-1)
		{
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == musicList.size() - 1) {
					return musicList.get(0);
				} else {
					++i;
					return musicList.get(i);
				}
			}
		}
		return -1;
	}

	// 获取上一首歌曲
	public static int getPreviousMusic(ArrayList<Integer> musicList, int id) 
	{
		if(id==-1)
		{
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == 0) {
					return musicList.get(musicList.size() - 1);
				} else {
					--i;
					return musicList.get(i);
				}
			}
		}
		return -1;
	}
	
	public static int getRandomMusic(ArrayList<Integer> musicList, int id)
	{
		int musicid = -1;
		if(id==-1)
		{
			return -1;
		}
		if(musicList.isEmpty())
		{
			return -1;
		}
		if(musicList.size()==1)
		{
			return id;
		}
		do
		{
			int count = (int) (Math.random() * musicList.size());
			musicid = musicList.get(count);
		}while(musicid==id);
		return musicid;
	}
}