package com.tachyon.techlabs.iplauction;

import android.Manifest;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;
import com.tachyon.techlabs.iplauction.R;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.app_intro_text)
                .image(R.drawable.ipl_logo)
                .background(R.color.ipl_logo_top)
                .backgroundDark(R.color.ipl_logo_top_dark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.page1title)
                .description(R.string.page1sub)
                .image(R.drawable.cricket_club)
                .background(R.color.owlblue)
                .backgroundDark(R.color.owlbluedark)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title(R.string.page2title)
                .description(R.string.page2sub)
                .image(R.drawable.cricket_player)
                .background(R.color.owlyellow)
                .backgroundDark(R.color.owlyellowdark)
                .build());

        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                if(position==3)
                {
                    startActivity(new Intent(IntroActivity.this, AfterRegistrationMainActivity.class));
                }
                return true;
            }

            @Override
            public boolean canGoBackward(int position) {
                return true;
            }
        });


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
