package org.wdd.app.android.interestcollection.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.wdd.app.android.interestcollection.R;

/**
 * Created by richard on 1/12/17.
 */

public abstract class BottomPopupMenu extends Dialog{


    public BottomPopupMenu(Context context) {
        super(context, R.style.appDialog);
        View rootView = createView(context);
        setContentView(rootView);
        initViews(rootView);

        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.bottomDialogStyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
        rootView.measure(0, 0);
        lp.height = rootView.getMeasuredHeight();
//        lp.alpha = 0.3f; // 透明度
        dialogWindow.setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    public abstract View createView(Context context);

    protected abstract void initViews(View rootView);
}
