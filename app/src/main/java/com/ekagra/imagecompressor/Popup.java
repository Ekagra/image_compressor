package com.ekagra.imagecompressor;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Abhishek on 24-03-2017.
 */

/**
 * By Default shows Error i.e. Fail toast
 * */
public class Popup extends Toast {

    private Context mContext;

    private View mView;

    private LayoutInflater mLayoutInflater;

    private TextView mTitleTextView, mMessageTextView;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public Popup(Context context) {
        super(context);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.popup_saved, null);
        initialiseView(mView);
        setView(mView);
        setGravity(Gravity.TOP | Gravity.END, 0, 0);
    }

    public Popup(Context context, int state) {
        super(context);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        if (state ==1 ) {
            mView = mLayoutInflater.inflate(R.layout.popup_saved, null);
        }
        initialiseView(mView);
        setView(mView);
        setGravity(Gravity.TOP | Gravity.END, 0, 0);
    }

    private void initialiseView(View mView) {

        mTitleTextView = (TextView) mView.findViewById(R.id.titleTextView);

        mMessageTextView = (TextView) mView.findViewById(R.id.messageTextView);

    }

    public void setTitle(String title) {

        if (title != null && title.length() != 0) {

            mTitleTextView.setText(title);

        } else {

            mTitleTextView.setVisibility(View.GONE);

        }

    }

    public void setMessage(String message) {

        if (message != null && message.length() != 0) {

            mMessageTextView.setText(message);

        } else {

            mMessageTextView.setVisibility(View.GONE);

        }

    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public static Popup makeText(Context mContext, String mTitle, String mMessage) {
        Popup mNexoolCustomToast = new Popup(mContext);

        mNexoolCustomToast.setTitle(mTitle);

        mNexoolCustomToast.setMessage(mMessage);

        return mNexoolCustomToast;
    }

    public static Popup makeText(Context mContext, String mTitle, String mMessage, int state) {
        Popup mNexoolCustomToast = new Popup(mContext, state);

        mNexoolCustomToast.setTitle(mTitle);

        mNexoolCustomToast.setMessage(mMessage);

        return mNexoolCustomToast;
    }
}