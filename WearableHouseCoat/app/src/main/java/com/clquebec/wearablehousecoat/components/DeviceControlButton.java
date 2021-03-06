package com.clquebec.wearablehousecoat.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.AppCompatButton;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.HapticFeedbackConstants;
import android.view.View;

import com.clquebec.framework.controllable.ControllableDevice;
import com.clquebec.framework.controllable.ControllableDeviceType;
import com.clquebec.framework.listenable.DeviceChangeListener;
import com.clquebec.framework.listenable.ListenableDevice;
import com.clquebec.implementations.controllable.PhilipsHue;
import com.clquebec.wearablehousecoat.R;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * WearableHouseCoat
 * Author: Tom
 * Creation Date: 03/02/18
 * <p>
 * This is the circular button that is used as a device toggle.
 * Right now, it's effectively just a custom layout for a button,
 * but in the future this class can be adapted (or extended) to include
 * logging and calls to our preference learner.
 */

public class DeviceControlButton extends AppCompatButton implements View.OnClickListener, View.OnLongClickListener, DeviceChangeListener {
    public static final int DEFAULT_BACKGROUND = Color.WHITE;
    public static final int DEFAULT_BACKGROUND_OFF = Color.argb(255, 0, 53, 84); //Prussian Blue
    public static final int DEFAULT_BACKGROUND_DISCONNECTED = Color.argb(255, 255, 0, 0);
    public static final int DEFAULT_BACKGROUND_AUTHENTICATE = Color.argb(255, 0, 255, 0);
    private static final float DEFAULT_PADDING = 5;
    private static final ControllableDeviceType DEFAULT_TYPE = ControllableDeviceType.LIGHT;
    private static final int DEFAULT_SIZE = 100;

    private int mBackgroundColor = DEFAULT_BACKGROUND;
    private int mBackgroundColorOff = DEFAULT_BACKGROUND_OFF;
    private int mBackgroundColorDisconnected = DEFAULT_BACKGROUND_DISCONNECTED;
    private int mBackgroundColorAuthenticate = DEFAULT_BACKGROUND_AUTHENTICATE;
    private float mPadding = DEFAULT_PADDING;
    private ControllableDeviceType mDeviceType = DEFAULT_TYPE;

    private ControllableDevice mDevice;

    private Paint mBackgroundPaint;
    private Paint mBackgroundPaintOff;
    private Paint mBackgroundPaintDisconnected;
    private Paint mBackgroundPaintAuthenticate;
    private TextPaint mTextPaint;
    private TextPaint mTextPaintOff;
    private Drawable mDeviceIcon = null;
    private Drawable mDeviceIconOff = null;

    private Vibrator vib;

    //Fields for painting - these are members so they are cached between draws
    private int mSize; /* The side length of the view */
    private float mRadius;
    private float[] mCenter = {0, 0};
    private int mTextHeight;

    public DeviceControlButton(Context context) {
        super(context);

        init(context, null);

        vib =(Vibrator) context.getSystemService(VIBRATOR_SERVICE);
    }

