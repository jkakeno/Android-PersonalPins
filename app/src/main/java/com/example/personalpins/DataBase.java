package com.example.personalpins;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Comment;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.Model.Tag;

import java.io.ByteArrayOutputStream;
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

    /*Image store and retrieve:
    * https://stackoverflow.com/questions/11790104/how-to-storebitmap-image-and-retrieve-image-from-sqlite-database-in-android*/

    /*Don't store blobs of 500KB in your database.
    * https://stackoverflow.com/questions/22005248/store-video-on-sqlite-database-or-sdcard-android*/

    /*Convert File to [] and vise versa.
     * https://stackoverflow.com/questions/6828634/write-byte-to-file-in-java */

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        String createBoardTable = "CREATE TABLE " + TABLE_BOARD + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_BOARD_TITLE + " TEXT, " + COL_BOARD_IMAGE + " BLOB )";
//        String createPinTable = "CREATE TABLE " + TABLE_PIN + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PIN_TITLE + " TEXT, " + COL_PIN_IMAGE + " BLOB, " +COL_PIN_VIDEO + " TEXT, " + COL_PIN_FOREIGN_KEY +" TEXT )";
        String createPinTable = "CREATE TABLE " + TABLE_PIN + "( " + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PIN_TITLE + " TEXT, " + COL_PIN_IMAGE + " BLOB, " +COL_PIN_VIDEO + " TEXT, " + COL_PIN_FOREIGN_KEY +" TEXT )";
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
        String boardTitle = board.getTitle();
        /*Get the image bitmap from the object.*/
        Bitmap boardBitmap = board.getImage();
        /*Convert bitmap to byte array.*/
        byte [] boardImage = getBytes(boardBitmap);
        SQLiteDatabase db= this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(COL_BOARD_TITLE, boardTitle);
        /*Store byte array.*/
        values.put(COL_BOARD_IMAGE, boardImage);
        db.insert(TABLE_BOARD,null,values);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    private byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
        return stream.toByteArray();
    }

    public void insertPin (Pin pin){
        String pinTitle = pin.getTitle();
        String boardId = pin.getBoardId();
//        long pinId = pin.getId();
        ArrayList<Tag> tagList = pin.getTagList();
        ArrayList<Comment> commentList = pin.getCommentList();

        SQLiteDatabase db= this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(COL_PIN_TITLE,pinTitle);
        /*Store byte array.*/
        if(pin.getImage()!=null) {
            Bitmap pinBitmap = pin.getImage();
            /*Convert bitmap to byte array.*/
            byte [] pinImage = getBytes(pinBitmap);
            values.put(COL_PIN_IMAGE, pinImage);
        }
        if(pin.getVideo()!=null) {
            Uri pinVideo = pin.getVideo();
//            File pinVideoFile = pin.getVideo();
//            byte [] pinVideo = getFileBytes(pinVideoFile);
            values.put(COL_PIN_VIDEO, pinVideo.toString());
//            values.put(COL_PIN_VIDEO, pinVideo);
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
        ContentValues values = new ContentValues();
        for(Tag tag: tagList) {
            values.put(COL_TAG, tag.getTag());
            values.put(COL_TAG_FOREIGN_KEY, pinId);
            db.insert(TABLE_TAG,null,values);
        }
    }

    public void insertComment (ArrayList<Comment> commentList, long pinId, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        for(Comment comment:commentList) {
            values.put(COL_COMMENT, comment.getComment());
            values.put(COL_COMMENT_FOREIGN_KEY, pinId);
            db.insert(TABLE_COMMENT,null,values);
        }
    }

    public ArrayList<Board> getBoardList(){
        ArrayList<Board> boardList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BOARD, new String[]{BaseColumns._ID,COL_BOARD_TITLE,COL_BOARD_IMAGE},null,null,null,null,BaseColumns._ID + " ASC");
        if(cursor.moveToFirst()){
            do{
                long boardId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String boardTitle = cursor.getString(cursor.getColumnIndex(COL_BOARD_TITLE));
                /*Get image byte array from data base.*/
                byte[] boardImage = cursor.getBlob(cursor.getColumnIndex(COL_BOARD_IMAGE));
                /*Convert byte array to bitmap.*/
                Bitmap boardBitmap = getImage(boardImage);
                Board board = new Board();
                board.setId(boardId);
                board.setTitle(boardTitle);
                /*Set the object image with the bitmap.*/
                board.setImage(boardBitmap);
                board.setPinList(getPinList(boardId));
                boardList.add(board);
            }while(cursor.moveToNext());
        }
        return boardList;
    }

    private Bitmap getImage(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public ArrayList<Pin> getPinList(long boardId){
        ArrayList<Pin> pinList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PIN + " WHERE " + COL_PIN_FOREIGN_KEY + " = " + boardId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor.moveToFirst()){
            do{
                long pinId = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                String pinTitle = cursor.getString(cursor.getColumnIndex(COL_PIN_TITLE));
                /*Get image byte array from data base.*/
                byte[] pinImage = cursor.getBlob(cursor.getColumnIndex(COL_PIN_IMAGE));
                String pinVideo = cursor.getString(cursor.getColumnIndex(COL_PIN_VIDEO));
//                byte[] pinVideo = cursor.getBlob(cursor.getColumnIndex(COL_PIN_VIDEO));

                Pin pin = new Pin();
                pin.setId(pinId);
                pin.setTitle(pinTitle);
                /*Set the object image with the bitmap.*/
                if(pinImage!=null) {
                    /*Convert byte array to bitmap.*/
                    Bitmap pinBitmap = getImage(pinImage);
                    pin.setImage(pinBitmap);
                }
                if(pinVideo!=null) {
                    pin.setVideo(Uri.parse(pinVideo));
//                    File pinVideoFile = getFile(pinVideo);
//                    pin.setVideo(pinVideoFile);
                }
                pin.setTagList(getTagList(pinId,db));
                pin.setCommentList(getCommentList(pinId,db));
                pinList.add(pin);
            }while(cursor.moveToNext());
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return pinList;
    }

    public ArrayList<Tag> getTagList(long pinId, SQLiteDatabase db) {
        ArrayList<Tag> tagList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TAG + " WHERE " + COL_TAG_FOREIGN_KEY + " = " + pinId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor.moveToFirst()){
            do{
                String tagName =cursor.getString(cursor.getColumnIndex(COL_TAG));
                Tag tag = new Tag();
                tag.setTag(tagName);
                tagList.add(tag);
            }while(cursor.moveToNext());
        }
        return tagList;
    }

    public ArrayList<Comment> getCommentList(long pinId, SQLiteDatabase db) {
        ArrayList<Comment> commentList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COMMENT + " WHERE " + COL_COMMENT_FOREIGN_KEY + " = " + pinId + " ORDER BY " + BaseColumns._ID + " ASC", null);
        if(cursor.moveToFirst()){
            do{
                String commentName = cursor.getString(cursor.getColumnIndex(COL_COMMENT));
                Comment comment = new Comment();
                comment.setComment(commentName);
                commentList.add(comment);
            }while(cursor.moveToNext());
        }
        return commentList;
    }

}
