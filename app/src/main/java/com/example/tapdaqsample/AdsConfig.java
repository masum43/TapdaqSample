package com.example.tapdaqsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import com.tapdaq.sdk.TMBannerAdView;
import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.TapdaqConfig;
import com.tapdaq.sdk.adnetworks.TMMediationNetworks;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.common.TMAdType;
import com.tapdaq.sdk.common.TMBannerAdSizes;
import com.tapdaq.sdk.listeners.TMAdListener;
import com.tapdaq.sdk.listeners.TMInitListener;
import com.tapdaq.sdk.model.rewards.TDReward;
import java.util.Arrays;
import java.util.Locale;

public class AdsConfig {


    private String mAppId = "6149ebeb86c31c4e07c10d68";
    private String mClientKey = "9d5df5b6-05ff-4f0f-9d78-33f377d74496";
    public static String getPlacementTag() {
        return "default";
    }

    public static boolean isReady = false;

    static Activity activity;
    static String nextActivity = "";
    public AdsConfig(Activity a){
        activity = a;
    }

    public static TMBannerAdView ad;



    public void init(String cName){
        //init tap daq on mainActivity
        TapdaqConfig config = Tapdaq.getInstance().config();
        config.setAutoReloadAds(true);
        //Setting config options here will override any settings changed in the debugger before initialisation
        config.registerTestDevices(TMMediationNetworks.AD_MOB, Arrays.asList("d1cf44a5-7561-4e21-967e-e6dbaf6f4a4c"));
        config.registerTestDevices(TMMediationNetworks.FACEBOOK, Arrays.asList("d1cf44a5-7561-4e21-967e-e6dbaf6f4a4c"));
        config.registerTestDevices(TMMediationNetworks.UNITY_ADS, Arrays.asList("d1cf44a5-7561-4e21-967e-e6dbaf6f4a4c"));

        Tapdaq.getInstance().initialize(activity, mAppId, mClientKey, config, new InitListener(cName));

    }

    private static class InitListener extends TMInitListener {
        private String gotoMain;
        public InitListener(String str) {
            gotoMain = str;
        }
        @Override
        public void didInitialise() {
            super.didInitialise();
            ad = new TMBannerAdView(activity);
            ad.load(activity, TMBannerAdSizes.FULL, new AdsConfig.AdListener(TMAdType.BANNER));

            // mBannerAd.load(activity, size, new AdsConfig.AdListener(TMAdType.BANNER));
            LoadInterstitial(TMAdType.VIDEO_INTERSTITIAL);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class<?> c = Class.forName(gotoMain);
                        Intent intent = new Intent(activity, c);
                        activity.startActivity(intent);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }, 3000);



