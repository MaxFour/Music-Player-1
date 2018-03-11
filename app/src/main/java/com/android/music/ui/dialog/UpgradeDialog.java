package com.android.music.ui.dialog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.billingclient.api.BillingClient;
import com.android.music.R;
import com.android.music.MusicApplication;
import com.android.music.billing.BillingManager;
import com.android.music.constants.Config;
import com.android.music.ui.activities.BaseActivity;
import com.android.music.ui.activities.MainActivity;
import com.android.music.utils.MPlayerUtils;

public class UpgradeDialog {

    private static final String TAG = "UpgradeDialog";

    private UpgradeDialog() {
        //no instance
    }

    public static MaterialDialog getUpgradeDialog(@NonNull Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.get_pro_title))
                .content(activity.getResources().getString(R.string.upgrade_dialog_message))
                .positiveText(R.string.btn_upgrade)
                .onPositive((dialog, which) -> {
                    if (MPlayerUtils.isAmazonBuild()) {
                        Intent storeIntent = MPlayerUtils.getMusicStoreIntent("com.android.music");
                        if (storeIntent.resolveActivity(MusicApplication.getInstance().getPackageManager()) != null) {
                            activity.startActivity(storeIntent);
                        } else {
                            activity.startActivity(MPlayerUtils.getMusicWebIntent("com.android.music"));
                        }
                    } else {
                        purchaseUpgrade(activity);
                    }
                })
                .negativeText(R.string.get_pro_button_no)
                .build();
    }

    private static void purchaseUpgrade(@NonNull Activity activity) {
        if (!(activity instanceof BaseActivity)) {
            Log.e(TAG, "Purchase may only be initiated with a BaseActivity");
            return;
        }
        BillingManager billingManager = ((BaseActivity) activity).getBillingManager();
        if (billingManager != null) {
            billingManager.initiatePurchaseFlow(Config.SKU_PREMIUM, BillingClient.SkuType.INAPP);
        }
    }

    public static MaterialDialog getUpgradeSuccessDialog(@NonNull Activity activity) {
        return new MaterialDialog.Builder(activity)
                .title(activity.getResources().getString(R.string.upgraded_title))
                .content(activity.getResources().getString(R.string.upgraded_message))
                .positiveText(R.string.restart_button)
                .onPositive((materialDialog, dialogAction) -> {
                    Intent intent = new Intent(activity, MainActivity.class);
                    ComponentName componentName = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                    activity.startActivity(mainIntent);
                })
                .build();
    }
}