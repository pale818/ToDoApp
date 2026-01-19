package hr.algebra.todoapp.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import hr.algebra.todoapp.R
import android.widget.Toast
import androidx.preference.SwitchPreferenceCompat


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        //NOTIFICATIONS
        findPreference<SwitchPreferenceCompat>("pref_notifications")
            ?.setOnPreferenceChangeListener { _, newValue ->
                val enabled = newValue as Boolean
                Toast.makeText(requireContext(),
                    if (enabled) "Notifications ON" else "Notifications OFF",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
    }

}
