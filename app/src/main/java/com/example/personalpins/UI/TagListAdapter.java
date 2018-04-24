package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.personalpins.Model.Tag;
import com.example.personalpins.R;

import java.util.ArrayList;


public class TagListAdapter extends RecyclerView.Adapter<TagListAdapter.ViewHolder> {

    private ArrayList<Tag> tagList;

    public TagListAdapter(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tag.setText(tagList.get(position).getTag());
    }

    @Override
    public int getItemCount() {
        if(tagList !=null){
           return tagList.size();
        }else{
           return 1;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tag;

        public ViewHolder(View view) {
            super(view);
            tag = (TextView) view.findViewById(R.id.tag);
        }
    }
}
