package com.xinli.portalclient;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.google.code.microlog4android.Level;
import com.google.code.microlog4android.appender.SyslogMessage;
import java.lang.reflect.Field;
import java.util.Set;
import org.dom4j.swing.XMLTableColumnDefinition;

public class SetAccountActivity extends Activity {
    public static final String DEFAULT_ACCOUNT = "default_account";
    public static final int REFRESH = 0;
    private Dialog accountDialog;
    private ImageView btnAdd;
    private SharedPreferences defaultAccFile;
    private int defaultAccountTextId;
    private EditText password;
    private SharedPreferences sp;
    private TableLayout tableLayout;
    private EditText username;

    class AnonymousClass_10 implements OnClickListener {
        private final /* synthetic */ String val$oldAccount;
        private final /* synthetic */ String val$oldPasswd;
        private final /* synthetic */ TextView val$tv;
        private final /* synthetic */ TextView val$tvValue;

        AnonymousClass_10(String str, String str2, TextView textView, TextView textView2) {
            this.val$oldAccount = str;
            this.val$oldPasswd = str2;
            this.val$tv = textView;
            this.val$tvValue = textView2;
        }

        public void onClick(DialogInterface dialog, int which) {
            if (SetAccountActivity.this.username.getText().toString().isEmpty() || SetAccountActivity.this.password.getText().toString().isEmpty()) {
                Toast.makeText(SetAccountActivity.this.getApplicationContext(), "\u5e10\u53f7\u548c\u5bc6\u7801\u5747\u4e0d\u80fd\u4e3a\u7a7a\uff01", 0).show();
                SetAccountActivity.this.username.setText(this.val$oldAccount);
                SetAccountActivity.this.password.setText(this.val$oldPasswd);
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, false);
            } else if (SetAccountActivity.this.username.getText().toString().equals(this.val$oldAccount) || !SetAccountActivity.this.sp.getAll().keySet().contains(SetAccountActivity.this.username.getText().toString())) {
                Editor editor = SetAccountActivity.this.sp.edit();
                editor.remove(this.val$oldAccount);
                editor.putString(SetAccountActivity.this.username.getText().toString(), SetAccountActivity.this.password.getText().toString());
                editor.commit();
                this.val$tv.setText(SetAccountActivity.this.accountShowOnPage(SetAccountActivity.this.username.getText().toString()));
                this.val$tvValue.setText(SetAccountActivity.this.username.getText().toString());
                if (SetAccountActivity.this.defaultAccFile.getString(DEFAULT_ACCOUNT, "").equals(this.val$oldAccount)) {
                    ((TextView) SetAccountActivity.this.findViewById(R.id.defaultAccount)).setText(SetAccountActivity.this.username.getText().toString());
                    Editor defEditor = SetAccountActivity.this.defaultAccFile.edit();
                    defEditor.putString(DEFAULT_ACCOUNT, SetAccountActivity.this.username.getText().toString());
                    defEditor.commit();
                }
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, true);
            } else {
                Toast.makeText(SetAccountActivity.this.getApplicationContext(), R.string.setAccountActivity_account_exists_error, 0).show();
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, false);
            }
        }
    }

    class AnonymousClass_12 implements OnClickListener {
        private final /* synthetic */ TextView val$oldAcc;
        private final /* synthetic */ TextView val$oldAccVal;
        private final /* synthetic */ View val$view;

        AnonymousClass_12(View view, TextView textView, TextView textView2) {
            this.val$view = view;
            this.val$oldAccVal = textView;
            this.val$oldAcc = textView2;
        }

        public void onClick(DialogInterface dialog, int which) {
            SetAccountActivity.this.tableLayout.removeView((TableRow) this.val$view.getParent());
            Editor editor = SetAccountActivity.this.sp.edit();
            editor.remove(this.val$oldAccVal.getText().toString());
            editor.commit();
            int delRow = this.val$oldAcc.getId() / 10;
            int nextAccountId = this.val$oldAcc.getId() + 10;
            for (int start = 0; start < SetAccountActivity.this.sp.getAll().size() - delRow; start++) {
                TextView t = (TextView) SetAccountActivity.this.findViewById(nextAccountId);
                ImageView editButton = (ImageView) SetAccountActivity.this.findViewById(nextAccountId + 1);
                ImageView delButton = (ImageView) SetAccountActivity.this.findViewById(nextAccountId + 2);
                TextView defaultText = (TextView) SetAccountActivity.this.findViewById(nextAccountId + 3);
                TextView valueText = (TextView) SetAccountActivity.this.findViewById(nextAccountId + 4);
                int newaccId = t.getId() - 10;
                t.setId(newaccId);
                editButton.setId(newaccId + 1);
                delButton.setId(newaccId + 2);
                defaultText.setId(newaccId + 3);
                valueText.setId(newaccId + 4);
                nextAccountId += 10;
            }
            if (this.val$oldAccVal.getText().equals(SetAccountActivity.this.defaultAccFile.getString(DEFAULT_ACCOUNT, ""))) {
                ((TextView) SetAccountActivity.this.findViewById(R.id.defaultAccount)).setText("");
                Editor defaulAccount = SetAccountActivity.this.defaultAccFile.edit();
                defaulAccount.remove(DEFAULT_ACCOUNT);
                defaulAccount.commit();
            }
        }
    }

    class AnonymousClass_14 implements OnClickListener {
        private final /* synthetic */ TextView val$oldAcc;
        private final /* synthetic */ View val$view;

        AnonymousClass_14(View view, TextView textView) {
            this.val$view = view;
            this.val$oldAcc = textView;
        }

        public void onClick(DialogInterface dialog, int which) {
            SetAccountActivity.this.setDefaultAccount(this.val$view.getId(), this.val$oldAcc.getText().toString());
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account);
        ImageButton buttBack = (ImageButton) findViewById(R.id.userbacklogin);
        TextView defaultAccount = (TextView) findViewById(R.id.defaultAccount);
        this.sp = getSharedPreferences("passwordFile", 0);
        this.defaultAccFile = getSharedPreferences(DEFAULT_ACCOUNT, 0);
        defaultAccount.setText(this.defaultAccFile.getString(DEFAULT_ACCOUNT, ""));
        this.tableLayout = (TableLayout) findViewById(R.id.accountTable);
        this.btnAdd = (ImageView) findViewById(R.id.btnAddAccount);
        int i = 1;
        for (String key : this.sp.getAll().keySet()) {
            showAccount(key, i);
            i++;
        }
        this.btnAdd.setBackgroundResource(R.drawable.account_add);
        this.btnAdd.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == 0) {
                    arg0.setBackgroundResource(R.drawable.account_add_down);
                } else if (arg1.getAction() == 1) {
                    arg0.setBackgroundResource(R.drawable.account_add);
                }
                return false;
            }
        });
        this.btnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetAccountActivity.this.addAccountDialog();
            }
        });
        buttBack.setBackgroundResource(R.drawable.returnbutton);
        buttBack.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                if (arg1.getAction() == 0) {
                    arg0.setBackgroundResource(R.drawable.returnbutton_down);
                } else if (arg1.getAction() == 1) {
                    arg0.setBackgroundResource(R.drawable.returnbutton);
                }
                return false;
            }
        });
        buttBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SetAccountActivity.this, MainActivity.class);
                SetAccountActivity.this.startActivity(intent);
                SetAccountActivity.this.finish();
            }
        });
    }

    public void addAccountDialog() {
        View longinDialogView = LayoutInflater.from(this).inflate(R.layout.add_account, null);
        this.username = (EditText) longinDialogView.findViewById(R.id.edit_usename);
        this.password = (EditText) longinDialogView.findViewById(R.id.edit_passwd);
        this.accountDialog = new Builder(this).setTitle("\u6dfb\u52a0\u5e10\u53f7").setView(longinDialogView).setPositiveButton("\u786e\u5b9a", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetAccountActivity.this.addAccount(SetAccountActivity.this.username.getText().toString(), SetAccountActivity.this.password.getText().toString());
            }
        }).setNeutralButton("\u53d6\u6d88", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, true);
            }
        }).create();
        this.accountDialog.show();
    }

    private void addAccount(String account, String passwd) {
        if (account.isEmpty() || passwd.isEmpty()) {
            Toast.makeText(getApplicationContext(), "\u5e10\u53f7\u6216\u5bc6\u7801\u5747\u4e0d\u80fd\u4e3a\u7a7a\uff01", 0).show();
            setDialogMiss(this.accountDialog, false);
            return;
        }
        Set<String> keys = this.sp.getAll().keySet();
        if (keys.contains(account)) {
            Toast.makeText(getApplicationContext(), R.string.setAccountActivity_account_exists_error, 0).show();
            setDialogMiss(this.accountDialog, false);
            return;
        }
        Editor editor = this.sp.edit();
        editor.putString(account, passwd);
        editor.commit();
        if (keys.size() == 0) {
            Editor defAccountEditor = this.defaultAccFile.edit();
            defAccountEditor.putString(DEFAULT_ACCOUNT, account);
            defAccountEditor.commit();
            ((TextView) findViewById(R.id.defaultAccount)).setText(account);
            this.defaultAccountTextId = 13;
        }
        Toast.makeText(getApplicationContext(), "\u5e10\u53f7\u4fdd\u5b58\u6210\u529f\uff01", 0).show();
        setDialogMiss(this.accountDialog, true);
        showAccount(account, keys.size() + 1);
    }

    private void setDialogMiss(Dialog accountDialog, boolean miss) {
        try {
            Field field = accountDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(accountDialog, Boolean.valueOf(miss));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (miss) {
            accountDialog.dismiss();
        }
    }

    private void showAccount(String account, int order) {
        TextView accountText = new TextView(this);
        accountText.setId(Integer.valueOf(new StringBuilder(String.valueOf(order)).append("0").toString()).intValue());
        accountText.setText(accountShowOnPage(account));
        accountText.setGravity(XMLTableColumnDefinition.NODE_TYPE);
        accountText.setPadding(25, 25, 0, 25);
        accountText.getPaint().setFlags(Level.ERROR_INT);
        accountText.setLayoutParams(new LayoutParams(-1, -2));
        TextView accountDefault = new TextView(this);
        accountDefault.setId(Integer.valueOf(new StringBuilder(String.valueOf(order)).append("3").toString()).intValue());
        if (this.defaultAccFile.getString(DEFAULT_ACCOUNT, "").equals(account)) {
            this.defaultAccountTextId = accountDefault.getId();
        } else {
            accountDefault.setText("\u8bbe\u4e3a\u9ed8\u8ba4");
        }
        LayoutParams defParams = new LayoutParams(-1, -2);
        defParams.rightMargin = 25;
        accountDefault.setPadding(0, 25, 0, 25);
        accountDefault.setLayoutParams(defParams);
        accountDefault.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!((TextView) SetAccountActivity.this.findViewById(Integer.valueOf(view.getId()).intValue())).getText().toString().isEmpty()) {
                    SetAccountActivity.this.defaultAccount(view);
                }
            }
        });
        ImageView ivEdit = new ImageView(this);
        ivEdit.setId(Integer.valueOf(new StringBuilder(String.valueOf(order)).append("1").toString()).intValue());
        ivEdit.setBackgroundResource(R.drawable.account_edit);
        LayoutParams editParams = new LayoutParams(-1, -2);
        editParams.rightMargin = 25;
        ivEdit.setLayoutParams(editParams);
        ivEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetAccountActivity.this.editAccount(view);
            }
        });
        ImageView ivDel = new ImageView(this);
        ivDel.setId(Integer.valueOf(new StringBuilder(String.valueOf(order)).append("2").toString()).intValue());
        ivDel.setBackgroundResource(R.drawable.account_del);
        LayoutParams delParams = new LayoutParams(-1, -2);
        delParams.rightMargin = 25;
        ivDel.setLayoutParams(delParams);
        ivDel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SetAccountActivity.this.delAccount(view);
            }
        });
        TextView accountValueText = new TextView(this);
        accountValueText.setId(Integer.valueOf(new StringBuilder(String.valueOf(order)).append("4").toString()).intValue());
        accountValueText.setText(account);
        accountValueText.setVisibility(Level.ERROR_INT);
        TableRow tableRow = new TableRow(this);
        tableRow.setGravity(17);
        tableRow.setBackgroundColor(getResources().getColor(R.color.lightbulue));
        tableRow.setLayoutParams(new LayoutParams(-1, -2));
        tableRow.addView(accountText);
        tableRow.addView(accountDefault);
        tableRow.addView(ivEdit);
        tableRow.addView(ivDel);
        tableRow.addView(accountValueText);
        this.tableLayout.addView(tableRow);
    }

    private String accountShowOnPage(String account) {
        StringBuilder accountShow = new StringBuilder();
        int accLen = account.length();
        if (accLen > 10) {
            accountShow = new StringBuilder(account.substring(0, SyslogMessage.TEN));
            accountShow.append("...").append(account.substring(accLen - 6));
        } else {
            accountShow.append(account);
        }
        return accountShow.toString();
    }

    private void editAccount(View view) {
        TextView tv = (TextView) findViewById(Integer.valueOf(view.getId() - 1).intValue());
        TextView tvValue = (TextView) findViewById(Integer.valueOf(view.getId() + 3).intValue());
        String oldAccount = tvValue.getText().toString();
        String oldPasswd = this.sp.getString(oldAccount, "");
        View longinDialogView = LayoutInflater.from(this).inflate(R.layout.add_account, null);
        this.username = (EditText) longinDialogView.findViewById(R.id.edit_usename);
        this.password = (EditText) longinDialogView.findViewById(R.id.edit_passwd);
        this.username.setText(oldAccount);
        this.password.setText(oldPasswd);
        this.accountDialog = new Builder(this).setTitle("\u7f16\u8f91\u5e10\u53f7").setView(longinDialogView).setPositiveButton("\u786e\u5b9a", new AnonymousClass_10(oldAccount, oldPasswd, tv, tvValue)).setNeutralButton("\u53d6\u6d88", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, true);
            }
        }).create();
        this.accountDialog.show();
    }

    private void delAccount(View view) {
        TextView oldAccVal = (TextView) findViewById(Integer.valueOf(view.getId() + 2).intValue());
        this.accountDialog = new Builder(this).setTitle("\u5220\u9664\u5e10\u53f7").setMessage(new StringBuilder("\u786e\u5b9a\u5220\u9664\u5e10\u53f7").append(oldAccVal.getText()).append("\u5417\uff1f").toString()).setPositiveButton("\u786e\u5b9a", new AnonymousClass_12(view, oldAccVal, (TextView) findViewById(Integer.valueOf(view.getId() - 2).intValue()))).setNeutralButton("\u53d6\u6d88", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, true);
            }
        }).create();
        this.accountDialog.show();
    }

    private void defaultAccount(View view) {
        TextView oldAcc = (TextView) findViewById(Integer.valueOf(view.getId() + 1).intValue());
        this.accountDialog = new Builder(this).setTitle("\u9ed8\u8ba4\u5e10\u53f7").setMessage(new StringBuilder("\u786e\u5b9a\u8bbe\u7f6e\u5e10\u53f7").append(oldAcc.getText()).append("\u4e3a\u9ed8\u8ba4\u5e10\u53f7\u5417\uff1f").toString()).setPositiveButton("\u786e\u5b9a", new AnonymousClass_14(view, oldAcc)).setNeutralButton("\u53d6\u6d88", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                SetAccountActivity.this.setDialogMiss(SetAccountActivity.this.accountDialog, true);
            }
        }).create();
        this.accountDialog.show();
    }

    private void setDefaultAccount(int id, String newAccount) {
        ((TextView) findViewById(R.id.defaultAccount)).setText(newAccount);
        Editor editor = this.defaultAccFile.edit();
        editor.putString(DEFAULT_ACCOUNT, newAccount);
        editor.commit();
        if (this.defaultAccountTextId != 0) {
            TextView oldDefTextView = (TextView) findViewById(this.defaultAccountTextId);
            if (oldDefTextView != null) {
                oldDefTextView.setText("\u8bbe\u4e3a\u9ed8\u8ba4");
            }
        }
        this.defaultAccountTextId = id;
        ((TextView) findViewById(this.defaultAccountTextId)).setText("");
    }
}
