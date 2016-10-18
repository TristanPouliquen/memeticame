package com.mecolab.memeticameandroid.Utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tristan on 14-09-16.
 */
public class CustomJsonArrayRequest extends JsonArrayRequest {
    private String token="M1J3p2906o4RuFrg10M39gtt";

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        HashMap<String, String> headers = new HashMap<String,String>();
        headers.put("Authorization", "Token token=" + token);
        return headers;
    }

    public CustomJsonArrayRequest(int method, String url, JSONArray jsonRequest,
                                   Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener,errorListener);
    }
}
