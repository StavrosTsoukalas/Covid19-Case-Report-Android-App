package com.example.covid_19app;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LanguageHandler {

    static boolean isGreek;
    static boolean firstTime = true;

    public static void setLocale(Activity activity, boolean language) {
        String languageCode;
        String def_lang = Locale.getDefault().toString();

        if(firstTime) {
            isGreek = def_lang.equals("el_GR");
            firstTime = false;
        }
        if (isGreek != language) {
            if (language) {
                languageCode = "el";
            } else {
                languageCode = "en";
            }
            Locale locale = new Locale(languageCode);
            Resources resources = activity.getResources();
            Configuration config = resources.getConfiguration();
            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            activity.finish();
            activity.overridePendingTransition(0, 0);
            activity.startActivity(activity.getIntent());
            activity.overridePendingTransition(0, 0);
        }
        isGreek = language; //Stores the current language
    }
}
