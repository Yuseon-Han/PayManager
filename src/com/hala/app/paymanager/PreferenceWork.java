package com.hala.app.paymanager;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.ArrayAdapter;

public class PreferenceWork {
	private Context mContext;
	private SharedPreferences mPref;
	
	private final String PREF_NAME_GROUP_LIST = "group_list"; 
	private final String GroupCnt = "group_cnt";
	private final String GroupNum = "group_num";
	
	private final String PREF_NAME_MEMBER_LIST = "member_list";
	private final String MemberCnt = "member_cnt";
	private final String MemberName = "member_name";
	private final String MemberAccount = "member_account";
	
	private final String PREF_NAME_BG_COLOR = "bg_color";
	
	ArrayList<String> mGroups = new ArrayList<String>();

	PreferenceWork(Context context){
		mContext = context;
	}
	
	public void saveGroup(ArrayAdapter<String> groups){
		if(groups==null) return;
		
		int cnt = groups.getCount()-1;
		mPref = mContext.getSharedPreferences(PREF_NAME_GROUP_LIST, 0);
		SharedPreferences.Editor editor = mPref.edit();
		editor.putInt(GroupCnt, cnt);
		
		for(int i=0; i<cnt; i++){
			editor.putString(GroupNum+i, groups.getItem(i+1));
		}
		editor.commit();
	}
	
	public void addGroup(String group){
		if(group==null) return;
		
		mPref = mContext.getSharedPreferences(PREF_NAME_GROUP_LIST, 0);
		SharedPreferences.Editor editor = mPref.edit();
		int cnt = mPref.getInt(GroupCnt, 0);
		editor.putString(GroupNum+cnt, group);
		editor.putInt(GroupCnt, cnt+1);
		editor.commit();
	}
	
	public boolean removeGroup(String group){
		if(group==null) return false;
		
		mPref = mContext.getSharedPreferences(PREF_NAME_GROUP_LIST, 0);
		SharedPreferences.Editor editor = mPref.edit();
		int cnt = mPref.getInt(GroupCnt, 0);
		
		int index = -1;
		boolean success = false;
		for(int i=0; i<cnt; i++){
			if(index==-1 && mPref.getString(GroupNum+i, "").equals(group)){
				if(i==cnt-1){
					editor.remove(GroupNum+i);
					success= true;
					break;
				}else{
					int tmp = i+1;
					editor.putString(GroupNum+i, mPref.getString(GroupNum+tmp, ""));
					index = i;
					continue;
				}
			}
			
			if(index!= -1 && i > index){
				if(i == cnt-1){
					editor.remove(GroupNum+i);
					success= true;
				}else{
					int tmp = i+1;
					editor.putString(GroupNum+i, mPref.getString(GroupNum+tmp, ""));
				}
			}
		}
		
		if(success) editor.putInt(GroupCnt, cnt-1);
		editor.commit();
		
		
		//remove member pref
		mPref = mContext.getSharedPreferences(PREF_NAME_MEMBER_LIST + group, 0);
		editor = mPref.edit();
		editor.clear();
		editor.commit();
		
		return success;
	}
	
	public ArrayList<String> getGroup(){
		ArrayList<String> groups = new ArrayList<String>();
		
		mPref = mContext.getSharedPreferences(PREF_NAME_GROUP_LIST, 0);
		int cnt = mPref.getInt(GroupCnt, 0);
		
		for(int i=0; i<cnt; i++){
			groups.add(mPref.getString(GroupNum+i, ""));
		}
		
		if(mGroups!=null){
			mGroups.clear();
			mGroups = null;
		}
		mGroups = groups;
		return groups;
	}
	
	public void saveMember(ArrayList<String> members, String group){
		if(members==null) return;
		
		mPref = mContext.getSharedPreferences(PREF_NAME_MEMBER_LIST + group, 0);
		
		SharedPreferences.Editor editor = mPref.edit();
		editor.putInt(MemberCnt, members.size());
		
		int cnt = members.size();
		for(int i=0; i<cnt; i++){
			editor.putString(MemberName+i, members.get(i));
		}
		
		editor.commit();
	}
	
	public ArrayList<String> getMember(String group){
		ArrayList<String> members = new ArrayList<String>();
		
		mPref = mContext.getSharedPreferences(PREF_NAME_MEMBER_LIST + group, 0);
		
		int cnt = mPref.getInt(MemberCnt, -1);
		if(cnt < 0) return null;
		
		for(int i=0; i<cnt; i++){
			members.add(mPref.getString(MemberName+i, ""));
		}
		
		return members;
	}
	
	public void saveAccount(String group, String name, String account){
		mPref = mContext.getSharedPreferences(PREF_NAME_MEMBER_LIST + group, 0);
		Editor editor = mPref.edit();
		editor.putString(MemberAccount+name, account);
		editor.commit();
	}
	
	public String getAccount(String group, String name){
		String account=null;
		mPref = mContext.getSharedPreferences(PREF_NAME_MEMBER_LIST + group, 0);
		account = mPref.getString(MemberAccount+name, " ");
		return account;
	}
	
	public void saveBGColor(int index){
		mPref = mContext.getSharedPreferences(PREF_NAME_BG_COLOR, 0);
		Editor editor = mPref.edit();
		editor.putInt("color_index", index);
		editor.commit();
	}
	
	public int getBGColor(){
		int index = 0;
		mPref = mContext.getSharedPreferences(PREF_NAME_BG_COLOR, 0);
		index = mPref.getInt("color_index", 0);
		return index;
	}
}
