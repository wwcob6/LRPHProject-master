package com.app.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.R;
import com.app.Util;
import com.app.audiorecord.MediaPlayerManager;
import com.app.db.DatabaseInfo;
import com.app.model.Constant;
import com.app.model.FileInfo;
import com.app.model.FileType;
import com.app.model.Msg;
import com.app.model.MyFile;
import com.app.sip.SipInfo;
import com.app.tools.PopupList;
import com.app.ui.ShowLocation;
import com.app.ui.ShowPhotoActivity;
import com.app.ui.SmallVideoPlay;
import com.app.view.CircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.util.FileSizeUtil;
import com.tb.emoji.EmojiUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.CLIPBOARD_SERVICE;
import static com.app.R.string.Thumbnail;

/**
 * Created by acer on 2016/10/9.
 */

public class MsgAdapter extends BaseAdapter {
    public static final String TAG = "MsgAdapter";
    private Context mContext;
    private List<Msg> msgList;
    private SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat format2 = new SimpleDateFormat("M月d日 HH:mm");
    //下载文件接口
    private DownloadListener mDownloadListener;
    //打开文件接口
    private OpenFileListener mOpenFileListener;

    private int lastProgress = -1;
    //最小宽度
    private int mMinWidth;
    //item的最大宽度
    private int mMaxWidth;
    //语音动画
    private ImageView animView;
private String avatar;
    private String id;
    public Handler openhandler;

