package com.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.model.FileInfo;
import com.app.R;

import java.util.ArrayList;


public class FileChooserAdapter extends BaseAdapter {

    private ArrayList<FileInfo> mFileLists;
    private Context mContext;

    public FileChooserAdapter(Context context, ArrayList<FileInfo> fileLists) {
        mFileLists = fileLists;
        mContext = context;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mFileLists.size();
    }

    @Override
    public FileInfo getItem(int position) {
        // TODO Auto-generated method stub
        return mFileLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.filechooser_gridview_item, null);
            holder.imgFileIcon = (ImageView) convertView.findViewById(R.id.imgFileIcon);
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tvFileName);
            holder.file_selected = (RelativeLayout) convertView.findViewById(R.id.file_selected);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FileInfo fileInfo = getItem(position);
        //TODO 
        if (fileInfo.isSelected()) {
            holder.file_selected.setBackgroundColor(Color.rgb(204, 204, 204));
        } else {
            holder.file_selected.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.tvFileName.setText(fileInfo.getFileName());
        getType(holder, fileInfo);
        return convertView;
    }

    private void getType(ViewHolder holder, FileInfo fileInfo) {
        if (fileInfo.isDirectory()) {      //文件夹
            holder.imgFileIcon.setImageResource(R.drawable.folder);
            holder.tvFileName.setTextColor(Color.GRAY);
        } else {
            switch (fileInfo.whichtype()) {
                case DOC:
                    holder.imgFileIcon.setImageResource(R.drawable.doc);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case DOCX:
                    holder.imgFileIcon.setImageResource(R.drawable.docx);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case PPT:
                    holder.imgFileIcon.setImageResource(R.drawable.ppt);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case PPTX:
                    holder.imgFileIcon.setImageResource(R.drawable.pptx);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case UNKNOWN:
                    holder.imgFileIcon.setImageResource(R.drawable.unknown);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case XLS:
                    holder.imgFileIcon.setImageResource(R.drawable.xls);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case XLXS:
                    holder.imgFileIcon.setImageResource(R.drawable.xlxs);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case PDF:
                    holder.imgFileIcon.setImageResource(R.drawable.pdf);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case PNG:
                    holder.imgFileIcon.setImageResource(R.drawable.png);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case TXT:
                    holder.imgFileIcon.setImageResource(R.drawable.txt);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case MP3:
                    holder.imgFileIcon.setImageResource(R.drawable.mp3);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case MP4:
                    holder.imgFileIcon.setImageResource(R.drawable.mp4);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case BMP:
                    holder.imgFileIcon.setImageResource(R.drawable.bmp);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case GIF:
                    holder.imgFileIcon.setImageResource(R.drawable.gif);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case AVI:
                    holder.imgFileIcon.setImageResource(R.drawable.avi);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case WMA:
                    holder.imgFileIcon.setImageResource(R.drawable.wma);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case RAR:
                    holder.imgFileIcon.setImageResource(R.drawable.rar);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case ZIP:
                    holder.imgFileIcon.setImageResource(R.drawable.zip);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case WAV:
                    holder.imgFileIcon.setImageResource(R.drawable.wav);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case JPG:
                    holder.imgFileIcon.setImageResource(R.drawable.jpg);
                    holder.tvFileName.setTextColor(Color.GRAY);
                    break;
                case NULL:
                    break;
                default:
                    break;
            }

        }
    }

    class ViewHolder {
        ImageView imgFileIcon;
        TextView tvFileName;
        RelativeLayout file_selected;
    }

}
