package com.b2creatived.coloreyetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


public class Leaderboard extends AppCompatActivity {

    TextView tv_record, tv_record2, tv_rank, tv_date;
    SharedPreferences sharedPreferences;
    TabLayout tabLayout;
    String level = "", date_level = "",  timetrial = "", date_timetrial = "", rank_classic = "", rank_timetrial = "";
    LinearLayout leaderboard_loading_layout, ll_leaderboard, ll_leaderboard_no;
    LinearLayout ll_leaderboard_ll_record, ll_leaderboard_ll_rank_and_date;
    TextView tv_leaderboard_no;
    String playeremail;
    boolean load_successful = false;
    private static String download_leaderboard_classic_header = "http://www.dappwall.com/OddColor/download_leaderboard_classic_header.php";
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        leaderboard_loading_layout = (LinearLayout) findViewById(R.id.leaderboard_loading_layout);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Classic"));
        tabLayout.addTab(tabLayout.newTab().setText("Time Trial"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tv_record = (TextView) findViewById(R.id.tv_record);
        tv_record2 = (TextView) findViewById(R.id.tv_record2);
        tv_rank = (TextView) findViewById(R.id.tv_rank);
        tv_date = (TextView) findViewById(R.id.tv_date);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final LeaderboardPagerAdapter adapter = new LeaderboardPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    if (load_successful) {
                        ll_leaderboard_ll_record.setVisibility(View.VISIBLE);
                        ll_leaderboard_ll_rank_and_date.setVisibility(View.VISIBLE);
                        ll_leaderboard_no.setVisibility(View.GONE);
                        tv_rank.setText("#" + rank_classic);
                        tv_record.setText(level);
                        tv_record2.setText(getString(R.string.leaderboard_levels));
                        tv_date.setText(date_level.substring(5, 7) + "." + date_level.substring(8, 10) + "." + date_level.substring(0, 4));
                    } else {
                        ShowSigninText();
                        ll_leaderboard_ll_record.setVisibility(View.GONE);
                        ll_leaderboard_ll_rank_and_date.setVisibility(View.GONE);
                        ll_leaderboard_no.setVisibility(View.VISIBLE);
                    }
                } else if (tab.getPosition() == 1) {
                    if (load_successful) {
                        ll_leaderboard_ll_record.setVisibility(View.VISIBLE);
                        ll_leaderboard_ll_rank_and_date.setVisibility(View.VISIBLE);
                        ll_leaderboard_no.setVisibility(View.GONE);
                        tv_rank.setText("#" + rank_timetrial);
                        tv_record.setText(format_time(Integer.valueOf(timetrial)));
                        tv_record2.setText(getString(R.string.leaderboard_time));
                        tv_date.setText(date_timetrial.substring(5, 7) + "." + date_timetrial.substring(8, 10) + "." + date_timetrial.substring(0, 4));
                    } else {
                        ShowSigninText();
                        ll_leaderboard_ll_record.setVisibility(View.GONE);
                        ll_leaderboard_ll_rank_and_date.setVisibility(View.GONE);
                        ll_leaderboard_no.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ll_leaderboard = (LinearLayout) findViewById(R.id.ll_leaderboard);
        ll_leaderboard_no = (LinearLayout) findViewById(R.id.ll_leaderboard_no);
        ll_leaderboard_ll_record = (LinearLayout) findViewById(R.id.ll_leaderboard_ll_record);
        ll_leaderboard_ll_rank_and_date = (LinearLayout) findViewById(R.id.ll_leaderboard_ll_rank_and_date);
        tv_leaderboard_no = (TextView) findViewById(R.id.tv_leaderboard_no);

        leaderboard_loading_layout.setVisibility(View.VISIBLE);
        LoadPlayerEmail();

        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //playeremail = sharedPreferences.getString("playeremail", "");
       // playeremail = "";
        //Log.i("playeremail", playeremail + "");
        if (!playeremail.equals("")) {
            Load_Data(playeremail);
            ll_leaderboard_ll_record.setVisibility(View.VISIBLE);
            ll_leaderboard_ll_rank_and_date.setVisibility(View.VISIBLE);
            ll_leaderboard_no.setVisibility(View.GONE);
        } else {
            ShowSigninText();
            ll_leaderboard_ll_record.setVisibility(View.GONE);
            ll_leaderboard_ll_rank_and_date.setVisibility(View.GONE);
            ll_leaderboard_no.setVisibility(View.VISIBLE);

        }

    }


    private void Load_Data(final String email) {
    //Log.i("LOAD DATA", "started");
        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, download_leaderboard_classic_header, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                int success = 0;
                try {
                    success = response.getInt("success");
                    if (success == 1) { //record uploaded successfully
                        level = response.getString("LEVEL");
                        date_level = response.getString("DATE_LEVEL");
                        timetrial = response.getString("TIMETRIAL");
                        date_timetrial = response.getString("DATE_TIME_TRIAL");
                        rank_classic = response.getString("RANK_CLASSIC");
                        rank_timetrial = response.getString("RANK_TIMETRIAL");
                        /*Log.i("level", level + "");
                        Log.i("date_level", date_level + "");
                        Log.i("timetrial", timetrial + "");
                        Log.i("date_timetrial", date_timetrial + "");
                        Log.i("rank_classic", rank_classic + "");
                        Log.i("rank_timetrial", rank_timetrial + "");*/

                        if (tabLayout.getSelectedTabPosition() == 0) {
                            tv_rank.setText("#" + rank_classic);
                            tv_record.setText(level);
                            tv_record2.setText(getString(R.string.leaderboard_levels));
                            tv_date.setText(date_level.substring(5, 7) + "." + date_level.substring(8, 10) + "." + date_level.substring(0, 4));
                        }
                        leaderboard_loading_layout.setVisibility(View.GONE);
                        load_successful = true;
                    } else {
                        Log.i("LoadData", "record upload failed. Maybe bad network reception?");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("LoadData", "Error: " + e);
                    //Toast.makeText(Leaderboard.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("LoadData", "Error: " + error);
                //Toast.makeText(Leaderboard.this, "Something went wrong. Maybe bad network reception?", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

        };

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    public String format_time(int time) {
        DecimalFormat df = new DecimalFormat("00");
        DecimalFormat df2 = new DecimalFormat("0");
        int h = (int)(time / (3600 * 1000));
        int r = (int)(time % (3600 * 1000));
        int m = (int)(r / (60 * 1000));
        r = (int)(r % (60 * 1000));
        int s = (int)(r / 1000);
        int ms = (time - ((60 * m + s) * 1000)) / 100; //3034-3000=34 -- 2994-2000=994
        String text = "";
        if (h > 0) text += df.format(h) + ":";
        text += df.format(m) + ":";
        text += df.format(s) + ".";
        text += df2.format(ms);
        return text;
    }

    public void ShowSigninText() {
        final SpannableStringBuilder sb = new SpannableStringBuilder(getString(R.string.leaderboard_showsignintext));
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 253, 198));
        final ForegroundColorSpan fcs2 = new ForegroundColorSpan(Color.rgb(255, 253, 198));
        final ForegroundColorSpan fcs3 = new ForegroundColorSpan(Color.rgb(255, 253, 198));
        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final StyleSpan bss2 = new StyleSpan(android.graphics.Typeface.BOLD);
        final StyleSpan bss3 = new StyleSpan(android.graphics.Typeface.BOLD);
        sb.setSpan(fcs, 40, 46, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 40, 46, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(fcs2, 48, 52, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss2, 48, 52, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(fcs3, 61, 65, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss3, 61, 65, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        tv_leaderboard_no.setText(sb);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("back", "leaderboard");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void LoadPlayerEmail(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playeremail = sharedPreferences.getString("playeremail", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName("Leaderboard");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            mTracker.enableAdvertisingIdCollection(true);
        }
    }
}
