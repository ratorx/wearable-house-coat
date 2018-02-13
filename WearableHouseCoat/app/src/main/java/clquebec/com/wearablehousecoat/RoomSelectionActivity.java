package clquebec.com.wearablehousecoat;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RoomSelectionActivity extends WearableActivity {
    public static String INTENT_ROOMS_EXTRA = "rooms";
    public static String INTENT_ROOM_NAME = "room";
    private TextView mTextView;
    private List<CharSequence> mRoomArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);

        ListView mListView = findViewById(R.id.room_listview);

        //Get rooms from Intent
        if(getIntent().getExtras() == null) {
            throw new IllegalArgumentException("Provide a list of rooms");
        }

        mRoomArray = getIntent().getExtras().getCharSequenceArrayList(INTENT_ROOMS_EXTRA);

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

        // Enables Always-on
        setAmbientEnabled();
    }
}
