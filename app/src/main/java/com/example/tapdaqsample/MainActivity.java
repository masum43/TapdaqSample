package com.example.tapdaqsample;

import static com.example.tapdaqsample.AdsConfig.getPlacementTag;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.adnetworks.TDMediatedNativeAd;
import com.tapdaq.sdk.adnetworks.TDMediatedNativeAdOptions;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.tapdaq.sdk.model.TMAdSize;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Map<String, TDMediatedNativeAd> mAd = new HashMap<>();
    private String TAG = "NATIVE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //banner
        TMBannerAdView mBannerAd;
        TMAdSize size;
        mBannerAd = findViewById(R.id.banner_ad);
        //you can change banner size Standard, Large, Full
        size = TMBannerAdSizes.get("Full");
        mBannerAd.load(this, size, new AdsConfig.AdListener(TMAdType.BANNER));

        //native
        TDMediatedNativeAdOptions options = new TDMediatedNativeAdOptions(); //optional param
        Tapdaq.getInstance().loadMediatedNativeAd(MainActivity.this, getPlacementTag(), options, new AdListener());

        NativeAdLayout adlayout = findViewById(R.id.native_ad);
        adlayout.populate(mAd.get(getPlacementTag()));
        mAd.remove(getPlacementTag());
    }

    private void updateView() {
        if (mAd.containsKey(getPlacementTag())) {
            findViewById(R.id.native_ad).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.native_ad).setVisibility(View.GONE);
        }
    }


    private class AdListener extends TMAdListener {
        @Override
        public void didLoad(TDMediatedNativeAd ad) {
            super.didLoad(ad);
            mAd.put(getPlacementTag(), ad);
            updateView();
            //Log.i(TAG, "didLoad");
            //mLogListAdapter.insert("didLoad", 0);
        }

        @Override
        public void didFailToLoad(TMAdError error) {
            updateView();

            String str = String.format(Locale.ENGLISH, "didFailToLoad: %d - %s", error.getErrorCode(), error.getErrorMessage());

            for (String key : error.getSubErrors().keySet()) {
                for (TMAdError value : error.getSubErrors().get(key)) {
                    String subError = String.format(Locale.ENGLISH, "%s - %d: %s", key,  value.getErrorCode(), value.getErrorMessage());
                    str = str.concat("\n ");
                    str = str.concat(subError);
                }
            }

            Log.e(TAG, str);
            Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            //mLogListAdapter.insert(str, 0);
        }

        @Override
        public void didDisplay() {
            Log.e(TAG, "didDisplay");
            //mLogListAdapter.insert("didDisplay", 0);
        }

        @Override
        public void didClick() {
            Log.e(TAG, "didClick");
            //mLogListAdapter.insert("didClick", 0);
        }


    }
}