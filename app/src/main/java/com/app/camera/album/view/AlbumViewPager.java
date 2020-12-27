package com.app.camera.album.view;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.camera.FileOperateUtil;
import com.app.camera.imageloader.DisplayImageOptions;
import com.app.camera.imageloader.ImageLoader;
import com.app.camera.imageloader.displayer.MatrixBitmapDisplayer;
import com.app.R;

import java.util.List;


/**
 * @ClassName: AlbumViewPager
 * @Description:  自定义viewpager  优化了事件拦截
 * @author LinJ
 * @date 2015-1-9 下午5:33:33
 *
 */
public class AlbumViewPager extends ViewPager implements MatrixImageView.OnMovingListener {
	public final static String TAG="AlbumViewPager";

	/**  图片加载器 优化了了缓存  */
	private ImageLoader mImageLoader;
	/**  加载图片配置参数 */
	private DisplayImageOptions mOptions;

	/**  当前子控件是否处理拖动状态  */
	private boolean mChildIsBeingDragged=false;

	/**  界面单击事件 用以显示和隐藏菜单栏 */
	private MatrixImageView.OnSingleTapListener onSingleTapListener;
	/**  播放按钮点击事件 */
	private OnPlayVideoListener onPlayVideoListener;
	private String filePath;
	public AlbumViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		mImageLoader = ImageLoader.getInstance(context);
		//设置网络图片加载参数
		DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
		builder = builder
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.displayer(new MatrixBitmapDisplayer());
		mOptions = builder.build();
	}


	public String getFilePath() {
		filePath=((ViewPagerAdapter)getAdapter()).getCurrentItemPath(getCurrentItem());
		if (filePath.contains("video")) {
			filePath = filePath.replace(getContext().getResources().getString(R.string.Thumbnail),
					getContext().getResources().getString(R.string.Video));
			filePath = filePath.replace(".jpg", ".mp4");
		}
		return filePath;
	}

	/**
	 *  删除当前项
	 *  @return  “当前位置/总数量”
	 */
	public String deleteCurrentPath(){
		return ((ViewPagerAdapter)getAdapter()).deleteCurrentItem(getCurrentItem());
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(mChildIsBeingDragged)
			return false;
		return super.onInterceptTouchEvent(arg0);
	}

	@Override
	public void startDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=true;
	}


	@Override
	public void stopDrag() {
		// TODO Auto-generated method stub
		mChildIsBeingDragged=false;
	}

	public void setOnSingleTapListener(MatrixImageView.OnSingleTapListener onSingleTapListener) {
		this.onSingleTapListener = onSingleTapListener;
	}
	public void setOnPlayVideoListener(OnPlayVideoListener onPlayVideoListener) {
		this.onPlayVideoListener = onPlayVideoListener;
	}
	public interface OnPlayVideoListener{
		void onPlay(String path);
	}

	public class ViewPagerAdapter extends PagerAdapter {
		private List<String> paths;//大图地址 如果为网络图片 则为大图url
		public ViewPagerAdapter(List<String> paths){
			this.paths=paths;
		}

		@Override
		public int getCount() {
			return paths.size();
		}

		@Override
		public Object instantiateItem(ViewGroup viewGroup, int position) {
			//注意，这里不可以加inflate的时候直接添加到viewGroup下，而需要用addView重新添加
			//因为直接加到viewGroup下会导致返回的view为viewGroup
			View imageLayout = inflate(getContext(),R.layout.item_album_pager, null);
			viewGroup.addView(imageLayout);
			assert imageLayout != null;
			MatrixImageView imageView = (MatrixImageView) imageLayout.findViewById(R.id.image);
			imageView.setOnMovingListener(AlbumViewPager.this);
			imageView.setOnSingleTapListener(onSingleTapListener);
			String path=paths.get(position);
			//final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			ImageButton videoIcon=(ImageButton)imageLayout.findViewById(R.id.videoicon);
			if(path.contains("video")){
				videoIcon.setVisibility(View.VISIBLE);
			}else {
				videoIcon.setVisibility(View.GONE);
			}
			videoIcon.setOnClickListener(playVideoListener);
			videoIcon.setTag(path);
			imageLayout.setTag(path);

			mImageLoader.loadImage(path, imageView, mOptions);
			return imageLayout;
		}

		OnClickListener playVideoListener=new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String path=v.getTag().toString();
				path=path.replace(getContext().getResources().getString(R.string.Thumbnail),
						getContext().getResources().getString(R.string.Video));
				path=path.replace(".jpg", ".mp4");
				if(onPlayVideoListener!=null)
					onPlayVideoListener.onPlay(path);
				else {
					Toast.makeText(getContext(), "onPlayVideoListener", Toast.LENGTH_SHORT).show();
//					throw new RuntimeException("onPlayVideoListener is null");
				}
			}
		};

		@Override
		public int getItemPosition(Object object) {
			//在notifyDataSetChanged时返回None，重新绘制
			return POSITION_NONE;
		}

		@Override
		public void destroyItem(ViewGroup container, int arg1, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		public String getCurrentItemPath(int position){
			String path=paths.get(position);
			return path;
		}
		//自定义获取当前view方法
		public String deleteCurrentItem(int position) {
			String path=paths.get(position);
			if(path!=null) {
				if (path.contains("video")){
					FileOperateUtil.deleteThumbFile(path,getContext());
				}
				FileOperateUtil.deleteSourceFile(path, getContext());
				paths.remove(path);
				notifyDataSetChanged();
				if(paths.size()>0)
					return (getCurrentItem()+1)+"/"+paths.size();
				else {
					return "0/0";
				}
			}
			return null;
		}
	}


}
