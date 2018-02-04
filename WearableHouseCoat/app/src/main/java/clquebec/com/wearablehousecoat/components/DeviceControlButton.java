package clquebec.com.wearablehousecoat.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import clquebec.com.framework.controllable.ControllableDevice;
import clquebec.com.framework.controllable.ControllableDeviceType;
import clquebec.com.wearablehousecoat.R;

/**
 * WearableHouseCoat
 * Author: Tom
 * Creation Date: 03/02/18
 *
 * This is the circular button that is used as a device toggle.
 * Right now, it's effectively just a custom layout for a button,
 * but in the future this class can be adapted (or extended) to include
 * logging and calls to our preference learner.
 */

public class DeviceControlButton extends Button implements View.OnClickListener {
    public static final int DEFAULT_BACKGROUND = Color.WHITE;
    private static final float DEFAULT_PADDING = 5;

    private int mBackgroundColor = DEFAULT_BACKGROUND;
    private float mPadding = DEFAULT_PADDING;
    private ControllableDeviceType mDeviceType;

    private ControllableDevice mDevice;

    private Paint mBackgroundPaint;
    private Drawable mDeviceIcon = null;

    //Fields for painting - these are members so they are cached between draws
    private int mSize; /* The side length of the view */
    private float mRadius;
    private float[] mCenter = {0, 0};

    public DeviceControlButton(Context context, AttributeSet attrs){
        super(context, attrs);

        //Get attribute array
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DeviceControlButton,
                0, 0
        );

        //Parse attributes
        try {
            mBackgroundColor = a.getColor(R.styleable.DeviceControlButton_background, DEFAULT_BACKGROUND);
            mPadding = a.getDimension(R.styleable.DeviceControlButton_padding, DEFAULT_PADDING);
            mDeviceType = ControllableDeviceType.getType(a.getInt(R.styleable.DeviceControlButton_type, 0));
        }finally{
            a.recycle(); //Recycle TypedArray
        }

        //Initialise paint objects
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        if(mDeviceType.getIcon() != 0) {
            mDeviceIcon = context.getDrawable(mDeviceType.getIcon());
        }

        //Get rid of button background
        setBackgroundResource(0);

        //Set on click listener to default toggle action
        setOnClickListener(this);
    }

    @Override
    public void onDraw(Canvas canvas){
        /* Draw a circle, with an icon on top. */
        canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaint);
        if(mDeviceIcon != null) {
            mDeviceIcon.draw(canvas);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /* Enforce squareness (so that the circle fills the space)
         * https://stackoverflow.com/questions/8981029/simple-way-to-do-dynamic-but-square-layout
         */

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mSize = width > height ? height : width;
        setMeasuredDimension(mSize, mSize);

        //Calculate things for painting
        float half = mSize/2.0f;
        mCenter[0] = half; mCenter[1] = half;
        mRadius = half - mPadding;

        //Fit icon into available space
        final float imageAvailableSize = mSize - mPadding*4; //Padding on both sides
        final int paddingBounds = (int) mPadding*2;

        if(mDeviceIcon != null) {
            final float scale = mDeviceIcon.getIntrinsicWidth() > mDeviceIcon.getIntrinsicHeight() ?
                imageAvailableSize / mDeviceIcon.getIntrinsicWidth() :
                imageAvailableSize / mDeviceIcon.getIntrinsicHeight();

            //Calculate dimensions
            //Casting to int at last moment to try and reduce rounding errors.
            final float imageWidth = scale * mDeviceIcon.getIntrinsicWidth();
            final float imageHeight = scale * mDeviceIcon.getIntrinsicHeight();
            final float paddingX = (mSize - imageWidth) / 2;
            final float paddingY = (mSize - imageHeight) / 2;

            mDeviceIcon.setBounds((int) paddingX, (int) paddingY, (int) (imageWidth + paddingX), (int) (imageHeight + paddingY));
        }
    }

    public void attachDevice(ControllableDevice device){
        mDevice = device;

        //Set the correct icon
        mDeviceType = mDevice.getType();
        if(mDeviceType.getIcon() != 0) {
            mDeviceIcon = getContext().getDrawable(mDeviceType.getIcon());
        }
    }

    @Override
    public void onClick(View view) {
        //By default, toggle the Device.

        Log.d("DeviceControlButton", "Device Control Button clicked");

        if(mDevice != null) {
            if (mDevice.isEnabled()) {
                mDevice.disable();
            } else {
                mDevice.enable();
            }
        }
    }
}