    public MsgAdapter(Context context, List<Msg> objects,
                      DownloadListener downloadListener, OpenFileListener openFileListener) {
        mContext = context;
        msgList = objects;
        mDownloadListener = downloadListener;
        mOpenFileListener = openFileListener;
        System.out.println("msgList = " + msgList.size());
        //获取屏幕的宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //最大宽度为屏幕宽度的百分之七十
        mMaxWidth = (int) (outMetrics.widthPixels * 0.7f);
        //最大宽度为屏幕宽度的百分之十五
        mMinWidth = (int) (outMetrics.widthPixels * 0.15f);
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        final Msg msg = msgList.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            VideoView videoView = (VideoView) viewHolder.videoview.getTag();
            if (videoView != null) {
                viewHolder.videoview.removeView(videoView);
            }
        }
        avatar=Constant.currentfriendavatar;
        id=Constant.currentfriendid;
        RequestOptions options = new RequestOptions().error(R.drawable.empty_photo);
        Glide.with(mContext).load(Util.getImageUrl(id, avatar)).apply(options).into(viewHolder.chatfrom);
        Glide.with(mContext).load(Util.getImageUrl(avatar)).apply(options).into(viewHolder.chatto);
        if (msg.getToUserId().equals(SipInfo.userId)) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            try {
                EmojiUtil.handlerEmojiText(viewHolder.leftMsg, msg.getContent(), mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            viewHolder.idRecRecoderLength.setVisibility(View.GONE);
            viewHolder.idRecRecoderTime.setVisibility(View.GONE);
            viewHolder.recFileLayout.setVisibility(View.GONE);
            viewHolder.recProgressBar.setVisibility(View.GONE);
            viewHolder.leftMsg.setVisibility(View.GONE);
            viewHolder.recFileShow.setVisibility(View.GONE);
            viewHolder.recvideoview.setVisibility(View.GONE);
            viewHolder.recpic.setVisibility(View.GONE);
            viewHolder.recLocationLayout.setVisibility(View.GONE);
            switch (msg.getType()) {
                case 0://文字表情
                    viewHolder.leftMsg.setVisibility(View.VISIBLE);
                    break;
                case 1://聊天内容是文件,根据文件类型再做判断
                    //从数据库中取出当前聊天消息id对应的文件表中的文件信息
                    MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                    viewHolder.recFileLayout.setVisibility(View.VISIBLE);
                    switch (myFile.getType()) {
                        case 0://语音消息
                            viewHolder.recProgressBar.setVisibility(View.GONE);
                            viewHolder.idRecRecoderLength.setVisibility(View.VISIBLE);
                            viewHolder.idRecRecoderTime.setVisibility(View.VISIBLE);
                            //如果是语音消息,数据库文件表中file_size存的是语音消息的语音长度
                            viewHolder.idRecRecoderTime.setText(String.valueOf(myFile.getSize() + "\""));
                            ViewGroup.LayoutParams lp = viewHolder.idRecRecoderLength.getLayoutParams();
                            lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * myFile.getSize());
                            break;
                        case 1://小视频
                            viewHolder.recIconPlay.setVisibility(View.VISIBLE);
                            viewHolder.recvideoview.setVisibility(View.VISIBLE);
                            if (myFile.getIsDownloadFinish() == 0) {
                                //无操作
                            } else if (myFile.getIsDownloadFinish() == 1) {
                                viewHolder.recProgressBar.setVisibility(View.GONE);
                            } else {
                                viewHolder.recProgressBar.setVisibility(View.VISIBLE);
                                viewHolder.recProgressBar.setProgress(msg.getProgress());
                            }
                            break;
                        case 2://单纯文件
                            viewHolder.recFileShow.setVisibility(View.VISIBLE);
                            viewHolder.recFileName.setText(myFile.getFileName());
                            //非语音消息,数据库文件表中file_size存的是文件的大小
                            viewHolder.recFileSize.setText(FileSizeUtil.FormetFileSize(myFile.getSize()));
                            setIcon(viewHolder, myFile.getFileType());
                            if (myFile.getIsDownloadFinish() == 0) {
                                viewHolder.recStatus.setText("未下载");
                            } else if (myFile.getIsDownloadFinish() == 1) {
                                viewHolder.recStatus.setText("已下载");
                            } else {
                                viewHolder.recStatus.setText("正在下载");
                                viewHolder.recProgressBar.setVisibility(View.VISIBLE);
                                viewHolder.recProgressBar.setProgress(msg.getProgress());
                            }
                            break;
                        case 3://图片
                            viewHolder.recpic.setVisibility(View.VISIBLE);
                            viewHolder.recProgressBar.setVisibility(View.GONE);
                            String path = SipInfo.localSdCard + "Files/Camera/Thumbnail/" + myFile.getFileName();
                            if (new File(path).exists()) {
                                viewHolder.recpic.setImageBitmap(BitmapFactory.decodeFile(path));
                            } else {
                                viewHolder.recpic.setImageDrawable(mContext.getDrawable(R.drawable.ic_error));
                            }
                            break;
                    }
                    break;
                case 2://位置信息
                    viewHolder.recFileLayout.setVisibility(View.VISIBLE);
                    viewHolder.recLocationLayout.setVisibility(View.VISIBLE);
                    viewHolder.recLocAdress.setText(msg.getContent());
                    break;
            }
        } else if (msg.getFromUserId().equals(SipInfo.userId)) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.idRecoderLength.setVisibility(View.GONE);
            viewHolder.idRecoderTime.setVisibility(View.GONE);
            viewHolder.fileLayout.setVisibility(View.GONE);
            viewHolder.progressBar.setVisibility(View.GONE);
            viewHolder.rightMsg.setVisibility(View.GONE);
            viewHolder.fileShow.setVisibility(View.GONE);
            viewHolder.videoview.setVisibility(View.GONE);
            viewHolder.pic.setVisibility(View.GONE);
            viewHolder.locationLayout.setVisibility(View.GONE);
            try {
                EmojiUtil.handlerEmojiText(viewHolder.rightMsg, msg.getContent(), mContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (msg.getType()) {
                case 0://文字表情
                    viewHolder.rightMsg.setVisibility(View.VISIBLE);
                    break;
                case 1://聊天内容是文件,根据文件类型再做判断
                    //从数据库中取出当前聊天消息id对应的文件表中的文件信息
                    final MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                    viewHolder.fileLayout.setVisibility(View.VISIBLE);
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                    switch (myFile.getType()) {
                        case 0://语音消息
                            viewHolder.progressBar.setVisibility(View.GONE);
                            viewHolder.idRecoderLength.setVisibility(View.VISIBLE);
                            viewHolder.idRecoderTime.setVisibility(View.VISIBLE);
                            viewHolder.idRecoderTime.setText(Math.round(msg.getRecordtime()) + "\"");
                            ViewGroup.LayoutParams lp = viewHolder.idRecoderLength.getLayoutParams();
                            lp.width = (int) (mMinWidth + (mMaxWidth / 60f) * msg.getRecordtime());
                            break;
                        case 1://小视频
                            viewHolder.iconPlay.setVisibility(View.VISIBLE);
                            viewHolder.videoview.setVisibility(View.VISIBLE);
                            if (myFile.getIsTransferFinish() == 0) {
                                if (lastProgress != -1) {
                                    if (lastProgress != msg.getProgress()) {
                                        viewHolder.progressBar.setProgress(msg.getProgress());
                                    } else {
                                        viewHolder.progressBar.setProgress(lastProgress);
                                    }
                                }
                                lastProgress = msg.getProgress();
                            } else {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                DatabaseInfo.sqLiteManager.updateIsFileTransferFinish(msg.getMsgId(), 1);

                            }
                            break;
                        case 2://单纯文件
                            viewHolder.fileShow.setVisibility(View.VISIBLE);
                            if (myFile != null) {
                                viewHolder.fileName.setText(myFile.getFileName());
                                viewHolder.fileSize.setText(FileSizeUtil.FormetFileSize(myFile.getSize()));
                                setIcon(viewHolder, new FileInfo(myFile.getLocalPath(), myFile.getFileName(), false).whichtype());
                                if (myFile.getIsTransferFinish() == 0) {
                                    if (lastProgress != -1) {
                                        if (lastProgress != msg.getProgress()) {
                                            viewHolder.progressBar.setProgress(msg.getProgress());
                                        } else {
                                            viewHolder.progressBar.setProgress(lastProgress);
                                        }
                                    }
                                    lastProgress = msg.getProgress();
                                    viewHolder.status.setText("发送中");
                                } else {
                                    viewHolder.progressBar.setVisibility(View.GONE);
                                    viewHolder.status.setText("已发送");
                                    DatabaseInfo.sqLiteManager.updateIsFileTransferFinish(msg.getMsgId(), 1);
                                }
                            }
                            break;
                        case 3://图片
                            viewHolder.pic.setVisibility(View.VISIBLE);
                            if (myFile.getIsTransferFinish() == 0) {
                                if (lastProgress != -1) {
                                    if (lastProgress != msg.getProgress()) {
                                        viewHolder.progressBar.setProgress(msg.getProgress());
                                    } else {
                                        viewHolder.progressBar.setProgress(lastProgress);
                                    }
                                }
                                lastProgress = msg.getProgress();
                            } else {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                DatabaseInfo.sqLiteManager.updateIsFileTransferFinish(msg.getMsgId(), 1);
                            }
                            if (new File(myFile.getLocalPath()).exists()) {
                                String thumbnailpath = myFile.getLocalPath().replace(mContext.getString(R.string.Image), mContext.getString(Thumbnail));
                                viewHolder.pic.setImageBitmap(BitmapFactory.decodeFile(thumbnailpath));
                            } else {
                                viewHolder.pic.setImageDrawable(mContext.getDrawable(R.drawable.ic_error));
                            }
                            break;
                    }
                    break;
                case 2://位置信息
                    viewHolder.fileLayout.setVisibility(View.VISIBLE);
                    viewHolder.locationLayout.setVisibility(View.VISIBLE);
                    viewHolder.locAdress.setText(msg.getContent());
                    break;
            }
        }
        //时间的显示
        if (msg.getIsTimeShow() == 1) {
            Date date = new Date(msg.getTime() * 1000L);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Calendar calendar1 = Calendar.getInstance();
            if (calendar.get(Calendar.YEAR) == calendar1.get(Calendar.YEAR) &&
                    calendar.get(Calendar.MONTH) == calendar1.get(Calendar.MONTH) &&
                    calendar.get(Calendar.DATE) == calendar1.get(Calendar.DATE)) {
                viewHolder.time.setText(format1.format(date));
            } else {
                viewHolder.time.setText(format2.format(date));
            }
            viewHolder.time.setVisibility(View.VISIBLE);
        } else {
            viewHolder.time.setVisibility(View.GONE);
        }

        viewHolder.iconPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                final VideoView sendvideoView = new VideoView(mContext);
                sendvideoView.setVideoPath(myFile.getLocalPath());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(180, 320);
                viewHolder.videoview.addView(sendvideoView, 0, params);
                viewHolder.videoview.setTag(sendvideoView);
                viewHolder.iconPlay.setVisibility(View.GONE);
                sendvideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setVolume(0f, 0f);
                        mp.start();
                        sendvideoView.start();
                    }
                });
                sendvideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        viewHolder.videoview.removeView(sendvideoView);
                        viewHolder.iconPlay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        final View finalConvertView = convertView;

        viewHolder.fileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msg.getType()) {
                    case 1:
                        final MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                        final File file = new File(myFile.getLocalPath());
                        Log.d(TAG, file.getPath());
                        switch (myFile.getType()) {
                            case 0://语音
                                if (animView != null) {
                                    animView.setBackgroundResource(R.drawable.adj);
                                    animView = null;
                                }
                                animView = (ImageView) finalConvertView.findViewById(R.id.id_recoder_anim);
                                animView.setBackgroundResource(R.drawable.play_anim);
                                AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
                                animation.start();
                                // 播放录音
                                MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                                    public void onCompletion(MediaPlayer mp) {
                                        //播放完成后修改图片
                                        animView.setBackgroundResource(R.drawable.adj);
                                    }
                                });
                                break;
                            case 1://小视频
                                Intent intent = new Intent(mContext, SmallVideoPlay.class);
                                intent.putExtra("videopath", myFile.getLocalPath());
                                mContext.startActivity(intent);
                                break;
                            case 2://文件
                                if (file.exists()) {
                                    mOpenFileListener.OpenFile(file);
                                } else {
                                    Toast.makeText(mContext, "文件已删除", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 3://图片
                                Intent openpic = new Intent(mContext, ShowPhotoActivity.class);
                                openpic.putExtra("path", myFile.getLocalPath());
                                openpic.putExtra("type", 0);
                                mContext.startActivity(openpic);
                                break;
                        }
                        break;
                    case 2://位置信息
                        Intent showloc = new Intent(mContext, ShowLocation.class);
                        showloc.putExtra("location", msg.getContent());
                        mContext.startActivity(showloc);
                        break;
                }
            }
        });
        viewHolder.recIconPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                final File file = new File(myFile.getLocalPath() + myFile.getFileName());

                if (myFile.getIsDownloadFinish() == 0) {
                    DatabaseInfo.sqLiteManager.updateFileDownload(msg.getMsgId(), 2);
                    mDownloadListener.onDownload(msg.getMsgId(), myFile.getFtpPath(), myFile.getFileName());
                }
                if (myFile.getIsDownloadFinish() == 1) {
                    final VideoView sendvideoView = new VideoView(mContext);
                    sendvideoView.setVideoPath(myFile.getLocalPath() + "/" + myFile.getFileName());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(180, 320);
                    viewHolder.recvideoview.addView(sendvideoView, 0, params);
                    viewHolder.recvideoview.setTag(sendvideoView);
                    viewHolder.recIconPlay.setVisibility(View.GONE);
                    sendvideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setVolume(0f, 0f);
                            mp.start();
                            sendvideoView.start();
                        }
                    });
                    sendvideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            viewHolder.recvideoview.removeView(sendvideoView);
                            viewHolder.recIconPlay.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
        viewHolder.recFileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msg.getType()) {
                    case 1:
                        final MyFile myFile = DatabaseInfo.sqLiteManager.queryFile(msg.getMsgId());
                        final File file = new File(myFile.getLocalPath() + myFile.getFileName());
                        Log.d(TAG, file.getPath());
                        switch (myFile.getType()) {
                            default:
                                if (myFile.getIsDownloadFinish() == 0) {
                                    DatabaseInfo.sqLiteManager.updateFileDownload(msg.getMsgId(), 2);
                                    mDownloadListener.onDownload(msg.getMsgId(), myFile.getFtpPath(), myFile.getFileName());
                                    playAnim(finalConvertView);
                                }
                                if (myFile.getIsDownloadFinish() == 1) {
                                    if (file.exists()) {
                                        if (myFile.getType() != 0) {
                                            if (myFile.getType() == 1) {
                                                Intent intent = new Intent(mContext, SmallVideoPlay.class);
                                                intent.putExtra("videopath", file.getAbsolutePath());
                                                mContext.startActivity(intent);
                                            }
                                            if (myFile.getType() == 2) {
                                                mOpenFileListener.OpenFile(file);
                                            }
                                        } else if (myFile.getType() == 0) {
                                            playAnim(finalConvertView);
                                            // 播放录音
                                            MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                                                public void onCompletion(MediaPlayer mp) {
                                                    //播放完成后修改图片
                                                    animView.setBackgroundResource(R.drawable.rec_adj);
                                                }
                                            });
                                        }
                                    } else {
                                        Toast.makeText(mContext, "文件已删除", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                break;
                            case 3:
                                Intent openpic = new Intent(mContext, ShowPhotoActivity.class);
                                openpic.putExtra("path", SipInfo.localSdCard + "Files/Camera/Thumbnail/" + myFile.getFileName());
                                openpic.putExtra("type", 1);
                                openpic.putExtra("ftppath", myFile.getFtpPath());
                                openpic.putExtra("msgid", myFile.getFileId());
                                mContext.startActivity(openpic);
                                break;
                        }
                        break;
                    case 2:
                        Intent showloc = new Intent(mContext, ShowLocation.class);
                        showloc.putExtra("location", msg.getContent());
                        mContext.startActivity(showloc);
                        break;
                }
            }
        });
        openhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // 播放录音
                MyFile myFile = DatabaseInfo.sqLiteManager.queryFile((String) msg.obj);
                File file = new File(myFile.getLocalPath() + myFile.getFileName());
                MediaPlayerManager.playSound(file.getPath(), new MediaPlayer.OnCompletionListener() {

                    public void onCompletion(MediaPlayer mp) {
                        //播放完成后修改图片
                        animView.setBackgroundResource(R.drawable.rec_adj);
                    }
                });
            }
        };
        //发送方的复制操作
        PopupList popupList = new PopupList();
        List<String> popupMenuItemList = new ArrayList<>();
        popupMenuItemList.add("复制");
        popupList.init(mContext, viewHolder.rightMsg, popupMenuItemList, new PopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", viewHolder.rightMsg.getText());
                myClipboard.setPrimaryClip(myClip);
            }
        });
        popupList.setTextSize(popupList.sp2px(12));
        popupList.setTextPadding(popupList.dp2px(10), popupList.dp2px(5), popupList.dp2px(10), popupList.dp2px(5));
        popupList.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
        //接受方的复制操作
        PopupList popupList1 = new PopupList();
        popupList1.init(mContext, viewHolder.leftMsg, popupMenuItemList, new PopupList.OnPopupListClickListener() {
            @Override
            public void onPopupListClick(View contextView, int contextPosition, int position) {
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text", viewHolder.leftMsg.getText());
                myClipboard.setPrimaryClip(myClip);
            }
        });
        popupList1.setTextSize(popupList.sp2px(12));
        popupList1.setTextPadding(popupList.dp2px(10), popupList.dp2px(5), popupList.dp2px(10), popupList.dp2px(5));
        popupList1.setIndicatorView(popupList.getDefaultIndicatorView(popupList.dp2px(16), popupList.dp2px(8), 0xFF444444));
        return convertView;
    }

    //播放对方语音动画
    private void playAnim(View finalConvertView) {
        if (animView != null) {
            animView.setBackgroundResource(R.drawable.rec_adj);
            animView = null;
        }
        animView = (ImageView) finalConvertView.findViewById(R.id.id_rec_recoder_anim);
        animView.setBackgroundResource(R.drawable.rec_play_anim);
        AnimationDrawable animation = (AnimationDrawable) animView.getBackground();
        animation.start();
    }

    private void setIcon(ViewHolder viewHolder, FileType fileType) {
        switch (fileType) {
            case DOC:
                viewHolder.fileIcon.setImageResource(R.drawable.doc);
                break;
            case DOCX:
                viewHolder.fileIcon.setImageResource(R.drawable.docx);
                break;
            case PPT:
                viewHolder.fileIcon.setImageResource(R.drawable.ppt);
                break;
            case PPTX:
                viewHolder.fileIcon.setImageResource(R.drawable.pptx);
                break;
            case UNKNOWN:
                viewHolder.fileIcon.setImageResource(R.drawable.unknown);
                break;
            case XLS:
                viewHolder.fileIcon.setImageResource(R.drawable.xls);
                break;
            case XLXS:
                viewHolder.fileIcon.setImageResource(R.drawable.xlxs);
                break;
            case PDF:
                viewHolder.fileIcon.setImageResource(R.drawable.pdf);
                break;
            case PNG:
                viewHolder.fileIcon.setImageResource(R.drawable.png);
                break;
            case TXT:
                viewHolder.fileIcon.setImageResource(R.drawable.txt);
                break;
            case MP3:
                viewHolder.fileIcon.setImageResource(R.drawable.mp3);
                break;
            case MP4:
                viewHolder.fileIcon.setImageResource(R.drawable.mp4);
                break;
            case BMP:
                viewHolder.fileIcon.setImageResource(R.drawable.bmp);
                break;
            case GIF:
                viewHolder.fileIcon.setImageResource(R.drawable.gif);
                break;
            case AVI:
                viewHolder.fileIcon.setImageResource(R.drawable.avi);
                break;
            case WMA:
                viewHolder.fileIcon.setImageResource(R.drawable.wma);
                break;
            case RAR:
                viewHolder.fileIcon.setImageResource(R.drawable.rar);
                break;
            case ZIP:
                viewHolder.fileIcon.setImageResource(R.drawable.zip);
                break;
            case WAV:
                viewHolder.fileIcon.setImageResource(R.drawable.wav);
                break;
            case JPG:
                viewHolder.fileIcon.setImageResource(R.drawable.jpg);
            case NULL:
                break;
            default:
                break;
        }
    }

    public void setIcon(ViewHolder viewHolder, String fileType) {
        switch (fileType) {
            case "doc":
                viewHolder.recFileIcon.setImageResource(R.drawable.doc);
                break;
            case "docx":
                viewHolder.recFileIcon.setImageResource(R.drawable.docx);
                break;
            case "ppt":
                viewHolder.recFileIcon.setImageResource(R.drawable.ppt);
                break;
            case "pptx":
                viewHolder.recFileIcon.setImageResource(R.drawable.pptx);
                break;
            case "unknown":
                viewHolder.recFileIcon.setImageResource(R.drawable.unknown);
                break;
            case "xls":
                viewHolder.recFileIcon.setImageResource(R.drawable.xls);
                break;
            case "xlxs":
                viewHolder.recFileIcon.setImageResource(R.drawable.xlxs);
                break;
            case "pdf":
                viewHolder.recFileIcon.setImageResource(R.drawable.pdf);
                break;
            case "png":
                viewHolder.recFileIcon.setImageResource(R.drawable.png);
                break;
            case "txt":
                viewHolder.recFileIcon.setImageResource(R.drawable.txt);
                break;
            case "mp3":
                viewHolder.recFileIcon.setImageResource(R.drawable.mp3);
                break;
            case "mp4":
                viewHolder.recFileIcon.setImageResource(R.drawable.mp4);
                break;
            case "bmp":
                viewHolder.recFileIcon.setImageResource(R.drawable.bmp);
                break;
            case "gif":
                viewHolder.recFileIcon.setImageResource(R.drawable.gif);
                break;
            case "avi":
                viewHolder.recFileIcon.setImageResource(R.drawable.avi);
                break;
            case "wma":
                viewHolder.recFileIcon.setImageResource(R.drawable.wma);
                break;
            case "rar":
                viewHolder.recFileIcon.setImageResource(R.drawable.rar);
                break;
            case "zip":
                viewHolder.recFileIcon.setImageResource(R.drawable.zip);
                break;
            case "wav":
                viewHolder.recFileIcon.setImageResource(R.drawable.wav);
                break;
            case "jpg":
                viewHolder.recFileIcon.setImageResource(R.drawable.jpg);
                break;
            case "null":
                break;
            default:
                viewHolder.recFileIcon.setImageResource(R.drawable.unknown);
                break;
        }

    }

    public interface DownloadListener {
        public void onDownload(String msgId, String filePath, String fileName);
    }

    public interface OpenFileListener {
        public void OpenFile(File file);
    }


    class ViewHolder {

        @Bind(R.id.time)
        TextView time;
        @Bind(R.id.chatfrom)
        CircleImageView chatfrom;
        @Bind(R.id.left_msg)
        TextView leftMsg;
        @Bind(R.id.rec_file_icon)
        ImageView recFileIcon;
        @Bind(R.id.rec_file_name)
        TextView recFileName;
        @Bind(R.id.rec_file_size)
        TextView recFileSize;
        @Bind(R.id.rec_status)
        TextView recStatus;
        @Bind(R.id.rec_file_show)
        LinearLayout recFileShow;
        @Bind(R.id.rec_icon_play)
        ImageButton recIconPlay;
        @Bind(R.id.recvideoview)
        RelativeLayout recvideoview;
        @Bind(R.id.recpic)
        ImageView recpic;
        @Bind(R.id.id_rec_recoder_anim)
        ImageView idRecRecoderAnim;
        @Bind(R.id.id_rec_recoder_length)
        FrameLayout idRecRecoderLength;
        @Bind(R.id.rec_loc_adress)
        TextView recLocAdress;
        @Bind(R.id.rec_location_layout)
        RelativeLayout recLocationLayout;
        @Bind(R.id.rec_progressBar)
        ProgressBar recProgressBar;
        @Bind(R.id.rec_file_layout)
        LinearLayout recFileLayout;
        @Bind(R.id.id_rec_recoder_time)
        TextView idRecRecoderTime;
        @Bind(R.id.left_layout)
        LinearLayout leftLayout;
        @Bind(R.id.id_recoder_time)
        TextView idRecoderTime;
        @Bind(R.id.right_msg)
        TextView rightMsg;
        @Bind(R.id.file_icon)
        ImageView fileIcon;
        @Bind(R.id.file_name)
        TextView fileName;
        @Bind(R.id.file_size)
        TextView fileSize;
        @Bind(R.id.status)
        TextView status;
        @Bind(R.id.file_show)
        LinearLayout fileShow;
        @Bind(R.id.icon_play)
        ImageButton iconPlay;
        @Bind(R.id.videoview)
        RelativeLayout videoview;
        @Bind(R.id.pic)
        ImageView pic;
        @Bind(R.id.id_recoder_anim)
        ImageView idRecoderAnim;
        @Bind(R.id.id_recoder_length)
        FrameLayout idRecoderLength;
        @Bind(R.id.loc_adress)
        TextView locAdress;
        @Bind(R.id.location_layout)
        RelativeLayout locationLayout;
        @Bind(R.id.progressBar)
        ProgressBar progressBar;
        @Bind(R.id.file_layout)
        LinearLayout fileLayout;
        @Bind(R.id.chatto)
        CircleImageView chatto;
        @Bind(R.id.right_layout)
        LinearLayout rightLayout;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

