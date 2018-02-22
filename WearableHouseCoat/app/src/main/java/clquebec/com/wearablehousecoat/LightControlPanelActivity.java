package clquebec.com.wearablehousecoat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

import java.util.Timer;
import java.util.TimerTask;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.implementations.controllable.PhilipsHue;

public class LightControlPanelActivity extends WearableActivity {
    private final static int REQUEST_PICK_COLOR = 1;

    private TextView mTextView;
    private ImageView mColourPreview;
    //This is temporary code to test the control of light colour and brightness
    private static PhilipsHue phTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control_panel);

        // Enables Always-on
        setAmbientEnabled();

        mColourPreview = findViewById(R.id.colourPreview);
        mColourPreview.setOnClickListener(view -> {
            Intent intent = new ColorPickActivity.IntentBuilder().oldColor(((Integer) mColourPreview.getTag())).build(LightControlPanelActivity.this);
            startActivityForResult(intent, REQUEST_PICK_COLOR);
        });

        if (phTest == null){
            phTest = new PhilipsHue(this);
        }

        String dT = getIntent().getExtras().getString("DeviceType");
        if (dT != null && dT.equals("HueLight")){
            mColourPreview.setColorFilter(phTest.getColor());
            mColourPreview.setTag(phTest.getColor());
        }

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
                String dT = getIntent().getExtras().getString("DeviceType");
                if (dT != null && dT.equals("HueLight")){
                    try{
                        phTest.setLightColor(pickedColor);

                        Timer mHereTimer = new Timer();
                        mHereTimer.schedule(new TimerTask() {
                            public void run() {
                                runOnUiThread(() -> {int c = phTest.getColor();
                                Log.d("Hue", "Set color is " + pickedColor + ", get color is " + c);
                                mColourPreview.setColorFilter(c);
                                mColourPreview.setTag(c);});
                            }
                        }, 3000);

                    }catch (ActionNotSupported e){
                        Log.e("LightControl", "SHOULD NEVER GET HERE");
                        assert(false);
                    }
                }
                break;
        }
    }
}
