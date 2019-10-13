package com.example.fragment;

import java.util.List;

import com.example.activity.MusicActivityMain;
import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;
import com.example.util.DownloadMP3;
import com.example.util.NetInfoUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MusicFragmentWebMusic extends Fragment {

	List<String[]> musicList;
	private Handler handle;
	LinearLayout ll_web;
	LinearLayout ll_error;
	ListView lv_musiclist;
	BaseAdapter ba;
	Handler mHandler;
	Context context;
	String title;
	String colon;
	String info;
	int selectTemp;
	private ProgressDialog loadDialog;
	
	public MusicFragmentWebMusic(String title) 
	{
		this.title = title;
		this.info = "";
		this.colon = "";
	}

	public MusicFragmentWebMusic(String title, String info) 
	{
		this.title = title;
		this.info = info;
		this.colon = "：";
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		context=getActivity().getApplicationContext();
		
		//获取控件引用
		ll_web = (LinearLayout) inflater.inflate(R.layout.fragment_web,container, false);
		ll_error = (LinearLayout) inflater.inflate(R.layout.web_error,null);
		lv_musiclist = (ListView) inflater.inflate(R.layout.fragment_web_music,null);
		
		//设置标题
		TextView tv_title = (TextView) ll_web.findViewById(R.id.web_textview_title_l);
		tv_title.setText(title + colon + info);
		
		// 添加搜索监听
		ImageView iv_search = (ImageView) ll_web.findViewById(R.id.web_imagebutton_search_l);
		iv_search.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				MusicFragmentSearch fragment = new MusicFragmentSearch();
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
			}
		});

		// 添加后退监听
		ImageView iv_back = (ImageView) ll_web.findViewById(R.id.web_imagebutton_back_l);
		iv_back.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				if (getFragmentManager().getBackStackEntryCount() > 1) 
				{
					fragmentManager.popBackStack();
				}
			}
		});
		
		//设置进度条
		loadDialog = new ProgressDialog(getActivity()); 
		loadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置进度条为横向
		loadDialog.setTitle("正在下载");//设置标题
		loadDialog.setIcon(R.drawable.webmusic_download);//设置图标
		loadDialog.setIndeterminate(false);//设置进度条值接不接受给定
		loadDialog.setMax(100);//设置最大值
		loadDialog.setCancelable(false);//设置能不能被取消
		
		handle = new Handler()
		{
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				LinearLayout ll_temp = (LinearLayout) ll_web.getChildAt(1);
				switch (msg.what) 
				{
				case Constant.LOAD_ERROR:
					ll_temp.removeViewAt(0);
					ll_error.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					ll_temp.addView(ll_error);
					break;
				case Constant.LOAD_COMPLETE:
					ll_temp.removeViewAt(0);
					ll_temp.addView(lv_musiclist);
					break;
				case Constant.DOWNLOAD_UPDATE:
					Bundle b=msg.getData();
					int progressTemp = b.getInt("download");
					loadDialog.setProgress(progressTemp);
					break;
				}
			}
		};

		new Thread() {
			public void run() {
				if(title.equals(Constant.FRAGMENT_ALBUM))
				{
					musicList = NetInfoUtil.searchSong(info);
				}
				else if(title.equals(Constant.FRAGMENT_SINGER))
				{
					musicList = NetInfoUtil.searchSong(info);
				}
				else if(title.equals(Constant.FRAGMENT_MUSIC))
				{
					musicList = NetInfoUtil.getSongList();
				}
				if(musicList==null)
				{
					handle.sendEmptyMessage(Constant.LOAD_ERROR);
					return;
				}
				ba = new BaseAdapter() 
				{
					LayoutInflater inflater = LayoutInflater.from(getActivity());
					int count = 1;

					@Override
					public int getCount() 
					{
						if (musicList.get(0).length!=1) 
						{
							count = musicList.size() + 1;
						}
						return count;
					}

					@Override
					public Object getItem(int position) {
						return null;
					}

					@Override
					public long getItemId(int position) {
						return 0;
					}

					@Override
					public View getView(int arg0, View arg1, ViewGroup arg2) 
					{
						if(count==1)
						{
							LinearLayout lll = (LinearLayout) inflater.inflate(
									R.layout.listview_count, null)
									.findViewById(R.id.linearlayout_null);
							TextView tv_sum = (TextView) lll.getChildAt(0);
							tv_sum.setText("很遗憾，结果空空如也"+"\n\n\n");
							return lll;
						}
						if (arg0 == musicList.size()) 
						{
							LinearLayout lll = (LinearLayout) inflater.inflate(
									R.layout.listview_count, null)
									.findViewById(R.id.linearlayout_null);
							TextView tv_sum = (TextView) lll.getChildAt(0);
							tv_sum.setText("共有" + (count - 1) + "首歌曲"+"\n\n\n");
							return lll;
						}
						String musicName = musicList.get(arg0)[1]+"-"+musicList.get(arg0)[0];
						LinearLayout ll = (LinearLayout) inflater.inflate
								(R.layout.fragment_localmusic_listview_row,null)
								.findViewById(R.id.LinearLayout_row);
						TextView tv = (TextView) ll.getChildAt(1);
						tv.setText(musicName);
						return ll;
					}
				};

				lv_musiclist.setAdapter(ba);

				lv_musiclist.setOnItemClickListener(new OnItemClickListener() 
				{
					@Override
					public void onItemClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) 
					{
						if (arg2 == arg0.getCount() - 1) 
						{
							return;
						}
						selectTemp = arg2;
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setMessage(musicList.get(selectTemp)[0]);
						builder.setTitle("下载");
						builder.setPositiveButton("确定",new OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog,int which) 
							{
								dialog.dismiss();
								loadDialog.setMessage(musicList.get(selectTemp)[4]);//设置内容
								loadDialog.show();//显示dialog
								new Thread() 
								{
									public void run() 
									{
										int musicid = -1 ;
										try 
										{
											 musicid = DownloadMP3.download(musicList.get(selectTemp)[3],
													musicList.get(selectTemp)[4],handle);
										} 
										catch (Exception e) 
										{
											e.printStackTrace();
										}
										finally
										{
											loadDialog.dismiss();
										}
										if(musicid!=-1)
										{
											SharedPreferences sp = getActivity().getSharedPreferences
													("music",Context.MODE_MULTI_PROCESS);
											SharedPreferences.Editor spEditor=sp.edit();
											spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
											spEditor.putInt(Constant.SHARED_ID, musicid);
											spEditor.commit();
											String playpath = DBUtil.getMusicPath(musicid);
											Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
											intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
											intentstop.putExtra("path", playpath);
											getActivity().sendBroadcast(intentstop);
											try
											{
												FragmentManager fm = getFragmentManager();
												fm.popBackStack(MusicActivityMain.mainFragmentId, 0);
											}
											catch(Exception e)
											{
												e.printStackTrace();
											}
										}
									}
								}.start();
							}
						});
						builder.setNegativeButton("取消",new OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog,int which)
							{
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
				});
				handle.sendEmptyMessage(Constant.LOAD_COMPLETE);
			}
		}.start();
		return ll_web;
	}
}
