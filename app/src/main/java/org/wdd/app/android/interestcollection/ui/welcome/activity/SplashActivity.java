package org.wdd.app.android.interestcollection.ui.welcome.activity;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.permission.PermissionListener;
import org.wdd.app.android.interestcollection.permission.PermissionManager;
import org.wdd.app.android.interestcollection.permission.Rationale;
import org.wdd.app.android.interestcollection.permission.RationaleListener;
import org.wdd.app.android.interestcollection.permission.SettingDialog;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.ui.main.activity.MainActivity;
import org.wdd.app.android.interestcollection.utils.Constants;

import java.util.List;

public class SplashActivity extends BaseActivity implements Runnable, PermissionListener {

    private final long WAIT_TIME = 5000;
    private final int REQUEST_PERMISSION_CODE = 100;

    private RelativeLayout mAdsContainer;
    private TextView mSkipView;
    private View mBgView;
    private View mProviderView;

    private Handler mHandler = new Handler();

    private boolean isCheckRequired = false;
    private long startTimeMillis;
    private boolean isAdOverdue = false;
    private boolean isAdClicked = false;
    private boolean isActivityPause = false;
    private boolean jumped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViews();
        checkPermission();
    }

    private void initViews() {
        mAdsContainer = (RelativeLayout) findViewById(R.id.activity_splash_ads_container);
        mSkipView = (TextView) findViewById(R.id.activity_splash_skip);
        mBgView = findViewById(R.id.activity_splash_bg);
        mProviderView = findViewById(R.id.activity_splash_provider);
    }

    private void checkPermission() {
        PermissionManager.with(this)
                .requestCode(REQUEST_PERMISSION_CODE)
                .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        PermissionManager.rationaleDialog(SplashActivity.this, rationale).show();
                    }
                }).send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(this);
        super.onBackPressed();
    }

    @Override
    public void run() {
        if (jumped) return;
        jumped = true;
        finish();
        MainActivity.show(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityPause = false;
        if (isAdOverdue || isAdClicked) {
            run();
            return;
        }
        if (isCheckRequired) {
            checkPermission();
            isCheckRequired = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityPause = true;
    }

    @Override
    public void onSucceed(int requestCode, List<String> grantedPermissions) {
        startTimeMillis = System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadSplashAd();
            }
        }, 1000);
    }

    @Override
    public void onFailed(int requestCode, List<String> deniedPermissions) {
        if (PermissionManager.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            PermissionManager.defaultSettingDialog(this)
                    .setSettingDialogListener(new SettingDialog.SettingDialogListener() {
                        @Override
                        public void onSettingClicked() {
                            isCheckRequired = true;
                        }

                        @Override
                        public void onCancelClicked() {
                            finish();
                        }
                    }).show();

            // 第二种：用自定义的提示语。
            // PermissionManager.defaultSettingDialog(this, REQUEST_CODE_SETTING)
            // .setTitle("权限申请失败")
            // .setMessage("我们需要的一些权限被您拒绝或者系统发生错误申请失败，请您到设置页面手动授权，否则功能无法正常使用！")
            // .setPositiveButton("好，去设置")
            // .show();

            // 第三种：自定义dialog样式。
            // SettingService settingService =
            //    PermissionManager.defineSettingDialog(this, REQUEST_CODE_SETTING);
            // 你的dialog点击了确定调用：
            // settingService.execute();
            // 你的dialog点击了取消调用：
            // settingService.cancel();
        } else {
            finish();
        }
    }

    public void loadSplashAd() {
        new SplashAD(this, mAdsContainer, mSkipView, Constants.TENCENT_APP_ID, Constants.SPLASH_AD_ID, new SplashADListener() {

            @Override
            public void onADDismissed() {
                if (isAdOverdue) {
                    run();
                } else {
                    if (isAdClicked) {

                    } else {
                        run();
                    }
                }
            }

            @Override
            public void onNoAD(int i) {
                waitOrJump();
            }

            @Override
            public void onADPresent() {
                AlphaAnimation hideAnim = new AlphaAnimation(1, 0);
                hideAnim.setDuration(800);
                hideAnim.setFillAfter(true);
                hideAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mBgView.setVisibility(View.GONE);
                        mProviderView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                AlphaAnimation showAnim = new AlphaAnimation(0, 1);
                hideAnim.setDuration(800);
                hideAnim.setFillAfter(true);

                mBgView.startAnimation(hideAnim);
                mProviderView.startAnimation(hideAnim);

                mAdsContainer.setVisibility(View.VISIBLE);
                mSkipView.setVisibility(View.VISIBLE);
                mAdsContainer.startAnimation(showAnim);
                mSkipView.startAnimation(showAnim);
            }

            @Override
            public void onADClicked() {
                isAdClicked = true;
            }

            @Override
            public void onADTick(long millisUntilFinished) {
                String skipText = getString(R.string.click_to_skip);
                mSkipView.setText(String.format(skipText, Math.round(millisUntilFinished / 1000f)));
                if (millisUntilFinished <= 1000) {
                    isAdOverdue = true;
                    if (isActivityPause) return;
                    onADDismissed();
                }
            }
        }, 3000);
    }

    private void waitOrJump() {
        long remainTimeMillis = WAIT_TIME - (System.currentTimeMillis() - startTimeMillis);
        if (remainTimeMillis > 0) {
            mHandler.postDelayed(this, remainTimeMillis);
        } else {
            run();
        }
    }
}
