package com.hala.app.paymanager;


import java.util.ArrayList;
import java.util.HashMap;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.hala.app.paymanager.R.string;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class GroupActivity extends Activity {
	public static final int MAX_GROUP_COUNT = 10;
	public static final int MAX_MEMBER_COUNT = 30;
	public static final int MAX_MEETING_COUNT = 7;
	private LayoutInflater mInflater;
	private Resources mRes;
	private PreferenceWork mPreferenceWork;
	
	//group
	private Spinner mSpinner;
	private ArrayAdapter<String> mAdapter;
//	private Button mBtnOrder;
	
	//member
	private LinearLayout mMemberList;
	private Button mBtnAddMember;
	private ScrollView mScrollViewMember;
	private int mCurrentSelectedIndex = -1; //for account 
	
	//meeting
	private int mCntOfMetting=1;
	private LinearLayout mMettingList;
	private ArrayAdapter<String> mMeetingAdapter;
	private TextView mTextView_addMeeting;
	private TextView mTextView_removeMeeting;
	
	//result
	private LinearLayout mResultLayout;
	private Button mBtn_enter;
	private EditText mEditText_result;
	
	//bg
	private LinearLayout mGroupMainView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        
        mPreferenceWork = new PreferenceWork(this);
        
        mRes = getResources();
        mInflater = getLayoutInflater();
        
        mGroupMainView = (LinearLayout) findViewById(R.id.group_bg_view);
        mGroupMainView.setBackgroundResource(MainActivity.SELECTED_BG_RES);
        
        mSpinner = (Spinner) findViewById(R.id.group_spinner);
        mSpinner.setOnItemSelectedListener(mSpinnerListener);
        
        mMemberList = (LinearLayout) findViewById(R.id.members);
        
        mScrollViewMember = (ScrollView) findViewById(R.id.scrollView_member);
        
        /////////////////////////////////////////////////////////////////
        mTextView_addMeeting = (TextView) findViewById(R.id.addMeeting);
        mTextView_removeMeeting = (TextView) findViewById(R.id.removeMeeting);
        
        mMettingList = (LinearLayout) findViewById(R.id.mettings_g);
        
        mBtn_enter = (Button) findViewById(R.id.btn_enter_g);
        mBtn_enter.setOnClickListener(listener);
        
        mEditText_result = (EditText) findViewById(R.id.edit_text_result_text_g);
        mResultLayout = (LinearLayout) findViewById(R.id.result_layout_g);
			
        init();
    }
    
    private void init(){
    	//spinner setting
    	mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item/*, mGroupList*/);

    	String str_addgroup = mRes.getString(R.string.selectGroup);
    	mAdapter.add(str_addgroup);
    	
    	ArrayList<String> groups = mPreferenceWork.getGroup();
    	
    	for(int i=0; i<groups.size(); i++){
    		mAdapter.add(groups.get(i));
    	}
    	
    	mSpinner.setAdapter(mAdapter);
    	//addMember();
    	
    	///////////////////////////////////////////////////////////////////
    	//button
    	int cnt = mCntOfMetting+1;
        String str = getResources().getString(R.string.addMetting);
        mTextView_addMeeting.setText(cnt + str);
        
        //meeting list
        LinearLayout tmp = (LinearLayout) mInflater.inflate(R.layout.meeting_item_in_group, null);
        TextView tmp_tv = (TextView) tmp.findViewById(R.id.mettingCnt);
        str = getResources().getString(R.string.CntOfMetting);
        tmp_tv.setText(mCntOfMetting + str);
        mMettingList.addView(tmp);
        
        mMeetingAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item);
        
        addMetting();
        addMetting();
        
        mMemberList.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        
        av =(AdView)findViewById(R.id.adView);
        AdRequest adRequest =new  AdRequest.Builder().build();
        //AdRequest adRequest =new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("003FD1C218C6B7281B10DB23A39DFA4E").build();
        av.loadAd(adRequest);
    }
    AdView av;
    @Override
    protected void onPause() {
        if(av!=null) av.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(av!=null) av.resume();
    }
    
    @Override
    public void onDestroy() {
      // Destroy the AdView.
      if (av != null) av.destroy();
      super.onDestroy();
    }
    
    private void addMetting(){
    	mCntOfMetting++;
    	
    	//add metting
        LinearLayout tmp = (LinearLayout) mInflater.inflate(R.layout.meeting_item_in_group, null);
        TextView tmp_tv = (TextView) tmp.findViewById(R.id.mettingCnt);
        String str = getResources().getString(R.string.CntOfMetting);
        tmp_tv.setText(mCntOfMetting + str);
        Spinner spinner = (Spinner) tmp.findViewById(R.id.meeting_spinner);
        if(spinner!=null) spinner.setAdapter(mMeetingAdapter);
        mMettingList.addView(tmp);
        
        //btn text update
        int cnt = mCntOfMetting+1;
        str = getResources().getString(R.string.addMetting);
        mTextView_addMeeting.setText(cnt + str);
        
        mMettingList.requestLayout();
        
        //update removeMetting btn
        if(mCntOfMetting > 1){
	        str = getResources().getString(R.string.removeMetting);
	        mTextView_removeMeeting.setText(mCntOfMetting + str);
			
	        if( mTextView_removeMeeting.getVisibility()!=View.VISIBLE){
	        	mTextView_removeMeeting.setVisibility(View.VISIBLE);
	        }
		}
        
        addMettingCheckBox();
    }
    
    private void addMettingCheckBox(){
    	for(int i=0; i<mMemberList.getChildCount(); i++){
    		 LinearLayout member_item = (LinearLayout) mMemberList.getChildAt(i);
    		 if(member_item==null) continue;
    		 
    		 CheckBox ch_attend = (CheckBox) member_item.findViewById(R.id.chbox_attend);
    		 
    		 LinearLayout ch_list = (LinearLayout) member_item.findViewById(R.id.chbox_list);
    		 if(ch_list==null) continue;
    		 
    		 LinearLayout ch_layout = (LinearLayout) mInflater.inflate(R.layout.meeting_checkbox_item, null);
    		 if(ch_layout==null) continue;
    		 
    		 TextView tx = (TextView) ch_layout.findViewById(R.id.tv_metting_cnt);
    		 if(tx==null) continue;
    		 
    		 CheckBox ch = (CheckBox) ch_layout.findViewById(R.id.chbox_metting_attend);
    		 
    		 String str = getResources().getString(R.string.CntOfMetting);
    		 tx.setText(mCntOfMetting + str);
    		 
    		 if(ch_attend!=null && ch!=null){
    			 ch.setChecked(ch_attend.isChecked());
    			 if(!ch_attend.isChecked()){
    				 ch.setEnabled(false);
    			 }
    		 }
    		 
    		 ch_list.addView(ch_layout);
    	}
    	
    	updateChBoxScrollViewWidth();
    }
    
    
    private void updateChBoxScrollViewWidth(){
    	if(mMemberList.getChildCount() < 1) return;
    	
    	int windowWidth = getWindowManager().getDefaultDisplay().getWidth();
    	
    	LinearLayout btn_set = (LinearLayout) mMemberList.getChildAt(0).findViewById(R.id.btn_set);
    	if(btn_set!=null){
    		int bnt_set_width = btn_set.getWidth();
        	int info_set_width = windowWidth - bnt_set_width;
        	
        	int cnt = mMemberList.getChildCount();
        	for(int i=0; i<cnt; i++){
        		LinearLayout info_set = (LinearLayout) mMemberList.getChildAt(i).findViewById(R.id.info_set);
            	if(info_set==null) return;
            	
            	info_set.getLayoutParams().width = info_set_width;
        	}
    	}
    }
    
    private void updateChBoxScrollViewWidth(int index){
    	if(mMemberList.getChildCount() < 1 || mMemberList.getChildCount() <= index) return;
    	
    	int windowWidth = getWindowManager().getDefaultDisplay().getWidth();
    	
    	LinearLayout btn_set = (LinearLayout) mMemberList.getChildAt(0).findViewById(R.id.btn_set);
    	if(btn_set==null) return;
    	
    	int bnt_set_width = btn_set.getWidth();
    	int info_set_width = windowWidth - bnt_set_width;
    	
    	LinearLayout info_set = (LinearLayout) mMemberList.getChildAt(index).findViewById(R.id.info_set);
        if(info_set==null) return;
        	
        info_set.getLayoutParams().width = info_set_width;
    }
    
    private void addMettingCheckBoxOfMemeber(int index){
    	 LinearLayout member_item = (LinearLayout) mMemberList.getChildAt(index);
		 if(member_item==null) return;
		 
		 LinearLayout ch_list = (LinearLayout) member_item.findViewById(R.id.chbox_list);
		 if(ch_list==null) return;
		 
		 for(int i=0; i<mCntOfMetting; i++){
			 LinearLayout ch = (LinearLayout) mInflater.inflate(R.layout.meeting_checkbox_item, null);
			 if(ch==null) return;
			 
			 TextView tx = (TextView) ch.findViewById(R.id.tv_metting_cnt);
			 if(tx==null) return;
 			   
			 String str = getResources().getString(R.string.CntOfMetting);
			 int cnt = i+1;
			 tx.setText(cnt + str);
			 
			 ch_list.addView(ch);
		 }
    }
    
    private void removeMettingCheckBox(){
    	for(int i=0; i<mMemberList.getChildCount(); i++){
    		 LinearLayout member_item = (LinearLayout) mMemberList.getChildAt(i);
    		 if(member_item==null) continue;
    		 
    		 LinearLayout ch_list = (LinearLayout) member_item.findViewById(R.id.chbox_list);
    		 if(ch_list==null) continue;
    		 
//    		 LinearLayout ch = (LinearLayout) mInflater.inflate(R.layout.meeting_checkbox_item, null);
//    		 if(ch==null) continue;
//    		 
//    		 ch_list.addView(ch);
    		 
    		 int childCnt = ch_list.getChildCount();
    		 if(childCnt>0){
    			 ch_list.removeViewAt(childCnt-1);
    		 }
    	}
    }
    
    private void addMember(){
    	//add metting
        LinearLayout tmp = (LinearLayout) mInflater.inflate(R.layout.member_item, null);
        TextView tmp_tv = (TextView) tmp.findViewById(R.id.number);
        mMemberList.addView(tmp);
        int count = mMemberList.getChildCount();
        tmp_tv.setText(""+count);
        
        addMettingCheckBoxOfMemeber(mMemberList.getChildCount()-1);
        updateChBoxScrollViewWidth(mMemberList.getChildCount()-1);
        
        //btn text update
        //mMemberList.requestLayout();
        
        CheckBox ch_attend = (CheckBox) tmp.findViewById(R.id.chbox_attend);
        ch_attend.setOnCheckedChangeListener(mAttendChBoxListener);
        
        //scroll to end
        if(mScrollViewMember!=null){
        	mScrollViewMember.post(new Runnable() {
				@Override
				public void run() {
					mScrollViewMember.fullScroll(ScrollView.FOCUS_DOWN);
				}
			});
        }
    }
    
    public void removeMember(View view){
    	int childIndex = -1;
    	for(int i=0; i<mMemberList.getChildCount(); i++){
    		LinearLayout tmp =  (LinearLayout) mMemberList.getChildAt(i);
    		TextView tv = (TextView) tmp.findViewById(R.id.btn_delete_member);
    		
    		if(view == tv){
    			childIndex = i;
				break;
    		}
    	}
    	
    	if(childIndex!=-1){
    		mMemberList.removeViewAt(childIndex);
    		
    		updateMembersNum();
    	}
    }
    
    private void updateMembersNum(){
    	for(int i=0; i<mMemberList.getChildCount(); i++){
    		LinearLayout tmp =  (LinearLayout) mMemberList.getChildAt(i);
    		TextView tv = (TextView) tmp.findViewById(R.id.number);
    		
    		if(tv!=null){
    			int cnt = i+1;
    			tv.setText(""+cnt);
    		}
    	}
    }
    
    private void updateMemberList(ArrayList<String> members){
    	if(members==null || members.size()==0){
    		mMemberList.removeAllViewsInLayout();
    		addMember();
    		removeEmptyMemebr();
    	}else{
    		int newMemListSize = members.size();
        	mMemberList.removeAllViewsInLayout();
        	
        	for(int i=0; i<newMemListSize; i++){
        		addMember();
        	}
//        	int currentMemListSize = mMemberList.getChildCount();
        	
//        	if(newMemListSize > currentMemListSize){
//        		int addCnt = newMemListSize - currentMemListSize;
//        		
//        		for(int i=0; i<addCnt; i++){
//        			addMember();
//        		}
//        	}else if(newMemListSize < currentMemListSize){
//        		int removeCnt = currentMemListSize - newMemListSize;
//        		
//        		int lastIndex = mMemberList.getChildCount()-1;
//        		for(int i=0; i<removeCnt; i++){
//        			mMemberList.removeViewAt(lastIndex-i);
//        		}
//        	}
        	
        	for(int i=0; i < mMemberList.getChildCount(); i++){
        		LinearLayout child = (LinearLayout) mMemberList.getChildAt(i);
        		if(child==null) continue;
        		
        		TextView name = (TextView) child.findViewById(R.id.name);
        		if(name==null) continue;
        		
        		name.setText(members.get(i));
        	}
    	}
    	
    	//update meeting spinner
    	updateMeetingSpinner(members);
    	
    }
    
    private void updateMeetingSpinner(ArrayList<String> members){
    	int cnt = 0;
    	if(members!=null){
    		cnt = members.size();
    	}
    	
    	mMeetingAdapter.clear();
    	
    	for(int i=0; i<cnt; i++){
    		mMeetingAdapter.add(members.get(i));
    	}
    	
    	int meetingcnt = mMettingList.getChildCount();
    	for(int i=0; i<meetingcnt; i++){
    		Spinner spinner = (Spinner) mMettingList.getChildAt(i).findViewById(R.id.meeting_spinner);
    		if(spinner!=null){
    			int selected_position = -1;
    			try{		
    				selected_position =spinner.getSelectedItemPosition();
    			}catch(Exception e){
    			}
    			spinner.setAdapter(mMeetingAdapter);
    			
    			if(spinner.getCount() > selected_position){
    				spinner.setSelection(selected_position);
    			}else{
    				spinner.setSelection(0);
    			}
    			
    		}
    	}
    }
    
    private void removeEmptyMemebr(){
    	for(int i=0; i<mMemberList.getChildCount(); i++){
    		EditText edit = (EditText) mMemberList.getChildAt(i).findViewById(R.id.name);
    		if(edit==null){
    			continue;
    		}
    		
    		if(edit.getText().toString().length()==0){
    			mMemberList.removeViewAt(i);
    		}
    	}
    }
    
    private void setAttendMemberCount(){
    	int meetingCnt = mMettingList.getChildCount();
    	for(int i=0; i<meetingCnt; i++){
    		//calculate attend cnt
    		int attendCnt = 0;
    		int memberCnt = mMemberList.getChildCount(); 
    		for(int j=0; j<memberCnt; j++){
    			LinearLayout ch_list = (LinearLayout) mMemberList.getChildAt(j).findViewById(R.id.chbox_list);
    			if(ch_list!=null && ch_list.getChildAt(i)!=null){
    				CheckBox chBox = (CheckBox) (ch_list.getChildAt(i).findViewById(R.id.chbox_metting_attend));
        			if(chBox!=null && chBox.isChecked()) attendCnt++;
    			}
    		}
    		
    		//set textView
    		LinearLayout child = (LinearLayout) mMettingList.getChildAt(i);
    		if(child!=null){
    			TextView howMany = (TextView) child.findViewById(R.id.howmany);
        		if(howMany!=null) howMany.setText(""+attendCnt);
    		}
    	}
    }
    
    
    private String getResultString(){
    	String result="";
    	
    	String group = mSpinner.getSelectedItem().toString();
		if(group==null || group.equals(mRes.getString(R.string.selectGroup))){
			return "error";
		}
    	
		ArrayList<Integer> payList = new ArrayList<Integer>();
		ArrayList<String> payerList = new ArrayList<String>();
		
    	for(int i=0; i<mCntOfMetting; i++){
    		LinearLayout child =  (LinearLayout) mMettingList.getChildAt(i);
    		if(child!=null){
	    		Spinner spinner = (Spinner) child.findViewById(R.id.meeting_spinner);
	    		Object obj = spinner.getSelectedItem();
	    		if(obj==null){
	    			return mRes.getString(R.string.info_save_member);
	    		}
	    		String selectedPayer = spinner.getSelectedItem().toString();
	    		
	    		EditText howmuch = (EditText) child.findViewById(R.id.howmuch);
	    		TextView howmany = (TextView) child.findViewById(R.id.howmany);
	    		if(howmuch==null || howmany == null){
	    			result = "";
	    			break;
	    		}
	    		
	    		int much =0;
	    		int many =0;
	    		try{
		    		much = Integer.parseInt(howmuch.getText().toString());
		    		many = Integer.parseInt(howmany.getText().toString());
	    		}catch(Exception e){
	    		}
	    		
	    		if(much==0 || many==0){
	    			continue;
	    		}
	    		
	    		int result_pay = much/many;
	    		int cnt = i+1;
	    		String result_tmp = cnt + mRes.getString(R.string.mettingAndResult) + 
	    				result_pay + mRes.getString(R.string.measure) + 
	    				", " + mRes.getString(R.string.calculater) +
	    				": " + selectedPayer + "(" + mRes.getString(R.string.account) + ": " 
	    				+  mPreferenceWork.getAccount(group, selectedPayer)+ ")" + "\n";
	    		
	    		result  = result+result_tmp;
	    		
	    		payerList.add(selectedPayer);
	    		payList.add(result_pay);
    		}
    	}
    	
    	result = result + getEachMemberResult(payList, payerList);
    	
    	return result;
    }
    
    private String getEachMemberResult(ArrayList<Integer> payList, ArrayList<String> payerList){
    	if(payList ==null || payerList==null || payList.size()==0 || payerList.size() ==0 || payList.size()!= payerList.size()){
    		return "";
    	}
    	
    	String result="\n";
    	
    	//for calculate payers one
    	HashMap<String, ArrayList<Integer>> hash_PayList = new HashMap<String, ArrayList<Integer>>();
    	HashMap<String, ArrayList<String>> hash_payerList = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> hash_payer = new ArrayList<String>();
    	
    	ArrayList<String> allMember = getMemberList();
    	
    	int MemberCnt = allMember.size();
    	int MeetingCnt = payList.size();
    	
    	for(int i=0; i<MemberCnt; i++){
    		ArrayList<Integer> mAccumulatePayList = new ArrayList<Integer>();
        	ArrayList<String> mAccuMulatePayerList = new ArrayList<String>();
    		
    		String myName = allMember.get(i);
    	
    		for(int j=0; j<MeetingCnt; j++){
    			//is this member attended?
    			boolean attend = true;
    			boolean payMyself = false;
    			LinearLayout ch_list = (LinearLayout) mMemberList.getChildAt(i).findViewById(R.id.chbox_list);
    			if(ch_list!=null && ch_list.getChildAt(j)!=null){
    				CheckBox chBox = (CheckBox) (ch_list.getChildAt(j).findViewById(R.id.chbox_metting_attend));
    				if(chBox!=null && !chBox.isChecked()){
    					attend = false;
    				}
    			}

    			//is this turn's payer is Me?
    			if(payerList.get(j).equals(myName)){
    				payMyself = true;
    			}
    			if(!attend || payMyself) continue;
    			
    			if(mAccuMulatePayerList.contains(payerList.get(j))){
    				int index = mAccuMulatePayerList.indexOf(payerList.get(j));
    				mAccumulatePayList.set(index, mAccumulatePayList.get(index) + payList.get(j));
    			}else{
    				mAccuMulatePayerList.add(payerList.get(j));
    				mAccumulatePayList.add(payList.get(j));
    			}
    		}
    		
    		if(payerList.contains(myName) && mAccuMulatePayerList.size()>0){
    			hash_payer.add(myName);
    			hash_PayList.put(myName, (ArrayList<Integer>) mAccumulatePayList.clone());
    			hash_payerList.put(myName, (ArrayList<String>) mAccuMulatePayerList.clone());
    		}else{
    			
    			result = result + "-" + myName + ": ";
        		for(int k=0; k<mAccumulatePayList.size(); k++){
        			if(mAccuMulatePayerList.get(k).equals(myName)){
        			}else{
        				result = result + mAccumulatePayList.get(k) + mRes.getString(R.string.measure)  + ">" + mAccuMulatePayerList.get(k) +", ";
        			}
        		}
        		result = result + "\n";
    		}
    		
    		mAccuMulatePayerList.clear();
    		mAccuMulatePayerList = null;
    		mAccumulatePayList.clear();
    		mAccumulatePayList = null;
    	}
    	
    	//for calculate payers one 
    	int payerCnt = hash_payerList.size();
    	for(int i=0; i<payerCnt; i++){
    		if(i<payerCnt-1){
    			//compare i and j
    			for(int j=i+1; j<payerCnt; j++){
    				String name_i = hash_payer.get(i);
    				String name_j = hash_payer.get(j);
    				
    				if(hash_payerList.get(name_i).contains(name_j) && hash_payerList.get(name_j).contains(name_i)){
    					
    					int indexItoJ = hash_payerList.get(name_i).indexOf(name_j);
    					int money_ItoJ = hash_PayList.get(name_i).get(indexItoJ); 
    					
    					int indexJtoI = hash_payerList.get(name_j).indexOf(name_i);
    					int money_JtoI = hash_PayList.get(name_j).get(indexJtoI); 
    					
    					if(money_ItoJ > money_JtoI){
    						int diff = money_ItoJ - money_JtoI;
    						
    						hash_PayList.get(name_i).set(indexItoJ, diff);
    						
    						hash_PayList.get(name_j).remove(indexJtoI);
    						hash_payerList.get(name_j).remove(indexJtoI);
    						
    					}else if(money_ItoJ < money_JtoI){
    						
    						int diff = money_JtoI - money_ItoJ;
    						
    						hash_PayList.get(name_j).set(indexJtoI, diff);
    						
    						hash_PayList.get(name_i).remove(indexItoJ);
    						hash_payerList.get(name_i).remove(indexItoJ);
    						
    					}else if(money_ItoJ == money_JtoI){
    						hash_PayList.get(name_i).remove(indexItoJ);
    						hash_payerList.get(name_i).remove(indexItoJ);
    						
    						hash_PayList.get(name_j).remove(indexJtoI);
    						hash_payerList.get(name_j).remove(indexJtoI);
    					}
    				}
    			}
    		}
    	}
    	
    	for(int i=0; i<payerCnt; i++){
    		String tmpName = hash_payer.get(i);
    		
    		result = result + "-" + tmpName+ ": ";
    		
    		ArrayList<String> tmp_payerList = hash_payerList.get(tmpName);
    		ArrayList<Integer> tmp_payList = hash_PayList.get(tmpName);
    		
    		for(int k=0; k<tmp_payList.size(); k++){
    			if(tmp_payerList.get(k).equals(tmpName)){
    			}else{
    				result = result + tmp_payList.get(k) + mRes.getString(R.string.measure)  + ">" + tmp_payerList.get(k) +", ";
    			}
    		}
    		result = result + "\n";
    	}
    	
    	hash_payer.clear();
    	hash_payerList.clear();
    	hash_PayList.clear();
    	
    	return result;
    }
    
    private ArrayList<String> getMemberList(){
    	ArrayList<String> members = new ArrayList<String>();
    	
    	int cnt = mMemberList.getChildCount();
    	
    	for(int i=0; i<cnt; i++){
    		EditText edit = null;
    		try{
    			edit = (EditText) mMemberList.getChildAt(i).findViewById(R.id.name);
    		}catch(Exception e){
    		}
    		
    		if(edit==null) continue;
    		
    		String name = edit.getText().toString();
    		
    		if(name.equals("")) continue;
    		
    		if(!members.contains(name)){
    			members.add(name);
    		}
    	}
    	
    	return members;
    }
    
    
    private void checkMemberListAndRemoveDuplicatedMember(){
    	ArrayList<String> members = new ArrayList<String>();
    	
    	int cnt = mMemberList.getChildCount();
    	
    	for(int i=0; i<cnt; i++){
    		EditText edit = null;
    		try{
    			edit = (EditText) mMemberList.getChildAt(i).findViewById(R.id.name);
    		}catch(Exception e){
    		}
    		
    		if(edit==null) continue;
    		
    		String name = edit.getText().toString();
    		
    		if(name.equals("")) continue;
    		
    		if(!members.contains(name)){
    			members.add(name);
    		}else{
    			mMemberList.removeViewAt(i);
        		updateMembersNum();
    		}
    	}
    	
    }
    
    public void addGroup(View view){
    	if(mAdapter.getCount() >= MAX_GROUP_COUNT+1){
			Toast.makeText(getApplicationContext(), R.string.noMoreAdd, Toast.LENGTH_SHORT).show();
		}else{
			showDialog(DIALOG_ADDGROUP);
		}
    }
    
    
    public void removeGroup(View view){
    	if(mSpinner.getSelectedItemPosition()!=0){
			showDialog(DIALOG_REMOVE_GROUP);
		}
    }
    
    public void addMember(View view){
    	EditText edit = null;
		try{
			edit = (EditText) mMemberList.getChildAt(mMemberList.getChildCount()-1).findViewById(R.id.name);
		}catch(Exception e){
		}
		
		if(edit!=null && edit.getText().toString().equals("")){
			Toast.makeText(getApplicationContext(), R.string.thereisblank, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String group = mSpinner.getSelectedItem().toString();
		
		
		if(mMemberList.getChildCount()>=MAX_MEMBER_COUNT){
			Toast.makeText(getApplicationContext(), R.string.noMoreAdd, Toast.LENGTH_SHORT).show();
		}else if(group==null || group.equals(mRes.getString(R.string.selectGroup))){
			Toast.makeText(getApplicationContext(), R.string.selectGroup_noti, Toast.LENGTH_SHORT).show();
		}else {
			addMember();
		}
    }
    
    
    public void saveMember(View view){
    	checkMemberListAndRemoveDuplicatedMember();
    	
    	String group = mSpinner.getSelectedItem().toString();
		if(group==null || group.equals(mRes.getString(R.string.selectGroup))){
			Toast.makeText(getApplicationContext(), R.string.selectGroup_noti, Toast.LENGTH_SHORT).show();
		}else{
			//mPreferenceWork.saveMember(getMemberList(), group);
			new SaveMemberAsynctask().execute(group);
		}
		
		updateMeetingSpinner(getMemberList());
    }
    
    public void addMeeting(View view){
    	if(mCntOfMetting>=MAX_MEETING_COUNT){
			Toast.makeText(getApplicationContext(), R.string.noMoreAdd, Toast.LENGTH_SHORT).show();
		}else{
			addMetting();
		} 
    }
    
    public void removeMetting(View view){
    	mMettingList.removeViewAt(mMettingList.getChildCount()-1);
		
		String str = getResources().getString(R.string.addMetting);
        mTextView_addMeeting.setText(mCntOfMetting + str);
		
		mCntOfMetting--;
		
		str = getResources().getString(R.string.removeMetting);
		mTextView_removeMeeting.setText(mCntOfMetting + str);
		
		if(mCntOfMetting==1){
			mTextView_removeMeeting.setVisibility(View.INVISIBLE);
		}
		
		removeMettingCheckBox();
    }
    private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v==mBtn_enter){
				removeEmptyMemebr();
				String group = mSpinner.getSelectedItem().toString();
				if(group==null || group.equals(mRes.getString(R.string.selectGroup))){
					Toast.makeText(getApplicationContext(), R.string.selectGroup_noti, Toast.LENGTH_SHORT).show();
				}else if(mMemberList.getChildCount()<1){
					Toast.makeText(getApplicationContext(), R.string.info_save_member, Toast.LENGTH_SHORT).show();
				}else{
					checkMemberListAndRemoveDuplicatedMember();
					
					new SaveMemberAsynctask().execute(group);
					updateMeetingSpinner(getMemberList());
					
					setAttendMemberCount();
					
					mResultLayout.setVisibility(View.VISIBLE);
					
					String result = getResultString();
					mEditText_result.setText(result);
					mEditText_result.setVisibility(View.VISIBLE);
				}
			}
		}
	};
	
    private OnItemSelectedListener mSpinnerListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			// TODO Auto-generated method stub
			String selected = parent.getItemAtPosition(position).toString();
			if(selected.equals(mRes.getString(R.string.selectGroup))){
				updateMemberList(null);
			}
			else{
				new GetMemberAsyncTask().execute(selected);
				//ArrayList<String> members = mPreferenceWork.getMember(selected);
				//updateMemberList(members);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private OnCheckedChangeListener mAttendChBoxListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			int index = -1;
			
			int cnt = mMemberList.getChildCount();
			for(int i=0; i<cnt; i++){
				CheckBox ch = (CheckBox) mMemberList.getChildAt(i).findViewById(R.id.chbox_attend);
				if(ch==null) continue;
				
				if(ch.equals(buttonView)){
					index = i;
					break;
				}
			}
			
			if(index==-1 || index >= mMemberList.getChildCount()){
				return;
			}
			
			LinearLayout ch_list = (LinearLayout) mMemberList.getChildAt(index).findViewById(R.id.chbox_list);
			if(ch_list==null){
				return;
			}
			
			for(int i=0; i<ch_list.getChildCount(); i++){
				try{
					CheckBox chBox = (CheckBox) (ch_list.getChildAt(i).findViewById(R.id.chbox_metting_attend));
					if(isChecked){
						chBox.setEnabled(true);
						chBox.setChecked(isChecked);
					}else{
						chBox.setChecked(isChecked);
						chBox.setEnabled(false);
					}
					
				}catch(Exception e){
				}
			}
		}
	};
	
	private void addGroup(String str){
		mAdapter.add(str);
		mSpinner.setSelection( mAdapter.getCount()-1);
		mPreferenceWork.addGroup(str);
	}

	private void removeGroup(){
		if(mSpinner.getSelectedItemPosition()!=0){
			String str= mSpinner.getSelectedItem().toString();
			boolean success = mPreferenceWork.removeGroup(str);
			if(success){
				mAdapter.remove(str);
				mSpinner.setSelection(0);
			}
		}
	}
	
	public void addAccount(View view) {
		int childIndex = -1;
		for (int i = 0; i < mMemberList.getChildCount(); i++) {
			LinearLayout tmp = (LinearLayout) mMemberList.getChildAt(i);
			TextView tv = (TextView) tmp.findViewById(R.id.btn_account);

			if (view == tv) {
				childIndex = i;
				break;
			}
		}

		if (childIndex != -1) {
			mCurrentSelectedIndex = childIndex;
			showDialog(DIALOG_ADDACCOUNT);
		}
	}
	
	public void saveAccount(String account){
		String name = null;
    	EditText edit = null;
    	try{
    		edit = (EditText) mMemberList.getChildAt(mCurrentSelectedIndex).findViewById(R.id.name);
    	}catch(Exception e){
    	}
    	
    	if(edit!=null){
    		name = edit.getText().toString();
    	}
    	
    	if(name!=null){
    		String group = mSpinner.getSelectedItem().toString();
			if(group==null || group.equals(mRes.getString(R.string.selectGroup))){
			}else{
				mPreferenceWork.saveAccount(group, name, account);
	    		Toast.makeText(getApplicationContext(), R.string.saveComplete_noti, Toast.LENGTH_SHORT).show();
			}
    	}
	}
	
	private final int DIALOG_ADDGROUP = 0;
	private final int DIALOG_REMOVE_GROUP = 1;
	private final int DIALOG_PROGRESS = 2;
	private final int DIALOG_ADDACCOUNT = 3;
	
	@SuppressLint("NewApi") @Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		if(id == DIALOG_ADDGROUP){
			AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
			builder.setTitle(R.string.addGroup);
			builder.setMessage(mRes.getString(R.string.putGroupName));
			final EditText editText = new EditText(GroupActivity.this);
			editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(8) });
			builder.setView(editText);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String str = editText.getText().toString();
					addGroup(str);
					editText.setText("");
				}
			});
			return builder.create();
		}else if(id==DIALOG_REMOVE_GROUP){
			AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
			builder.setTitle(R.string.removeGroup);
			builder.setMessage(mRes.getString(R.string.confirmRemoveGroup));
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeGroup();
				}
			});
			builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			return builder.create();
		}else if(id==DIALOG_PROGRESS){
			ProgressDialog progDialog = new ProgressDialog(this);
			progDialog.setMessage("loading..");
            progDialog.setIndeterminate(true);
            return progDialog;
		}else if(id==DIALOG_ADDACCOUNT){
			AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
			builder.setTitle(R.string.addAccount);
			builder.setMessage(mRes.getString(R.string.putAccount));
			final EditText editText = new EditText(GroupActivity.this);
			editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30) });
			
			
			//find account. and set if ot exists [S]
			String name = null;
	    	EditText edit = null;
	    	try{
	    		edit = (EditText) mMemberList.getChildAt(mCurrentSelectedIndex).findViewById(R.id.name);
	    	}catch(Exception e){
	    	}
	    	
	    	if(edit!=null){
	    		name = edit.getText().toString();
	    	}
	    	
	    	if(name!=null){
	    		String group = mSpinner.getSelectedItem().toString();
	    		String account = mPreferenceWork.getAccount(group, name);
	    		
	    		if(!account.equals(" ")){
	    			editText.setText(account);
	    		}else{
	    			editText.setText("");
	    		}
	    	}
			// [E]
			
			builder.setView(editText);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String str = editText.getText().toString();
					saveAccount(str);
					editText.setText("");
				}
			});
			
			builder.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					removeDialog(DIALOG_ADDACCOUNT);
				}
			});
			return builder.create();
		}
		
		return super.onCreateDialog(id);
	}
	
	public class GetMemberAsyncTask extends AsyncTask<String, Integer, ArrayList<String>>{

		@Override
		protected  ArrayList<String> doInBackground(String... params) {
			// TODO Auto-generated method stub
			//publishProgress(DIALOG_PROGRESS);
			ArrayList<String> result = mPreferenceWork.getMember(params[0]);
			//updateMemberList(result);
//			for (int i = 0; i < 3000; i++) {
//				for (int j = 0; j < 1000; j++) {
//					;
//				}
//			}
			return result;
//			return null;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			updateMemberList(result);
			super.onPostExecute(result);
			removeDialog(DIALOG_PROGRESS);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			showDialog(DIALOG_PROGRESS);
			super.onPreExecute();
		}
		
		
//		@Override
//		protected void onPostExecute(ArrayList<String> result) {
//			updateMemberList(result);
//    		removeDialog(DIALOG_PROGRESS);
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			showDialog(DIALOG_PROGRESS);
//			super.onPreExecute();
//		}
//
//		@Override
//		protected ArrayList<String> doInBackground(String... params) {
//			// TODO Auto-generated method stub
//			publishProgress(DIALOG_PROGRESS);
//			ArrayList<String> result = mPreferenceWork.getMember(params[0]);
//			// delay time to show progress dialog
////			for (int i = 0; i < 3000; i++) {
////				for (int j = 0; j < 1000; j++) {
////					;
////				}
////			}
//			return result;
//		}
	}
	
	public class SaveMemberAsynctask extends AsyncTask<String, Integer, Void>{
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), R.string.saveComplete_noti, Toast.LENGTH_SHORT).show();
			super.onPostExecute(result);
		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			mPreferenceWork.saveMember(getMemberList(), params[0]);
			return null;
		}
	}
	
}
