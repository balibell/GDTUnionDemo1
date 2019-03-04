package com.qq.e.union.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;

public class NativeADUnifiedSampleActivity extends Activity implements NativeADUnifiedListener {

  private AQuery mAQuery;
  private Button mDownloadButton;
  private RelativeLayout mADInfoContainer;
  private NativeUnifiedADData mAdData;
  private H mHandler = new H();
  private static final int MSG_INIT_AD = 0;
  private static final int MSG_VIDEO_START = 1;
  private static final int AD_COUNT = 1;
  private static final String TAG = NativeADUnifiedSampleActivity.class.getSimpleName();


  // 与广告有关的变量，用来显示广告素材的UI
  private NativeUnifiedAD mAdManager;
  private MediaView mMediaView;
  private ImageView mImagePoster;
  private NativeAdContainer mContainer;

  private int mPlayNetwork = NativeADUnifiedActivity.PLAY_NETWORK_WIFI;
  private int mPlayMute = NativeADUnifiedActivity.PLAY_SOUND_MUTE;
  private boolean mNoneOption = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_unified_ad_simple);
    initView();

    mNoneOption = getIntent().getBooleanExtra(NativeADUnifiedActivity.NONE_OPTION, false);
    mPlayMute = getIntent().getIntExtra(NativeADUnifiedActivity.PLAY_MUTE,
        NativeADUnifiedActivity.PLAY_NETWORK_WIFI);
    mPlayNetwork = getIntent().getIntExtra(NativeADUnifiedActivity.PLAY_NETWORK,
        NativeADUnifiedActivity.PLAY_SOUND_MUTE);
    Log.d(TAG, "onCreate: mNoneOption:" + mNoneOption + " mPlayMute:" +mPlayMute
        + " mPlayNetwork:" + mPlayNetwork);

    mAdManager = new NativeUnifiedAD(this, Constants.APPID, getPosId(), this);
    mAdManager.loadData(AD_COUNT);
  }

  private void initView() {
    mMediaView = findViewById(R.id.gdt_media_view);
    mImagePoster = findViewById(R.id.img_poster);
    mADInfoContainer = findViewById(R.id.ad_info_container);
    mDownloadButton = findViewById(R.id.btn_download);
    mContainer = findViewById(R.id.native_ad_container);
    mAQuery = new AQuery(findViewById(R.id.root));
  }

  private String getPosId() {
    return getIntent().getStringExtra(NativeADUnifiedActivity.POS_ID);
  }

  @Override
  public void onADLoaded(List<NativeUnifiedADData> ads) {
    if (ads != null && ads.size() > 0) {
      Message msg = Message.obtain();
      msg.what = MSG_INIT_AD;
      mAdData = ads.get(0);
      msg.obj = mAdData;
      mHandler.sendMessage(msg);
    }
  }

  private void initAd(final NativeUnifiedADData ad) {
    renderAdUi(ad);

    List<View> clickableViews = new ArrayList<>();
    clickableViews.add(mDownloadButton);
    ad.bindAdToView(this, mContainer, null, clickableViews);
    ad.setNativeAdEventListener(new NativeADEventListener() {
      @Override
      public void onADExposed() {
        Log.d(TAG, "onADExposed: ");
      }

      @Override
      public void onADClicked() {
        Log.d(TAG, "onADClicked: ");
      }

      @Override
      public void onADError(AdError error) {
        Log.d(TAG, "onADError error code :" + error.getErrorCode()
            + "  error msg: " + error.getErrorMsg());
      }

      @Override
      public void onADStatusChanged() {
        Log.d(TAG, "onADStatusChanged: ");
        updateAdAction(mDownloadButton, ad);
      }
    });

    if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
      mHandler.sendEmptyMessage(MSG_VIDEO_START);
      VideoOption videoOption = getVideoOption(mNoneOption, mPlayNetwork, mPlayMute);

      ad.bindMediaView(mMediaView, videoOption, new NativeADMediaListener() {
            @Override
            public void onVideoInit() {
              Log.d(TAG, "onVideoInit: ");
            }

            @Override
            public void onVideoLoading() {
              Log.d(TAG, "onVideoLoading: ");
            }

            @Override
            public void onVideoReady() {
              Log.d(TAG, "onVideoReady: ");
            }

            @Override
            public void onVideoLoaded(int videoDuration) {
              Log.d(TAG, "onVideoLoaded: ");

            }

            @Override
            public void onVideoStart() {
              Log.d(TAG, "onVideoStart: ");
            }

            @Override
            public void onVideoPause() {
              Log.d(TAG, "onVideoPause: ");
            }

            @Override
            public void onVideoResume() {
              Log.d(TAG, "onVideoResume: ");
            }

            @Override
            public void onVideoCompleted() {
              Log.d(TAG, "onVideoCompleted: ");
            }

            @Override
            public void onVideoError(AdError error) {
              Log.d(TAG, "onVideoError: ");
            }
          });
    }

    updateAdAction(mDownloadButton, ad);
  }

  @Nullable
  public static VideoOption getVideoOption(boolean mNoneOption, int mPlayNetwork, int mPlayMute) {
    VideoOption videoOption = null;
    if (!mNoneOption) {
      VideoOption.Builder builder = new VideoOption.Builder();
      if (mPlayNetwork == NativeADUnifiedActivity.PLAY_NETWORK_WIFI) {
        builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI);
      } else if (mPlayNetwork == NativeADUnifiedActivity.PLAY_NETWORK_ALWAYS) {
        builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
      }
      if (mPlayMute == NativeADUnifiedActivity.PLAY_SOUND_MUTE) {
        builder.setAutoPlayMuted(true);
      } else if (mPlayMute == NativeADUnifiedActivity.PLAY_SOUND_NOT_MUTE) {
        builder.setAutoPlayMuted(false);
      }
      videoOption = builder.build();
    }
    return videoOption;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mAdData != null) {
      // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
      mAdData.resume();
    }
  }

  private void renderAdUi(NativeUnifiedADData ad) {
    int patternType = ad.getAdPatternType();
    if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
        || patternType == AdPatternType.NATIVE_VIDEO) {
      mAQuery.id(R.id.img_logo).image(ad.getIconUrl(), false, true);
      mAQuery.id(R.id.img_poster).image(ad.getImgUrl(), false, true, 0, 0,
          new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
              if (iv.getVisibility() == View.VISIBLE) {
                iv.setImageBitmap(bm);
              }
            }
          });
      mAQuery.id(R.id.text_title).text(ad.getTitle());
      mAQuery.id(R.id.text_desc).text(ad.getDesc());
    } else if (patternType == AdPatternType.NATIVE_3IMAGE) {
      mAQuery.id(R.id.img_1).image(ad.getImgList().get(0), false, true);
      mAQuery.id(R.id.img_2).image(ad.getImgList().get(1), false, true);
      mAQuery.id(R.id.img_3).image(ad.getImgList().get(2), false, true);
      mAQuery.id(R.id.native_3img_title).text(ad.getTitle());
      mAQuery.id(R.id.native_3img_desc).text(ad.getDesc());
    } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
      mAQuery.id(R.id.img_logo).image(ad.getImgUrl(), false, true);
      mAQuery.id(R.id.img_poster).clear();
      mAQuery.id(R.id.text_title).text(ad.getTitle());
      mAQuery.id(R.id.text_desc).text(ad.getDesc());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mAdData != null) {
      // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
      mAdData.destroy();
    }
  }

  public static void updateAdAction(Button button, NativeUnifiedADData ad) {
    if (!ad.isAppAd()) {
      button.setText("浏览");
      return;
    }
    switch (ad.getAppStatus()) {
      case 0:
        button.setText("下载");
        break;
      case 1:
        button.setText("启动");
        break;
      case 2:
        button.setText("更新");
        break;
      case 4:
        button.setText(ad.getProgress() + "%");
        break;
      case 8:
        button.setText("安装");
        break;
      case 16:
        button.setText("下载失败，重新下载");
        break;
      default:
        button.setText("浏览");
        break;
    }
  }

  @Override
  public void onNoAD(AdError error) {
    Log.d(TAG, "onNoAd error code: " + error.getErrorCode()
        + ", error msg: " + error.getErrorMsg());
  }

  private class H extends Handler {
    public H() {
      super();
    }

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_INIT_AD:
          NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
          initAd(ad);
          break;
        case MSG_VIDEO_START:
          mImagePoster.setVisibility(View.GONE);
          mMediaView.setVisibility(View.VISIBLE);
          break;
      }
    }
  }
}
