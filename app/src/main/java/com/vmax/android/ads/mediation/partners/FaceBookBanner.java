package com.vmax.android.ads.mediation.partners;


import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.vmax.android.ads.api.VmaxAdPartner;
import com.vmax.android.ads.mediation.partners.VmaxCustomAd;
import com.vmax.android.ads.mediation.partners.VmaxCustomAdListener;
import com.vmax.android.ads.util.Constants;

import java.util.Map;

/*
 * Tested with facebook SDK 4.16.1
 */
public class FaceBookBanner extends VmaxCustomAd implements AdListener {
    private static final String PLACEMENT_ID_KEY = "placementid";

    private AdView mFacebookBanner;
    private VmaxCustomAdListener mBannerListener;
    public boolean LOGS_ENABLED = true;
    private boolean isFirstAd = false;
    private VmaxAdPartner vmaxAdPartner;

    @Override
    public void loadAd(final Context context,
                       final VmaxCustomAdListener customEventBannerListener,
                       final Map<String, Object> localExtras,
                       final Map<String, Object> serverExtras) {
        try {

            Log.d("vmax", "Inside FacebookBanner loadAd ");
            mBannerListener = customEventBannerListener;

            final String placementId;
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                if(mBannerListener!=null)
                 mBannerListener.onAdFailed(Constants.AdError.ERROR_MANDATORY_PARAM_MISSING,"FaceBookBanner Mandatory parameters missing");
                return;
            }
            if (localExtras != null) {

                if (localExtras.containsKey("vmaxAdPartner"))
                {
                    vmaxAdPartner = (VmaxAdPartner)localExtras.get("vmaxAdPartner");
                    Log.d("vmax","VmaxAdPartnerName "+ "FaceBook");
                    vmaxAdPartner.setPartnerName("FaceBook");
                    Log.d("vmax","VmaxAdPartnerSDKVersion "+ "4.16.1");
                    vmaxAdPartner.setPartnerSDKVersion("4.16.1");
                }

                if (localExtras.containsKey("test")) {

                    String[] mTestAvdIds = (String[]) localExtras
                            .get("test");
                    if (mTestAvdIds != null) {
                        for (int i = 0; i < mTestAvdIds.length; i++) {
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "test devices: "
                                                + mTestAvdIds[i]);
                            }
                            AdSettings.addTestDevice(mTestAvdIds[i]);
                            if (LOGS_ENABLED) {
                                Log.i("vmax",
                                        "Test mode: "
                                                + AdSettings.isTestMode(context));
                            }

                        }
                    }
                }

                String isInlineDisplay="";
                if(localExtras.containsKey("isInlineDisplay")){
                    isInlineDisplay=localExtras.get("isInlineDisplay").toString();
                }

                if(isInlineDisplay.equalsIgnoreCase("true")){
                    mFacebookBanner = new AdView(context, placementId,
                            AdSize.RECTANGLE_HEIGHT_250);
                }
                else{
                    if(isTablet(context)){
                        mFacebookBanner = new AdView(context, placementId,
                                AdSize.BANNER_HEIGHT_90);
                    }
                    else{
                        mFacebookBanner = new AdView(context, placementId,
                                AdSize.BANNER_HEIGHT_50);
                    }

                }
            }
            AdSettings.setMediationService("VMAX");
            mFacebookBanner.setAdListener(this);
            mFacebookBanner.loadAd();
        } catch (Exception e) {
            if (mBannerListener != null) {
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"FaceBookBanner "+e.getMessage());
            }
            e.printStackTrace();
            return;
        }
    }

    public boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onInvalidate() {
        try {
            if (LOGS_ENABLED) {
                Log.d("vmax", "onInvalidate.");
            }
            if (mFacebookBanner != null) {
                if (LOGS_ENABLED) {
                    Log.d("vmax", "Facebook banner ad onInvalidate.");
                }
                mFacebookBanner.removeAllViews();
                mFacebookBanner.setAdListener(null);
                mFacebookBanner.destroy();
                mFacebookBanner = null;
                isFirstAd = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showAd() {

    }

    @Override
    public void onAdLoaded(Ad ad) {

        if (!isFirstAd) {
            isFirstAd = true;
            if (LOGS_ENABLED) {
                Log.d("vmax",
                        "Facebook banner ad loaded successfully. Showing ad...");

            }
            mBannerListener.onAdLoaded(mFacebookBanner);
        } else {
            if (LOGS_ENABLED) {
                Log.i("vmax",
                        "Recommended: Make sure you switch OFF refresh of Facebook from thier dashboard.");

            }
            mFacebookBanner.removeAllViews();
            try {
                if (mFacebookBanner != null) {
                    mFacebookBanner.setAdListener(null);
                    mFacebookBanner.destroy();
                    mFacebookBanner = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final Ad ad, final AdError error) {
        if (LOGS_ENABLED) {
            Log.d("vmax",
                    "Facebook banner ad failed to load. error: "
                            + error.getErrorCode());
        }
        if(mBannerListener!=null) {
            if (error.getErrorCode() == 1000)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_NETWORK_ERROR, "FaceBookBanner " + error.getErrorMessage());
            else if (error.getErrorCode() == 1001)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_NOFILL, "FaceBookBanner " + error.getErrorMessage());
            else if (error.getErrorCode() == 1002)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED, "FaceBookBanner " + error.getErrorMessage());
            else if (error.getErrorCode() == 2000)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_INTERNAL_SERVER, "FaceBookBanner " + error.getErrorMessage());
            else if (error.getErrorCode() == 2001)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookBanner " + error.getErrorMessage());
            else if (error.getErrorCode() == 3001)
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookBanner " + error.getErrorMessage());
            else
                mBannerListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookBanner " + error.getErrorMessage());
        }
    }

    @Override
    public void onAdClicked(Ad ad) {
        if (LOGS_ENABLED) {
            Log.d("vmax", "Facebook banner ad clicked.");
        }
        mBannerListener.onAdClicked();
    }

    private boolean extrasAreValid(final Map<String, Object> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY)
                .toString();
        return (placementId != null && placementId.length() > 0);
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

        if (mFacebookBanner != null) {
            if (LOGS_ENABLED) {
                Log.d("vmax", "Facebook banner ad onDestroy.");
            }
            mFacebookBanner.removeAllViews();
            mFacebookBanner.destroy();

            mFacebookBanner = null;
        }
    }

}
