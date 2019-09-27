package com.simplelib.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.simplelib.R;

public abstract class CustomDialog {
    public static final int LIGHT_THEME = R.style.Theme_AppCompat_Light_Dialog_Alert;
    public static final int DARK_THEME = R.style.Theme_AppCompat_Dialog_Alert;

    public static final String DEFAULT_POSITIVE_TEXT = "Ok";
    public static final String DEFAULT_NEGATIVE_TEXT = "Cancel";

    private Context context;

    private View contentView;

    private int theme;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    private String title;

    protected boolean cancelable = true;

    protected boolean showButtons = true;
    protected boolean positiveButton = true;
    protected boolean negativeButton = true;

    protected String positiveText = DEFAULT_POSITIVE_TEXT;
    protected String negativeText = DEFAULT_NEGATIVE_TEXT;

    public CustomDialog(Context context, int layoutId) {
        this.context = context;

        setLayoutId(layoutId);
    }

    public CustomDialog(Context context, View contentView) {
        this.context = context;

        setContentView(contentView);
    }

    public CustomDialog(Context context, int layoutId, String title) {
        this.context = context;
        this.title = title;

        setLayoutId(layoutId);
    }

    public CustomDialog(Context context, View contentView, String title) {
        this.context = context;
        this.title = title;

        setContentView(contentView);
    }

    public void setLayoutId(int layoutId) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(layoutInflater.inflate(layoutId, null));
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    public View findViewById(int id) {
        if (contentView != null)
            return contentView.findViewById(id);
        else
            return null;
    }

    public void setTheme(int id) {
        this.theme = id;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void setTitle(String title) {
        this.title = title;

        if (dialog != null)
            dialog.setTitle(title);
    }

    public Context getContext() {
        return context;
    }

    public Window getWindow() {
        if (dialog != null)
            return dialog.getWindow();
        else
            return null;
    }

    public void build() {
        builder = new AlertDialog.Builder(context, theme);
        builder.setTitle(title);

        create(contentView);
        builder.setView(contentView);

        if (showButtons && positiveButton) {
            builder.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    boolean reopen = onAccept();
                    if (reopen) {
                        build();
                        show();
                    }
                }
            });
        }
        if (showButtons && negativeButton) {
            builder.setNegativeButton(negativeText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    boolean reopen = onDeny();
                    if (reopen) {
                        build();
                        show();
                    }
                }
            });
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hideKeyboard();

                boolean reopen = onDismissDialog();
                if (reopen) {
                    build();
                    show();
                }
            }
        });

        dialog = builder.create();
        dialog.setCancelable(cancelable);

        if (title == null || (title != null && title.length() <= 0))
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams dialogParams = dialog.getWindow().getAttributes();
        dialogParams.dimAmount = 0.7f;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void show() {
        if (dialog == null)
            build();

        dialog.show();
    }

    public void hide() {
        if (dialog != null)
            dialog.hide();

        hideKeyboard();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    public boolean onAccept() {
        return false;
    }

    public boolean onDeny() {
        return false;
    }

    public boolean onDismissDialog() {
        return false;
    }

    public abstract void create(View contentView);
}