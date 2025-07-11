package com.kashtansystem.project.gloriyamarketing.activity.main;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqBusinessRegions;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetClients;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetRefusalList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceType;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqProductBalance;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSpecEvents;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import java.util.List;

/**
 * Created by FlameKaf on 25.07.2017.
 * ----------------------------------
 */

public class SettingsActivity extends AppPreferenceActivity
{
    private static Preference.OnPreferenceChangeListener preferenceChangeListener = new Preference
        .OnPreferenceChangeListener()
    {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value)
        {
            String stringValue = value.toString();
            if (preference instanceof ListPreference)
            {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            }
            else
            {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static boolean isXLargeTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static void bindPreferenceSummaryToValue(Preference preference)
    {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(preferenceChangeListener);
        // Trigger the listener immediately with the preference's
        // current value.
        preferenceChangeListener.onPreferenceChange(preference, PreferenceManager
            .getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
            break;
            case R.id.mSyncAll:
                new LoadAgentReferences(this).execute("all");
            break;
            case R.id.mSyncRegions:
                new LoadAgentReferences(this).execute("regions");
            break;
            case R.id.mSyncClients:
                new LoadAgentReferences(this).execute("clients");
            break;
            case R.id.mSyncPriceType:
                new LoadAgentReferences(this).execute("price types");
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onIsMultiPane()
    {
        return isXLargeTablet(this);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target)
    {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    protected boolean isValidFragment(String fragmentName)
    {
        return PreferenceFragment.class.getName().equals(fragmentName) ||
               DataSyncPreferenceFragment.class.getName().equals(fragmentName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(C.KEYS.SYNC_FREQUENCY_PREF_REGIONS));
            bindPreferenceSummaryToValue(findPreference(C.KEYS.SYNC_FREQUENCY_PREF_CLIENTS));
            bindPreferenceSummaryToValue(findPreference(C.KEYS.SYNC_FREQUENCY_PREF_PT));
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            inflater.inflate(R.menu.settings_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }


    private class LoadAgentReferences extends AsyncTask<String, Integer, Void>
    {
        private final int[] msgs =
        {
            R.string.dialog_text_load_bus_regions,
            R.string.dialog_text_load_price_type,
            R.string.dialog_text_load_price,
            R.string.dialog_text_download_clients,
            R.string.dialog_text_load_discount,
            R.string.dialog_text_load_product,
        };
        private Dialog dialog;
        private Context context;

        LoadAgentReferences(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            dialog = getProgressDialog("");
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            if (!dialog.isShowing())
                dialog.show();
            ((TextView)dialog.findViewById(R.id.dialogSLabel)).setText(context.getString(msgs[values[0]]));
        }

        @Override
        protected Void doInBackground(String... params)
        {
            switch (params[0])
            {
                case "all":
                    ReqGetRefusalList.load(context); // Обновление списка причин отказов
                    publishProgress(0);
                    ReqBusinessRegions.load(context);
                    publishProgress(1);
                    ReqPriceType.load(context);
                    publishProgress(2);
                    ReqPriceList.load(context);
                    publishProgress(5);
                    ReqProductBalance.load(context);
                    publishProgress(4);
                    ReqSpecEvents.load(context);
                    publishProgress(3);
                    AppDB.getInstance(context).saveClients(ReqGetClients.load());
                break;
                case "regions":
                    publishProgress(0);
                    ReqBusinessRegions.load(context);
                break;
                case "price types":
                    publishProgress(1);
                    ReqPriceType.load(context);
                break;
                case "clients":
                    publishProgress(3);
                    AppDB.getInstance(context).saveClients(ReqGetClients.load());
                break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            dialog.cancel();
        }

        @Override
        protected void onCancelled(Void result)
        {
            if (dialog.isShowing())
                dialog.cancel();
        }

        private Dialog getProgressDialog(String text)
        {
            final Dialog dialog = new Dialog(context);
            dialog.setTitle(R.string.app_name);
            dialog.setContentView(R.layout.dialogbox_save);
            dialog.setCanceledOnTouchOutside(false);
            ((TextView)dialog.findViewById(R.id.dialogSLabel)).setText(text);
            return dialog;
        }
    }
}