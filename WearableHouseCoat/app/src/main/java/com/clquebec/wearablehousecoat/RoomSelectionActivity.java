package com.clquebec.wearablehousecoat;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class RoomSelectionActivity extends WearableActivity {
    public static RoomSelectionActivity currentInstance;

    public static final String INTENT_ROOMS_EXTRA = "rooms";
    public static final String INTENT_ROOM_NAME = "room";
    private static List<CharSequence> mRoomArray;

    private ProgressBar mLoadingProgress;
    private TextView mLoadingText;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentInstance = this;

        setContentView(R.layout.activity_room_selection);

       mListView = findViewById(R.id.room_listview);

        mLoadingProgress = findViewById(R.id.loadingRoomsProgress);
        mLoadingText = findViewById(R.id.loadingRoomsText);

        setArrayAdapter();

        // Enables Always-on
        setAmbientEnabled();
    }

    @Override
    public void onDestroy(){
        currentInstance = null;
        super.onDestroy();
    }

    public void setArrayAdapter() {
        if (mRoomArray == null || mRoomArray.size() == 0){
            mLoadingProgress.setVisibility(View.VISIBLE);
            mLoadingText.setVisibility(View.VISIBLE);
            mListView.setAdapter(null);
            mListView.setOnItemClickListener(null);
        }else{
            mLoadingProgress.setVisibility(View.GONE);
            mLoadingText.setVisibility(View.GONE);

            ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    mRoomArray
            );

            mListView.setAdapter(arrayAdapter);
            mListView.setOnItemClickListener((adapterView, view, i, l) -> {
                //Return, with the String Room name
                Intent result = new Intent(INTENT_ROOM_NAME);
                result.putExtra(INTENT_ROOM_NAME, mRoomArray.get(i));
                setResult(RESULT_OK, result);
                finish();
            });
        }
    }

    public static void setRoomList(List<CharSequence> roomList){
        if (roomList == null){
            throw new IllegalArgumentException("Room array must be passed");
        }else{
            mRoomArray = roomList;
            if (currentInstance != null){
                currentInstance.setArrayAdapter();
            }
        }
    }

}
