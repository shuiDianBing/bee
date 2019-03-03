package com.stynet.frameset.proxy;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

/**
 * Created by Administrator<<shuiDianBing on  2019/3/2 Refer to the website: nullptr
 */
public class VolleyProcessor implements HttpProcessor{
    private static RequestQueue queue;
    public VolleyProcessor(Context context){
        queue = Volley.newRequestQueue(context);
    }
    @Override
    public void get(String url, Map<String, Object> params, Callback callback) {

    }

    @Override
    public void post(String url, Map<String, Object> params, final Callback callback) {
        queue.add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFailure(error.toString());
            }
        }));
    }
}
