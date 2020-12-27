package com.app.application;

import android.app.Application;
import android.content.Context;

import com.app.R;

import com.app.tools.Utils;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


/**
 * Created by asus on 2018/1/19.
 */

public class AppContext extends Application {
    private static Context context;
    public static ImageLoader instance;
    private static AppContext instance1;
//    private HttpProxyCacheServer proxy;
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initImageLoader();
        instance1 = this;
    }

    public static Context getAppContext() {
        return context;
    }
    public static AppContext getInstance(){
        return instance1;
    }

    private final static void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(getDefaultDisplayOption())
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new BaseImageDownloader(context))
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
        instance = ImageLoader.getInstance();

    }

    private final static DisplayImageOptions getDefaultDisplayOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.image_default) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.image_default) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .showImageOnLoading(R.drawable.image_default).build(); // 创建配置过得DisplayImageOption对象
        return options;
    }

//    public static HttpProxyCacheServer getProxy(Context context) {
//        AppContext myApplication = (AppContext) context.getApplicationContext();
//        return myApplication.proxy == null ? (myApplication.proxy = myApplication.newProxy()) : myApplication.proxy;
//    }
//
//    private HttpProxyCacheServer newProxy() {
//        return new HttpProxyCacheServer.Builder(this).maxCacheFilesCount(300).cacheDirectory(Utils.getVideoCacheDir(this))
//                .fileNameGenerator(new MyFileNameGenerator()).build();
//    }

    public class MyFileNameGenerator implements FileNameGenerator {
        // Urls contain mutable parts (parameter 'sessionToken') and stable video's id (parameter 'videoId').
        // e. g. http://example.com?guid=abcqaz&sessionToken=xyz987
        public String generate(String url) {
//            Uri uri = Uri.parse(url);
//            String audioId = uri.getQueryParameter("guid");
            String audioId = url;
            return audioId ;
        }
    }
}
