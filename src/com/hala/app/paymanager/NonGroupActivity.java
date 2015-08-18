package com.hala.app.paymanager;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NonGroupActivity extends Activity {
	private LayoutInflater mInflater;
	
	private Button mBtn_addMetting;
	private Button mBtn_removeMetting;
	private int mCntOfMetting=1;
	
	private LinearLayout mMettingList;
	private LinearLayout mResultLayout;
	
	private Button mBtn_enter;
	private EditText mEditText_result;
	
	private Resources mRes;
	
	//bg
	private LinearLayout mNonGroupMainView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nongroup);
        
        mRes = getResources();
        mInflater = getLayoutInflater();
        
        mNonGroupMainView = (LinearLayout) findViewById(R.id.non_group_bg_view);
        mNonGroupMainView.setBackgroundResource(MainActivity.SELECTED_BG_RES);
        
        mBtn_addMetting = (Button) findViewById(R.id.btn_add_metting);
        mBtn_addMetting.setOnClickListener(listener);
        
        mBtn_removeMetting = (Button) findViewById(R.id.btn_remove_metting);
        mBtn_removeMetting.setOnClickListener(listener);
        
        mMettingList = (LinearLayout) findViewById(R.id.mettings);
        
        mBtn_enter = (Button) findViewById(R.id.btn_enter);
        mBtn_enter.setOnClickListener(listener);
        
        mEditText_result = (EditText) findViewById(R.id.edit_text_result_text);
        mResultLayout = (LinearLayout) findViewById(R.id.result_layout);
        init();
    }
    
    private void init(){
    	//button
    	int cnt = mCntOfMetting+1;
        String str = getResources().getString(R.string.addMetting);
        mBtn_addMetting.setText(cnt + str);
        
        //meeting list
        LinearLayout tmp = (LinearLayout) mInflater.inflate(R.layout.meeting_item, null);
        TextView tmp_tv = (TextView) tmp.findViewById(R.id.mettingCnt);
        str = getResources().getString(R.string.CntOfMetting);
        tmp_tv.setText(mCntOfMetting + str);
        mMettingList.addView(tmp);
        
        
        addMetting();
        addMetting();
        
        
        //ad
        av =(AdView)findViewById(R.id.adView_nonGroup);
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
        LinearLayout tmp = (LinearLayout) mInflater.inflate(R.layout.meeting_item, null);
        TextView tmp_tv = (TextView) tmp.findViewById(R.id.mettingCnt);
        String str = getResources().getString(R.string.CntOfMetting);
        tmp_tv.setText(mCntOfMetting + str);
        mMettingList.addView(tmp);
        
        //btn text update
        int cnt = mCntOfMetting+1;
        str = getResources().getString(R.string.addMetting);
        mBtn_addMetting.setText(cnt + str);
        
        mMettingList.requestLayout();
        
        //update removeMetting btn
        if(mCntOfMetting > 1){
	        str = getResources().getString(R.string.removeMetting);
	        mBtn_removeMetting.setText(mCntOfMetting + str);
			
	        if( mBtn_removeMetting.getVisibility()!=View.VISIBLE){
	        	mBtn_removeMetting.setVisibility(View.VISIBLE);
	        }
		}
    }
    
    private String getResultString(){
    	String result="";
    	
    	for(int i=0; i<mCntOfMetting; i++){
    		EditText howmuch = (EditText) mMettingList.getChildAt(i).findViewById(R.id.howmuch);
    		EditText howmany = (EditText) mMettingList.getChildAt(i).findViewById(R.id.howmany);
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
    				result_pay + mRes.getString(R.string.measure) + "\n";
    		
    		result  = result+result_tmp;
    	}
    	
    	if(!result.equals("")){
    		result = result + mRes.getString(R.string.result_account);
    	}
    	return result;
    }
    
    private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v==mBtn_addMetting){
				if(mCntOfMetting>9){
					Toast.makeText(getApplicationContext(), R.string.noMoreAdd, Toast.LENGTH_SHORT).show();
				}else{
					addMetting();
				} 
			}else if(v==mBtn_removeMetting){
				mMettingList.removeViewAt(mMettingList.getChildCount()-1);
				
				String str = getResources().getString(R.string.addMetting);
		        mBtn_addMetting.setText(mCntOfMetting + str);
				
				mCntOfMetting--;
				
				str = getResources().getString(R.string.removeMetting);
		        mBtn_removeMetting.setText(mCntOfMetting + str);
				
				if(mCntOfMetting==1){
					mBtn_removeMetting.setVisibility(View.INVISIBLE);
				}
			}else if(v==mBtn_enter){
				mResultLayout.setVisibility(View.VISIBLE);
				
				String result = getResultString();
				mEditText_result.setText(result);
				mEditText_result.setVisibility(View.VISIBLE);
			}
		}
	};
}
