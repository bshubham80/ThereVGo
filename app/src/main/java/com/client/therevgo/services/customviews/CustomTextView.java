package com.client.therevgo.services.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.client.therevgo.services.constants.Config;

/**
 * Created by shubham on 20/6/16.
 */
public class CustomTextView extends AppCompatTextView {


    public CustomTextView(Context context) {
        super(context);
        setFont(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont(context);
    }

    private void setFont(Context context){
        Typeface face= Typeface.createFromAsset(context.getAssets(), Config.FONT_PATH);
        this.setTypeface(face);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        Typeface face= Typeface.createFromAsset(getContext().getAssets(), Config.FONT_PATH);
        super.setTypeface(face, style);
    }
}
