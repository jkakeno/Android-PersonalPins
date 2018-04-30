package com.example.personalpins;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    public static Uri boardUri;
    public static Uri pinImageUri;
    public static Uri pinVideoUri;
    public static String selectedMedia;

    ArrayList<Board> boardList;
    ArrayList<Pin> pinList;
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
        ViewPagerFragment viewPagerFragment = ViewPagerFragment.newInstance(boardList, pinList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, viewPagerFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_PICK_BOARD_PHOTO){

                    boardUri = data.getData();

            }else if(requestCode == REQUEST_PICK_PIN_PHOTO){

                    pinImageUri = data.getData();

                    /*Start Pin_Edit_Fragment and pass the selected board.*/
                    PinEditFragment pinEditFragment = PinEditFragment.newInstance(selectedBoard);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, pinEditFragment).addToBackStack(null).commit();

            }else if(requestCode == REQUEST_PICK_PIN_VIDEO){

                    pinVideoUri=data.getData();

                    /*Start Pin_Edit_Fragment and pass the selected board.*/
                    PinEditFragment pinEditFragment = PinEditFragment.newInstance(selectedBoard);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, pinEditFragment).addToBackStack(null).commit();

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
        Log.d(TAG,"Board: " + board.getTitle() + " was added to the database.");

        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onBoardListAdapterInteraction(Board selectedBoard) {
        Log.d(TAG,"Board selected is: " +selectedBoard.getTitle());

        this.selectedBoard = selectedBoard;
        ArrayList<Pin> pinList =selectedBoard.getPinList();

        /*Start PinListFragment and pass the selected board id and its list of pins.*/
        PinListFragment pinListFragment = PinListFragment.newInstance(selectedBoard,pinList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pinListFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPinListAdapterInteraction(Pin pin) {
        Log.d(TAG,"Pin selected is: " + pin.getTitle());

        /*Start PinDetailFragment pass the selected pin.*/
        PinDetailFragment pinDetailFragment = PinDetailFragment.newInstance(pin);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, pinDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onPinListFragmentMenuInteraction(String media) {
        selectedMedia=media;
        Log.d(TAG, "Media selected: " +media);

        /* Open files:
        * https://developer.android.com/guide/topics/providers/document-provider
        * https://developer.android.com/reference/android/content/Intent#action_open_document*/
        /*NOTE: Using ACTION_OPEN_DOCUMENT makes the URI persist after the app is restarted.*/
        if (media.equals("Photo")) {
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

        /*Store the pin in the database.*/
        db.insertPin(pin);
        Log.d(TAG,"Pin: " + pin.getTitle() + " was added to the database.");

        /*Get the previous fragment back to display.*/
        getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onPinSearchFragmentSearchQueryInteraction(String query) {
        Log.d(TAG, "Search query: " + query);

        /*Get a pin list from the data base based on the query.*/
        pinList = db.getPinList(query);

        /*Instantiate ViewPagerFragment.newInstance() and pass the pinList.*/
        ViewPagerFragment viewPagerFragment = ViewPagerFragment.newInstance(boardList, pinList);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, viewPagerFragment).commit();
    }

    @Override
    public void onPhotoCameraIconInteraction(int media) {
        Log.d(TAG, "Camera icon pressed: "+ media);

        Uri uri =getOutputMediaFileUri(media);

        /*NOTE: Read the doc below to take a photo and save it to gallary.
        * https://developer.android.com/training/camera/photobasics
        * */

        /*Start intent to take photos.*/
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*Put the photo in the created uri.*/
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        /*Request action from the camera.*/
        startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
    }

    @Override
    public void onVideoCameraIconInteraction(int media) {
        Log.d(TAG, "Camera icon pressed: "+ media);

        Uri uri =getOutputMediaFileUri(media);

        /*NOTE: Read the doc below to take a video.
        * https://developer.android.com/training/camera/videobasics
        * */

        /*Start intent to take photos.*/
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        /*Put the photo in the created uri.*/
        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        /*Request action from the camera.*/
        startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
    }

    private Uri getOutputMediaFileUri(int mediaType) {

        /*TODO: To get the uri for the file use FileProvider. Read the doc below to set up FileProvider.
        * https://developer.android.com/reference/android/support/v4/content/FileProvider
        * */

        /* Note: If you saved your photo to the directory provided by getExternalFilesDir(),
        * the media scanner (used to send files to public directories)
        * cannot access the files because they are private to your app.*/

        /*Desired directory for image is: content://com.android.providers.media.documents/document/image%3A1617*/
        /*Desired directory for video is: content://com.android.providers.media.documents/document/video%3A1616*/

        /*Create the file directory.*/
        File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG,"Directory: " + mediaStorageDir);
        /*Directory: /storage/emulated/0/Android/data/com.example.personalpins/files/Pictures*/

        /* Create the prefix and suffix for the file.*/
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

        /* Create the file.*/
        File mediaFile=null;
        try {
            mediaFile = File.createTempFile(fileName, fileType, mediaStorageDir);
            Log.d(TAG, "File: " + Uri.fromFile(mediaFile));
            /*File: file:///storage/emulated/0/Android/data/com.example.personalpins/files/Pictures/IMG_20180429_1331001966913621.jpg*/

        } catch (IOException e){
            Log.d(TAG, "Error creating file: " + mediaStorageDir.getAbsolutePath() + fileName + fileType);
        }

        /*Get the file's uri.*/
        Uri uri = FileProvider.getUriForFile(MainActivity.this,BuildConfig.APPLICATION_ID + ".provider" , mediaFile);
        Log.d(TAG,"Uri: "+ uri);
        /*Uri: content://com.example.personalpins.provider/external_files/Android/data/com.example*/

        /* Return the file's URI (the path in the device where the file is created).*/
        return uri;
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
