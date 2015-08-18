package com.hala.app.paymanager;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

	private Button mBtn_nonGroup;
	private Button mBtn_Group;
	private Button mBtn_Travel;
	
	private Resources mRes;
	
	//bg
	private LinearLayout mMain_View;
	private LinearLayout mBG_List;
	private int[] bg_res = {R.drawable.bg_01,R.drawable.bg_02,
			R.drawable.bg_03, R.drawable.bg_04, 
			R.drawable.bg_05, R.drawable.bg_05_2,
			R.drawable.bg_10, R.drawable.bg_10_2,
			R.drawable.bg_06, R.drawable.bg_06_2,
			R.drawable.bg_11, R.drawable.bg_11_2,
			R.drawable.bg_08,
			};
	
	public static int SELECTED_BG_RES = R.drawable.bg_01;
	PreferenceWork mPreferenceWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mRes = getResources();
        mPreferenceWork = new PreferenceWork(this);
        SELECTED_BG_RES = bg_res[mPreferenceWork.getBGColor()];

        mMain_View = (LinearLayout) findViewById(R.id.main_bg_view);
        mMain_View.setBackgroundResource(SELECTED_BG_RES);
        
        mBtn_nonGroup = (Button) findViewById(R.id.btn_non_group);
        mBtn_Group = (Button) findViewById(R.id.btn_group);
        mBtn_Travel = (Button) findViewById(R.id.btn_travel);
        mBG_List = (LinearLayout) findViewById(R.id.bg_list);
        
        
        OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v==mBtn_Group){
					 Intent intent = new Intent(MainActivity.this, GroupActivity.class);
					 startActivity(intent); 
				}else if(v==mBtn_nonGroup){
					 Intent intent = new Intent(MainActivity.this, NonGroupActivity.class);
					 startActivity(intent); 
				}else if(v==mBtn_Travel){
					Intent intent = new Intent(MainActivity.this, NonGroupActivity.class);
					 startActivity(intent); 
				}
			}
		};
		
        mBtn_nonGroup.setOnClickListener(listener);
        mBtn_Group.setOnClickListener(listener);
        
        initBGList();
    }
    
    private void initBGList(){
    	if(mBG_List!=null && mRes!=null && bg_res !=null){
	    	for(int i=0; i<bg_res.length; i++){
	    		final int index = i;
	    		ImageView view = new ImageView(getApplicationContext());
	    		view.setBackgroundResource(R.drawable.new_box_normal);
	    		view.setImageResource(bg_res[i]);
	    		
	    		int size = mRes.getDimensionPixelSize(R.dimen.bg_item_size);
	    		int padding = mRes.getDimensionPixelSize(R.dimen.bg_item_padding);
	    		view.setLayoutParams(new LayoutParams(size, size));
	    		view.setPadding(padding, padding, padding, padding);
	    		
	    		view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mMain_View.setBackgroundResource(bg_res[index]);
						SELECTED_BG_RES = bg_res[index];
						mPreferenceWork.saveBGColor(index);
					}
				});
	    		
	    		mBG_List.addView(view);
	    	}
    	}
    }
    


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
//            return rootView;
//        }
//    }

}
