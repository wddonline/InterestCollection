package org.wdd.app.android.interestcollection.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by richard on 1/12/17.
 */

public class AppConfManager {

    private static AppConfManager INSTANCE;

    public static AppConfManager getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new AppConfManager(context);
        }
        return INSTANCE;
    }

    private final String CONF_NAME = "app_conf";

    private final String NICKNAME = "nickname";
    private final String SEX = "sex";

    private Context context;

    private AppConfManager(Context context) {
        this.context = context;
    }

    public void saveNickname(String nickname) {
        SharedPreferences preference = context.getSharedPreferences(CONF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        if (TextUtils.isEmpty(nickname)) {
            editor.remove(NICKNAME);
        } else {
            editor.putString(NICKNAME, nickname);
        }

        editor.commit();
    }

    public String getNickname() {
        SharedPreferences preferences = context.getSharedPreferences(CONF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(NICKNAME, "");
    }

    public void saveSex(String sex) {
        SharedPreferences preference = context.getSharedPreferences(CONF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        if (TextUtils.isEmpty(sex)) {
            editor.remove(SEX);
        } else {
            editor.putString(SEX, sex);
        }

        editor.commit();
    }

    public String getSex() {
        SharedPreferences preferences = context.getSharedPreferences(CONF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SEX, "0");
    }
}
