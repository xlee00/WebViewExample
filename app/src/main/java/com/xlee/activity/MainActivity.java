package com.xlee.activity;

/**
 * MainActivity.java[V 1.0.0]
 * classes :
 * 李志华 Create at 22 Oct 2015 16:00:10
 */

import com.xlee.eric.JumpingBeans;
import com.xlee.eric.Network;
import com.xlee.eric.webViewExample.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author 李志华 <br/>
 * create at 22 Oct 2015 16:00:10
 */
public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final String WEBURL = "http://xlee.sinaapp.com";
//    private static final String WEBURL = "https://www.baidu.com/";
    private static final int TEXT_SIZE = 100;// px
    private static final int DISMISS_LOADING_PAGE_PROGRESS = 60;
    // private static final int AUTO_EXIT_TIME = 5000;// 5s
    private static final int PRESS_TO_EXIT_TIME = 1500;// 1.5S

    private WebView webView;
    private TextView mTxtLoadPage;
    private TextView mTxtToast;
    private JumpingBeans mJumpingBeans;
    private PopupWindow mPopupWindowLoading;
    private Toast mToast;

    private long mTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLoadPage();
        initToast();
        if (!Network.isConnected(this)) {
            controllNoNetwork();
        } else {
            initViews();
        }
    }

    private void initLoadPage() {
        mTxtLoadPage = new TextView(this);
        mTxtLoadPage.setTextSize(TEXT_SIZE);
        mTxtLoadPage.setBackgroundColor(Color.BLACK);
        mTxtLoadPage.setText(R.string.loading);
        mTxtLoadPage.setGravity(Gravity.CENTER);
        mTxtLoadPage.setTextColor(Color.WHITE);
        TextPaint tp = mTxtLoadPage.getPaint();
        tp.setFakeBoldText(true);
        mJumpingBeans = JumpingBeans.with(mTxtLoadPage).appendJumpingDots().build();
        mPopupWindowLoading = new PopupWindow(mTxtLoadPage, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPopupWindowLoading.setFocusable(false);
        mPopupWindowLoading.setAnimationStyle(R.style.loading_page_out);
        this.getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MainActivity.this.getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mPopupWindowLoading.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            }
        });
    }

    private void initToast() {
        if (null == mToast) {
            LayoutInflater inflater = getLayoutInflater();
            mTxtToast = (TextView) inflater.inflate(R.layout.toast_txt, null);
            mToast = new Toast(getApplicationContext());
            mToast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 40);
            mToast.setDuration(Toast.LENGTH_LONG);
            mToast.setView(mTxtToast);
        }
    }

    private void initViews() {
        webView = (WebView) findViewById(R.id.wv_main);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(WEBURL);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= DISMISS_LOADING_PAGE_PROGRESS && null != mPopupWindowLoading
                        && mPopupWindowLoading.isShowing()) {
                    mPopupWindowLoading.dismiss();
                }
                Log.i(TAG, "newProgress ==" + newProgress);
            }

            @Override
            public void onRequestFocus(WebView view) {
                super.onRequestFocus(view);
                Log.i(TAG, "onRequestFocus view ==" + view.getClass().getName());
            }
        });
    }

    private void controllNoNetwork() {
        mTxtToast.setText(R.string.network_not_available);
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.show();
        // this.getWindow().getDecorView().postDelayed(new Runnable() {
        // @Override
        // public void run() {
        // MainActivity.this.finish();
        // }
        // }, AUTO_EXIT_TIME);
        if (null != mJumpingBeans) {
            mJumpingBeans.stopJumping();
        }
        mTxtLoadPage.setText(R.string.loading_failure);
    }

    @Override
    public void onPause() {
        if (null != mJumpingBeans) {
            mJumpingBeans.stopJumping();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (0 != mTime && System.currentTimeMillis() - mTime < PRESS_TO_EXIT_TIME) {
            finish();
        } else {
            mTime = System.currentTimeMillis();
            mTxtToast.setText(R.string.press_again_to_exit);
            mToast.setDuration(Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (null != mPopupWindowLoading && mPopupWindowLoading.isShowing()) {
            mPopupWindowLoading.dismiss();
            mPopupWindowLoading = null;
        }
    }
}
