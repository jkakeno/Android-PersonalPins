package com.example.personalpins;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.UI.BoardEditFragment;
import com.example.personalpins.UI.PinDetailFragment;
import com.example.personalpins.UI.PinEditFragment;
import com.example.personalpins.UI.PinListFragment;
import com.example.personalpins.UI.ViewPagerFragment;

import java.io.IOException;
import java.util.ArrayList;

/*This app allows users to organize their photos and videos in collections.
The user can add comments or tags to each photo or video.
In addition the user can search for photos and videos by the tags assigned.*/

public class MainActivity extends AppCompatActivity implements InteractionListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_TAKE_PHOTO = 0;
    public static final int REQUEST_TAKE_VIDEO = 1;
    public static final int REQUEST_PICK_BOARD_PHOTO = 2;
    public static final int REQUEST_PICK_PIN_PHOTO = 3;
    public static final int REQUEST_PICK_PIN_VIDEO = 4;

    public static final int MEDIA_TYPE_IMAGE = 5;
    public static final int MEDIA_TYPE_VIDEO = 6;

    /*Create a field to store the path to save the picture or video in the device.*/
    public static Bitmap boardBitmap;
    public static Uri boardUri;
    public static Bitmap pinBitmap;
    public static Uri pinUri;
    public static Uri pinVideoUri;
    public static String selectedMedia;

    ArrayList<Board> boardList;
    Board selectedBoard;
    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Instantiate db.*/
        db = new DataBase(this);
        /*Get board list from db and initialize boardList variable.*/
        boardList = db.getBoardList();

        /*Set the action bar logo.
        * http://www.vogella.com/tutorials/AndroidActionBar/article.html*/
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_camera_roll);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        /*Instantiate ViewPagerFragment.newInstance() and pass the boardList.*/
        ViewPagerFragment viewPagerFragment = ViewPagerFragment.newInstance(boardList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, viewPagerFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PICK_BOARD_PHOTO){
                if(data !=null){
                    boardUri = data.getData();
                    /*Store board image bitmap.*/
                    /*https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri*/
                    try {
                        boardBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),boardUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(requestCode == REQUEST_PICK_PIN_PHOTO){
                if(data !=null) {
                    pinUri = data.getData();
                    /*Store pin image bitmap.*/
                    /*https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri*/
                    try {
                        pinBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),pinUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    /*Start Pin_Edit_Fragment and pass the selected board.*/
                    PinEditFragment pinEditFragment = PinEditFragment.newInstance(selectedBoard);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, pinEditFragment).addToBackStack(null).commit();
                }
            }else if(requestCode == REQUEST_PICK_PIN_VIDEO){
                if(data !=null){
                    pinVideoUri=data.getData();
                    /*Persist permission to open uri files after app is restarted:
                    * http://www.andreamaglie.com/2015/access-storage-framework-uri-permission/
                    * https://stackoverflow.com/questions/19837358/android-kitkat-securityexception-when-trying-to-read-from-mediastore*/
//                    this.grantUriPermission(this.getPackageName(), pinVideoUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    this.getContentResolver().takePersistableUriPermission(pinVideoUri, takeFlags);
                    /*Start Pin_Edit_Fragment and pass the selected board.*/
                    PinEditFragment pinEditFragment = PinEditFragment.newInstance(selectedBoard);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, pinEditFragment).addToBackStack(null).commit();
                }
            }
        }
    }


    @Override
    public void onBoardListFragmentFabInteraction(boolean isClicked) {
        Log.d(TAG, "BoardListFragment FAB pressed");
        /*Start Board_Edit_Fragment.*/
        BoardEditFragment boardEditFragment = new BoardEditFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, boardEditFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBoardEditBoardImageInteraction(boolean isClicked) {
        if(isClicked){
            Log.d(TAG,"Board Image clicked");
            /*Start activity for result with request code and set the board image with the selected image.*/
            Intent pickPhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickPhotoIntent.setType("image/*");
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_BOARD_PHOTO);
        }else{
            Log.d(TAG,"Board Image not clicked");
            /*Set default board image with fixed image.*/
            /*https://stackoverflow.com/questions/4896223/how-to-get-an-uri-of-an-image-resource-in-android/38340580*/
            boardUri = Uri.parse("android.resource://com.example.personalpins/" + R.drawable.ic_action_camera_roll);
            /*Store board image bitmap.*/
            /*https://stackoverflow.com/questions/3879992/how-to-get-bitmap-from-an-uri*/
            try {
                boardBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),boardUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBoardEditCancelInteraction(boolean isClicked) {
        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onBoardEditSaveInteraction(Board board) {
        /*Send the saved board to the data base.*/
        db.insertBoard(board);
        Log.d(TAG,"New selectedBoard was added: "+board.getTitle());


        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onBoardListAdapterInteraction(Board selectedBoard) {
        Log.d(TAG,"Board selected is: " +selectedBoard.getTitle());
        this.selectedBoard = selectedBoard;

//        long selectedBoardId = selectedBoard.getId();
        ArrayList<Pin> pinList =selectedBoard.getPinList();

        /*Start PinListFragment and pass the selected board id and its list of pins.*/
        PinListFragment pinListFragment = PinListFragment.newInstance(selectedBoard,pinList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pinListFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPinListAdapterInteraction(Pin pin) {
        /*Start PinDetailFragment pass the selected pin.*/
        PinDetailFragment pinDetailFragment = PinDetailFragment.newInstance(pin);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pinDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPinListFragmentMenuInteraction(String media) {
        selectedMedia=media;
        Log.d(TAG, "Media selected: " +media);
        if (media.equals("Photo")) {
            /* Open files:
            * https://developer.android.com/guide/topics/providers/document-provider
            * https://developer.android.com/reference/android/content/Intent#action_open_document*/
            Intent pickPhotoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickPhotoIntent.setType("image/*");
            startActivityForResult(pickPhotoIntent, REQUEST_PICK_PIN_PHOTO);
        } else if(media.equals("Video")){
            Intent pickVideoIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickVideoIntent.setType("video/*");
            startActivityForResult(pickVideoIntent, REQUEST_PICK_PIN_VIDEO);
        }
    }

    @Override
    public void onPinEditCancelInteraction(boolean isClicked) {
        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onPinEditSaveInteraction(Pin pin) {
        /*Send the saved pin to the database.*/
        db.insertPin(pin);

        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onCameraIconInteraction(boolean isClicked) {
        /*TODO:Start media activity to take a picture or video.*/
        Log.d(TAG, "Camera icon pressed");
    }

    /*This method deletes old instance states sent from the activity to the system to save memory space and prevent "TransactionTooLargeExceptions".
    * This can happen when sending bitmaps within bundle.*/
    /*https://stackoverflow.com/questions/39098590/android-os-transactiontoolargeexception-on-nougat%5D*/
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }
}
