package com.example.personalpins.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Board implements Parcelable{
    long id;
    String title;
    Uri image;
    ArrayList<Pin> pinList;

    public Board() {
    }

    protected Board(Parcel in) {
        id = in.readLong();
        title = in.readString();
        image = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<Board> CREATOR = new Creator<Board>() {
        @Override
        public Board createFromParcel(Parcel in) {
            return new Board(in);
        }

        @Override
        public Board[] newArray(int size) {
            return new Board[size];
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

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public ArrayList<Pin> getPinList() {
        return pinList;
    }

    public void setPinList(ArrayList<Pin> pinList) {
        this.pinList = pinList;
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
    }
}
