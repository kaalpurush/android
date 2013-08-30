package com.codelixir.deshitv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class FullscreenWebChromeClient extends WebChromeClient {
        protected static Activity mActivity = null;

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;

        private FrameLayout mContentView;
        private FrameLayout mFullscreenContainer;

        private static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        public FullscreenWebChromeClient(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                mOriginalOrientation = mActivity.getRequestedOrientation();
                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);                
                decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
                mActivity.setRequestedOrientation(requestedOrientation);
            }

            super.onShowCustomView(view, requestedOrientation, callback);
        }
        
        public boolean isFullscreen(){
        	return (mCustomView != null);
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }

            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
            mActivity.setRequestedOrientation(mOriginalOrientation);
        }

        private void setFullscreen(boolean enabled) {
            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                } else {
                    mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private static class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Context ctx) {
                super(ctx);
                setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
            }

            @Override
            public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}