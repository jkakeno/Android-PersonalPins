package com.example.personalpins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Comment;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.Model.Tag;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final String TAG = DataBase.class.getSimpleName();
    private static final String DB_NAME="PersonalPinDB";
    private static final int DB_VER = 1;
    public static final String TABLE_BOARD="BoardTable";
    public static final String COL_BOARD_TITLE="BoardTitle";
    public static final String COL_BOARD_IMAGE= "BoardImage";
    public static final String TABLE_PIN="PinTable";
    public static final String COL_PIN_TITLE="PinTitle";
    public static final String COL_PIN_IMAGE= "PinImage";
    public static final String COL_PIN_VIDEO= "PinVideo";
    public static final String COL_PIN_FOREIGN_KEY="PinForeignKey";
    public static final String TABLE_TAG="TagTable";
    public static final String COL_TAG="Tag";
    public static final String COL_TAG_FOREIGN_KEY="TagForeignKey";
    public static final String TABLE_COMMENT="CommentTable";
    public static final String COL_COMMENT="Comment";
    public static final String COL_COMMENT_FOREIGN_KEY="CommentForeignKey";

    public DataBase(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    /*Don't store blobs of 500KB in your database.
    * https://stackoverflow.com/questions/22005248/store-video-on-sqlite-database-or-sdcard-android*/

    /*SQL data size limitation:
    * https://stackoverflow.com/questions/21432556/android-java-lang-illegalstateexception-couldnt-read-row-0-col-0-from-cursorw*/

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String createBoardTable = "CREATE TABLE " + TABLE_BOARD + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_BOARD_TITLE + " TEXT, " + COL_BOARD_IMAGE + " TEXT )";
        String createPinTable = "CREATE TABLE " + TABLE_PIN + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PIN_TITLE + " TEXT, " + COL_PIN_IMAGE + " TEXT, " +COL_PIN_VIDEO + " TEXT, " + COL_PIN_FOREIGN_KEY +" TEXT )";
        String createTagTable = "CREATE TABLE " + TABLE_TAG + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TAG + " TEXT, " + COL_TAG_FOREIGN_KEY + " TEXT )";
        String createCommentTable = "CREATE TABLE " + TABLE_COMMENT + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_COMMENT + " TEXT, " + COL_COMMENT_FOREIGN_KEY + " TEXT )";

        db.execSQL(createBoardTable);
        db.execSQL(createPinTable);
        db.execSQL(createTagTable);
        db.execSQL(createCommentTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        String deleteBoardTable = "DELETE TABLE IF EXISTS " + TABLE_BOARD;
        String deletePinTable = "DELETE TABLE IF EXISTS " + TABLE_PIN;
        String deleteTagTable = "DELETE TABLE IF EXISTS " + TABLE_TAG;
        String deleteCommentTable = "DELETE TABLE IF EXISTS " + TABLE_COMMENT;

        db.execSQL(deleteBoardTable);
        db.execSQL(deletePinTable);
        db.execSQL(deleteTagTable);
        db.execSQL(deleteCommentTable);
        onCreate(db);
    }

    /*Helper Methods.*/
    public void insertBoard (Board board){
        /*Get the info needed from the boject.*/
        String boardTitle = board.getTitle();
        Uri boardImage = board.getImage();
        /*Insert the info into the database.*/
        SQLiteDatabase db= this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(COL_BOARD_TITLE, boardTitle);
        values.put(COL_BOARD_IMAGE, boardImage.toString());
        db.insert(TABLE_BOARD,null,values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }


    public void insertPin (Pin pin){
        /*Get the info needed from the boject.*/
        String pinTitle = pin.getTitle();
        String boardId = pin.getBoardId();
        ArrayList<Tag> tagList = pin.getTagList();
        ArrayList<Comment> commentList = pin.getCommentList();

        /*Insert the info into the database.*/
        SQLiteDatabase db= this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(COL_PIN_TITLE,pinTitle);
        if(pin.getImage()!=null) {
            Uri pinImage = pin.getImage();
            values.put(COL_PIN_IMAGE, pinImage.toString());
        }
        if(pin.getVideo()!=null) {
            Uri pinVideo = pin.getVideo();
            values.put(COL_PIN_VIDEO, pinVideo.toString());
        }
        values.put(COL_PIN_FOREIGN_KEY, boardId);
        /*Insert the pin into the data base and get the assigned id at the same time.*/
        long pinId = db.insertWithOnConflict(TABLE_PIN,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        if(tagList!=null) {
            /*Pass the pin id as the foreign key.*/
            insertTag(tagList, pinId, db);
        }
        if(commentList!=null) {
            /*Pass the pin id as the foreign key.*/
            insertComment(commentList, pinId, db);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    public void insertTag (ArrayList<Tag> tagList, long pinId, SQLiteDatabase db){
        /*Insert the info into the database.*/
        ContentValues values = new ContentValues();
        for(Tag tag: tagList) {
            values.put(COL_TAG, tag.getTag());
            values.put(COL_TAG_FOREIGN_KEY, pinId);
            db.insert(TABLE_TAG,null,values);
        }
    }

    public void insertComment (ArrayList<Comment> commentList, long pinId, SQLiteDatabase db){
        /*Insert the info into the database.*/
        ContentValues values = new ContentValues();
        for(Comment comment:commentList) {
            values.put(COL_COMMENT, comment.getComment());
            values.put(COL_COMMENT_FOREIGN_KEY, pinId);
            db.insert(TABLE_COMMENT,null,values);
        }
    }

    public ArrayList<Board> getBoardList(){
        /*Make an empty list.*/
        ArrayList<Board> boardList = new ArrayList<>();
        /*Query the database.*/
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOARD, new String[]{BaseColumns._ID,COL_BOARD_TITLE,COL_BOARD_IMAGE},null,null,null,null,BaseColumns._ID + " ASC");
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    /*Get the inform needed from the database to make an object.*/
                    long boardId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    String boardTitle = cursor.getString(cursor.getColumnIndex(COL_BOARD_TITLE));
                    String boardImage = cursor.getString(cursor.getColumnIndex(COL_BOARD_IMAGE));
                    /*Make the object.*/
                    Board board = new Board();
                    board.setId(boardId);
                    board.setTitle(boardTitle);
                    board.setImage(Uri.parse(boardImage));
                    board.setPinList(getPinList(boardId));
                    /*Add the object the list.*/
                    boardList.add(board);
                } while (cursor.moveToNext());
            }
            /*Return the object list.*/
            return boardList;
        }
        /*Return the empty list.*/
        return boardList;
    }

    public ArrayList<Pin> getPinList(long boardId){
        /*Make an empty list.*/
        ArrayList<Pin> pinList = new ArrayList<>();
        /*Query the database.*/
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PIN + " WHERE " + COL_PIN_FOREIGN_KEY + " = " + boardId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    /*Get the inform needed from the database to make an object.*/
                    long pinId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    String pinTitle = cursor.getString(cursor.getColumnIndex(COL_PIN_TITLE));
                    String pinImage = cursor.getString(cursor.getColumnIndex(COL_PIN_IMAGE));
                    String pinVideo = cursor.getString(cursor.getColumnIndex(COL_PIN_VIDEO));
                    /*Make the object.*/
                    Pin pin = new Pin();
                    pin.setId(pinId);
                    pin.setTitle(pinTitle);
                    if (pinImage != null) {
                        pin.setImage(Uri.parse(pinImage));
                    }
                    if (pinVideo != null) {
                        pin.setVideo(Uri.parse(pinVideo));
                    }
                    pin.setTagList(getTagList(pinId, db));
                    pin.setCommentList(getCommentList(pinId, db));
                    /*Add the object the list.*/
                    pinList.add(pin);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
            /*Return the object list.*/
            return pinList;
        }
        /*Return the empty list.*/
        return pinList;
    }

    public ArrayList<Tag> getTagList(long pinId, SQLiteDatabase db) {
        /*Make an empty list.*/
        ArrayList<Tag> tagList = new ArrayList<>();
        /*Query the database.*/
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAG + " WHERE " + COL_TAG_FOREIGN_KEY + " = " + pinId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    /*Get the inform needed from the database to make an object.*/
                    String tagName = cursor.getString(cursor.getColumnIndex(COL_TAG));
                    /*Make the object.*/
                    Tag tag = new Tag();
                    tag.setTag(tagName);
                    /*Add the object the list.*/
                    tagList.add(tag);
                } while (cursor.moveToNext());
            }
            /*Return the object list.*/
            return tagList;
        }
        /*Return the empty list.*/
        return  tagList;
    }

    public ArrayList<Comment> getCommentList(long pinId, SQLiteDatabase db) {
        /*Make an empty list.*/
        ArrayList<Comment> commentList = new ArrayList<>();
        /*Query the database.*/
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMMENT + " WHERE " + COL_COMMENT_FOREIGN_KEY + " = " + pinId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor!=null) {
            if (cursor.moveToFirst()) {
                do {
                    /*Get the inform needed from the database to make an object.*/
                    String commentName = cursor.getString(cursor.getColumnIndex(COL_COMMENT));
                    /*Make the object.*/
                    Comment comment = new Comment();
                    comment.setComment(commentName);
                    /*Add the object the list.*/
                    commentList.add(comment);
                } while (cursor.moveToNext());
            }
            /*Return the object list.*/
            return commentList;
        }
        /*Return the empty list.*/
        return commentList;
    }

}
