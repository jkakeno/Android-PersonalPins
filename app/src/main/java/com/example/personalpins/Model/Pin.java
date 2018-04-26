package com.example.personalpins.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Pin implements Parcelable{
    long id;
    String title;
    /*TODO: Make image type Uri.*/
    Bitmap image;
    Uri video;
//    File video;
    String boardId;
    ArrayList<Tag> tagList;
    ArrayList<Comment> commentList;

    public Pin() {
    }

    protected Pin(Parcel in) {
        id = in.readLong();
        title = in.readString();
        image = in.readParcelable(Uri.class.getClassLoader());
        video = in.readParcelable(Uri.class.getClassLoader());
        boardId = in.readString();
    }

    public static final Creator<Pin> CREATOR = new Creator<Pin>() {
        @Override
        public Pin createFromParcel(Parcel in) {
            return new Pin(in);
        }

        @Override
        public Pin[] newArray(int size) {
            return new Pin[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Uri getVideo() {
        return video;
    }

    public void setVideo(Uri video) {
        this.video = video;
    }

    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(String boardId) {
        this.boardId = boardId;
    }

    public ArrayList<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(ArrayList<Tag> tagList) {
        this.tagList = tagList;
    }

    public ArrayList<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeParcelable(image, i);
        parcel.writeParcelable(video, i);
        parcel.writeString(boardId);
    }
}
