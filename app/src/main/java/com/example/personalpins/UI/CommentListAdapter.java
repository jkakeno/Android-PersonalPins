package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.personalpins.Model.Comment;
import com.example.personalpins.R;

import java.util.ArrayList;


public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private ArrayList<Comment> commentList;

    public CommentListAdapter(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.comment.setText(commentList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        if(commentList !=null){
           return commentList.size();
        }else{
           return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView comment;

        public ViewHolder(View view) {
            super(view);
            comment = (TextView) view.findViewById(R.id.comment);
        }
    }
}
