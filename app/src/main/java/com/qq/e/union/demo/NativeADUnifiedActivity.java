package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;


public class NativeADUnifiedActivity extends Activity {

  private Spinner mPlayNetworkSpinner;
  private Spinner mPlayMuteSpinner;
  private CheckBox mVideoOptionCheckBox;

  public static final int PLAY_NETWORK_WIFI = 0;
  public static final int PLAY_NETWORK_ALWAYS = 1;
  public static final int PLAY_SOUND_MUTE = 0;
  public static final int PLAY_SOUND_NOT_MUTE = 1;
  public static final String POS_ID = "pos_id";
  public static final String PLAY_MUTE = "mute";
  public static final String PLAY_NETWORK = "network";
  public static final String NONE_OPTION = "none_option";

  private static final String TAG = NativeADUnifiedActivity.class.getSimpleName();

  private int mPlayNetwork = PLAY_NETWORK_WIFI;
  private int mPlayMute = PLAY_SOUND_MUTE;
  private boolean mNoneOption = false;


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_unified_ad);
    ((EditText) findViewById(R.id.posId)).setText(Constants.NativeUnifiedPosID);

    mVideoOptionCheckBox = findViewById(R.id.cb_none_video_option);
    mVideoOptionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: isChecked:" + isChecked);
        mNoneOption = isChecked;
        mPlayNetworkSpinner.setEnabled(!isChecked);
        mPlayMuteSpinner.setEnabled(!isChecked);
      }
    });

    mPlayNetworkSpinner = findViewById(R.id.spinner_network);
    mPlayNetworkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: position:" + position);
        mPlayNetwork = position;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mPlayMuteSpinner = findViewById(R.id.spinner_mute);
    mPlayMuteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick: position:" + position);
        mPlayMute = position;
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

  }
  
  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.NativeUnifiedPosID : posId;
  }

  public void onNormalViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeADUnifiedSampleActivity.class);
    intent.putExtra(POS_ID, getPosID());
    intent.putExtra(PLAY_MUTE, mPlayMute);
    intent.putExtra(PLAY_NETWORK, mPlayNetwork);
    intent.putExtra(NONE_OPTION, mNoneOption);
    startActivity(intent);
  }

  public void onRecyclerViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeADUnifiedRecyclerViewActivity.class);
    intent.putExtra(POS_ID, getPosID());
    intent.putExtra(PLAY_MUTE, mPlayMute);
    intent.putExtra(PLAY_NETWORK, mPlayNetwork);
    intent.putExtra(NONE_OPTION, mNoneOption);
    startActivity(intent);
  }

  public void onListViewClick(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeADUnifiedListViewActivity.class);
    intent.putExtra(POS_ID, getPosID());
    intent.putExtra(PLAY_MUTE, mPlayMute);
    intent.putExtra(PLAY_NETWORK, mPlayNetwork);
    intent.putExtra(NONE_OPTION, mNoneOption);
    startActivity(intent);
  }
}
