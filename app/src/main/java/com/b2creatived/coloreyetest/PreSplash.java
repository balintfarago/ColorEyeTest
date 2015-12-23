package com.b2creatived.coloreyetest;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class PreSplash extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Button btn_next, btn_googleplus, btn_signout;
    TextView tv_whysignin;

    SharedPreferences sharedPreferences;

    Tracker mTracker;
    Typeface RobotoLight, RobotoMedium, RobotoRegular, RobotoBold;

    private static final int RC_SIGN_IN = 1;  /* Request code used to invoke sign in user interactions. */
    private GoogleApiClient mGoogleApiClient; /* Client used to interact with Google APIs. */
    private boolean mIsResolving = false; /* Is there a ConnectionResult resolution in progress? */
    private boolean mShouldResolve = false; /* Should we automatically resolve ConnectionResults when possible? */
    String logintype = "";
    int num_runs = 0;
    boolean show_presplash = true;

    private static String signup = "http://www.dappwall.com/OddColor/signup.php";

    CoordinatorLayout coordinatorlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.presplash);

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

        //We want to know if the user opened the app by clicking the notification
        Bundle intent_extras = getIntent().getExtras();
        if (intent_extras != null && intent_extras.containsKey("noti_id")) {
            if (mTracker != null) {
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Notification")
                        .setAction("Click")
                        .setLabel("MissYou")
                        .build());
            }
        }


        //if user already decided on the sign-in mode (google+ or guest) then do not display the splash screen and send him to MainScreen
        LoadSessionState();
        if (!show_presplash) {
            Intent go_splash = new Intent(PreSplash.this, MainScreen.class);
            go_splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            go_splash.putExtra("SHOW_CONNECTED_TOAST", "no");
            finish();
            startActivity(go_splash);
        }

        RobotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.otf");
        RobotoMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.otf");
        RobotoRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.otf");
        RobotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.otf");

        coordinatorlayout = (CoordinatorLayout)findViewById(R.id.coordinatorlayout);

        btn_next = (Button) findViewById(R.id.btn_next);
        btn_googleplus = (Button) findViewById(R.id.btn_google);
        btn_signout = (Button) findViewById(R.id.btn_signout);
        tv_whysignin = (TextView) findViewById(R.id.tv_whysignin);
        tv_whysignin.setTypeface(RobotoLight);




        //This is faster than checking mGoogleApiClient.isConnected()
        //we just send to user to MainScreen with a flag: google+ OR guest
        LoadLoginType();

        btn_googleplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNetwork.isInternetAvailable(PreSplash.this)) {
                    onSignInClicked();
                } else {
                    ShowSnackBarNoInternet();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveLoginType("logintype", "guest");
                SaveSessionState("show_presplash", false); //don't show presplash again, send user to MainScreen
                Intent go_play2 = new Intent(PreSplash.this, MainScreen.class);
                go_play2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(go_play2);
            }
        });

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSignOutClicked();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!logintype.equals("guest")) mGoogleApiClient.connect();
        //Log.i("TAG onStart", "mGoogleApiClient.connect()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) mGoogleApiClient.disconnect();
        //Log.i("TAG onStop", "mGoogleApiClient.disconnect()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName("PreSplash");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            mTracker.enableAdvertisingIdCollection(true);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        //Log.d("TAG onConnected", "" + bundle);
        mShouldResolve = false;

        String player_id = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();
        String player_full_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
        String player_given_name = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName();
        String player_email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        String player_PhotoUrl = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl();
        String player_GooglePlusProfile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getUrl();

        //Log.d("TAG onConnected", "family name: " + Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getFamilyName());
        //Log.d("TAG onConnected", "given name: " + Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getName().getGivenName());
        //Log.e("TAG onConnected", "Id: " + player_id + ", Name: " + player_full_name + ", given_name: " + player_given_name + ", email: " + player_email + ", Image: " + player_PhotoUrl + ", plusProfile: " + player_GooglePlusProfile);

        UploadUserData(player_id, player_full_name, player_given_name, player_email, player_PhotoUrl, player_GooglePlusProfile);
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i("TAG onConnectionSusp", "onConnectionSuspended() " + i);
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
               // Log.e("TAG onConnectionFailed", "error: " + connectionResult);
                Toast.makeText(PreSplash.this, "error: " + connectionResult, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show the signed-out UI
            showSignedOutUI_Default();
        }
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        //Log.i("TAG onSignInClicked", "User clicked the sign-in button");
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        //Log.i("TAG onSignInClicked", "User clicked the sign-out button");
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        showSignedOutUI();
        SaveLoginType("logintype", "guest");
    }

    private void showSignedOutUI() {
        //Toast.makeText(PreSplash.this, "Signed out", Toast.LENGTH_LONG).show();
        ShowSnackBarSignedOut();
    }

    private void showSignedOutUI_Default() {
        //Toast.makeText(PreSplash.this, "Not signed in", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
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

    public void SavePlayerEmail(String key, String value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
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

    public void SaveRuns(String key, Integer value){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public void LoadRuns(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        num_runs = sharedPreferences.getInt("num_runs", 0);
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


    private void UploadUserData(final String google_id, final String full_name, final String given_name, final String email, final String photourl, final String googleplusprofile) {
       /*Log.i("UploadUserData", "START");
        Log.i("google_id", google_id);
        Log.i("full_name", full_name);
        Log.i("given_name", given_name);
        Log.i("email", email);
        Log.i("photourl", photourl);
        Log.i("googleplusprofile", googleplusprofile);*/

        CustomRequest jsonObjReq = new CustomRequest(com.android.volley.Request.Method.POST, signup, null, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                int success = 0;
                try {
                    success = response.getInt("success");
                    if (success == 1) { //new user added to database
                        String lastid = response.getString("lastid");
    Log.i("lastid", lastid + "");
                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);
                        if (lastid != null && !lastid.equals("")) SavePlayerID("playerid", lastid);

                        Intent gosplash = new Intent(PreSplash.this, MainScreen.class);
                        gosplash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(gosplash);
                        finish();

                    } else if (success == 2) { //user already registered
                        //This is activated when the user uninstalled the app some time in the past and now they install it again.

                        ShowSnackBarConnected();

                        SaveLoginType("logintype", "google+");
                        SaveSessionState("show_presplash", false); //don't show presplash next time, send user to MainScreen
                        if (full_name != null && full_name.length() > 0) SavePlayerName("playername", full_name);
                        if (email != null && email.length() > 0) SavePlayerEmail("playeremail", email);
                        if (photourl != null && photourl.length() > 0) SavePlayerPhoto("playerphoto", photourl);

                        Intent go_splash = new Intent(PreSplash.this, MainScreen.class);
                        go_splash.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        go_splash.putExtra("SHOW_CONNECTED_TOAST", "no");
                        finish();
                        startActivity(go_splash);

                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("UploadUserData", "Error: " + e);
                    //Log.i("Response", String.valueOf(response));
                    Toast.makeText(PreSplash.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("UploadUserData", "Error: " + error);
                Toast.makeText(PreSplash.this, "Something went wrong. Maybe bad network reception?", Toast.LENGTH_SHORT).show();
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


    public void ShowSnackBarNoInternet() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.checkinternet), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", null);
        snackbar.show();
    }

    public void ShowSnackBarConnected() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.welcomeback), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", null);
        snackbar.show();
    }

    public void ShowSnackBarSignedOut() {
        Snackbar snackbar = Snackbar.make(coordinatorlayout, getString(R.string.signedout), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.CYAN);
        snackbar.setAction("OK", null);
        snackbar.show();
    }
}
