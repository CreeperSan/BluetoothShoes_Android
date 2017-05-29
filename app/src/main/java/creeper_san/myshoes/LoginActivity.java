package creeper_san.myshoes;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import butterknife.BindView;
import butterknife.OnClick;
import creeper_san.myshoes.base.BaseActivity;

public class LoginActivity extends BaseActivity implements ServiceConnection {

    @BindView(R.id.loginToolbar)
    Toolbar loginToolbar;
    @BindView(R.id.loginAccount)
    TextInputEditText loginAccount;
    @BindView(R.id.loginPassword)
    TextInputEditText loginPassword;
    @BindView(R.id.loginRemember)
    CheckBox loginRemember;
    @BindView(R.id.loginLogin)
    Button loginLogin;
    @BindView(R.id.loginRegister)
    Button loginRegister;

    private ShoesService shoesService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        toStartBindServer(ShoesService.class,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private void initActionBar() {
        setSupportActionBar(loginToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("登陆");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.avtivity_login;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        shoesService = ((ShoesService.ShoesServerBinder) binder).getService();
        boolean isAutoLogin = shoesService.getIsAutoLogin();
        if (isAutoLogin){
            loginRemember.setChecked(true);
            loginAccount.setText(shoesService.getRememberAccount());
            loginPassword.setText(shoesService.getRememberPassword());
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name) {

    }

    @OnClick({R.id.loginLogin, R.id.loginRegister})
    public void onClick(View view) {
        final String account = loginAccount.getText().toString().trim();
        final String password = loginPassword.getText().toString().trim();
        if (password.equals("") || account.equals("")){
            toast("请先输入完整哦");
            return;
        }
        if (shoesService==null){
            toast("还未连接到服务哇");
            return;
        }
        switch (view.getId()) {
            case R.id.loginLogin:
                shoesService.login(account, password, loginRemember.isChecked(), new ShoesService.OnInterServerResultListener() {
                    @Override
                    public void onResult(String resultStr) {
                        if (resultStr.equals("Success")){
                            toast("登陆成功");
                            finish();
                        }
                    }
                    @Override
                    public void onFail(String reason) {
                        toast("登录失败，请检查是否填写正确以及网络是否畅通");
                    }
                });
                break;
            case R.id.loginRegister:
                dialogSimpleInput("请确认你的密码", "密码", 1, InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
                        "确定", new OnDialogInputListener() {
                            @Override
                            public void onClick(TextInputEditText editText, DialogInterface dialog, int which) {
                                String inputPassword = editText.getText().toString().trim();
                                if (password.equals(inputPassword)){
                                    shoesService.register(account, password, loginRemember.isChecked(), new ShoesService.OnInterServerResultListener() {
                                        @Override
                                        public void onResult(String resultStr) {
                                            toast("注册成功");
                                            finish();
                                        }

                                        @Override
                                        public void onFail(String reason) {
                                            toast(reason);
                                        }
                                    });
                                }else {
                                    toast("噢，两次输入的密码不一样，请再确认下？");
                                }
                            }
                        }, "取消", null);
                break;
        }
    }
}
