package com.b2creatived.coloreyetest;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardFragment2 extends Fragment {


    ArrayList<String> arr_userid, arr_username, arr_photo, arr_level, arr_time, arr_timedate;
    //TextView tv_record, tv_rank, tv_date;
    ListView lv;
    private String download_leaderboard_timetrial = "http://www.dappwall.com/OddColor/download_leaderboard_timetrial.php";
    Typeface RobotoRegular, RobotoLight;

    boolean loadingMore = false;
    int num_loadstart, num_loadfinish;
    int lastPos;
    View loadMoreView;
    boolean all_items_downloaded = false;
    LinearLayout llMain, loading_ll;
    ProgressBar pb;
    static String Bresult = "";
    JSONArray jArray;
    InputStream is=null;

    RecyclerView rv;
    CustomAdapter mAdapter;
    LinearLayoutManager llm;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leaderboard_fragment, container, false);

        RobotoRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.otf");
        RobotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.otf");

        rv = (RecyclerView)view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        loading_ll = (LinearLayout) view.findViewById(R.id.users_loading_layout);
        loading_ll.setVisibility(View.VISIBLE);
        pb = (ProgressBar)view.findViewById(R.id.users_progressbar);
        pb.setVisibility(View.VISIBLE);
        llMain = (LinearLayout) view.findViewById(R.id.ll_leaderboard_1);
        llMain.setVisibility(View.GONE);


        arr_userid = new ArrayList<String>();
        arr_username = new ArrayList<String>();
        arr_photo = new ArrayList<String>();
        arr_time = new ArrayList<String>();
        arr_timedate = new ArrayList<String>();

        num_loadstart = 0;
        num_loadfinish = 8;

        if(CheckNetwork.isInternetAvailable(getActivity())) {
            DownloadUsersTimeTrial(String.valueOf(num_loadstart), String.valueOf(num_loadfinish));
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = rv.getChildCount();
                totalItemCount = llm.getItemCount();
                firstVisibleItem = llm.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    //Log.i("total, visible", totalItemCount + "-" + visibleItemCount);
                    //Log.i("firstvisible, threshold", firstVisibleItem + "-" + visibleThreshold);
                    //Log.i("Yaeye!", "end called");
                    //we need the -1 otherwise 1 item will be missed the first time we scroll. I don't know why only the first
                    //time but the point it's working this way
                    DownloadUsersTimeTrial(String.valueOf(totalItemCount - 1), String.valueOf(num_loadfinish));
                    loading = true;
                }
            }
        });

        return view;
    }

    private void DownloadUsersTimeTrial(final String start, final String finish) {

        loadingMore = true;
        StringRequest postReq = new StringRequest(Request.Method.POST, download_leaderboard_timetrial, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //Log.i("response.length()", response.length() + "");
                if (response.length() > 10) { //if there are any records retrieved, otherwise null
                    try {
                        jArray = new JSONArray(response);
                        for(int i=0;i<jArray.length();i++){
                            JSONArray innerJsonArray = jArray.getJSONArray(i);
                            for(int j=0;j<innerJsonArray.length();j++){
                                JSONObject jsonObject = innerJsonArray.getJSONObject(j);
                                arr_userid.add(jsonObject.getString("ID"));
                                arr_username.add(jsonObject.getString("FULL_NAME"));
                                arr_photo.add(jsonObject.getString("PHOTO_URL"));
                                arr_time.add(jsonObject.getString("TIMETRIAL"));
                                arr_timedate.add(jsonObject.getString("DATE_TIME_TRIAL"));
                            }
                        }

                        pb.setVisibility(View.GONE);
                        loading_ll.setVisibility(View.GONE);
                        llMain.setVisibility(View.VISIBLE);

                        //set adapter only when users are downloaded the first time aka. no scrolling has been made
                        if (arr_userid.size() < 14) {
                            mAdapter = new CustomAdapter(getActivity(), arr_userid, arr_username, arr_photo, arr_time, arr_timedate, true);
                            rv.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }
                        loadingMore = false;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    all_items_downloaded = true;
                    mAdapter.hideFooter();
                    mAdapter.notifyDataSetChanged();
                    pb.setVisibility(View.GONE);
                    loading_ll.setVisibility(View.GONE);
                    llMain.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.i("VOLLEY_ERROR", error.toString());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("start", start);
                params.put("finish", finish);
                return params;
            }

        };

        postReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        postReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(postReq);
    }


    /*---------------------------RECYCLER VIEW--------------------------------------*/


    public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        ArrayList<String> arr_userid = new ArrayList<String>();
        ArrayList<String> arr_username = new ArrayList<String>();
        ArrayList<String> arr_photo = new ArrayList<String>();
        ArrayList<String> arr_time = new ArrayList<String>();
        ArrayList<String> arr_timedate = new ArrayList<String>();
        boolean show_footer;
        public LayoutInflater inflater;

        public static final int TYPE_ITEM = 1;
        public static final int TYPE_FOOTER = 2;
        Context context;

        public CustomAdapter(Context context) {
            this.context = context;
        }

        public CustomAdapter(Context context, ArrayList<String> arr_userid, ArrayList<String> arr_username, ArrayList<String> arr_photo, ArrayList<String> arr_time, ArrayList<String> arr_timedate, boolean show_footer) {
            super();
            this.arr_userid = arr_userid;
            this.arr_username = arr_username;
            this.arr_photo = arr_photo;
            this.arr_time = arr_time;
            this.arr_timedate = arr_timedate;
            this.show_footer = show_footer;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public class GenericViewHolder extends RecyclerView.ViewHolder {
            public TextView txtName, txtPoints, txtDate;
            public ImageView ivUser;
            public LinearLayout llLoadmore;

            public GenericViewHolder(View v) {
                super(v);
                txtName = (TextView) v.findViewById(R.id.txtName);
                txtPoints = (TextView) v.findViewById(R.id.txtPoints);
                txtDate = (TextView) v.findViewById(R.id.txtDate);
                ivUser = (ImageView) v.findViewById(R.id.ivUser);
                llLoadmore = (LinearLayout) v.findViewById(R.id.loadmore);
            }
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {
            TextView tvloadmore;

            public FooterViewHolder (View itemView) {
                super (itemView);
                this.tvloadmore = (TextView) itemView.findViewById (R.id.tvloadmore);
            }
        }


        public void add(int position, String item) {
            arr_userid.add(position, item);
            notifyItemInserted(position);
        }

        public void remove(String item) {
            int position = arr_userid.indexOf(item);
            arr_userid.remove(position);
            notifyItemRemoved(position);
        }

        public CustomAdapter(ArrayList<String> myDataset) {
            arr_userid = myDataset;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType) {
            if(viewType == TYPE_FOOTER) {
                View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.loadmore, parent, false);
                return new FooterViewHolder (v);
            } else if(viewType == TYPE_ITEM) {
                View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.leaderboard_row, parent, false);
                return new GenericViewHolder (v);
            }
            return null;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof FooterViewHolder) {
                FooterViewHolder footerHolder = (FooterViewHolder) holder;
                footerHolder.tvloadmore.setText(getString(R.string.leaderboard_loading));
            } else if(holder instanceof GenericViewHolder) {
                GenericViewHolder genericViewHolder = (GenericViewHolder) holder;

                genericViewHolder.txtName.setText(arr_username.get(position));
                genericViewHolder.txtPoints.setText(format_time(Integer.valueOf(arr_time.get(position))));

                if (!arr_timedate.get(position).equals("0000-00-00")) {
                    String year = arr_timedate.get(position).substring(0, 4);
                    String month = arr_timedate.get(position).substring(5, 7);
                    String day = arr_timedate.get(position).substring(8, 10);
                    genericViewHolder.txtDate.setText(month + "." + day + "." + year);
                } else {
                    genericViewHolder.txtDate.setText("");
                }

                if (!arr_photo.get(position).equals("")) {
                    Picasso.with(getActivity())
                            .load(arr_photo.get(position))
                            .into(genericViewHolder.ivUser);
                } else {
                    genericViewHolder.ivUser.setImageDrawable(null);
                }
            }
        }

        @Override
        public int getItemCount() {
            if (show_footer) {
                return arr_userid.size() + 1; //+1 is for the footer as it's an extra item
            } else {
                return arr_userid.size();
            }
        }

        @Override
        public int getItemViewType (int position) {
            if (show_footer) {
                if (isPositionFooter(position)) {
                    return TYPE_FOOTER;
                }
                return TYPE_ITEM;
            } else {
                return TYPE_ITEM;
            }
        }

        private boolean isPositionFooter (int position) {
            //if position == arr_userid.size then we need to show footerView otherwise we'll get indexOutOfBoundsException
            return position == arr_userid.size ();
        }
        public void hideFooter() {
            show_footer = false;
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
}