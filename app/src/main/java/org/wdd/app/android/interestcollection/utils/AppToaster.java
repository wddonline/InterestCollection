package org.wdd.app.android.interestcollection.utils;

import android.widget.Toast;

import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;


/**
 * Created by wangdd on 16-11-27.
 */

public class AppToaster {

    public static void show(int res) {
        Toast.makeText(InterestCollectionApplication.getInstance(), InterestCollectionApplication.getInstance().getText(res), Toast.LENGTH_SHORT).show();
    }

    public static void show(String txt) {
        Toast.makeText(InterestCollectionApplication.getInstance(), txt, Toast.LENGTH_SHORT).show();
    }
}
