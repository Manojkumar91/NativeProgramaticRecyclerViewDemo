package com.vmax.android.ads.mediation.partners;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.MediaView;
import com.facebook.ads.MediaViewListener;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAd.Rating;
import com.vmax.android.ads.api.VmaxAdPartner;
import com.vmax.android.ads.api.VmaxAdView;
import com.vmax.android.ads.nativeads.NativeAdConstants;
import com.vmax.android.ads.util.Constants;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/*
 * Tested with facebook SDK 4.25.0
 */
public class FaceBookNative extends VmaxCustomAd implements AdListener {

    private NativeAd nativeAd;
    private static final String PLACEMENT_ID_KEY = "placementid";
    private VmaxCustomNativeAdListener mNativeAdListener;
    public boolean LOGS_ENABLED = true;
    private VmaxCustomAdListener vmaxCustomAdListener;
    private Context context;
    private VmaxAdPartner vmaxAdPartner;
    private boolean autoPlayMode = true;
    private VmaxAdView vmaxAdView;
    @Override
    public void loadAd(Context context,
                       VmaxCustomAdListener vmaxCustomAdListener,
                       Map<String, Object> localExtras, Map<String, Object> serverExtras) {

        try {
            this.context = context;
            this.vmaxCustomAdListener = vmaxCustomAdListener;
            final String placementId;
            if (localExtras != null) {
                if (localExtras.containsKey("adview")) {
                    vmaxAdView = (VmaxAdView) localExtras.get("adview");
                }

                if (localExtras.containsKey("vmaxAdPartner"))
                {
                    vmaxAdPartner = (VmaxAdPartner)localExtras.get("vmaxAdPartner");
                    Log.d("vmax","VmaxAdPartnerName "+ "FaceBook");
                    vmaxAdPartner.setPartnerName("FaceBook");
                    Log.d("vmax","VmaxAdPartnerSDKVersion "+ "4.25.0");
                    vmaxAdPartner.setPartnerSDKVersion("4.25.0");
                }

                if (localExtras.containsKey("nativeListener")) {
                    if (LOGS_ENABLED) {
                        Log.i("Log", "nativeListener in localextras ");
                    }
                    mNativeAdListener = (VmaxCustomNativeAdListener) localExtras.get("nativeListener");


                }
            }
            if (extrasAreValid(serverExtras)) {
                placementId = serverExtras.get(PLACEMENT_ID_KEY).toString();
            } else {
                if (mNativeAdListener != null) {
                    mNativeAdListener.onAdFailed(Constants.AdError.ERROR_MANDATORY_PARAM_MISSING,"FaceBookNative Mandatory parameters missing");
                }
                return;
            }

            if (localExtras != null) {
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

                if(localExtras.containsKey("isAutoPlayEnabled"))
                {
                    autoPlayMode = (boolean) localExtras.get("isAutoPlayEnabled");
                }
            }

            nativeAd = new NativeAd(context, placementId);
            AdSettings.setMediationService("VMAX");
            nativeAd.setAdListener(this);
            nativeAd.loadAd(NativeAd.MediaCacheFlag.ALL);
        } catch (Exception e) {
            if (mNativeAdListener != null) {
                mNativeAdListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN,"FaceBookNative "+e.getMessage());
            }
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onAdClicked(Ad arg0) {
        Log.i("vmax", "fb onAdClicked");
        if (vmaxCustomAdListener != null) {
            vmaxCustomAdListener.onAdClicked();
        }
    }

    @Override
    public void onLoggingImpression(Ad ad) {
        Log.d("vmax","facebook: onLoggingImpression()");
        if(vmaxCustomAdListener!=null){
            vmaxCustomAdListener.logMediationImpression();
        }
    }


    @Override
    public void onAdLoaded(Ad ad) {
        try {
            String adChoiceIcon = null, adChoiceURl = null;
            int adChoiceIconHeight = 0;
            int adChoiceIconWidth = 0;
            String coverImageURL = null;
            int coverImageHeight = 0;
            int coverImageWidth = 0;
            String iconForAd = null;
            int iconAdWidth = 0;
            int iconAdHeight = 0;

            if (ad != nativeAd) {
                return;
            }
            nativeAd.unregisterView();
            String titleForAd = nativeAd.getAdTitle();
            if (nativeAd.getAdCoverImage() != null) {
                coverImageURL = nativeAd.getAdCoverImage().getUrl();
                coverImageHeight = nativeAd.getAdCoverImage().getHeight();
                coverImageWidth = nativeAd.getAdCoverImage().getWidth();
            }
            if (nativeAd.getAdIcon() != null) {
                iconForAd = nativeAd.getAdIcon().getUrl();
                iconAdHeight = nativeAd.getAdIcon().getHeight();
                iconAdWidth = nativeAd.getAdIcon().getWidth();
            }
            String socialContextForAd = nativeAd.getAdSocialContext();
            String titleForAdButton = nativeAd.getAdCallToAction();
            String textForAdBody = nativeAd.getAdBody();
            if (nativeAd.getAdChoicesIcon() != null) {
                adChoiceIcon = nativeAd.getAdChoicesIcon().getUrl();
                adChoiceIconHeight = nativeAd.getAdChoicesIcon().getWidth();
                adChoiceIconWidth = nativeAd.getAdChoicesIcon().getHeight();
            }
            if (nativeAd.getAdChoicesLinkUrl() != null) {
                adChoiceURl = nativeAd.getAdChoicesLinkUrl();
            }


            MediaView nativeMediaView = new MediaView(context);
            Log.d("vmax","auto play=="+autoPlayMode);
            nativeAd.setMediaViewAutoplay(autoPlayMode);
            nativeMediaView.setAutoplay(autoPlayMode);
            nativeMediaView.setAutoplayOnMobile(autoPlayMode);
            nativeMediaView.setNativeAd(nativeAd);

            /**Added MediaView Listener */
            nativeMediaView.setListener(new MediaViewListener() {
                @Override
                public void onVolumeChange(MediaView mediaView, float v) {

                }

                @Override
                public void onPause(MediaView mediaView) {

                }

                @Override
                public void onPlay(MediaView mediaView) {
                    Log.d("vmax","mediaView:onPlay()");
                    if(vmaxAdView!=null) {
                        vmaxAdView.updateRefreshFlagForNativeMediationVideo(false);
                    }
                    vmaxCustomAdListener.onAdMediaStart();
                }

                @Override
                public void onFullscreenBackground(MediaView mediaView) {
                }

                @Override
                public void onFullscreenForeground(MediaView mediaView) {
                }

                @Override
                public void onExitFullscreen(MediaView mediaView) {
                    vmaxCustomAdListener.onAdCollapsed();
                }

                @Override
                public void onEnterFullscreen(MediaView mediaView) {
                    vmaxCustomAdListener.onAdExpand();
                }

                @Override
                public void onComplete(MediaView mediaView) {
                    Log.d("vmax","Facebook:onComplete()");
                    if(vmaxAdView!=null) {
                        vmaxAdView.updateRefreshFlagForNativeMediationVideo(true);
                    }
                    vmaxCustomAdListener.onVideoAdEnd(true);
                }
            });

            AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);


            String appRatingForAd = "";
            Double rating = getDoubleRating(nativeAd.getAdStarRating());
            Log.d("vmax", "getAdStarRating : " + nativeAd.getAdStarRating());
            if (rating != null) {
                appRatingForAd = Double.toString(rating);
            }
            if (LOGS_ENABLED) {
                Log.d("vmax", "Title for Ad : " + titleForAd);
                Log.d("vmax", "coverImage URL : " + coverImageURL);
                Log.d("vmax", "socialContextForAd : " + socialContextForAd);
                Log.d("vmax", "titleForAdButton : " + titleForAdButton);
                Log.d("vmax", "textForAdBody : " + textForAdBody);
                Log.d("vmax", "appRatingForAd : " + appRatingForAd);
                Log.d("vmax", "iconForAd : " + iconForAd);
            }

            JSONObject fbJSON = new JSONObject();
            try {
                fbJSON.put(NativeAdConstants.NativeAd_TITLE, titleForAd);
                fbJSON.put(NativeAdConstants.NativeAd_CTA_TEXT, titleForAdButton);
                fbJSON.put(NativeAdConstants.NativeAd_RATING, appRatingForAd);
                fbJSON.put(NativeAdConstants.NativeAd_DESC, textForAdBody);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ICON, iconForAd);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ICON_WIDTH, "" + iconAdWidth);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ICON_HEIGHT, "" + iconAdHeight);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MAIN, coverImageURL);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MAIN_WIDTH, "" + coverImageWidth);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MAIN_HEIGHT, "" + coverImageHeight);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MEDIUM, coverImageURL);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MEDIUM_WIDTH, "" + coverImageWidth);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_MEDIUM_HEIGHT, "" + coverImageHeight);
                fbJSON.put(NativeAdConstants.NativeAd_MEDIA_VIEW, nativeMediaView);
                fbJSON.put(NativeAdConstants.NativeAd_ADCHOICE_VIEW, adChoicesView);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ADCHOICEICON, adChoiceIcon);
                fbJSON.put(NativeAdConstants.NativeAd_AD_CHOICCE_URL, adChoiceURl);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ADCHOICEICON_WIDTH, "" + adChoiceIconWidth);
                fbJSON.put(NativeAdConstants.NativeAd_IMAGE_ADCHOICEICON_HEIGHT, "" + adChoiceIconHeight);
                fbJSON.put(NativeAdConstants.NativeAd_TYPE, Constants.NativeAdType.VMAX_FACEBOOK_MEDIA);


                Object[] objArray = new Object[]{fbJSON};
                if (mNativeAdListener != null) {
                    mNativeAdListener.onAdLoaded(objArray);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onError(final Ad ad, final AdError error) {
        try {
            if (error != null) {
                if (LOGS_ENABLED) {
                    Log.d("vmax", "Facebook native ad failed to load. error: " + error.getErrorCode());
                }
                if (mNativeAdListener != null) {
                    if(error.getErrorCode() == 1000)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_NETWORK_ERROR, "FaceBookNative "+error.getErrorMessage());
                    else if (error.getErrorCode() == 1001)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_NOFILL, "FaceBookNative "+error.getErrorMessage());
                    else if (error.getErrorCode() == 1002)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_ADREQUEST_NOT_ALLOWED, "FaceBookNative "+error.getErrorMessage());
                    else if (error.getErrorCode() == 2000)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_INTERNAL_SERVER, "FaceBookNative "+error.getErrorMessage());
                    else if (error.getErrorCode() == 2001)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookNative "+error.getErrorMessage());
                    else if (error.getErrorCode() == 3001)
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookNative "+error.getErrorMessage());
                    else
                        mNativeAdListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookNative "+error.getErrorMessage());
                }
            }
            else {
                if (mNativeAdListener != null) {
                    mNativeAdListener.onAdFailed(Constants.AdError.ERROR_UNKNOWN, "FaceBookNative Unknown error");
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void showAd() {
    }

    @Override
    public void onInvalidate() {
        if (LOGS_ENABLED) {
            Log.i("vmax", "onInvalidate fb native : ");
        }
        try {
            if (nativeAd != null) {
                nativeAd.unregisterView();
                nativeAd.setAdListener(null);
                nativeAd.destroy();
                nativeAd = null;
                if (LOGS_ENABLED) {
                    Log.i("vmax", "onInvalidate fb native clear : ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean extrasAreValid(final Map<String, Object> serverExtras) {
        final String placementId = serverExtras.get(PLACEMENT_ID_KEY)
                .toString();
        return (placementId != null && placementId.length() > 0);
    }


    public void handleImpression(ViewGroup viewgroup, View view, List<View> listOfView) {
        try {
            if (LOGS_ENABLED) {
                Log.i("vmax", "handleImpressions fb: ");
            }

            if (nativeAd != null) {
                if (listOfView != null) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax", " registerViewForInteraction with list of views: " + listOfView.size());
                    }
                    nativeAd.registerViewForInteraction(view, listOfView);
                } else if (view != null) {
                    if (LOGS_ENABLED) {
                        Log.i("vmax", " registerViewForInteraction with only view: ");
                    }
                    nativeAd.registerViewForInteraction(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Double getDoubleRating(final Rating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getValue() / rating.getScale();
    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

    }

}
