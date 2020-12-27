package com.app.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.R;
import com.app.friendCircleMain.photoview.PhotoView;
import com.app.friendCircleMain.photoview.PhotoViewAttacher;
import com.app.tools.ViewPagerFixed;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import uk.co.senab.photoview.PhotoView;
//import uk.co.senab.photoview.PhotoViewAttacher;


public class AlbumSecondActivity extends Activity {
    private ViewPagerFixed vp;

    private List<ImageView> imageViews=new ArrayList<>();//显示图片的ImageView
    private int position;//从上个页面获取的子项position
    private List<String> urls=new ArrayList<>();//上个页面获取的URL列表
    private DisplayImageOptions options;
    private MyViewPagerAdapter adapter=new MyViewPagerAdapter();
    private LinearLayout vp_ll;
    private Map<Integer,float[]> xyMap=new HashMap<>();//接收所有图片的坐标
    private float pivotX,pivotY;//放大缩小的中心点
    private TextView vp_text;
    private Context mContext;
    private Button back;
    private int mTouchSlop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_albumsecondactivity);
        hideNavigationBar();
        mContext=this;
        initView();


        position=getIntent().getIntExtra("position", 0);
        //得到放缩中心点
        pivotX=xyMap.get(position)[0];
        pivotY=xyMap.get(position)[1];
        initData();
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position1) {//左右滑动时更新中心点和文字信息
                pivotX=xyMap.get(position1)[0];
                pivotY=xyMap.get(position1)[1];
                vp_text.setText((position1+1)+"/"+xyMap.size());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }

    }
    @SuppressWarnings("unchecked")
    private void initView() {
        ViewConfiguration configuration = ViewConfiguration.get(this);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

        vp=(ViewPagerFixed) findViewById(R.id.vp);
        vp_ll=(LinearLayout)findViewById(R.id.vp_ll);
        vp_text=(TextView)findViewById(R.id.vp_text);
//        back=(Button) findViewById(R.id.album_back);
        xyMap=(HashMap<Integer,float[]>)getIntent().getExtras().get("xyMap");
    }
    private void initData() {
        options=new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(5))
                .build();

        urls=getIntent().getStringArrayListExtra("urls");
        for(int i=0;i<urls.size();i++)//获取图片，设置PhotoView，加到ViewPager当中
        {
            PhotoView photoView=new PhotoView(mContext);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {//单击图片退出大图
                @Override
                public void onPhotoTap(View view, float v, float v1) {
                    AlbumSecondActivity.this.finish();
                }
            });

            ImageLoader.getInstance().displayImage(urls.get(i), photoView, options);
            imageViews.add(photoView);

        }
        vp.setAdapter(adapter);
        vp.setCurrentItem(position, true);
        vp_text.setText((position+1)+"/"+xyMap.size());
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

    }

    private class MyViewPagerAdapter extends PagerAdapter
    {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=imageViews.get(position);
            container.addView(view);
            return view;
        }

    }



    /**
     * 重写finish方法
     */
    @Override
    public void finish() {
        //大图页面是全屏，小图页面非全屏，从全屏退到非全屏页面会产生抖动现象，因此退出前设置成非全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        ScaleAnimation scaleAnimation=new ScaleAnimation(1, 0, 1, 0, pivotX,pivotY);//动画从正常大小缩小至0
        scaleAnimation.setDuration(350);
        scaleAnimation.setFillAfter(true);
        vp_ll.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                AlbumSecondActivity.super.finish();//动画结束时调用Activity的finish方法
                overridePendingTransition(0, 0);//紧跟着禁用Activity默认动画
            }
        });

    }
    public void hideNavigationBar() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE;//0x00001000; // SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }

        try {
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
