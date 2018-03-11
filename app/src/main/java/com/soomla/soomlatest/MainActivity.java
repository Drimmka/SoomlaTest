package com.soomla.soomlatest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private static final String NEW_ICON_SRC = "http://blog.soomla.com/wp-content/uploads/2018/03/MonetizationBenchmarks-BlogSidebar.png";
    private static final String ICON_SRC = "https://www.facebook.com/images/ad_network/audience_network_icon.png";

    private AdView adView;
    private WebView webView;
    private WebViewHandler webViewHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAdView();
        initLoadAdButton();
        initWebViewHandler();
    }

    private void initWebViewHandler() {
        webViewHandler = new WebViewHandler();
    }

    private void initAdView() {
        adView = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                webView = findWebView(ad);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                      //  webView.loadUrl("javascript:(function(){document.body.innerHTML = document.body.innerHTML.replace('" + ICON_SRC+"', '" + NEW_ICON_SRC+"')})()");
                    }
                });
                webView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        adView.setVisibility(View.INVISIBLE);
        //AdSettings.addTestDevice("d91e5199-5505-42d9-8bcc-029d9520000b"); //emulator
        //AdSettings.addTestDevice("b179acf8-eb38-48bc-9d17-691160ec254d");// phone
        // Request an ad
        adView.loadAd();
    }

    private WebView findWebView(Ad ad) {
        Field f = null; //NoSuchFieldException
        try {
            f = ad.getClass().getSuperclass().getSuperclass().getDeclaredField("mChildren");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        View[] arr = null;
        try{
            arr = (View[])f.get(ad);
        }
        catch (Exception e){

        }

        if (arr[0] instanceof WebView){
            return (WebView) arr[0];

        }
        return null;
    }

    private void initLoadAdButton() {
        Button loadButton = findViewById(R.id.buttonLoadBanner);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoadButtonPressed();
            }
        });

    }


    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    private void handleLoadButtonPressed() {
        adView.setVisibility(View.VISIBLE);
        Button loadButton = findViewById(R.id.buttonLoadBanner);
        loadButton.setEnabled(false);
    }

    class MyJavaScriptInterface {


        @JavascriptInterface
        public void showHTML(String html) {
            //String newHtml = html.replace(ICON_SRC, NEW_ICON_SRC);
            //webView.loadUrl(newHtml);
            //webView.loadUrl("https://www.google.com");

        }

    }

}
