package com.app.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.app.R;
import com.app.process.ProcessTasks;
import com.app.when_page.PageFrameLayout;

public class SplashActivity extends FragmentActivity {
    private PageFrameLayout contentFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        super.onCreate(savedInstanceState);
        ProcessTasks.commonLaunchTasks(getApplication());
        init();
    }
    public void init(){
        SharedPreferences sharedPreferences=this.getSharedPreferences("share",MODE_PRIVATE);

        boolean isFirstRun=sharedPreferences.getBoolean("isFirstRun",true);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(isFirstRun){
            editor.putBoolean("isFirstRun",false);
            editor.commit();
            setContentView(R.layout.activity_main2);
            contentFrameLayout = (PageFrameLayout) findViewById(R.id.contentFrameLayout);
            // 设置资源文件和选中圆点
            contentFrameLayout.setUpViews(new int[]{
                    R.layout.page_tab1,
                    R.layout.page_tab2,
                    R.layout.page_tab4
            }, R.mipmap.banner1_on,R.mipmap.banner_off);
        }
        else{
            Intent intent=new Intent();
            intent.setClass(SplashActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}

