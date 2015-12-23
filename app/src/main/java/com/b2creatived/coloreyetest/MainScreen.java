package com.b2creatived.coloreyetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class MainScreen extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;
    Tracker mTracker;

    //NavigationView header
    RelativeLayout rl_header;
    TextView tv_header_name, tv_header_email;
    ImageView header_profile_image;

    Typeface RobotoRegular, RobotoLight, RobotoMedium;
    View mDialogView;
    SharedPreferences sharedpref;
    boolean ach1, ach2, ach3, ach4, ach5, ach6;
    Button btn_classic, btn_timetrial;

    private static final int SHARE_GOOGLE = 100;
    private static final int REQUEST_INVITE = 2; /* Request code for inviting friends */
    private static final int RC_SIGN_IN = 1;  /* Request code used to invoke sign in user interactions. */
    private GoogleApiClient mGoogleApiClient; /* Client used to interact with Google APIs. */
    private boolean mIsResolving = false; /* Is there a ConnectionResult resolution in progress? */
    private boolean mShouldResolve = false; /* Should we automatically resolve ConnectionResults when possible? */
    SharedPreferences sharedPreferences;
    String logintype;
    Intent intent_from_presplash, intent_from_game;
    String show_connected_toast = "yes";
    boolean show_presplash = true;
    String playername = "Unknown soldier";
    String playeremail = "";
    String playerid = "";
    String playerphoto = "";
    boolean signin_btn_clicked = false;
    boolean coming_from_presplash = false;

    private static String signup = "http://www.dappwall.com/OddColor/signup.php";
    private static String save_result_classic = "http://www.dappwall.com/OddColor/save_result_classic.php";
    private static String save_result_timetrial = "http://www.dappwall.com/OddColor/save_result_timetrial.php";

    Menu menuNav;
    MenuItem loginItem;

    CallbackManager callbackManager;
    ShareDialog shareDialog;
    CoordinatorLayout coordinatorlayout;

    boolean hide_share_dialog = false;
    int num_game_runs = 0;
    boolean coming_from_game = false;
    Dialog dialog_share;
    AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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
                            .setLabel("Main")
                            .build());
                }
                SaveHideShareDialog("hide_share_dialog", true);
                if (dialog_share != null && dialog_share.isShowing()) dialog_share.dismiss();
                if (mAdView != null) {
                    Log.i("mAdView hide", "hide");
                    mAdView.destroy();
                    mAdView.setVisibility(View.GONE);
                }
                ShowSnackBarAdsHidden();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainScreen.this, "cancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainScreen.this, "error", Toast.LENGTH_SHORT).show();
            }
        });


        mAdView = (AdView) findViewById(R.id.adView);
        LoadHideShareDialog();
        hide_share_dialog = true; //only until the ads can be created!
        Log.i("hide_share_dialog", hide_share_dialog + "");
        if (!hide_share_dialog) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        }

        RobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.otf");
        RobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.otf");
        RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.otf");

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        intent_from_presplash = getIntent();
        if (intent_from_presplash != null) show_connected_toast = intent_from_presplash.getStringExtra("SHOW_CONNECTED_TOAST");


        coordinatorlayout = (CoordinatorLayout)findViewById(R.id.coordinatorlayout);

        //show this only the first time and every 1 out of 3 times
        LoadHideShareDialog();
        LoadGameRuns();
        num_game_runs++;

        intent_from_game = getIntent();

        if (intent_from_game != null) {
            //user comes back from Classic or TimeTrial. Let's increase and save that number.
            LoadGameRuns();
            num_game_runs++;
            SaveGameRuns("num_game_runs", num_game_runs);

            coming_from_game = intent_from_game.getBooleanExtra("COMING_FROM_GAME", false);
            Log.i("coming_from_game", coming_from_game + "");
            Log.i("hide_share_dialog", hide_share_dialog + "");
            Log.i("num_game_runs", num_game_runs + "");
            if (coming_from_game && num_game_runs % 2 == 0 && !hide_share_dialog) {  //we show the share dialog every 3rd time to not be too pushy
                    //Show_ShareDialog();
            }
        }


        LoadSessionState(); //this is for checking if the user is redirected to here immediately when launching the app
        //the result is used in onConnected();

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        menuNav = navigationView.getMenu();
        loginItem  = menuNav.findItem(R.id.navigation_item_7);
        tv_header_name = (TextView) findViewById(R.id.tv_header_name);
        tv_header_name.setText(getString(R.string.main_unknown_soldier));
        tv_header_name.setTypeface(RobotoMedium);
        tv_header_email = (TextView) findViewById(R.id.tv_header_email);
        tv_header_email.setText(getString(R.string.main_unknown_soldier_email));
        tv_header_email.setTypeface(RobotoRegular);
        header_profile_image = (ImageView) findViewById(R.id.header_profile_image);
        rl_header = (RelativeLayout) findViewById(R.id.rl_header);
        header_profile_image.setImageResource(R.mipmap.ic_user);
        rl_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (logintype.equals("guest")) {
                    showSignInDialog();
                }
            }
        });

        LoadLoginType();
        Log.i("LOGINTYPE", logintype + "");
        if (logintype.equals("google+")) {
            LoadPlayerName();
            LoadPlayerEmail();
            LoadPlayerPhoto();
            Log.i("LOAD playername", playername + "");
            Log.i("LOAD playerphoto", playerphoto + "");
            Log.i("LOAD playeremail", playeremail + "");
            //addDrawerItems_whenSignedIn(playername);
            tv_header_name.setText(playername);
            tv_header_email.setText(playeremail);
            if (CheckNetwork.isInternetAvailable(this) && playerphoto != null && !playerphoto.equals("")) {
                Picasso.with(this)
                        .load(playerphoto)
                        .into(header_profile_image);
            } else {
                header_profile_image.setImageResource(R.mipmap.ic_user);
            }
            loginItem.setTitle("Sign out");

        } else if (logintype.equals("guest")) {
            //addDrawerItems_whenSignedOut();
            tv_header_name.setText(getString(R.string.main_unknown_soldier));
            tv_header_email.setText(getString(R.string.main_unknown_soldier_email));
            header_profile_image.setImageResource(R.mipmap.ic_user);
            loginItem.setTitle(getString(R.string.main_signinwithgoogle));
        }
        LoadPlayerID();

        setupDrawer();

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        btn_classic = (Button) findViewById(R.id.btn_play);
        btn_timetrial = (Button) findViewById(R.id.btn_play2);

        btn_classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_play = new Intent(MainScreen.this, ClassicGame.class);
                startActivity(go_play);
            }
        });

        btn_timetrial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent go_play2 = new Intent(MainScreen.this, TimeTrial.class);
                startActivity(go_play2);
            }
        });



        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        int classic_actual = sharedpref.getInt("RECORD", 0);
        String date_classic_actual = sharedpref.getString("RECORD_DATE", "");
        int timetrial_actual = sharedpref.getInt("TIMERECORD", 1000000);
        String date_timetrial_actual = sharedpref.getString("TIMERECORD_DATE", "");

        //Log.i("classic_actual", classic_actual + "");
        //Log.i("date_classic_actual", date_classic_actual + "");
        //Log.i("timetrial_actual", timetrial_actual + "");
        //Log.i("date_timetrial_actual", date_timetrial_actual + "");

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.main_eyetest));
        }

        recordRunTime();
        //Log.v("TAG", "Starting CheckRecentRun service...");
        startService(new Intent(this,  CheckRecentRun.class));

        //Initializing NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //if you want to preserve the selection for the next time when the navdrawer is opened then add these 2 lines:
               // if (menuItem.isChecked()) menuItem.setChecked(false);
               // else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
                        if (mTracker != null) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Achievements")
                                    .setAction("Click")
                                    .setLabel("NavDrawer")
                                    .build());
                        }
                        ShowAchievements();
                        return true;
                    case R.id.navigation_item_2:
                        if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                            if (mTracker != null) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Leaderboard")
                                        .setAction("Click")
                                        .setLabel("NavDrawer")
                                        .build());
                            }
                            Intent go_leaderboard = new Intent(MainScreen.this, Leaderboard.class);
                            startActivityForResult(go_leaderboard, 1);
                        } else {
                            ShowSnackBarNoInternet();
                        }
                        return true;
                    case R.id.navigation_item_3:
                        if (mTracker != null) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("TermsOfUse")
                                    .setAction("Click")
                                    .setLabel("NavDrawer")
                                    .build());
                        }
                        Intent go_terms = new Intent(MainScreen.this, TermsOfUseActivity.class);
                        startActivity(go_terms);
                        return true;
                    case R.id.navigation_item_4:
                        if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                            if (mTracker != null) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Rate")
                                        .setAction("Click")
                                        .setLabel("NavDrawer")
                                        .build());
                            }
                            Intent intent_rate = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest"));
                            startActivity(intent_rate);
                        } else {
                            ShowSnackBarNoInternet();
                        }
                        return true;
                    case R.id.navigation_item_5:
                        if (mTracker != null) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("SendEmail")
                                    .setAction("Click")
                                    .setLabel("NavDrawer")
                                    .build());
                        }
                        String release = Build.VERSION.RELEASE;
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"balintfarago@gmail.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT, "");
                        i.putExtra(Intent.EXTRA_TEXT, "\n\n\nModel: " + getDeviceName() + "\nAndroid version: " + release);
                        try {
                            startActivity(Intent.createChooser(i, getString(R.string.main_sendingemail)));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MainScreen.this, getString(R.string.main_noinstalled_emailclient), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.navigation_item_6:
                        if (mTracker != null) {
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("InviteFriends")
                                    .setAction("Click")
                                    .setLabel("Main")
                                    .build());
                        }
                        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                                .setMessage(getString(R.string.invitation_message))
                                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                                .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                                .setCallToActionText(getString(R.string.invitation_cta))
                                .build();
                        startActivityForResult(intent, REQUEST_INVITE);

                        break;
                    case R.id.navigation_item_7:

                        if (mGoogleApiClient.isConnected() || logintype.equals("google+")) {
                            if (mTracker != null) {
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("SignOut")
                                        .setAction("Click")
                                        .setLabel("NavDrawer")
                                        .build());
                            }
                            showSignOutDialog();
                        } else {
                            if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                                if (mTracker != null) {
                                    mTracker.send(new HitBuilders.EventBuilder()
                                            .setCategory("SignIn")
                                            .setAction("Click")
                                            .setLabel("NavDrawer")
                                            .build());
                                }
                                signin_btn_clicked = true;
                                onSignInClicked();
                            } else {
                                ShowSnackBarNoInternet();
                            }
                        }
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //This is necessary otherwise the app will exit on Back button press even if the navigation drawer is open
    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {
            super.onBackPressed();
        }
    }

    protected boolean isNavDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START);
    }

    protected void closeNavDrawer() {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {


            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.main_eyetest));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }


            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(getString(R.string.main_eyetest));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void ShowAchievements() {
        final Dialog dialog = new Dialog(MainScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_achievements);
        dialog.show();

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width)/7, LinearLayout.LayoutParams.WRAP_CONTENT);

        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
        ach1 = sharedpref.getBoolean("ACH1", false);
        ach2 = sharedpref.getBoolean("ACH2", false);
        ach3 = sharedpref.getBoolean("ACH3", false);
        ach4 = sharedpref.getBoolean("ACH4", false);
        ach5 = sharedpref.getBoolean("ACH5", false);
        ach6 = sharedpref.getBoolean("ACH6", false);

        TextView tv_ach = (TextView) dialog.findViewById(R.id.tv_ach);
        TextView tv_achh = (TextView) dialog.findViewById(R.id.tv_achh);
        TextView tv_ach_1A = (TextView) dialog.findViewById(R.id.tv_ach_1A);
        TextView tv_ach_2A = (TextView) dialog.findViewById(R.id.tv_ach_2A);
        TextView tv_ach_3A = (TextView) dialog.findViewById(R.id.tv_ach_3A);
        TextView tv_ach_4A = (TextView) dialog.findViewById(R.id.tv_ach_4A);
        TextView tv_ach_5A = (TextView) dialog.findViewById(R.id.tv_ach_5A);
        TextView tv_ach_6A = (TextView) dialog.findViewById(R.id.tv_ach_6A);

        ImageView iv_ach_1 = (ImageView) dialog.findViewById(R.id.iv_ach_1);
        ImageView iv_ach_2 = (ImageView) dialog.findViewById(R.id.iv_ach_2);
        ImageView iv_ach_3 = (ImageView) dialog.findViewById(R.id.iv_ach_3);
        ImageView iv_ach_4 = (ImageView) dialog.findViewById(R.id.iv_ach_4);
        ImageView iv_ach_5 = (ImageView) dialog.findViewById(R.id.iv_ach_5);
        ImageView iv_ach_6 = (ImageView) dialog.findViewById(R.id.iv_ach_6);

        tv_ach.setTypeface(RobotoRegular);
        tv_achh.setTypeface(RobotoLight);
        tv_ach_1A.setTypeface(RobotoRegular);
        tv_ach_2A.setTypeface(RobotoRegular);
        tv_ach_3A.setTypeface(RobotoRegular);
        tv_ach_4A.setTypeface(RobotoRegular);
        tv_ach_5A.setTypeface(RobotoRegular);
        tv_ach_6A.setTypeface(RobotoRegular);


        if (ach1) {
            tv_ach_1A.setTextColor(Color.parseColor("#486783"));
            iv_ach_1.setImageResource(R.mipmap.trophy_bronze);
        } else {
            tv_ach_1A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_1.setImageResource(R.mipmap.trophy_off);
        }

        if (ach2) {
            tv_ach_2A.setTextColor(Color.parseColor("#486783"));
            iv_ach_2.setImageResource(R.mipmap.trophy_silver);
        } else {
            tv_ach_2A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_2.setImageResource(R.mipmap.trophy_off);
        }

        if (ach3) {
            tv_ach_3A.setTextColor(Color.parseColor("#486783"));
            iv_ach_3.setImageResource(R.mipmap.trophy_gold);
        } else {
            tv_ach_3A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_3.setImageResource(R.mipmap.trophy_off);
        }

        if (ach4) {
            tv_ach_4A.setTextColor(Color.parseColor("#486783"));
            iv_ach_4.setImageResource(R.mipmap.trophy_bronze);
        } else {
            tv_ach_4A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_4.setImageResource(R.mipmap.trophy_off);
        }

        if (ach5) {
            tv_ach_5A.setTextColor(Color.parseColor("#486783"));
            iv_ach_5.setImageResource(R.mipmap.trophy_silver);
        } else {
            tv_ach_5A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_5.setImageResource(R.mipmap.trophy_off);
        }

        if (ach6) {
            tv_ach_6A.setTextColor(Color.parseColor("#486783"));
            iv_ach_6.setImageResource(R.mipmap.trophy_gold);
        } else {
            tv_ach_6A.setTextColor(Color.parseColor("#bfcdd9"));
            iv_ach_6.setImageResource(R.mipmap.trophy_off);
        }

    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        //Log.i("onSignInClicked", "User clicked the sign-in button");
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {

        //Log.i("Splash onSignOutClicked", "User clicked the sign-out button");
        if (mGoogleApiClient.isConnected()) {
            //Log.i("Splash onSignOutClicked", "isConnected");
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        ShowSnackBarSignedOut();
        logintype = "guest";
        loginItem.setTitle(getString(R.string.main_signinwithgoogle));
        tv_header_name.setText(getString(R.string.main_unknown_soldier));
        tv_header_email.setText(getString(R.string.main_unknown_soldier_email));
        header_profile_image.setImageResource(R.mipmap.ic_user);
        SaveLoginType("logintype", "guest");
        SaveSessionState("show_presplash", true); //user signed out, we want to display the user the presplash screen again the next time he launches the app
        show_connected_toast = "yes";
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName("MainScreen");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            mTracker.enableAdvertisingIdCollection(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Connect only when coming from PreSplash. Don't connect when coming from Leaderboard or Main or TimeTrial
        if (coming_from_presplash) {
            if (CheckNetwork.isInternetAvailable(this)) mGoogleApiClient.connect();
        }
        //Log.i("TAG onStart", "mGoogleApiClient.connect()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        //Log.i("TAG onStop", "mGoogleApiClient.disconnect()");
    }

    @Override
    public void onDestroy() {
        Log.i("TAG onDestroy", "mGoogleApiClient.disconnect()");
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //Log.d("TAG MainScreen onConnected", "" + bundle);
        mShouldResolve = false;

        String player_id = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();
        String player_full_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
        String player_given_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName();
        String player_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String player_PhotoUrl = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl();
        String player_GooglePlusProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getUrl();

        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);

        int classic_actual = sharedpref.getInt("RECORD", 0);
        String date_classic_actual = sharedpref.getString("RECORD_DATE", "");
        int timetrial_actual = sharedpref.getInt("TIMERECORD", 1000000);
        String date_timetrial_actual = sharedpref.getString("TIMERECORD_DATE", "");

        UploadUserData(player_id, player_full_name, player_given_name, player_email, player_PhotoUrl, player_GooglePlusProfile);

        //Log.e("TAG MainScreen onConnected", "Id: " + player_id + ", Name: " + player_full_name + ", given_name: " + player_given_name + ", email: " + player_email + ", Image: " + player_PhotoUrl + ", plusProfile: " + player_GooglePlusProfile);

        if (player_full_name != null && player_full_name.length() > 0) {
            tv_header_name.setText(player_full_name);
            tv_header_email.setText(player_email);
            Picasso.with(this)
                    .load(player_PhotoUrl)
                    .into(header_profile_image);
        } else {
            tv_header_name.setText(getString(R.string.main_unknown_soldier));
            tv_header_email.setText(getString(R.string.main_unknown_soldier_email));
            header_profile_image.setImageResource(R.mipmap.ic_user);
        }
        loginItem.setTitle(getString(R.string.main_signout));

        //Log.d("show_connected_toast", "" + show_connected_toast);
        //show_connected_toast = true only when user comes from PreSplash or from Class or TimeTrial by clicking the HOME btn
        //though we still want to show this toast when the user explicitly signs in in this activity
        //Log.d("signin_btn_clicked", "" + signin_btn_clicked);
        if (!signin_btn_clicked) { //if user didn't click the sign-in button in navdrawer so he probably comes from PreSplash
            if (show_connected_toast != null) {  //We don't want to display the Toast message if the user was already signed in
                if (!show_connected_toast.equals("no")) {
                    ShowSnackBarConnected();
                }
            } else {
                ShowSnackBarConnected();
            }
        } else {
            ShowSnackBarConnected();
        }

        logintype = "google+"; //when the user opens this screen as guest but decides to sign in then we need this to not show the SignInDialog when he click the header
        SaveLoginType("logintype", "google+");
        SaveSessionState("show_presplash", false); //User connected to google+, don't show the presplash screen again the next time he signs in
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i("TAG onConnectionSusp", "onConnectionSuspended() " + i);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
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
                // Could not resolve the connection result
                //Log.e("TAG onConnectionFailed", "error: " + connectionResult);
            }
        } else {
            // Show the signed-out UI
           // showSignedOutUI_Default();
        }
    }

    public void showSignOutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
        builder.setTitle(getString(R.string.main_areyoursure_signout));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onSignOutClicked();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void showSignInDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainScreen.this);
        builder.setTitle(getString(R.string.main_tired));
        builder.setMessage(getString(R.string.main_tired_text));
        builder.setPositiveButton(getString(R.string.signin), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signin_btn_clicked = true;
                onSignInClicked();
            }
        });

        builder.setNegativeButton(getString(R.string.later), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

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
            } else {
                if (data != null) {
                    String info = data.getStringExtra("back");
                    if (!info.equals("leaderboard")) {
                        coming_from_presplash = true;
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    } else {
                        coming_from_presplash = false;
                    }
                } else {
                    coming_from_presplash = true;
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }

            }
        }

        if (requestCode == SHARE_GOOGLE) {
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Share_Google")
                        .setAction("Done")
                        .setLabel("Main")
                        .build());
            }
            SaveHideShareDialog("hide_share_dialog", true);
            if (dialog_share != null && dialog_share.isShowing()) dialog_share.dismiss();
            if (mAdView != null) {
                mAdView.destroy();
                mAdView.setVisibility(View.GONE);
            }
            ShowSnackBarAdsHidden();
        }

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("InviteFriends")
                            .setAction("Done")
                            .setLabel("Main")
                            .build());
                }
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                //Log.d("INVITES SENT", getString(R.string.sent_invitations_fmt, ids.length));
                SaveHideShareDialog("hide_share_dialog", true);
                if (dialog_share != null && dialog_share.isShowing()) dialog_share.dismiss();
                if (mAdView != null) {
                    mAdView.destroy();
                    mAdView.setVisibility(View.GONE);
                }
                ShowSnackBarAdsHidden();
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

    public void LoadPlayerPhoto(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playerphoto = sharedPreferences.getString("playerphoto", "");
    }

    public void SavePlayerID(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void LoadPlayerID(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        playerid = sharedPreferences.getString("playerid", "");
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

    public void SaveGameRuns(String key, Integer value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public void LoadGameRuns(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        num_game_runs = sharedPreferences.getInt("num_game_runs", 0);
    }


    private void UploadUserData(final String google_id, final String full_name, final String given_name, final String email, final String photourl, final String googleplusprofile) {
        /*Log.i("MainScreen UploadUserData", "START");
        Log.i("google_id", google_id);
        Log.i("full_name", full_name);
        Log.i("given_name", given_name);
        Log.i("email", email);
        Log.i("photourl", photourl);
        Log.i("googleplusprofile", googleplusprofile);*/

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, signup, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.i("UploadUserData", "onResponse");

                int success = 0;
                try {
                    success = response.getInt("success");
                    //Log.i("UploadUserData success", "" + success);
                    if (success == 1) { //new user added to database
                        String lastid = response.getString("lastid");
                        //Log.i("UploadUserData lastid", "success: " + lastid);

                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);
                        if (lastid != null && !lastid.equals("")) SavePlayerID("playerid", lastid);

                    } else if (success == 2) {
                        //This activates when the user was already logged in when they quit the game the last time
                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);

                    } else {
                        Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                    }

                    if (success > 0) {
                        int script_level = 0;
                        if (response.getString("level").equals("")) {
                            script_level = 0;
                        } else {
                            script_level = Integer.valueOf(response.getString("level"));
                        }

                        int script_timetrial = 0;
                        if (response.getString("timetrial").equals("")) {
                            script_timetrial = 0;
                        } else {
                            script_timetrial = Integer.valueOf(response.getString("timetrial"));
                        }

                        sharedpref = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                        int current_record_classic = sharedpref.getInt("RECORD", 0);
                        int current_record_timetrial = sharedpref.getInt("TIMERECORD", 1000000);

                        //Log.i("script_level", script_level + "");
                        //Log.i("script_timetrial", script_timetrial + "");
                        //Log.i("current_record_classic", current_record_classic + "");
                        //Log.i("current_record_timetrial", current_record_timetrial + "");

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String formattedDate = df.format(c.getTime());

                        if (current_record_classic > script_level) { //if locally saved record is higher than in the cloud
                            //Log.i("HIGHER?", "classic locally is higher");
                            SaveResult_Classic(email, String.valueOf(current_record_classic), formattedDate);
                        } else {
                            //Log.i("HIGHER?", "classic locally is not higher");
                        }

                        //if there was already a record. We need to check if script_timetrial > 0 otherwise when no previous
                        //record was saved in the cloud than it's 0. We need to save the result in the cloud if the
                        //current < script_timetrial or script_timetrial == 0 because in the last case there was no previous
                        //record uploaded.
                        if (current_record_timetrial != 1000000) {
                            if (current_record_timetrial < script_timetrial || script_timetrial == 0) { //if locally saved record is higher than in the cloud or there was no previous record uploaded in the cloud
                                //Log.i("HIGHER?", "timetrial locally is higher");
                                SaveResult_TimeTrial(email, String.valueOf(current_record_timetrial), formattedDate);
                            } else {
                                //Log.i("HIGHER?", "timetrial locally is not higher");
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.i("UploadUserData", "Error: " + e);
                    //Log.i("Response", String.valueOf(response));
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("UploadUserData", "Error: " + error);
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
                return params;
            }

        };

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void SaveResult_Classic(final String email, final String level, final String formattedDate) {

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, save_result_classic, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                int success = 0;
                try {
                    success = response.getInt("success");
                    /*if (success == 1) { //record uploaded successfully
                        Log.i("SaveResult_Splash3", "record upload successful");
                    } else {
                        Log.i("SaveResult_Splash3", "record upload failed. Maybe bad network reception?");
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                   // Log.i("SaveResult_Splash3", "Error: " + e);
                    //Toast.makeText(MainScreen.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("SaveResult_Splash3", "Error: " + error);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("level", level);
                params.put("date", formattedDate);
                return params;
            }

        };

        jsonObjReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void SaveResult_TimeTrial(final String email, final String time, final String formattedDate) {

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, save_result_timetrial, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                int success = 0;
                try {
                    success = response.getInt("success");
                    /*if (success == 1) { //record uploaded successfully
                        Log.i("SaveResult_Splash3", "record upload successful");
                    } else {
                        Log.i("SaveResult_Splash3", "record upload failed. Maybe bad network reception?");
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                    //Log.i("SaveResult_Splash3", "Error: " + e);
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("SaveResult_Splash3", "Error: " + error);
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

    public void ShowSnackBarNoInternetOverDialog() {
        Snackbar snackbar = Snackbar.make(mDialogView, getString(R.string.checkinternet), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainScreen.this, "snackbar OK clicked", Toast.LENGTH_LONG).show();
            }
        });
        snackbar.show();
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

    public void ShowSnackBarSignedOut() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.signedout), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainScreen.this, "snackbar OK clicked", Toast.LENGTH_LONG).show();
            }
        });
        snackbar.show();
    }

    public void recordRunTime() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("lastRun", System.currentTimeMillis());
        editor.commit();
    }

    public void Show_ShareDialog() {
        dialog_share = new Dialog(MainScreen.this, R.style.DialogTheme);
        dialog_share.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Drawable d = new ColorDrawable(Color.BLACK);
        //d.setAlpha(190);
        //dialog.getWindow().setBackgroundDrawable(d);

        LayoutInflater inflater = this.getLayoutInflater();
        // inflate the custom dialog view
        mDialogView = inflater.inflate(R.layout.dialog_share, null);
        dialog_share.setContentView(mDialogView);
        dialog_share.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        dialog_share.show();

        WindowManager manager = (WindowManager) getSystemService(Activity.WINDOW_SERVICE);
        int width, height;

        Point point = new Point();
        manager.getDefaultDisplay().getSize(point);
        width = point.x;
        height = point.y;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_share.getWindow().getAttributes());
        lp.width = width;
        lp.height = height;
        dialog_share.getWindow().setAttributes(lp);

        TextView tv_share1 = (TextView) dialog_share.findViewById(R.id.tv_share1);
        TextView tv_share2 = (TextView) dialog_share.findViewById(R.id.tv_share2);
        tv_share1.setTypeface(RobotoRegular);
        tv_share2.setTypeface(RobotoLight);

        Button btn_share_google = (Button) dialog_share.findViewById(R.id.btn_share_google);
        btn_share_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                    if (mTracker != null) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Share_Google")
                                .setAction("Click")
                                .setLabel("Main")
                                .build());
                    }

                    Intent shareIntent = new PlusShare.Builder(MainScreen.this)
                        .setType("text/plain")
                        .setText("This is a cool app, I thought you'd like it :)")
                        .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest"))
                        .getIntent();

                    startActivityForResult(shareIntent, SHARE_GOOGLE);
                } else {
                    ShowSnackBarNoInternetOverDialog();
                }

            }
        });

        Button btn_share_facebook = (Button) dialog_share.findViewById(R.id.btn_share_facebook);
        btn_share_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                    if (mTracker != null) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Share_Facebook")
                                .setAction("Click")
                                .setLabel("Main")
                                .build());
                    }

                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle("This is a cool app, I thought you'd like it :)")
                                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest"))
                                .build();

                        shareDialog.show(linkContent);
                    }
                } else {
                    ShowSnackBarNoInternetOverDialog();
                }
            }
        });

       /* Button btn_share_email = (Button) dialog.findViewById(R.id.btn_share_email);
        btn_share_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_Email")
                            .setAction("Click")
                            .setLabel("Main")
                            .build());
                }

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","balintfarago@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_EMAIL, "balintfarago@gmail.com");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is a cool app, I thought you'd like it :)");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "I made it to Level \" + String.valueOf(level) + \" in Eye Test - Spot the Odd! How far can you go?\n\nDownload from here: https://play.google.com/store/apps/details?id=com.b2creatived.coloreyetest");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        Button btn_share_sms = (Button) dialog.findViewById(R.id.btn_share_sms);
        btn_share_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Share_SMS")
                            .setAction("Click")
                            .setLabel("Main")
                            .build());
                }

                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.putExtra("sms_body", getString(R.string.invitation_message) + " https://goo.gl/egTcyD");
                sendIntent.setType("vnd.android-dir/mms-sms");
                startActivity(sendIntent);
            }
        });*/

        Button btn_invite = (Button) dialog_share.findViewById(R.id.btn_invite);
        btn_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(MainScreen.this)) {
                    if (mTracker != null) {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("InviteFriends")
                                .setAction("Click")
                                .setLabel("Main")
                                .build());
                    }

                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            .setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                            .setCallToActionText(getString(R.string.invitation_cta))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);
                } else {
                    ShowSnackBarNoInternetOverDialog();
                }

            }
        });

        Button btn_dont_show_again = (Button) dialog_share.findViewById(R.id.btn_dontshow);
        btn_dont_show_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("DontShow")
                            .setAction("Click")
                            .setLabel("Main")
                            .build());
                }
                SaveHideShareDialog("hide_share_dialog", true);
                dialog_share.dismiss();
            }
        });

        Button btn_close = (Button) dialog_share.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTracker != null) {
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Close")
                            .setAction("Click")
                            .setLabel("Main")
                            .build());
                }

                dialog_share.dismiss();


            }
        });
    }
    public void ShowSnackBarAdsHidden() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.adshidden), Snackbar.LENGTH_LONG);
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

