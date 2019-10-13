package com.example.fragment;

import java.util.List;

import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.GetBitmap;
import com.example.util.NetInfoUtil;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MusicFragmentWeb extends Fragment 
{

	private Handler handle;
	ScrollView sv_content;
	LinearLayout ll_content;
	LinearLayout ll_error;
	LinearLayout ll_web;
	List<String[]> singerListTop;
	List<String[]> albumListTop;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		ll_web = (LinearLayout) inflater.inflate(R.layout.fragment_web,container, false);
		sv_content = (ScrollView) inflater.inflate(R.layout.fragment_web_content, null);
		ll_error = (LinearLayout) inflater.inflate(R.layout.web_error, null);
		ll_content = (LinearLayout) sv_content.getChildAt(0);

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

		//添加搜索监听
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
		
		handle = new Handler() 
		{
			public void handleMessage(Message msg) 
			{
				super.handleMessage(msg);

				switch (msg.what) 
				{
				case Constant.LOAD_COMPLETE:
					ll_web.removeViewAt(1);
					ll_web.addView(sv_content);
					break;
				case Constant.LOAD_ERROR:
					ll_web.removeViewAt(1);
					ll_error.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					ll_web.addView(ll_error);
					break;
				}
			}
		};

		new Thread() 
		{
			@SuppressWarnings("deprecation")
			public void run() 
			{
				albumListTop = NetInfoUtil.getAlbumsListTop();
				if(albumListTop==null)
				{
					handle.sendEmptyMessage(Constant.LOAD_ERROR);
					return;
				}
				singerListTop = NetInfoUtil.getSingerListTop();
				if(singerListTop==null)
				{
					handle.sendEmptyMessage(Constant.LOAD_ERROR);
					return;
				}
				
				//获取各个控件
				LinearLayout ll_content_firstchild = (LinearLayout) ll_content.getChildAt(0);//第一行
				RelativeLayout ll_content_music = (RelativeLayout) ll_content_firstchild.getChildAt(0);//歌曲
				RelativeLayout ll_content_singer = (RelativeLayout) ll_content_firstchild.getChildAt(1);//歌手
				RelativeLayout ll_content_album = (RelativeLayout) ll_content_firstchild.getChildAt(2);//专辑
				LinearLayout ll_content_thirdchild = (LinearLayout) ll_content.getChildAt(2);//三行
				RelativeLayout ll_content_singer_first = (RelativeLayout) ll_content_thirdchild.getChildAt(0);//歌手top1
				RelativeLayout ll_content_album_first = (RelativeLayout) ll_content_thirdchild.getChildAt(1);//专辑top1
				LinearLayout ll_content_fourthchild = (LinearLayout) ll_content.getChildAt(3);//四行
				RelativeLayout ll_content_singer_second = (RelativeLayout) ll_content_fourthchild.getChildAt(0);//歌手top2
				RelativeLayout ll_content_album_second = (RelativeLayout) ll_content_fourthchild.getChildAt(1);//专辑top2
				LinearLayout ll_content_fifthchild = (LinearLayout) ll_content.getChildAt(4);//五行
				RelativeLayout ll_content_singer_third = (RelativeLayout) ll_content_fifthchild.getChildAt(0);//歌手top3
				RelativeLayout ll_content_album_third = (RelativeLayout) ll_content_fifthchild.getChildAt(1);//专辑top3

				try
				{
					BitmapDrawable bd_temp=new BitmapDrawable(GetBitmap.getOneBitmap(albumListTop.get(0)[2],albumListTop.get(0)[0]));
					ll_content_album_first.setBackground(bd_temp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					BitmapDrawable bd_temp=new BitmapDrawable(GetBitmap.getOneBitmap(albumListTop.get(1)[2],albumListTop.get(1)[0]));
					ll_content_album_second.setBackground(bd_temp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					BitmapDrawable bd_temp=new BitmapDrawable(GetBitmap.getOneBitmap(albumListTop.get(2)[2],albumListTop.get(2)[0]));
					ll_content_album_third.setBackground(bd_temp);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				try
				{
					if(albumListTop.get(0).length!=1)
					{
						TextView tv_temp;
						String textTemp;
						tv_temp=(TextView)ll_content_album_first.getChildAt(0);
						textTemp=albumListTop.get(0)[0];
						tv_temp.setText(textTemp);
						tv_temp=(TextView)ll_content_album_second.getChildAt(0);
						textTemp=albumListTop.get(1)[0];
						tv_temp.setText(textTemp);
						tv_temp=(TextView)ll_content_album_third.getChildAt(0);
						tv_temp.setText(albumListTop.get(2)[0]);
						tv_temp=(TextView)ll_content_singer_first.getChildAt(0);
						tv_temp.setText(singerListTop.get(0)[1]);
						tv_temp=(TextView)ll_content_singer_second.getChildAt(0);
						tv_temp.setText(singerListTop.get(1)[1]);
						tv_temp=(TextView)ll_content_singer_third.getChildAt(0);
						tv_temp.setText(singerListTop.get(2)[1]);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				//添加各个控件监听
				ll_content_music.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
								Constant.FRAGMENT_MUSIC);
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
					}
				});

				ll_content_singer.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						MusicFragmentWebOther fragment = new MusicFragmentWebOther(
								Constant.FRAGMENT_SINGER);
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
					}

				});

				ll_content_album.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						MusicFragmentWebOther fragment = new MusicFragmentWebOther(Constant.FRAGMENT_ALBUM);
						FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
						MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
					}
				});

				ll_content_singer_first.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
									Constant.FRAGMENT_SINGER, singerListTop.get(0)[1]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
						
					}
				});

				ll_content_album_first.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
								Constant.FRAGMENT_ALBUM, albumListTop.get(0)[0]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
					}
				});

				ll_content_singer_second.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
								Constant.FRAGMENT_SINGER, singerListTop.get(1)[1]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
					}
				});

				ll_content_album_second.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
								Constant.FRAGMENT_ALBUM, albumListTop.get(1)[0]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
					}
				});

				ll_content_singer_third.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
									Constant.FRAGMENT_SINGER, singerListTop.get(2)[1]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
					}
				});

				ll_content_album_third.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) 
					{
						try
						{
							MusicFragmentWebMusic fragment = new MusicFragmentWebMusic(
								Constant.FRAGMENT_ALBUM, albumListTop.get(2)[0]);
							FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
							MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
						}
						catch(Exception e)
						{
							Toast.makeText(getActivity(), "您所选的专辑不存在", Toast.LENGTH_SHORT);
						}
					}
				});
				handle.sendEmptyMessage(Constant.LOAD_COMPLETE);
			}
		}.start();
		return ll_web;
	}
}