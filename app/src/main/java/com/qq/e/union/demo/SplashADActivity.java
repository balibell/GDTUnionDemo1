package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * @author tysche
 */

public class SplashADActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_ad);
    ((EditText) findViewById(R.id.posId)).setText(Constants.SplashPosID);
    findViewById(R.id.splashADDemoButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SplashADActivity.this, SplashActivity.class);
        intent.putExtra("pos_id", getPosID());
        intent.putExtra("need_logo", needLogo());
        startActivity(intent);
      }
    });
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? Constants.SplashPosID : posId;
  }

  private boolean needLogo() {
    return ((CheckBox) findViewById(R.id.checkBox)).isChecked();
  }
}