    public DeviceControlButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);

        vib =(Vibrator) context.getSystemService(VIBRATOR_SERVICE);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) { //Parse attributes, if supplied
            //Get attribute array
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.DeviceControlButton,
                    0, 0
            );

            //Parse attributes
            try {
                mBackgroundColor = a.getColor(R.styleable.DeviceControlButton_dcb_background, DEFAULT_BACKGROUND);
                mBackgroundColorOff = a.getColor(R.styleable.DeviceControlButton_dcb_backgroundoff, DEFAULT_BACKGROUND_OFF);
                mPadding = a.getDimension(R.styleable.DeviceControlButton_padding, DEFAULT_PADDING);
                mDeviceType = ControllableDeviceType.getType(a.getInt(R.styleable.DeviceControlButton_type, 0));
            } finally {
                a.recycle(); //Recycle TypedArray
            }
        }

        //Initialise paint objects
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setColor(mBackgroundColor);

        mBackgroundPaintOff = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaintOff.setStyle(Paint.Style.FILL);
        mBackgroundPaintOff.setColor(mBackgroundColorOff);

        mBackgroundPaintDisconnected = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaintDisconnected.setStyle(Paint.Style.FILL);
        mBackgroundPaintDisconnected.setColor(mBackgroundColorDisconnected);

        mBackgroundPaintAuthenticate = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaintAuthenticate.setStyle(Paint.Style.FILL);
        mBackgroundPaintAuthenticate.setColor(mBackgroundColorAuthenticate);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mBackgroundColorOff);

        mTextPaintOff = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintOff.setColor(mBackgroundColor);

        if (mDeviceType.getIcon() != 0) {
            mDeviceIcon = context.getDrawable(mDeviceType.getIcon());
        }

        if (mDeviceType.getFadedIcon() != 0) {
            mDeviceIconOff = context.getDrawable(mDeviceType.getFadedIcon());
        }

        //Get rid of button background
        setBackgroundResource(0);

        //Set on click listener to default toggle action
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        /* Draw a circle, with an icon on top. */
        if (mDevice == null || !mDevice.isConnected()) {
            canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaintDisconnected);

            if (mDevice != null && mDevice.getName() != null) {
                canvas.drawText(mDevice.getName(), mPadding * 4, mCenter[1] + mTextHeight / 2, mTextPaintOff);
            }
        }else if (mDevice instanceof PhilipsHue && ((PhilipsHue) mDevice).getRequiresAuthentication()){
            canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaintAuthenticate);

            if (mDevice != null && mDevice.getName() != null) {
                canvas.drawText(mDevice.getName(), mPadding * 4, mCenter[1] + mTextHeight / 2, mTextPaint);
            }
        }else if (mDevice.isEnabled()) {
            canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaint);

            if (mDevice.getName() != null) {
                canvas.drawText(mDevice.getName(), mPadding * 4, mCenter[1] + mTextHeight / 2, mTextPaint);
            }
        } else {
            canvas.drawCircle(mCenter[0], mCenter[1], mRadius, mBackgroundPaintOff);

            if(mDevice.getName() != null) {
                canvas.drawText(mDevice.getName(), mPadding * 4, mCenter[1] + mTextHeight / 2, mTextPaintOff);
            }
        }
        if (mDeviceIcon != null) {
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

        if (height != 0) {
            mSize = width > height ? height : width;
        } else {
            mSize = width;
        }

        if (mSize == 0) {
            if (getParent() != null) {
                mSize = ((View) getParent()).getMeasuredWidth() / 3; //Automatically set to 1/3rd size
            } else {
                mSize = DEFAULT_SIZE; //If all else fails.
            }
        }

        setMeasuredDimension(mSize, mSize);

        measure();
    }

    public void setSize(int size) {
        mSize = size;
        measure();
    }

    private void measure() {
        //Calculate things for painting
        //Called on both onMeasure and setSize();
        float half = mSize / 2.0f;
        mCenter[0] = half;
        mCenter[1] = half;
        mRadius = half - mPadding;

        //Fit icon into available space
        final float imageAvailableSize = (mSize - mPadding * 4) / 2; //Padding on both sides

        if (mDeviceIcon != null) {
            //Calculate icon dimensions
            //Casting to int at last moment to try and reduce rounding errors..

            final float diagonalLength = (float) Math.sqrt(
                    mDeviceIcon.getIntrinsicWidth() * mDeviceIcon.getIntrinsicWidth()
                            + mDeviceIcon.getIntrinsicHeight() * mDeviceIcon.getIntrinsicHeight()
            );
            final float scale = imageAvailableSize / diagonalLength;

            final float imageWidth = scale * mDeviceIcon.getIntrinsicWidth();
            final float imageHeight = scale * mDeviceIcon.getIntrinsicHeight();
            final float paddingX = (mSize - imageWidth) / 2;

            mDeviceIcon.setBounds((int) paddingX, (int) mPadding, (int) (imageWidth + paddingX), (int) (imageHeight + mPadding));
            if (mDeviceIconOff != null) {
                mDeviceIconOff.setBounds((int) paddingX, (int) mPadding, (int) (imageWidth + paddingX), (int) (imageHeight + mPadding));
            }

            //Calculate text dimensions
            float textWidth = mTextPaint.measureText(mDevice.getName());
            float newTextSize = ((mSize - mPadding * 8) / textWidth) * mTextPaint.getTextSize();
            mTextPaint.setTextSize(newTextSize);
            mTextPaintOff.setTextSize(newTextSize);

            Rect textDimensions = new Rect();
            mTextPaint.getTextBounds(mDevice.getName(), 0, mDevice.getName().length(), textDimensions);
            mTextHeight = textDimensions.height();
        }
    }

    public void attachDevice(ControllableDevice device) {
        if(device != null) {
            mDevice = device;

            //Set the correct icon
            mDeviceType = mDevice.getType();
            if (mDeviceType.getIcon() != 0) {
                mDeviceIcon = getContext().getDrawable(mDeviceType.getIcon());
                mDeviceIconOff = getContext().getDrawable(mDeviceType.getFadedIcon());
            }

            if (mDevice instanceof ListenableDevice){
                ((ListenableDevice) mDevice).addListener(this);
            }

            //Re-calculate size
            measure();

            //Redraw view
            measure();
            invalidate();
        }
    }

    @Override
    public void onClick(View view) {
        //Call quickAction in the attached device
        vib.vibrate(20);
        if (mDevice != null && mDevice.isConnected()) {

            mDevice.quickAction();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        //Call extendedAction in the attached device
        vib.vibrate(50);
        if (mDevice != null && mDevice.isConnected()) {

            mDevice.extendedAction();
            return true;
        }

        //Did not capture event
        return false;
    }

    @Override
    public void updateState(ListenableDevice device) {
        this.invalidate();
    }

    @Override
    protected void finalize() throws Throwable {
        if (mDevice instanceof ListenableDevice){
            ((ListenableDevice) mDevice).removeListener(this);
        }

        super.finalize();
    }
}
