package clquebec.com.wearablehousecoat.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

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

public class DeviceControlButton extends Button {
    public static final int DEFAULT_BACKGROUND = Color.WHITE;
    private static final float DEFAULT_PADDING = 5;

    private int mBackgroundColor = DEFAULT_BACKGROUND;
    private float mPadding = DEFAULT_PADDING;
    private int mDeviceType = 0; /* TODO: Change this to a DeviceType enum */

    private Paint mBackgroundPaint;

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
            mDeviceType = a.getInt(R.styleable.DeviceControlButton_type, 0);
        }finally{
            a.recycle(); //Recycle TypedArray
        }

        //Initialise paint objects
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        //Get rid of button background
        setBackgroundResource(0);
    }

    @Override
    public void onDraw(Canvas canvas){
        /* Draw a circle, with an icon on top. */
        canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaint);
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

    }
}
