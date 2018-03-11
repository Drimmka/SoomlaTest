package com.soomla.soomlatest;

import android.content.Intent;
import android.net.Uri;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String FIELD_MCHILDREN = "mChildren";
    private static final String NEW_ICON_SRC1 = "https://avatars2.githubusercontent.com/u/2118838?s=280&v=4";
    private static final String NEW_ICON_SRC2 = "http://blog.soomla.com/wp-content/uploads/2018/03/MonetizationBenchmarks-BlogSidebar.png";

    private static final String REDIRECT_INSTALL_URL1 = "http://blog.soomla.com/";
    private static final String REDIRECT_INSTALL_URL2 = "https://www.google.com";

    private ArrayList<AdView> adViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAdViewsList();
        initLoadAdButton();
    }

    //create ad views list and populate the first ad
    private void initAdViewsList() {
        adViews = new ArrayList<>();
        //first banner is preloaded, and set to invisible till the button is pressed
        adViews.add(initAdView(R.id.banner_container, REDIRECT_INSTALL_URL1, NEW_ICON_SRC1));
    }


    //create adView and load ad into it
    private AdView initAdView(int containerID, final String newActionUrl, final String newIconImageSrc) {
        AdView adView = new AdView(this, "YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(containerID);

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
                // find the webview using reflection
                WebView webView = findWebView(ad);
                //enable javascript
                webView.getSettings().setJavaScriptEnabled(true);
                //setwebView client to replace image url and handle install now clicks
                setWebClient(webView, newActionUrl, newIconImageSrc);
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

        adView.setVisibility(View.INVISIBLE); //for the first one only
        AdSettings.addTestDevice("72f0908e-d5af-4140-bce7-ed847395c516"); //emulator
        //AdSettings.addTestDevice("b179acf8-eb38-48bc-9d17-691160ec254d");// phone
        // Request an ad
        adView.loadAd();
        return adView;
    }

    //create new webClient for this webView
    private void setWebClient(WebView view, String newActionUrl, String newIconImageSrc) {
        view.setWebViewClient(new CustomWebViewClient(getApplicationContext(), newActionUrl, newIconImageSrc));
    }

    //find the webview in the returned ad object
    private WebView findWebView(Ad ad) {
        Field f = null;
        try {
            f = ad.getClass().getSuperclass().getSuperclass().getDeclaredField(FIELD_MCHILDREN);
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
        if (adViews != null) {
            for (int i = 0; i < adViews.size(); i++){
                adViews.get(i).destroy();
            }
        }
        super.onDestroy();
    }

    private void handleLoadButtonPressed() {
        if (adViews.get(0).getVisibility() == View.VISIBLE) { //first ad was presented, then add another one
            addNewBanner();
        }
        else {                  //no banner shown yet, then show the first one
            adViews.get(0).setVisibility(View.VISIBLE);
            Button loadButton = findViewById(R.id.buttonLoadBanner);
            loadButton.setText(R.string.load_another_banner);
        }
    }

    //dynamically create new banner and add it to the container layout
    private void addNewBanner() {
        LinearLayout newBannerLAyout = new LinearLayout(this);
        newBannerLAyout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        newBannerLAyout.setOrientation(LinearLayout.VERTICAL);
        newBannerLAyout.setId(View.generateViewId());
        LinearLayout parent = findViewById(R.id.linearLayoutBanners);
        parent.addView(newBannerLAyout);

        AdView newadView = initAdView(newBannerLAyout.getId(), REDIRECT_INSTALL_URL2, NEW_ICON_SRC2); //of course here it's possible also to manage a list of urls to
                                                                                    // have different urls and icons for each new adview
        newadView.setVisibility(View.VISIBLE);
        adViews.add(newadView);
    }

}
