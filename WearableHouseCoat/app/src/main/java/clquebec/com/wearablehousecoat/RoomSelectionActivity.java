package clquebec.com.wearablehousecoat;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RoomSelectionActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_selection);

        ListView mListView = findViewById(R.id.room_listview);

        List<String> testArray = new ArrayList<>();
        testArray.add("Kitchen");
        testArray.add("Room");
        testArray.add("Living Room");
        testArray.add("Dungeon");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                testArray
        );

        mListView.setAdapter(arrayAdapter);

        // Enables Always-on
        setAmbientEnabled();
    }
}
