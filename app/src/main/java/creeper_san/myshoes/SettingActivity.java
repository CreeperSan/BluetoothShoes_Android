package creeper_san.myshoes;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;

import creeper_san.myshoes.base.BaseActivity;

public class SettingActivity extends BaseActivity {
    @Override
    protected int getLayoutID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settingLinearLayout,new SettingPrefFragment()).commit();
    }
}
