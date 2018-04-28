package com.example.personalpins;

import com.example.personalpins.Model.Board;
import com.example.personalpins.Model.Pin;

public interface InteractionListener {

    /*BoardListFragmentFabInteraction method.*/
    void onBoardListFragmentFabInteraction(boolean isClicked);

    /*CameraIconInteraction method.*/
    void onCameraIconInteraction(int media);

    /*BoardEditFragmentBoardImageInteraction method.*/
    void onBoardEditBoardImageInteraction(boolean isClicked);

    /*BoardEditFragmentCancelInteraction method.*/
    void onBoardEditCancelInteraction(boolean isClicked);

    /*BoardEditFragmentSaveInteraction method.*/
    void onBoardEditSaveInteraction(Board board);

    /*BoardListAdapterInteraction method.*/
    void onBoardListAdapterInteraction(Board board);

    /*PinListAdapterInteraction method.*/
    void onPinListAdapterInteraction(Pin pin);

    /*PinListFragmentMenuInteraction method.*/
    void onPinListFragmentMenuInteraction(String media);

    /*PinEditFragmentCancelInteraction method.*/
    void onPinEditCancelInteraction(boolean isClicked);

    /*PinEditFragmentSaveInteraction method.*/
    void onPinEditSaveInteraction(Pin pin);
}
