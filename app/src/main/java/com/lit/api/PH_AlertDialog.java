package com.lit.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.WindowManager;

import com.lit.R;

/**
 * All code found below has been supplied by Philips.
 * All code originates from sample projects found at:
 * TODO: http://www.developers.meethue.com
 *
 * For explanation on key concepts visit: https://github.com/PhilipsHue/PhilipsHueSDK-Java-MultiPlatform-Android
 */
public final class PH_AlertDialog {

    private ProgressDialog pdialog;
    private static PH_AlertDialog dialogs;

    private PH_AlertDialog() {

    }

    public static synchronized PH_AlertDialog getInstance() {
        if (dialogs == null) {
            dialogs = new PH_AlertDialog();
        }
        return dialogs;
    }

    /**
     * 
     * @param activityContext
     * @param msg
     * @param btnNameResId  String resource id for button name
     */
    public static void showErrorDialog(Context activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(msg).setPositiveButton(btnNameResId, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (! ((Activity) activityContext).isFinishing()) {
            alert.show();
        }
       
    }

    /**
     * Stops running progress-bar
     */
    public void closeProgressDialog() {

        if (pdialog != null) {
            pdialog.dismiss();
            pdialog = null;
        }
    }

    /**
     * Shows progress-bar
     * 
     * @param resID
     * @param ctx
     */
    public void showProgressDialog(int resID, Context ctx) {
        String message = ctx.getString(resID);
        pdialog = ProgressDialog.show(ctx, null, message, true, true);
        pdialog.setCancelable(false);

    }

    /**
     * 
     * @param activityContext
     * @param msg
     * @param btnNameResId
     */
    public static void showAuthenticationErrorDialog(
            final Activity activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.title_error).setMessage(msg)
                .setPositiveButton(btnNameResId, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityContext.finish();

                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }

}
