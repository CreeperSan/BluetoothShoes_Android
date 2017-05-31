package creeper_san.myshoes

import android.os.Bundle
import android.preference.PreferenceFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import creeper_san.myshoes.helper.SettingHelper

class SettingPrefFragment: PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceManager.sharedPreferencesName = SettingHelper.NAME
        addPreferencesFromResource(R.xml.pref_setting)
    }

}
