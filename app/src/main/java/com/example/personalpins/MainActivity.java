package com.example.personalpins;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Pin;
import com.example.personalpins.UI.BoardEditFragment;
import com.example.personalpins.UI.PinDetailFragment;
import com.example.personalpins.UI.PinEditFragment;
import com.example.personalpins.UI.PinListFragment;
import com.example.personalpins.UI.ViewPagerFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
        getSupportActionBar().setLogo(R.drawable.ic_camera_roll_taskbar);
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
            boardUri = Uri.parse("android.resource://com.example.personalpins/" + R.drawable.ic_board_image_default);
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
        Log.d(TAG,"Pin is: " + pin.getTitle());
        /*Start PinDetailFragment pass the selected pin.*/
        PinDetailFragment pinDetailFragment = PinDetailFragment.newInstance(pin);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pinDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"Back pressed.");
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
        Log.d(TAG, "Camera icon pressed");

        pinUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        if(pinUri == null){
            Toast.makeText(this,"Something is wrong with your device's external storage.", Toast.LENGTH_SHORT).show();
        }else{
            /*Start the camera.*/
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /*Put the image capture in the extra.*/
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, pinUri);
            /*Request action from the camera.*/
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }
    }

    /*This method deletes old instance states sent from the activity to the system to save memory space and prevent "TransactionTooLargeExceptions".
    * This can happen when sending bitmaps within bundle.*/
    /*https://stackoverflow.com/questions/39098590/android-os-transactiontoolargeexception-on-nougat%5D*/
    @Override
    protected void onSaveInstanceState(Bundle oldInstanceState) {
        super.onSaveInstanceState(oldInstanceState);
        oldInstanceState.clear();
    }

    private Uri getOutputMediaFileUri(int mediaType) {
        /*TODO: Set the file directory to Images not Internal Storage.*/
        /*https://stackoverflow.com/questions/20523658/how-to-create-application-specific-folder-in-android-gallery*/

        /*Check for external storage.*/
        if(isExternalStorageAvailable()){
            /*Get the URI.*/
            /* 1. Get the external storage directory.*/
            File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            /* 2. Create a unique file name.*/
            String fileName = "";
            String fileType = "";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            if (mediaType == MEDIA_TYPE_IMAGE) {
                fileName = "IMG_"+ timeStamp;
                fileType = ".jpg";
            } else if(mediaType == MEDIA_TYPE_VIDEO) {
                fileName = "VID_"+ timeStamp;
                fileType = ".mp4";
            } else {
                return null;
            }

            /* 3. Create the file.*/
            File mediaFile;
            try {
                mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir);
                Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

            /* 4. Return the file's URI (the path in the device where the file is created).*/
//                return Uri.fromFile(mediaFile);
            /*Replace with this return if target api >= 24 .
            * https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en*/
                return FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID + ".provider" , mediaFile);
            } catch (IOException e){
                Log.d(TAG, "Error creating file: " + mediaStorageDir.getAbsolutePath() + fileName + fileType);
            }
        }
        /*Something went wrong.*/
        return null;
    }

    private boolean isExternalStorageAvailable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }else{
            return false;
        }
    }
}
