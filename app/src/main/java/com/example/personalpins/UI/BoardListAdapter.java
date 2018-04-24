package com.example.personalpins.UI;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personalpins.InteractionListener;
import com.example.personalpins.Model.Board;
import com.example.personalpins.R;

import java.util.ArrayList;


public class BoardListAdapter extends RecyclerView.Adapter<BoardListAdapter.ViewHolder> {

    private ArrayList<Board> boardList;
    InteractionListener listener;

    public BoardListAdapter(ArrayList<Board> boardList, InteractionListener listener) {
        this.boardList = boardList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.board_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.boardTitle.setText(boardList.get(position).getTitle());

        if(boardList.get(position).getPinList()!=null){
            holder.pinCount.setText(String.valueOf(boardList.get(position).getPinList().size()));
        }else{
            holder.pinCount.setText("0");
        }

        final Board board = boardList.get(position);

        /*Set the adapter holder view with the image boardUri.*/
        /*https://stackoverflow.com/questions/2928904/how-to-set-the-bitmap-to-the-imageview-in-main-xml-captured-from-the-camera*/
        holder.boardImage.setImageBitmap(board.getImage());

        /*Notify the main activity of the board_item image clicked.*/
        holder.boardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBoardListAdapterInteraction(board);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView boardTitle;
        public final TextView pinCount;
        public final ImageView boardImage;

        public ViewHolder(View view) {
            super(view);
            boardTitle = (TextView) view.findViewById(R.id.boardTitle);
            pinCount = (TextView) view.findViewById(R.id.pinCount);
            boardImage = (ImageView) view.findViewById(R.id.boardImage);
        }
    }
}
