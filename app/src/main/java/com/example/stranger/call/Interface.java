package com.example.stranger.call;

import android.webkit.JavascriptInterface;

public class Interface {
    callActivity CallActivity;

    public Interface(callActivity callActivity) {
        this.CallActivity = callActivity;
    }

    @JavascriptInterface
    public void onPeerConnected(){
        CallActivity.onPeerConnected();
    }
}
