package com.example.fragment;

import java.util.List;

import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.NetInfoUtil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class MusicFragmentWebOther extends Fragment 
{

	private Handler handle;
	FragmentTransaction fragmentTransaction;
	LinearLayout ll_web;
	LinearLayout ll_error;
	ListView lv_musiclist;
	BaseAdapter ba;
	List<String[]> musicList;
	String title;

	public MusicFragmentWebOther(String title) 
	{
		this.title = title;
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
		fragmentTransaction = getFragmentManager().beginTransaction();
		ll_web = (LinearLayout) inflater.inflate(R.layout.fragment_web,container, false);
		ll_error = (LinearLayout) inflater.inflate(R.layout.web_error, null);
		lv_musiclist = (ListView) inflater.inflate(R.layout.fragment_web_music,null);
		TextView tv_title = (TextView) ll_web.findViewById(R.id.web_textview_title_l);
		tv_title.setText(title);

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

		handle = new Handler() 
		{
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);
				LinearLayout ll_temp = (LinearLayout) ll_web.getChildAt(1);
				switch (msg.what)
				{
				case Constant.LOAD_COMPLETE:
					ll_temp.removeViewAt(0);
					ll_temp.addView(lv_musiclist);
					break;
				case Constant.LOAD_ERROR:
					ll_temp.removeViewAt(0);
					ll_error.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					ll_temp.addView(ll_error);
					break;
				}
			}
		};

		new Thread()
		{
			public void run() 
			{
				try
				{
					if (title.equals(Constant.FRAGMENT_SINGER)) 
					{
						musicList = NetInfoUtil.getSingerList();
					} 
					else if (title.equals(Constant.FRAGMENT_ALBUM)) 
					{
						musicList = NetInfoUtil.getAlbumsList();
					}
					if(musicList==null)
					{
						handle.sendEmptyMessage(Constant.LOAD_ERROR);
						return;
					}
					ba = new BaseAdapter() {
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
							if (arg0 == musicList.size())
							{
								LinearLayout lll = (LinearLayout) inflater.inflate(
										R.layout.listview_count, null)
										.findViewById(R.id.linearlayout_null);
								TextView tv_sum = (TextView) lll.getChildAt(0);
								tv_sum.setText("共有" + (count-1) + "个"+title+"\n\n\n");
								return lll;
							}
							String musicName="";
							
							if (title.equals(Constant.FRAGMENT_SINGER))
							{
								musicName = musicList.get(arg0)[1];
							} else if (title.equals(Constant.FRAGMENT_ALBUM))
							{
								musicName = musicList.get(arg0)[0];
							}
	
							LinearLayout ll = (LinearLayout) inflater.inflate
									(R.layout.fragment_localmusic_listview_row,null)
									.findViewById(R.id.LinearLayout_row);
							TextView tv = (TextView) ll.getChildAt(1);
							tv.setText(musicName);
							return ll;
						}
					};
	
					lv_musiclist.setAdapter(ba);
	
					lv_musiclist.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> arg0,
								View arg1, int arg2, long arg3) {
							if (arg2 == arg0.getCount() - 1) 
							{
								return;
							}
							MusicFragmentWebMusic fragment;
							if(title.equals(Constant.FRAGMENT_ALBUM))
							{
								fragment = new MusicFragmentWebMusic(
										Constant.FRAGMENT_ALBUM, musicList.get(arg2)[0]);
							}else
							{
								fragment = new MusicFragmentWebMusic(
										Constant.FRAGMENT_SINGER, musicList.get(arg2)[1]);
							}
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
					});
					handle.sendEmptyMessage(Constant.LOAD_COMPLETE);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
		return ll_web;
	}
}