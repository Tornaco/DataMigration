package org.newstand.datamigration.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.newstand.datamigration.R;
import org.newstand.datamigration.provider.SettingsProvider;

/**
 * Created by Nick@NewStand.org on 2017/5/3 16:01
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class AppIntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }

        addSlide(AppIntroFragment.newInstance(getString(R.string.title_backup_restore),
                getString(R.string.backup_intro),
                R.drawable.photo_backup_help_card_header,
                getResources().getColor(R.color.primary)));

        addSlide(AppIntroFragment.newInstance(getString(R.string.title_transport_sender_receiver),
                getString(R.string.transport_intro),
                R.drawable.image_exchange,
                getResources().getColor(R.color.primary)));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(getResources().getColor(R.color.primary_dark));

        setDoneText(getString(R.string.action_done));
        setImageNextButton(ContextCompat.getDrawable(this, R.drawable.ic_arrow_forward));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);

    }

    @Override
    public void onBackPressed() {
        // Hooked.
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        SettingsProvider.setAppIntroNoticed(true);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        SettingsProvider.setAppIntroNoticed(true);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}