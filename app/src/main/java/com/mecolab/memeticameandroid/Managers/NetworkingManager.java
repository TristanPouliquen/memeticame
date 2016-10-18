package com.mecolab.memeticameandroid.Managers;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

/**
 * Created by tristan on 14-09-16.
 */
public class NetworkingManager {
    private static final int DEFAULT_TIMEOUT = 10000; //10 seconds
    public static final String BASE_URL = "http://mcctrack4.ing.puc.cl/api/v2/";

    private static NetworkingManager mInstance;
    private static Context mContext;
    private RequestQueue mRequestQueue;
    private RetryPolicy mRetryPolicy;

    private NetworkingManager(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
        mRetryPolicy = new DefaultRetryPolicy(DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(mRetryPolicy);
        getRequestQueue().add(req);
    }

    public static synchronized NetworkingManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkingManager(context);
        }
        return mInstance;
    }
}
