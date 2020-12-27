package com.app.friendcircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.R;
import com.app.publish.event.EditImageEvent;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.activity.BaseSwipeBackActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PhotoActivity extends BaseSwipeBackActivity {
    public static final String EXTRA_INDEX = "index";
    public static final String EXTRA_PHOTOS = "photos";
    private ViewPager mViewPager;
    private MyPageAdapter mPageAdapter;
    private List<String> mPhotos;
    private int currentIndex;
    private int mSize;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Intent intent = getIntent();
        currentIndex = intent.getIntExtra(EXTRA_INDEX, 0);
        mPhotos = intent.getStringArrayListExtra(EXTRA_PHOTOS);
        mSize = mPhotos == null ? 0 : mPhotos.size();
        RelativeLayout bottomLayout = (RelativeLayout) findViewById(R.id.photo_relativeLayout);
        bottomLayout.setBackgroundColor(0x70000000);

        findViewById(R.id.photo_bt_exit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.photo_bt_del).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mPhotos.size() == 1) {
                    FileUtils.deleteCircleDir();
                    mPhotos.clear();
                    finish();
                } else {
                    mPhotos.remove(currentIndex);
                    if (currentIndex == mSize - 1) {
                        currentIndex--;
                    }
                    mPageAdapter.addAll(mPhotos);
                    mViewPager.removeAllViews();
                    mPageAdapter.notifyDataSetChanged();
                    mViewPager.setCurrentItem(currentIndex);
                }
            }
        });
        findViewById(R.id.photo_bt_enter).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPageAdapter = new MyPageAdapter(this);
        mViewPager.setAdapter(mPageAdapter);
        mPageAdapter.addAll(mPhotos);
        mViewPager.setCurrentItem(currentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public static class MyPageAdapter extends PagerAdapter {
        private Context mContext;
        private List<String> mList;

        public MyPageAdapter(Context context) {
            mContext = context;
            mList = new ArrayList<>();
        }

        public void addAll(List<String> list) {
            if (list == null) {
                return;
            }
            mList.clear();
            int size = list.size();
            if ("add".equals(list.get(size - 1))) {
                mList.addAll(list.subList(0, size - 1));
            } else {
                mList.addAll(list);
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mContext);
            imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            Glide.with(mContext).load(mList.get(position)).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new EditImageEvent(mPhotos));
    }
}
