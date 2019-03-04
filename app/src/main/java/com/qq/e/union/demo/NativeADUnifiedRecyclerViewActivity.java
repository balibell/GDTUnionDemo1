package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class NativeADUnifiedRecyclerViewActivity extends Activity
    implements NativeADUnifiedListener {

  private static final String TAG = NativeADUnifiedRecyclerViewActivity.class.getSimpleName();
  private AQuery mAQuery;

  private NativeUnifiedAD mAdManager;
  private List<NativeUnifiedADData> mAds;

  private CustomAdapter mAdapter;

  private H mHandler = new H();

  private static final int AD_COUNT = 3;
  private static final int ITEM_COUNT = 50;
  private static final int FIRST_AD_POSITION = 5;
  private static final int AD_DISTANCE = 10;
  private static final int MSG_REFRESH_LIST = 1;

  private static final int TYPE_DATA = 0;
  private static final int TYPE_AD = 1;

  private int mPlayNetwork = NativeADUnifiedActivity.PLAY_NETWORK_WIFI;
  private int mPlayMute = NativeADUnifiedActivity.PLAY_SOUND_MUTE;
  private boolean mNoneOption = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_unified_ad_recyclerview);
    initView();

    mNoneOption = getIntent().getBooleanExtra(NativeADUnifiedActivity.NONE_OPTION, false);
    mPlayMute = getIntent().getIntExtra(NativeADUnifiedActivity.PLAY_MUTE,
        NativeADUnifiedActivity.PLAY_NETWORK_WIFI);
    mPlayNetwork = getIntent().getIntExtra(NativeADUnifiedActivity.PLAY_NETWORK,
        NativeADUnifiedActivity.PLAY_SOUND_MUTE);
    mAdManager = new NativeUnifiedAD(this, Constants.APPID, getPosId(), this);
    mAdManager.loadData(AD_COUNT);
  }

  private String getPosId() {
    return getIntent().getStringExtra(NativeADUnifiedActivity.POS_ID);
  }


  private void initView() {
    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    LinearLayoutManager manager = new LinearLayoutManager(this);
    manager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView.setLayoutManager(manager);
    List<NormalItem> list = new ArrayList<>();
    for (int i = 0; i < ITEM_COUNT; ++i) {
      list.add(new NormalItem("No." + i + " Normal Data"));
    }
    mAdapter = new CustomAdapter(this, list);
    recyclerView.setAdapter(mAdapter);
    mAQuery = new AQuery(this);
  }

  @Override
  public void onADLoaded(List<NativeUnifiedADData> ads) {
    mAds = ads;
    mHandler.sendEmptyMessage(MSG_REFRESH_LIST);
  }

  @Override
  public void onNoAD(AdError error) {
    Log.d(TAG, "onNoAd error code: " + error.getErrorCode()
        + ", error msg: " + error.getErrorMsg());
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mAds != null) {
      for (NativeUnifiedADData ad : mAds) {
        ad.resume();
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mAds != null) {
      for (NativeUnifiedADData ad : mAds) {
        ad.destroy();
      }
    }
    mAds = null;
  }

  class CustomAdapter extends RecyclerView.Adapter<CustomHolder> {

    private List<Object> mData;
    private Context mContext;
    private TreeSet mADSet = new TreeSet();

    public CustomAdapter(Context context, List list) {
      mContext = context;
      mData = list;
    }

    public void addAdToPosition(NativeUnifiedADData nativeUnifiedADData, int position) {
      if (position >= 0 && position < mData.size()) {
        mData.add(position, nativeUnifiedADData);
        mADSet.add(position);
      }
    }

    @Override
    public int getItemViewType(int position) {
      return mADSet.contains(position) ? TYPE_AD : TYPE_DATA;
    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View view;
      switch (viewType) {
        case TYPE_AD:
          view = LayoutInflater.from(mContext).inflate(R.layout.item_ad_unified, null);
          break;

        case TYPE_DATA:
          view = LayoutInflater.from(mContext).inflate(R.layout.item_data, null);
          break;

        default:
            view = null;
      }
      return new CustomHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(CustomHolder holder, int position) {
      switch (getItemViewType(position)) {
        case TYPE_AD:
          initItemView(position, holder);
          break;
        case TYPE_DATA:
          holder.title.setText(((NormalItem) mData.get(position))
              .getTitle());
          break;
      }
    }

    private void initItemView(int position, final CustomHolder holder) {
      final NativeUnifiedADData ad = (NativeUnifiedADData) mData.get(position);
      AQuery logoAQ = holder.logoAQ;
      logoAQ.id(R.id.img_logo).image(
          TextUtils.isEmpty(ad.getIconUrl()) ? ad.getImgUrl() : ad.getIconUrl(), false, true);
      holder.name.setText(ad.getTitle());
      holder.desc.setText(ad.getDesc());
      List<View> clickableViews = new ArrayList<>();
      clickableViews.add(holder.download);
      // 视频广告
      if (ad.getAdPatternType() == 2) {
        holder.poster.setVisibility(View.INVISIBLE);
        holder.mediaView.setVisibility(View.VISIBLE);
      } else {
        holder.poster.setVisibility(View.VISIBLE);
        holder.mediaView.setVisibility(View.INVISIBLE);
      }
      ad.bindAdToView(NativeADUnifiedRecyclerViewActivity.this, holder.container, null,
          clickableViews);
      logoAQ.id(R.id.img_poster).image(ad.getImgUrl(), false, true, 0, 0,
          new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
              if (iv.getVisibility() == View.VISIBLE) {
                iv.setImageBitmap(bm);
              }
            }
          });

      setAdListener(holder, ad);

      NativeADUnifiedSampleActivity.updateAdAction(holder.download, ad);
    }

    private void setAdListener(final CustomHolder holder, final NativeUnifiedADData ad) {
      ad.setNativeAdEventListener(new NativeADEventListener() {
        @Override
        public void onADExposed() {
          Log.d(TAG, "onADExposed: " + ad.getTitle());
        }

        @Override
        public void onADClicked() {
          Log.d(TAG, "onADClicked: " + ad.getTitle());
        }

        @Override
        public void onADError(AdError error) {
          Log.d(TAG, "onADError error code :" + error.getErrorCode()
              + "  error msg: " + error.getErrorMsg());
        }

        @Override
        public void onADStatusChanged() {
          NativeADUnifiedSampleActivity.updateAdAction(holder.download, ad);
        }
      });
      // 视频广告
      if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
        VideoOption videoOption =
            NativeADUnifiedSampleActivity.getVideoOption(mNoneOption, mPlayNetwork, mPlayMute);
        ad.bindMediaView(holder.mediaView, videoOption, new NativeADMediaListener() {
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
    }

    @Override
    public int getItemCount() {
      return mData.size();
    }
  }

  class CustomHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public MediaView mediaView;
    public RelativeLayout adInfoContainer;
    public TextView name;
    public TextView desc;
    public ImageView logo;
    public ImageView poster;
    public Button download;
    public NativeAdContainer container;
    public AQuery logoAQ;

    public CustomHolder(View itemView, int adType) {
      super(itemView);
      switch (adType) {
        case TYPE_AD:
          mediaView = itemView.findViewById(R.id.gdt_media_view);
          adInfoContainer = itemView.findViewById(R.id.ad_info);
          logo = itemView.findViewById(R.id.img_logo);
          poster = itemView.findViewById(R.id.img_poster);
          name = itemView.findViewById(R.id.text_title);
          desc = itemView.findViewById(R.id.text_desc);
          download = itemView.findViewById(R.id.btn_download);
          container = itemView.findViewById(R.id.native_ad_container);
          logoAQ = new AQuery(itemView);

        case TYPE_DATA:
          title = itemView.findViewById(R.id.title);
          break;

      }
    }
  }

  class NormalItem {
    private String mTitle;

    public NormalItem(String title) {
      this.mTitle = title;
    }

    public String getTitle() {
      return mTitle;
    }

  }

  private class H extends Handler {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case MSG_REFRESH_LIST:
          if (mAds != null && mAds.size() > 0 && mAdapter != null) {
            for (int i = 0; i < mAds.size(); i++) {
              mAdapter.addAdToPosition(mAds.get(i), i * AD_DISTANCE + FIRST_AD_POSITION);
            }
          }
          mAdapter.notifyDataSetChanged();
          break;

        default:
      }
    }
  }
}
