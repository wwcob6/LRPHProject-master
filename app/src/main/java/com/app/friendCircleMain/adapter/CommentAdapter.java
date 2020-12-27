package com.app.friendCircleMain.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.Util;
import com.app.model.Comments;
import com.app.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<Comments> mCommentsList;
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView replyName;
        TextView replyContent;
        TextView replyTime;
        TextView replyAddlikes;
        CircleImageView replyAvatar;
        ImageView commenttedPicture;
         public ViewHolder(View view){
             super(view);
             replyName=(TextView)view.findViewById(R.id.reply_name);
             replyContent=(TextView)view.findViewById(R.id.reply_content);
             replyTime=(TextView)view.findViewById(R.id.reply_time);
             replyAddlikes=(TextView)view.findViewById(R.id.reply_addlikes);
             replyAvatar=(CircleImageView)view.findViewById(R.id.reply_avatar);
             commenttedPicture=(ImageView)view.findViewById(R.id.commentted_picture);
         }
    }

    public CommentAdapter(List<Comments> commentsList){
        mCommentsList=commentsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_view,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Comments comments=mCommentsList.get(position);
        holder.replyName.setText(comments.getReplyName());
        if((comments.getPraisetype()!=null)&&!("".equals(comments.getPraisetype()))){
            holder.replyContent.setVisibility(View.INVISIBLE);
            holder.replyAddlikes.setVisibility(View.VISIBLE);
            holder.replyAddlikes.setText(comments.getPraisetype());
        }else {
            holder.replyContent.setVisibility(View.VISIBLE);
            holder.replyAddlikes.setVisibility(View.INVISIBLE);
            holder.replyContent.setText(comments.getComment());
        }
        holder.replyTime.setText(comments.getCreate_time());
//        holder.replyAvatar.setImageResource(R.drawable.d_han);
        if (comments.getAvatar()==null)
        {
            holder.replyAvatar.setImageResource(R.drawable.defaultavator);
        }else {
            ImageLoader.getInstance().displayImage(Util.getImageUrl(comments.getId(), comments.getAvatar()), holder.replyAvatar);
        }
        if(comments.getPic()==null){
            holder.commenttedPicture.setImageResource(R.drawable.defaultavator);
        }else {
            ImageLoader.getInstance().displayImage(Util.getImageUrl(comments.getPic()), holder.commenttedPicture);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }
}