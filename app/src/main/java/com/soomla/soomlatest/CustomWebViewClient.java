package com.soomla.soomlatest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * CustomWebViewClient class to handle icon image replacing
 * and to handle "install now" clicks redirect
 */

public class CustomWebViewClient extends WebViewClient {

    private static final String ICON_SRC = "https://www.facebook.com/images/ad_network/audience_network_icon.png";
    private static final String INSTALL_ACTION_URL = "play.google.com";

    private Context context;
    private String newActionUrl;
    private String newIconImageSrc;

    public CustomWebViewClient(Context context, String newActionUrl, String newIconImageSrc){
        this.context = context;
        this.newActionUrl = newActionUrl;
        this.newIconImageSrc = newIconImageSrc;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        view.evaluateJavascript("javascript:(function(){document.body.innerHTML = document.body.innerHTML.replace('" + ICON_SRC+"', '" + newIconImageSrc+"')})()", null);
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUrlLoading(view, url);
    }


    private boolean handleUrlLoading(WebView view, String url) {
        if(!url.contains(INSTALL_ACTION_URL)) {
            view.loadUrl(url);
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(newActionUrl));
            context.startActivity(i);
        }
        return true;
    }
}
