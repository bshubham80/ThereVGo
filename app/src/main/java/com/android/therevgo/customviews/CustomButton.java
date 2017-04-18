package com.android.therevgo.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import com.android.therevgo.constants.Config;

/**
 * Created by shubham on 23/6/16.
 */
public class CustomButton extends AppCompatButton {


    public CustomButton(Context context) {
        super(context);
        setFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public CustomButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context){
        Typeface face= Typeface.createFromAsset(context.getAssets(), Config.FONT_PATH);
        this.setTypeface(face);
    }
}
