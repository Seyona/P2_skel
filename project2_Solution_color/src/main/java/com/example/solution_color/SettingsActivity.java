package com.example.solution_color;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity extends Activity {
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		PrefsFragment mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();

		MyPreferences myPref = new MyPreferences();



	  }


	
	public static class PrefsFragment extends PreferenceFragment {

		@TargetApi(Build.VERSION_CODES.M)
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);

		}


	}

	public static class MyPreferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
		private final String PREF_FILE_NAME = "Preferences";

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences); //deprecated
			PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
		}


		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME, MODE_APPEND);
			SharedPreferences.Editor editor = settings.edit();
			if (key.equals("share_text")) {
				editor.putString("share_text", sharedPreferences.getString("share_text",getString(R.string.sharemessage)));
			}

			if (key.equals("share_subject")){
				editor.putString("share_subject", sharedPreferences.getString("share_subject",getString(R.string.shareTitle)));
			}

			if (key.equals("Sketch_bar")){
				editor.putInt("Sketch_bar", Integer.parseInt(sharedPreferences.getString("Sketch_bar", getString(R.string.sketch_value))));
			}

			if (key.equals("Sat_bar")) {
				editor.putInt("Sat_bar", Integer.parseInt(sharedPreferences.getString("Sat_bar", getString(R.string.color_value))));
			}
			editor.apply();
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		}

		@Override
		public void onPause() {
			getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
			super.onPause();
		}

	}
}