            Log.i("MEDIATION-SAMPLE", "didInitialise");
        }

        @Override
        public void didFailToInitialise(TMAdError error) {
            super.didFailToInitialise(error);

            String str = String.format(Locale.ENGLISH, "didFailToInitialise: %d - %s", error.getErrorCode(), error.getErrorMessage());

            for (String key : error.getSubErrors().keySet()) {
                for (TMAdError value : error.getSubErrors().get(key)) {
                    String subError = String.format(Locale.ENGLISH, "%s - %d: %s", key,  value.getErrorCode(), value.getErrorMessage());
                    str = str.concat("\n ");
                    str = str.concat(subError);
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class<?> c = Class.forName(gotoMain);
                        Intent intent = new Intent(activity, c);
                        activity.startActivity(intent);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }, 3000);
            Log.i("MEDIATION-SAMPLE", str);
        }
    }



    public static void loadBanner(Activity activity, TMBannerAdView mBannerAd) {
//        TMAdSize size = TMBannerAdSizes.get("Full");
//        mBannerAd.load(activity, size, new AdsConfig.AdListener(TMAdType.BANNER));

        if (ad!= null && ad.isReady()) {
            if (ad.getParent() != null) {
                ((ViewGroup) ad.getParent()).removeView(ad);
            }
            mBannerAd.removeAllViews();
            mBannerAd.addView(ad);
            mBannerAd.invalidate();

//            //loading a new banner
//            ad = new TMBannerAdView(activity);
//            ad.load(activity, TMBannerAdSizes.STANDARD, new AdsConfig.AdListener(TMAdType.BANNER));
        }
        else  {
            ad = new TMBannerAdView(activity);
            ad.load(activity, TMBannerAdSizes.FULL, new AdsConfig.AdListener(TMAdType.BANNER));
            mBannerAd.addView(ad);
        }
    }


    public static void ReadyInterstitialAd(int type) {
        String tag = getPlacementTag();

        switch (type) {
            case TMAdType.STATIC_INTERSTITIAL:
                isReady = Tapdaq.getInstance().isInterstitialReady(activity, tag);
                Log.i("MEDIATION-SAMPLE", Tapdaq.getInstance().isInterstitialReady(activity, tag)+ "Ready Interstitial");
                break;
            case TMAdType.VIDEO_INTERSTITIAL:
                isReady = Tapdaq.getInstance().isVideoReady(activity, tag);
                break;
            case TMAdType.REWARD_INTERSTITIAL:
                isReady = Tapdaq.getInstance().isRewardedVideoReady(activity, tag);
                break;
        }

        if (isReady) {
        }
    }


    public static void ShowInterstitial(int type, String className) {
        nextActivity = className;
        switch (type) {
            case TMAdType.STATIC_INTERSTITIAL:
            {
                if (Tapdaq.getInstance().isInterstitialReady(activity, getPlacementTag())) {
                    Tapdaq.getInstance().showInterstitial(activity, getPlacementTag(), new AdListener(type));
                    Log.i("MEDIATION-SAMPLE", "Show Interstitial");

                } else {
                    Log.i("MEDIATION-SAMPLE", "Interstitial ad not available, call Load first");
                }
                break;
            }
            case TMAdType.VIDEO_INTERSTITIAL:
            {
                if (Tapdaq.getInstance().isVideoReady(activity, getPlacementTag())) {
                    Tapdaq.getInstance().showVideo(activity, getPlacementTag(), new AdListener(type));
                } else {
                    Log.i("MEDIATION-SAMPLE", "Video ad not available, call Load first");
                }
                break;
            }
            case TMAdType.REWARD_INTERSTITIAL:
            {
                if (Tapdaq.getInstance().isRewardedVideoReady(activity, getPlacementTag())) {
                    Tapdaq.getInstance().showRewardedVideo(activity, getPlacementTag(), new AdListener(type));
                } else {
                    Log.i("MEDIATION-SAMPLE", "Rewarded ad not available, call Load first");
                }
                break;
            }
            default:
                break;
        }
    }


    public static void LoadInterstitial(int type) {
        switch (type) {
            case TMAdType.STATIC_INTERSTITIAL:
                Tapdaq.getInstance().loadInterstitial(activity, getPlacementTag(), new AdListener(type));
                Log.i("MEDIATION-SAMPLE", "Load Interstitial");

                break;
            case TMAdType.VIDEO_INTERSTITIAL:
                Tapdaq.getInstance().loadVideo(activity, getPlacementTag(), new AdListener(type));
                break;
            case TMAdType.REWARD_INTERSTITIAL:
                Tapdaq.getInstance().loadRewardedVideo(activity, getPlacementTag(), new AdListener(type));
                break;
            default:
                break;
        }
    }


    public static class AdListener extends TMAdListener {
        private int mType;
        public AdListener(int type) {
            mType = type;
        }

        @Override
        public void didLoad() {
            //call below method if you want to show interstitial in First Activity
            if(mType == TMAdType.BANNER){
                Log.i("MEDIATION-SAMPLE", "Banner Ad Loaded" + TMAdType.getString(mType));
            } else {
                ReadyInterstitialAd(mType);
                Log.i("MEDIATION-SAMPLE", "Ad Loaded - Now you can show it by calling  showInterstitialAd()" + TMAdType.getString(mType));

            }
        }

        @Override
        public void didFailToLoad(TMAdError error) {
            Log.i("MEDIATION-SAMPLE", "Ad Loaded failed" + TMAdType.getString(mType));

            try {
                Class<?> c = Class.forName(nextActivity);
                Intent intent = new Intent(activity, c);
                activity.startActivity(intent);
            } catch (ClassNotFoundException ignored) {
            }
            String str = String.format(Locale.ENGLISH, "didFailToLoad %s: %d - %s", TMAdType.getString(mType), error.getErrorCode(), error.getErrorMessage());

            for (String key : error.getSubErrors().keySet()) {
                for (TMAdError value : error.getSubErrors().get(key)) {
                    String subError = String.format(Locale.ENGLISH, "%s - %d: %s", key,  value.getErrorCode(), value.getErrorMessage());
                    str = str.concat("\n ");
                    str = str.concat(subError);
                }
            }
        }

        @Override
        public void didRefresh() {
            Log.i("MEDIATION-SAMPLE", "didRefresh " + TMAdType.getString(mType));
        }

        @Override
        public void willDisplay() {
            Log.i("MEDIATION-SAMPLE", "willDisplay " + TMAdType.getString(mType));
        }

        @Override
        public void didDisplay() {
            Log.i("MEDIATION-SAMPLE", "didDisplay " + TMAdType.getString(mType));

        }

        @Override
        public void didClick() {
            try {
                Class<?> c = Class.forName(nextActivity);
                Intent intent = new Intent(activity, c);
                activity.startActivity(intent);
            } catch (ClassNotFoundException ignored) {
            }
            Log.i("MEDIATION-SAMPLE", "didClick " + TMAdType.getString(mType));
        }

        @Override
        public void didVerify(TDReward reward) {
            String str = String.format(Locale.ENGLISH, "didVerify %s: Reward name: %s. Value: %d. Valid: %b. Custom Json: %s", TMAdType.getString(mType), reward.getName(), reward.getValue(), reward.isValid(), reward.getCustom_json().toString());
            Log.i("MEDIATION-SAMPLE", str);
        }

        @Override
        public void didClose() {

            try {
                Class<?> c = Class.forName(nextActivity);
                Intent intent = new Intent(activity, c);
                activity.startActivity(intent);
            } catch (ClassNotFoundException ignored) {
            }
            Log.i("MEDIATION-SAMPLE", "Ad Closed " + TMAdType.getString(mType));
            Log.i("MEDIATION-SAMPLE", "didClose " + TMAdType.getString(mType));
        }
    }
}
