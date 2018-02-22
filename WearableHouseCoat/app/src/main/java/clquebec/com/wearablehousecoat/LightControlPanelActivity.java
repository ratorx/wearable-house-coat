package clquebec.com.wearablehousecoat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.philips.lighting.hue.sdk.wrapper.domain.BridgeState;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;

import org.jraf.android.androidwearcolorpicker.app.ColorPickActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import clquebec.com.framework.controllable.ActionNotSupported;
import clquebec.com.implementations.controllable.PhilipsHue;
import clquebec.com.implementations.controllable.PhilipsHueListener;

public class LightControlPanelActivity extends WearableActivity implements PhilipsHueListener {
    private final static int REQUEST_PICK_COLOR = 1;

    private TextView mTextView;
    private ImageView mColourPreview;
    private SeekBar mBrightnessBar;
    private boolean changingBrightness = false;
    //This is temporary code to test the control of light colour and brightness
    private static PhilipsHue phTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_control_panel);

        // Enables Always-on
        setAmbientEnabled();

        mColourPreview = findViewById(R.id.colourPreview);
        mBrightnessBar = findViewById(R.id.brightnessBar);
        mBrightnessBar.setMax(255);
        mBrightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                changingBrightness = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                changingBrightness = false;
                String dT = getIntent().getExtras().getString("DeviceType");
                if (dT != null && dT.equals("HueLight")){
                    phTest.setBrightness(seekBar.getProgress());

                }
            }
        });
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
            mBrightnessBar.setProgress(phTest.getBrightness());
        }

        PhilipsHue.addListener(this);

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

                    }catch (ActionNotSupported e){
                        Log.e("LightControl", "SHOULD NEVER GET HERE");
                        assert(false);
                    }
                }
                break;
        }
    }

    @Override
    public void updateState(BridgeState bs) {
        List<LightPoint> lights = bs.getLights();

        if (lights.size() > 0 && !changingBrightness){
            LightPoint l = lights.get(0);
            mBrightnessBar.setProgress(l.getLightState().getBrightness());
            Log.d("Hue", "Ran listener event");
        }
    }
}
