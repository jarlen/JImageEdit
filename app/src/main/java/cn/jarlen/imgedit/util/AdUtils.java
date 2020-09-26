package cn.jarlen.imgedit.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Random;

import cn.jarlen.imgedit.R;

public class AdUtils {


    public static String getAdUiId(Context context) {
        String[] adArray = context.getResources().getStringArray(R.array.ad_unid_array);
        int index = new Random().nextInt(8);
        Log.e("jarlen", "getAdUiId  : " + index);
        return adArray[index];
    }


    public static void loadBanner(Activity activity, final ViewGroup adContainer) {
        loadBanner(activity, adContainer, false);
    }

    public static void loadBanner(Activity activity, final ViewGroup adContainer, boolean closable) {
        final AdView adView = new AdView(activity);
        adView.setAdUnitId(getAdUiId(activity));
        adContainer.addView(adView);

        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        AdSize adSize = getAdSize(activity);
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);
        if (closable) {
            final ImageView btnClose = new ImageView(activity);
            btnClose.setBackgroundColor(Color.parseColor("#999999"));
            btnClose.setImageResource(R.drawable.ic_close_white_24dp);
            RelativeLayout.LayoutParams closeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            closeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            closeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            btnClose.setLayoutParams(closeParams);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adContainer.setVisibility(View.GONE);
                }
            });

            adView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    adContainer.addView(btnClose);
                }
            });
        }
        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private static AdSize getAdSize(Activity context) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
