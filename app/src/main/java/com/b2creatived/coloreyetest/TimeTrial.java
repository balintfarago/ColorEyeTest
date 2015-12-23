package com.b2creatived.coloreyetest;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.facebook.FacebookSdk;

public class TimeTrial extends Activity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    TextView tv_level;
    Chronometer chronometer;
    Button btn_redraw, btn_half;
    TextView tv_animation;
    LinearLayout ll_header, ll_footer;
    ImageView iv_animation;

    Typeface RobotoLight, RobotoMedium, RobotoRegular, RobotoBold;
    int buttons_in_row;
    int random_button;
    int width;
    int level;
    int actual_record = 0;
    int tiles_in_a_row;
    boolean gothelp = false;
    LinearLayout layout;
    SharedPreferences sharedpref;
    SharedPreferences sharedPreferences;
    String playername = "Unknown soldier";
    String playeremail = "";
    String show_connected_toast = "yes";
    boolean show_signin_dialog_timetrial = false;
    int num_runs_timetrial = 0;
    int alltime = 0;
    boolean leaving_activity = false;

    Dialog dialogsignin;
    Button btn_signin;

    private static final int RC_SIGN_IN = 1;  /* Request code used to invoke sign in user interactions. */
    private GoogleApiClient mGoogleApiClient; /* Client used to interact with Google APIs. */
    private boolean mIsResolving = false; /* Is there a ConnectionResult resolution in progress? */
    private boolean mShouldResolve = false; /* Should we automatically resolve ConnectionResults when possible? */
    String logintype = "";
    boolean show_presplash = true;
    CoordinatorLayout coordinatorlayout;

    private static String save_result_timetrial = "http://www.dappwall.com/OddColor/save_result_timetrial.php";
    private static String signup_with_result_timetrial = "http://www.dappwall.com/OddColor/signup_with_result_timetrial.php";

    Arrays arrays = new Arrays();
    View mDialogView;
    Tracker mTracker;

    private static final int REQUEST_INVITE = 2; /* Request code for inviting friends */
    private static final int SHARE_GOOGLE = 100;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    InterstitialAd mInterstitialAd;
    boolean hide_share_dialog = false;
    boolean show_adsgone_msg = true;
    Dialog dialog_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.timetrial);
        //Log.i("ACTIVITY", "----------TIMETRIAL-------");
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        //with the code below we would know if the user has really shared our game. However, this works only if the user is
        //signed in the app, otherwise the onSuccess will be activated even if the user cancels the share dialog. This is by design
        //by the facebook team. Still, we want to display a message to the user when the share happens.
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_Facebook")
                            .setAction("Click")
                            .setLabel("Classic")
                            .build());
                }

                SaveHideShareDialog("hide_share_dialog", true);

            }

            @Override
            public void onCancel() {
                Toast.makeText(TimeTrial.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(TimeTrial.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.Interstitial_Timetrial));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                leaving_activity = true;
                Intent go_home = new Intent(TimeTrial.this, MainScreen.class);
                go_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                go_home.putExtra("SHOW_CONNECTED_TOAST", "no");
                go_home.putExtra("COMING_FROM_GAME", true);
                startActivity(go_home);
            }
        });

        Random r = new Random();
        int n = r.nextInt(2) + 0;
        Log.i("random", "" + n);
        LoadHideShareDialog();
        hide_share_dialog = true; //only until the ads can be created!
        Log.i("hide_share_dialog", hide_share_dialog + "");
        if (!hide_share_dialog && n % 2 == 0) requestNewInterstitial();

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        RobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.otf");
        RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.otf");
        RobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.otf");
        RobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.otf");

        coordinatorlayout = (CoordinatorLayout)findViewById(R.id.coordinatorlayout);

        //check if user is signed-in with Google+. The check will be done in GameOver();
        LoadLoginType();
        LoadPlayerName();
        LoadPlayerEmail();
        LoadNumRunsTimeTrial();

        chronometer = (Chronometer) findViewById(R.id.chronometer1);
        tv_level = (TextView)findViewById(R.id.tv_level);
        tv_level.setText("10");
        btn_redraw = (Button) findViewById(R.id.btn_redraw);
        btn_half = (Button) findViewById(R.id.btn_half);
        tv_animation = (TextView)findViewById(R.id.tv_animation);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/IndieFlower.otf");
        tv_animation.setTypeface(myTypeface);


        ll_header = (LinearLayout)findViewById(R.id.ll_header);
        ll_footer = (LinearLayout)findViewById(R.id.ll_footer);
        layout = (LinearLayout) findViewById(R.id.linear_layout_tags);

        iv_animation = (ImageView) findViewById(R.id.iv_animation);

        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);

        tv_level.setTypeface(RobotoLight);
        chronometer.setTypeface(RobotoLight);
        btn_redraw.setTypeface(RobotoLight);
        btn_half.setTypeface(RobotoLight);

        btn_redraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                       .setCategory("RedrawButton")
                       .setAction("Click")
                       .setLabel("TimeTrial")
                       .build());
                }

                tiles_in_a_row = 0;
                gothelp = true;
                layout.removeAllViews();
                drawMapHard(7);

            }
        });

        btn_half.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("HalfButton")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }
                tiles_in_a_row = 0;
                gothelp = true;
                halfTiles(49);
            }
        });

        if (!sharedpref.getBoolean("HELP_TIMETRIAL", false)) {
            ShowGoal();
        } else {
            StartGame();
        }

      /*  chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            public void onChronometerTick(Chronometer chronometer) {
                if (chronometer.getText().toString().equalsIgnoreCase("00:05:0")) { //When reaches 5 seconds.
                    //Define here what happens when the Chronometer reaches the time above.
                    chronometer.stop();
                    Toast.makeText(getBaseContext(), "Reached the end.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });*/

    }

    public void drawMapMedium(Integer buttons) {

        buttons_in_row = buttons;
        Random r = new Random();
        int random_color = (r.nextInt(arrays.colors_medium.length) + 0);
        random_button = (r.nextInt(buttons_in_row * buttons_in_row) + 1);

        layout = (LinearLayout) findViewById(R.id.linear_layout_tags);
        layout.setOrientation(LinearLayout.VERTICAL);
        Button btn = null;
        for (int i = 0; i < buttons_in_row; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < buttons_in_row; j++) {
                btn = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(5, 5, 5, 5);
                btn.setLayoutParams(params);
                btn.setId(j + 1 + (i * buttons_in_row));
                btn.setWidth(width / buttons_in_row);
                btn.setHeight(width / buttons_in_row);
                btn.setBackgroundResource(R.drawable.button_wrong);
                GradientDrawable drawable = (GradientDrawable) btn.getBackground();
                drawable.setColor(Color.parseColor("#" + arrays.getMediumColor0(random_color)));
                btn.setOnClickListener(this);
                row.addView(btn);
            }

            layout.addView(row);
        }
        Button b = (Button) layout.findViewById(random_button);
        GradientDrawable drawable2 = (GradientDrawable) b.getBackground();
        drawable2.setColor(Color.parseColor("#" + arrays.getMediumColor1(random_color)));

    }

    public void drawMapHard(Integer buttons) {

        buttons_in_row = buttons;
        Random r = new Random();
        int random_color = (r.nextInt(arrays.colors_hard.length) + 0);
        random_button = (r.nextInt(buttons_in_row * buttons_in_row) + 1);

        layout = (LinearLayout) findViewById(R.id.linear_layout_tags);
        layout.setOrientation(LinearLayout.VERTICAL);
        Button btn = null;
        for (int i = 0; i < buttons_in_row; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < buttons_in_row; j++) {
                btn = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
                params.setMargins(5, 5, 5, 5);
                btn.setLayoutParams(params);
                btn.setId(j + 1 + (i * buttons_in_row));
                btn.setWidth(width / buttons_in_row);
                btn.setHeight(width / buttons_in_row);
                btn.setBackgroundResource(R.drawable.button_wrong);
                GradientDrawable drawable = (GradientDrawable) btn.getBackground();
                drawable.setColor(Color.parseColor("#" + arrays.getHardColor0(random_color)));
                btn.setOnClickListener(this);
                row.addView(btn);
            }

            layout.addView(row);
        }
        Button b = (Button) layout.findViewById(random_button);
        GradientDrawable drawable2 = (GradientDrawable) b.getBackground();
        drawable2.setColor(Color.parseColor("#" + arrays.getHardColor1(random_color)));

    }


    public void onClick(View view) {
        ((Button) view).setText("*");
        ((Button) view).setEnabled(false);

        int myId = view.getId();
        if (myId == random_button) {

            layout.removeAllViews();
            tiles_in_a_row++;

            level--;
            if (level > 7) {
                drawMapMedium(7);
            } else if (level > 0 && level <= 7) {
                drawMapHard(7);
            } else {
                GameOver();
            }


            /*if (level > 0) {
                drawMapMedium(7);
            } else {
                GameOver();
            }*/

            tv_level.setText(String.valueOf(level));
        } else {
            Button b3 = (Button) layout.findViewById(myId);
            GradientDrawable drawable3 = (GradientDrawable) b3.getBackground();
            drawable3.setColor(Color.parseColor("#000000"));
        }

    }

    public void halfTiles(int num) {

        Random r = new Random();
        int random_tile;

        List<Integer> list = new ArrayList<>();
        for (int i=1; i<=num; i++) {
            list.add(i);
        }

        int x = list.size()/2 + 1;

        List<Integer> list2 = new ArrayList<>();
        for (int i=1; i<x; i++) {
            random_tile = (r.nextInt(list.size()) + 1);
            if (random_tile == random_button) {
                i--;
            } else if (!list2.contains(random_tile)) {
                list2.add(random_tile);
            } else {
                i--;
            }
        }

        /*for (int i=0; i<list2.size(); i++) {
            Log.i("list2[" + i + "]", list2.get(i) + "");
        }*/

        for (int i=0; i<list.size(); i++) {
            for (int j=0; j<list2.size(); j++) {
                if (list.get(i) == list2.get(j)) {
                    Button b2 = (Button) layout.findViewById(list.get(i));
                    b2.setVisibility(View.INVISIBLE);
                }
            }
        }
    }




    public void GameOver () {

        dialog_over = new Dialog(TimeTrial.this, R.style.DialogTheme);
        dialog_over.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //dialog_over.setContentView(R.layout.dialog_result_timetrial);
        LayoutInflater inflater = this.getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_result_timetrial, null);
        dialog_over.setContentView(mDialogView);

        dialog_over.setCancelable(false);
        dialog_over.show();

        //DisplayMetrics metrics = getResources().getDisplayMetrics();
        //int width = metrics.widthPixels;
        //dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        int width, height;

        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        width = point.x;
        height = point.y;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_over.getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        dialog_over.getWindow().setAttributes(lp);

        TextView tv_result_1 = (TextView) dialog_over.findViewById(R.id.tv_result_1);
        TextView tv_result_2 = (TextView) dialog_over.findViewById(R.id.tv_result_2);
        TextView tv_result_4 = (TextView) dialog_over.findViewById(R.id.tv_result_4);
        TextView tv_result_3b = (TextView) dialog_over.findViewById(R.id.tv_result_3b);

        ImageView iv_star1 = (ImageView) dialog_over.findViewById(R.id.iv_star1);
        ImageView iv_star2 = (ImageView) dialog_over.findViewById(R.id.iv_star2);
        ImageView iv_star3 = (ImageView) dialog_over.findViewById(R.id.iv_star3);
        ImageView iv_flag = (ImageView) dialog_over.findViewById(R.id.iv_flag);
        LinearLayout lv_ach_unlocked = (LinearLayout) dialog_over.findViewById(R.id.lv_ach_unlocked);

        String time = chronometer.getText().toString();
        int minutes = Integer.valueOf(time.substring(0, 2));
        int seconds = Integer.valueOf(time.substring(3, 5));
        int millis = Integer.valueOf(time.substring(6));
        tv_result_1.setText(String.valueOf(chronometer.getText().toString()));

        alltime = (60 * minutes + seconds) * 1000 + millis*100;

        //Log.i("alltime", alltime + "");

        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        //Log.i("actual record", sharedpref.getInt("TIMERECORD", 1000000) + "");
        actual_record = sharedpref.getInt("TIMERECORD", 1000000);
        //Log.i("actual record and alltime", actual_record + " ," + alltime);

        if (alltime < actual_record) { //if new record was made
            //Log.i("NO TIMERECORD", "alltime < actual");

            Calendar c = Calendar.getInstance();
            //Log.i("Current date", c.getTime() + "");

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());
            //Log.i("Current formattedDate", formattedDate + "");

            //Save the record
            SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
            editor.putInt("TIMERECORD", alltime);
            editor.putString("TIMERECORD_DATE", formattedDate);
            editor.commit();

            if (actual_record == 1000000) { //if this was the first game
                //Log.i("RECORD STATUS", "NEW record - First game");
                tv_result_2.setText("That's a new record!");  //just for motivational reasons to make user play more
                tv_result_2.setVisibility(View.VISIBLE);
                iv_flag.setVisibility(View.VISIBLE);
                tv_result_3b.setVisibility(View.GONE);
            } else { //if this was the second, third etc. game
                //Log.i("RECORD STATUS", "NEW record - Not the first game");

                //Let's calculate the difference between the actual_record and the current result
                int diff = actual_record - alltime;
                String diff_formatted = format_time(diff);

                tv_result_2.setText(getString(R.string.newrecord));
                tv_result_3b.setVisibility(View.VISIBLE);
                tv_result_3b.setTextColor(Color.parseColor("#6DDC0C"));
                tv_result_3b.setText("(-" + diff_formatted + ")");
                iv_flag.setVisibility(View.VISIBLE);
            }

            //If new record and we have the user's email either because they are signed in or because they were signed in and saved
            //their email in SharedPreferences
            LoadPlayerEmail();
            if (logintype.equals("google+")) {
                if (!playeremail.equals("")) {
                    //Log.i("SaveResult_TimeTrial_g+", "playermail ok");
                    if (CheckNetwork.isInternetAvailable(TimeTrial.this)) SaveResult_TimeTrial(playeremail, String.valueOf(alltime), formattedDate); //convert alltime to string because the hashmap in Volley expects a String
                } else {
                    //Log.i("SaveResult_TimeTrial_g+", "playeremail = ''");
                }
            } else if (logintype.equals("guest")) {
                if (!playeremail.equals("")) {
                    //Log.i("SaveResult_TimeTrial_guest", "playermail ok");
                    if (CheckNetwork.isInternetAvailable(TimeTrial.this)) SaveResult_TimeTrial(playeremail, String.valueOf(alltime), formattedDate);
                } else {
                    //Log.i("SaveResult_TimeTrial_guest", "playeremail = ''");
                }
            }

            //...

        } else if (alltime == actual_record) {
            if (actual_record == 1000000) { //if this was the first game
                //Log.i("RECORD STATUS", "DEUCE - First game");
                tv_result_2.setText("Almost!");  //just for motivational reasons to make user play more
                tv_result_2.setVisibility(View.VISIBLE);
                iv_flag.setVisibility(View.GONE);
                tv_result_3b.setVisibility(View.GONE);
            } else { //if this was the second, third etc. game
                //Log.i("RECORD STATUS", "DEUCE - Not the first game");

                tv_result_2.setText(getString(R.string.almost));
                tv_result_3b.setVisibility(View.VISIBLE);
                tv_result_3b.setTextColor(Color.parseColor("#0BB3DC"));
                tv_result_3b.setText("(=" + format_time(actual_record) + ")");
                iv_flag.setVisibility(View.GONE);
            }

        } else { //if no record was made
            if (actual_record != 1000000) { //if not the first game
                //Log.i("RECORD STATUS", "NO record - Not the first game");

                //Let's calculate the difference between the actual_record and the current result
                int diff = Math.abs(actual_record - alltime); //othwerwise the diff will be negative what we wouldn't want
                String diff_formatted = format_time(diff);

                tv_result_3b.setVisibility(View.VISIBLE);
                tv_result_3b.setTextColor(Color.parseColor("#EE5F27"));
                tv_result_3b.setText("(+" + diff_formatted + ")");

                tv_result_2.setText(getString(R.string.youcandobetterthanthat));
                tv_result_2.setVisibility(View.VISIBLE);
                iv_flag.setVisibility(View.GONE);
            } else { //if first game
                //Log.i("RECORD STATUS", "NO record - First game");
                tv_result_2.setText(getString(R.string.youcandobetterthanthat));
                tv_result_3b.setVisibility(View.GONE);
                iv_flag.setVisibility(View.GONE);
            }
        }

        String ach_str_1 = "00:14:00";
        String ach_str_2 = "00:11:00";
        String ach_str_3 = "00:09:00";

        int mach_1 = Integer.valueOf(ach_str_1.substring(0, 2));
        int sach_1 = Integer.valueOf(ach_str_1.substring(3, 5));
        int msach_1 =Integer.valueOf(ach_str_1.substring(6));
        int alltime_ach1 = (60 * mach_1 + sach_1) * 1000 + msach_1;

        int mach_2 = Integer.valueOf(ach_str_2.substring(0, 2));
        int sach_2 = Integer.valueOf(ach_str_2.substring(3, 5));
        int msach_2 =Integer.valueOf(ach_str_2.substring(6));
        int alltime_ach2 = (60 * mach_2 + sach_2) * 1000 + msach_2;

        int mach_3 = Integer.valueOf(ach_str_3.substring(0, 2));
        int sach_3 = Integer.valueOf(ach_str_3.substring(3, 5));
        int msach_3 =Integer.valueOf(ach_str_3.substring(6));
        int alltime_ach3 = (60 * mach_3 + sach_3) * 1000 + msach_3;

        if (alltime < alltime_ach3) {
            //user did under 9 seconds so we give him/her all the three badges
            if ( !(sharedpref.getBoolean("ACH4", false) && sharedpref.getBoolean("ACH5", false) && sharedpref.getBoolean("ACH6", false))) {
                iv_star1.setVisibility(View.VISIBLE);
                iv_star3.setVisibility(View.VISIBLE);
                iv_star2.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }

            if (!sharedpref.getBoolean("ACH6", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH6", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }
            if (!sharedpref.getBoolean("ACH5", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH5", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }
            if (!sharedpref.getBoolean("ACH4", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH4", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }


        } else if (alltime >= alltime_ach3 && alltime < alltime_ach2) {
            //user did between 9 and 11 seconds so we give him/her the silver and bronze badges
            if ( !(sharedpref.getBoolean("ACH4", false) && sharedpref.getBoolean("ACH5", false))) {
                iv_star1.setVisibility(View.VISIBLE);
                iv_star2.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }

            if (!sharedpref.getBoolean("ACH5", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH5", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }
            if (!sharedpref.getBoolean("ACH4", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH4", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }

        } else if (alltime >= alltime_ach2 && alltime < alltime_ach1) {
            //user did between 11 and 14 seconds so we give him/her the bronze badge
            if (!sharedpref.getBoolean("ACH4", false)) {
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("ACH4", true);
                editor.commit();
                tv_result_4.setVisibility(View.VISIBLE);
                iv_star1.setVisibility(View.VISIBLE);
                lv_ach_unlocked.setVisibility(View.VISIBLE);
            }
        }

        //Show the sign in dialog when the user plays the 2nd or 7th time.
        //Show the dialog only once in this type of game
        LoadShowSignInDialogState();
        LoadNumRunsTimeTrial();
        //Log.i("GameOver num_runs_timetrial", num_runs_timetrial + "");
        //Log.i("GameOver show_signin_dialog_timetrial", show_signin_dialog_timetrial + "");
        if ( (num_runs_timetrial == 2 || num_runs_timetrial == 7) && !show_signin_dialog_timetrial && CheckNetwork.isInternetAvailable(this) ) {
            if (!logintype.equals("google+")) {
                ShowSignInDialog();
                //we can't set the state here to true because the user can still cancel the dialog at the 2nd run and we want
                //to show them the dialog again some time later
            }
        }

        if (chronometer != null) chronometer.stop();

        Button btn_again =(Button) dialog_over.findViewById(R.id.btn_again);
        btn_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("AgainButton")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }
                dialog_over.dismiss();
                StartGame();

            }
        });

        ImageButton btn_home =(ImageButton) dialog_over.findViewById(R.id.btn_home);
        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("HomeButton")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }

                leaving_activity = true;
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Intent go_home = new Intent(TimeTrial.this, MainScreen.class);
                    go_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    go_home.putExtra("SHOW_CONNECTED_TOAST", "no");
                    go_home.putExtra("COMING_FROM_GAME", true);
                    startActivity(go_home);
                }
            }
        });

        //since the google+ app will open, we are leaving the activity. We need to set leaving_activity = true otherwise
        //onStart will be active and thus the Connected to Google+ msg will be displayed
        leaving_activity = true;
        Button btn_share_google = (Button) dialog_over.findViewById(R.id.btn_share_google);
        btn_share_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_Google")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }

                Intent shareIntent = new PlusShare.Builder(TimeTrial.this)
                        .setType("text/plain")
                        .setText("My time is " + String.valueOf(chronometer.getText().toString()) + " in Eye Test - Spot the Odd! Can you do it faster?")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest"))
                        .getIntent();

                startActivityForResult(shareIntent, SHARE_GOOGLE);

            }
        });

        //since the facebook app will open, we are leaving the activity. We need to set leaving_activity = true otherwise
        //onStart will be active and thus the Connected to Google+ msg will be displayed
        leaving_activity = true;
        Button btn_share_facebook = (Button) dialog_over.findViewById(R.id.btn_share_facebook);
        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_Facebook")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    String sharetext = "My time is " + String.valueOf(chronometer.getText().toString()) + " in Eye Test - Spot the Odd! Can you do it faster?";
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(sharetext)
                            .setContentDescription("Download this cool app, Eye Test - Spot the Odd!")
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest"))
                            .build();

                    shareDialog.show(linkContent);
                }
            }
        });

        Button btn_share_email = (Button) dialog_over.findViewById(R.id.btn_share_email);
        btn_share_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_Email")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","balintfarago@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "balintfarago@gmail.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Thought you would like this app");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "My time is " + String.valueOf(chronometer.getText().toString()) + " in Eye Test - Spot the Odd! Can you do it faster?\n\nDownload from here: https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        Button btn_invite = (Button) dialog_over.findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("InviteFriends")
                            .setAction("Click")
                            .setLabel("TimeTrial")
                            .build());
                }

                //since the google+ app will open, we are leaving the activity. We need to set leaving_activity = true otherwise
                //onStart will be active and thus the Connected to Google+ msg will be displayed
                leaving_activity = true;
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                        .setCallToActionText(getString(R.string.invitation_cta))
                        .build();
                startActivityForResult(intent, REQUEST_INVITE);

            }
        });

    }

    public void StartGame() {
        if (chronometer != null) chronometer.start();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        width = (width * 9) / 10;
        layout.removeAllViews();

        gothelp = false;
        actual_record = 0;
        tiles_in_a_row = 0;
        level = 10;
        tv_level.setText(String.valueOf(level));
        drawMapHard(7);
        LoadNumRunsTimeTrial();
        num_runs_timetrial += 1;
        SaveNumRunsTimeTrial("num_runs_timetrial", num_runs_timetrial);
    }

    public void ShowGoal() {
        final Dialog dialog_goal = new Dialog(TimeTrial.this);
        dialog_goal.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_goal.setContentView(R.layout.dialog_game_instructions);
        dialog_goal.setCancelable(false);
        dialog_goal.show();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog_goal.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_goal = (TextView) dialog_goal.findViewById(R.id.tv_goal);
        tv_goal.setText(getString(R.string.guide_timetrial));

        Button btn_ok = (Button) dialog_goal.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_goal.dismiss();
                StartGame();
                SharedPreferences.Editor editor = getSharedPreferences("PREFS_NAME", MODE_PRIVATE).edit();
                editor.putBoolean("HELP_TIMETRIAL", true);
                editor.commit();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("onononT", "onPause");
        //if user opens another app then we need to set this otherwise Connected to Google+ msg will be displayed
        leaving_activity = true;
        mGoogleApiClient.connect();
        if (chronometer != null) chronometer.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("onononT", "onStop");
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        if (chronometer != null) chronometer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("onononT", "onResume");
        if (mTracker != null) {
            mTracker.setScreenName("TimeTrial");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            mTracker.enableAdvertisingIdCollection(true);
        }
        //for now when the user comes back from another app we don't stop the clock
        if (chronometer.getTimeElapsed() > 0) {
            chronometer.resume();
            //Log.i("getTimeElapsed", String.valueOf(chronometer.getTimeElapsed())); //11178 = 11 s 178 ms, 8301 = 8s301ms
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chronometer != null) chronometer.stop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d("TAG onConnecteddddd", "" + bundle);
        mShouldResolve = false;
        //if (bundle != null) {
            Log.i("bundle", "not null");
            String player_id = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();
            String player_full_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
            String player_given_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName();
            String player_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String player_PhotoUrl = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl();
            String player_GooglePlusProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getUrl();

            //Log.e("TAG onConnected", "Id: " + player_id + ", Name: " + player_full_name + ", given_name: " + player_given_name + ", email: " + player_email + ", Image: " + player_PhotoUrl + ", plusProfile: " + player_GooglePlusProfile);
            //Log.e("TAG onConnected", "time: " + alltime);

            Log.e("TAG onConnected", "leaving_activity: " + leaving_activity);
            /*if (!leaving_activity) {
                Toast.makeText(TimeTrial.this, "Connected to Google+", Toast.LENGTH_SHORT).show();
                ShowSnackBarConnected();
            }*/
            show_connected_toast = "no";
            show_signin_dialog_timetrial = true;
            SaveShowSignInDialogState("show_signin_dialog_timetrial", true);

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c.getTime());

            sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
            int level = sharedpref.getInt("RECORD", 0);

            //We not only need to create a new user in the database but we also need to save the record
            if (CheckNetwork.isInternetAvailable(this)) {
                CreateUserAndUploadRecord(player_id, player_full_name, player_given_name, player_email, player_PhotoUrl, player_GooglePlusProfile, String.valueOf(level), formattedDate, String.valueOf(alltime), formattedDate);
            } else {
                ShowSnackBarNoInternet();
            }

        //} else Log.i("bundle", "null");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("TAG onConnectionSusp", "onConnectionSuspended() " + i);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //Log.i("TAG", "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    //Log.i("TAG onConnectionFailed", "Trying to connect");
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    //Log.i("TAG onConnectionFailed", "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                //Log.e("TAG onConnectionFailed", "error: " + connectionResult);
            }
        }
    }

    private void onSignInClicked() {
        //Log.i("TAG onSignInClicked", "User clicked the sign-in button");
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically connect in the future.
        //Log.i("TAG onSignInClicked", "User clicked the sign-out button");
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        SaveLoginType("logintype", "guest");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //Log.d("TAG", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
                //If the user cancelled the sign in we need to change back the SIGN IN WITH GOOGLE+ button
                if (dialogsignin.isShowing()) {
                    btn_signin.setText(getString(R.string.classic_signinginwithgoogle));
                    btn_signin.setEnabled(true);
                    btn_signin.setClickable(true);
                    btn_signin.setBackgroundResource(R.drawable.google_button_selector);
                }
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

        if (requestCode == SHARE_GOOGLE) {
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share_Google")
                        .setAction("Done")
                        .setLabel("TimeTrial")
                        .build());
            }

            SaveHideShareDialog("hide_share_dialog", true);

        }

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("InviteFriends")
                            .setAction("Done")
                            .setLabel("TimeTrial")
                            .build());
                }
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);

                SaveHideShareDialog("hide_share_dialog", true);

                //Log.d("INVITES SENT", getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.send_failed), Snackbar.LENGTH_LONG);
                snackbar.setActionTextColor(Color.CYAN);
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                snackbar.show();
            }
        }


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

    public void SaveLoginType(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void LoadLoginType(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        logintype = sharedPreferences.getString("logintype", "guest");
    }

    public void SaveSessionState(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public void LoadSessionState(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        show_presplash = sharedPreferences.getBoolean("show_presplash", true);
    }

    public void SaveShowSignInDialogState(String key, boolean value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void LoadShowSignInDialogState() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        show_signin_dialog_timetrial = sharedPreferences.getBoolean("show_signin_dialog_timetrial", false);
    }

    public void SaveNumRunsTimeTrial(String key, int value) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void LoadNumRunsTimeTrial() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        num_runs_timetrial = sharedPreferences.getInt("num_runs_timetrial", 0);
    }

    public void SavePlayerName(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void LoadPlayerName(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playername = sharedPreferences.getString("playername", "Unknown soldier");
    }

    public void SavePlayerEmail(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void LoadPlayerEmail(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playeremail = sharedPreferences.getString("playeremail", "");
    }

    public void SavePlayerPhoto(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void SavePlayerID(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void SaveHideShareDialog(String key, boolean value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void LoadHideShareDialog(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        hide_share_dialog = sharedPreferences.getBoolean("hide_share_dialog", false);
    }


    private void SaveResult_TimeTrial(final String email, final String time, final String formattedDate) {
        //Log.i("SaveResult_TimeTrial", "START");
        //Log.i("email", email);
        //Log.i("time", time + "");
        //Log.i("formattedDate", formattedDate + "");

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, save_result_timetrial, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("SaveResult_TimeTrial", "onResponse");

                int success = 0;
                try {
                    success = response.getInt("success");
                    //Log.i("SaveResult_TimeTrial success", "" + success);
                    if (success == 1) { //record uploaded successfully
                        //Log.i("SaveResult_TimeTrial", "record upload successful");
                    } else {
                        //Log.i("SaveResult_TimeTrial", "record upload failed. Maybe bad network reception?");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.i("SaveResult_TimeTrial", "Error: " + e);
                    //Log.i("SaveResult_TimeTrial", String.valueOf(response));
                    //Toast.makeText(TimeTrial.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("SaveResult_TimeTrial", "Error: " + error);
                //Toast.makeText(TimeTrial.this, "Something went wrong. Maybe bad network reception?", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("time", time);
                params.put("date", formattedDate);
                return params;
            }

        };

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void CreateUserAndUploadRecord(final String google_id, final String full_name, final String given_name, final String email, final String photourl, final String googleplusprofile, final String level, final String date_level, final String time, final String date_timetrial) {
        /*Log.i("CreateUserAndUploadRecord", "START");
        Log.i("google_id", google_id);
        Log.i("full_name", full_name);
        Log.i("given_name", given_name);
        Log.i("email", email);
        Log.i("photourl", photourl);
        Log.i("googleplusprofile", googleplusprofile);
        Log.i("level", level);
        Log.i("date_level", date_level);
        Log.i("time", time);
        Log.i("date_timetrial", date_timetrial);*/

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, signup_with_result_timetrial, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("CreateUserAndUploadRecord", "onResponse");

                int success = 0;
                try {
                    success = response.getInt("success");
                    //Log.i("CreateUserAndUploadRecord success", "" + success);
                    if (success == 1) { //new user added to database
                        String lastid = response.getString("lastid");
                        //Log.i("CreateUserAndUploadRecord lastid", "success: " + lastid);

                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (lastid != null && lastid.length() > 0) SavePlayerID("playerid", lastid);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);

                        ShowSnackBarConnectedOverDialog();
                        //Dismiss SignIn Dialog
                        if (dialogsignin != null && dialogsignin.isShowing()) {
                            dialogsignin.dismiss();
                        }

                    } else if (success == 2) { //user already registered
                        //This is activated when the user uninstalled the app some time in the past and now they install it again.

                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);

                        //Dismiss SignIn Dialog
                        if (dialogsignin != null && dialogsignin.isShowing()) {
                            dialogsignin.dismiss();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                        if (dialogsignin != null && dialogsignin.isShowing()) {
                            dialogsignin.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.i("CreateUserAndUploadRecord", "Error: " + e);
                    //Log.i("Response", String.valueOf(response));
                    Toast.makeText(TimeTrial.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    if (dialogsignin != null && dialogsignin.isShowing()) {
                        dialogsignin.dismiss();
                    }
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("CreateUserAndUploadRecord", "Error: " + error);
                Toast.makeText(TimeTrial.this, "Something went wrong. Maybe bad network reception?", Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("google_id", google_id);
                params.put("full_name", full_name);
                params.put("given_name", given_name);
                params.put("email", email);
                params.put("photourl", photourl);
                params.put("googleplusprofile", googleplusprofile);
                params.put("level", level);
                params.put("date_level", date_level);
                params.put("time", time);
                params.put("date_timetrial", date_timetrial);
                return params;
            }

        };

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void ShowSignInDialog() {
        dialogsignin = new Dialog(TimeTrial.this, R.style.CustomDialog);
        dialogsignin.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogsignin.setContentView(R.layout.dialog_signin_motivator);
        dialogsignin.setCancelable(true);

        dialogsignin.show();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialogsignin.getWindow().setLayout((9 * width) / 10, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView tv_1 = (TextView) dialogsignin.findViewById(R.id.tv_1);
        TextView tv_2 = (TextView) dialogsignin.findViewById(R.id.tv_2);
        TextView tv_3 = (TextView) dialogsignin.findViewById(R.id.tv_3);

        tv_1.setTypeface(RobotoBold);
        tv_2.setTypeface(RobotoRegular);
        tv_3.setTypeface(RobotoLight);

        btn_signin = (Button) dialogsignin.findViewById(R.id.btn_signin);
        btn_signin.setEnabled(true);
        btn_signin.setClickable(true);
        btn_signin.setBackgroundResource(R.drawable.google_button_selector);
        btn_signin.setText(getString(R.string.classic_signinginwithgoogle));
        btn_signin.setAllCaps(true);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInClicked();
                btn_signin.setEnabled(false);
                btn_signin.setClickable(false);
                btn_signin.setText(getString(R.string.classic_signingin));
                btn_signin.setBackgroundColor(Color.GRAY);
            }
        });

    }

    public void ShowSnackBarConnected() {
        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append(getString(R.string.connectedtogoogle));
        snackbarText.setSpan(new ForegroundColorSpan(0xFFFF0000), 13, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Snackbar snackbar = Snackbar.make(coordinatorlayout, snackbarText, Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainScreen.this, "snackbar OK clicked", Toast.LENGTH_LONG).show();
            }
        });


        snackbar.show();
    }

    public void ShowSnackBarConnectedOverDialog() {
        SpannableStringBuilder snackbarText = new SpannableStringBuilder();
        snackbarText.append(getString(R.string.connectedtogoogle));
        snackbarText.setSpan(new ForegroundColorSpan(0xFFFF0000), 13, snackbarText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (mDialogView != null) {
            Snackbar snackbar = Snackbar.make(mDialogView, snackbarText, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.CYAN);
            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(MainScreen.this, "snackbar OK clicked", Toast.LENGTH_LONG).show();
                }
            });

            snackbar.show();
        }
    }

    public void ShowSnackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.checkinternet), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainScreen.this, "snackbar OK clicked", Toast.LENGTH_LONG).show();
            }
        });
        snackbar.show();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("35AC0592DD154F9C701A67B905463C9E")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        Log.i("onBackPressed", "CLICKED");
        leaving_activity = true;
        if (dialog_over != null && dialog_over.isShowing()) dialog_over.dismiss();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Intent go_home = new Intent(TimeTrial.this, MainScreen.class);
            go_home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            go_home.putExtra("SHOW_CONNECTED_TOAST", "no");
            go_home.putExtra("COMING_FROM_GAME", true);
            startActivity(go_home);
        }
        super.onBackPressed();
    }

}
