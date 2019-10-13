package com.example.fragment;

import java.util.ArrayList;

import com.example.bnmusic.R;
import com.example.util.Constant;
import com.example.util.DBUtil;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicFragmentPlaylist extends Fragment
{
	public static BaseAdapter ba;
	ArrayList<String[]> playlist;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		LinearLayout ll_main = (LinearLayout)inflater.inflate(R.layout.fragment_playlist, container, false);
		
		ImageButton ib_back = (ImageButton) ll_main.findViewById(R.id.playlist_imagebutton_back_l);
		ib_back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.popBackStack();
			}
		});
		
		TextView tv_title=(TextView)ll_main.findViewById(R.id.playlist_textview_title_l);
		tv_title.setText("我的歌单");
		
		ImageView iv_search = (ImageView) ll_main.findViewById(R.id.playlist_imagebutton_search_l);
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
		
		LinearLayout ll_create = (LinearLayout)ll_main.findViewById(R.id.playlist_linearlayout_create);
		ll_create.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				final EditText etTemp = new EditText(getActivity());
				etTemp.setSingleLine();
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("创建歌单");
				builder.setView(etTemp);
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog,int which) 
					{
						dialog.dismiss();
						DBUtil.createPlayList(etTemp.getText().toString());
						playlist = DBUtil.getPlayList();
						ba.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener()
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
		return ll_main;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		playlist = DBUtil.getPlayList();
		setListView();
	}
	
	private void setListView()
	{
		ba = new BaseAdapter()
		{
			LayoutInflater inflater = LayoutInflater.from(getActivity());

			@Override
			public int getCount() {
				return playlist.size() + 1;
			}

			@Override
			public Object getItem(int arg0) {
				return null;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public View getView(int arg0, View arg1, ViewGroup arg2) 
			{
				if (arg0 == playlist.size())
				{
					LinearLayout lll = (LinearLayout) inflater.inflate(
							R.layout.listview_count, null).findViewById(
							R.id.linearlayout_null);
					TextView tv_sum = (TextView) lll.getChildAt(0);
					tv_sum.setText("共有" + playlist.size() + "个歌单\n\n\n");
					return lll;
				}

				LinearLayout ll = (LinearLayout) inflater.inflate
						(R.layout.fragment_localmusic_listview_row,
						null).findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				tv.setText(playlist.get(arg0)[1]);
				return ll;
			}
		};

		ListView lv = (ListView) getActivity().findViewById(R.id.web_listview_music);

		lv.setAdapter(ba);

		lv.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) 
			{
				// 如果点击的是最后一项
				if (arg2 == arg0.getCount() - 1) 
				{
					return;
				}
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				MusicFragmentFour fragment=new MusicFragmentFour(Constant.FRAGMENT_MYLIST,playlist.get(arg2)[0]);
				MusicFragmentMain.changeFragment(fragmentTransaction, fragment);
			}
		});
		
		lv.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int selectTemp = arg2;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("更多功能");
				builder.setItems(new String[] {"删除歌单"}, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) 
					{
						switch(which)
						{
						case 0:
							int temp=Integer.parseInt(playlist.get(selectTemp)[0]);
							DBUtil.deletePlayList(temp);
							playlist=DBUtil.getPlayList();
							ba.notifyDataSetChanged();
							break;
						}
						dialog.dismiss();
					}
				}).create().show();
				return false;
			}
		});
	}
}