package com.admin.claire.garbag_truck;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by claire on 2017/8/13.
 */

public class Utf8JsonRequest extends JsonObjectRequest {

    public Utf8JsonRequest(int method, String url, JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public Utf8JsonRequest(String url, JSONObject jsonRequest,
                           Response.Listener<JSONObject> listener,
                           Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String utf8String = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(utf8String),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // log error
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            // log error
            return Response.error(new ParseError(e));
        }
    }

}
