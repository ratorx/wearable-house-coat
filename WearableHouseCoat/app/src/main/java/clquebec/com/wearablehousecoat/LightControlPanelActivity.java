package clquebec.com.wearablehousecoat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

public class LightControlPanelActivity extends WearableActivity {
    private final static int REQUEST_PICK_COLOR = 1;

    private TextView mTextView;
    private ImageView mColourPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control_panel);

        // Enables Always-on
        setAmbientEnabled();

        mColourPreview = findViewById(R.id.colourPreview);
        mColourPreview.setTag(Color.WHITE);
        mColourPreview.setOnClickListener(view -> {
            Intent intent = new ColorPickActivity.IntentBuilder().oldColor(((Integer) mColourPreview.getTag())).build(LightControlPanelActivity.this);
            startActivityForResult(intent, REQUEST_PICK_COLOR);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_COLOR:
                if (resultCode == RESULT_CANCELED) {
                    // The user pressed 'Cancel'
                    break;
                }

                int pickedColor = ColorPickActivity.getPickedColor(data);
                mColourPreview.setTag(pickedColor);
                mColourPreview.setColorFilter(pickedColor);
                break;
        }
    }
}
