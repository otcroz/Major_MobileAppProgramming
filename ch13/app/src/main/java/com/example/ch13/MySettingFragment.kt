package com.example.ch13

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MySettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MySettingFragment : PreferenceFragmentCompat() { // 상속 받기: PreferenceFragmentCompat()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // 프러퍼런스 연결하기
        val idPreference:EditTextPreference? = findPreference("id")
        idPreference?.title = "ID 변경"
        //idPreference?.summary = "ID를 변경할 수 있습니다."
        //idPreference?.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
        idPreference?.summaryProvider = Preference.SummaryProvider<EditTextPreference>{
            preference ->
                val text = preference.text
                if(TextUtils.isEmpty(text)){
                    "ID 설정이 되지 않았습니다."
                }
                else{
                    "설정된 ID는 $text 입니다."
                }
        }
        val colorPreference:ListPreference? = findPreference("color")
        colorPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
    }


}