package com.eht.apps.basic.okk;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
//import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.io.InputStream;
import java.util.List;

import com.eht.apps.basic.okk.MainActivity;
import com.eht.apps.basic.okk.xmlparser.OkkXmlParser;

import static com.eht.apps.basic.okk.MainActivity.*;
import static com.eht.apps.basic.okk.R.string.pref_app_char_delay;
import static com.eht.apps.basic.okk.R.string.pref_word_prediction_enable;
import static com.eht.apps.basic.okk.R.string.pref_wordprediction_count;

//import com.example.testapp1.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    //private Preference mypref;
    SharedPreferences prefs ;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

@Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.okk_settings);

    try {
        bindPreferenceSummaryToValue(findPreference("wordprediction_checkbox"));

    } catch (Exception ex) {
        ex.printStackTrace();
    }

    bindPreferenceSummaryToValue(findPreference("wordpred_count"));
    bindPreferenceSummaryToValue(findPreference("app_fontsize_txtarea"));
    bindPreferenceSummaryToValue(findPreference("app_fontsize_btn_singlechar"));
    bindPreferenceSummaryToValue(findPreference("app_fontsize_btn_multichar"));
    bindPreferenceSummaryToValue(findPreference("character_delay"));
    bindPreferenceSummaryToValue(findPreference("app_language_file"));




/*
        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.registerOnSharedPreferenceChangeListener(listener);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
*/

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {

            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                Preference mypref = new Preference(getApplicationContext());

                mypref = (Preference) findPreference(key);

                Preference.OnPreferenceChangeListener onprefchanged =   new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        if (preference.getKey().equals("wordprediction_checkbox")) {
                            MainActivity.wordPredictionFeatureEnabled = Boolean.parseBoolean(newValue.toString());
                            if (MainActivity.wordPredictionFeatureEnabled == false) {
                                MainActivity.dicfile = "";
                            }
                            else {
                                try {
                                    InputStream ishq = getAssets().open(MainActivity.charmapfilepath);

                                    List<String> fns = OkkXmlParser.getLanguageAttributeFileName(ishq);

                                    MainActivity.fontfilepath = fns.get(0);    // font file
                                    MainActivity.dicfile = fns.get(1);    // dict file
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        if (preference.getKey().equals("app_language_file")) {

                            MainActivity.charmapfilepath = newValue.toString();

                            try {
                                InputStream ishq = getAssets().open(MainActivity.charmapfilepath);

                                List<String> fns = OkkXmlParser.getLanguageAttributeFileName(ishq);

                                MainActivity.fontfilepath = fns.get(0);    // font file
                                MainActivity.dicfile = fns.get(1);    // dict file
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        }

                        if (preference.getKey().equals("character_delay")) {
                            MainActivity.timerDelay = Long.parseLong(newValue.toString(), 10);
                        }

                        if (preference.getKey().equals("wordpred_count")) {
                            MainActivity.predictedWordsCount = Integer.parseInt(newValue.toString());
                        }

                        if (preference.getKey().equals("app_fontsize_txtarea")) {
                            MainActivity.txtareafontsize = Integer.parseInt(newValue.toString());
                        }

                        if (preference.getKey().equals("app_fontsize_btn_singlechar")) {
                            MainActivity.btnfontsize1 = Integer.parseInt(newValue.toString());
                        }

                        if (preference.getKey().equals("app_fontsize_btn_multichar")) {
                            MainActivity.btnfontsize2 = Integer.parseInt(newValue.toString());
                        }

                        return true;
                    }
                };


                prefs.registerOnSharedPreferenceChangeListener(listener);


                //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                //prefs.unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) onprefchanged);

            }
        };

        //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(listener);
    }


    @Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		//setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.okk_settings);
		//PreferenceCategory fakeHeader = new PreferenceCategory(this);
		//fakeHeader.setTitle(R.string.title_activity_settings);
		//getPreferenceScreen().addPreference(fakeHeader);
		//addPreferencesFromResource(R.xml.okk_settings);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.

       }

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {

			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);


			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 *
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}




/*

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference mypref = new Preference(getApplicationContext());

        mypref = (Preference) findPreference(key);

        Preference.OnPreferenceChangeListener onprefchanged =   new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                if (preference.getKey().equals("wordprediction_checkbox")) {
                    wordPredictionFeatureEnabled = Boolean.parseBoolean(newValue.toString());
                    if (wordPredictionFeatureEnabled == false) {
                        dicfile = "";
                    }
                    else {
                        try {
                            InputStream ishq = getAssets().open(charmapfilepath);

                            List<String> fns = OkkXmlParser.getLanguageAttributeFileName(ishq);

                            fontfilepath = fns.get(0);    // font file
                            dicfile = fns.get(1);    // dict file
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }

                if (preference.getKey().equals("app_language_file")) {

                    charmapfilepath = newValue.toString();

                    try {
                        InputStream ishq = getAssets().open(charmapfilepath);

                        List<String> fns = OkkXmlParser.getLanguageAttributeFileName(ishq);

                        fontfilepath = fns.get(0);    // font file
                        dicfile = fns.get(1);    // dict file
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }

                if (preference.getKey().equals("character_delay")) {
                    timerDelay = Long.parseLong(newValue.toString(), 10);
                }

                if (preference.getKey().equals("wordpred_count")) {
                    predictedWordsCount = Integer.parseInt(newValue.toString());
                }

                if (preference.getKey().equals("app_fontsize_txtarea")) {
                    txtareafontsize = Integer.parseInt(newValue.toString());
                }

                if (preference.getKey().equals("app_fontsize_btn_singlechar")) {
                    btnfontsize1 = Integer.parseInt(newValue.toString());
                }

                if (preference.getKey().equals("app_fontsize_btn_multichar")) {
                    btnfontsize2 = Integer.parseInt(newValue.toString());
                }

                return true;
            }
        };


        mypref.setOnPreferenceChangeListener(onprefchanged);


        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.unregisterOnSharedPreferenceChangeListener((SharedPreferences.OnSharedPreferenceChangeListener) onprefchanged);

    }
*/


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class OkkSettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.okk_settings);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("wordprediction_checkbox"));
            bindPreferenceSummaryToValue(findPreference("wordpred_count"));
            bindPreferenceSummaryToValue(findPreference("app_fontsize_txtarea"));
            bindPreferenceSummaryToValue(findPreference("app_fontsize_btn_singlechar"));
            bindPreferenceSummaryToValue(findPreference("app_fontsize_btn_multichar"));
            bindPreferenceSummaryToValue(findPreference("character_delay"));
            bindPreferenceSummaryToValue(findPreference("app_language_file"));
        }

    }

}
