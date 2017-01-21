package com.robthecornallgmail.memarket.Util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by rob on 02/01/17.
 */

public class MyHelper {
    public static String readHttpStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View progressView, final View loginFormView, Resources resource) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = resource.getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

//    public static String executePostHttpRequest(final String path, Map<String, String> params) throws Exception
//    {
//        String result = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            String postData = getQuery(params);
//            byte[] postDataBytes = postData.getBytes("UTF-8");
//
//            URL url = new URL(path);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setConnectTimeout(30000);
//            urlConnection.setReadTimeout(30000);
//            urlConnection.setRequestMethod("POST");
//
//            OutputStream out = urlConnection.getOutputStream();
//            out.write(postDataBytes);
//            out.close();
//            result = MyHelper.readHttpStream(urlConnection.getInputStream());
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return result;
//    }
//    private static String getQuery(ContentValues params) throws UnsupportedEncodingException
//    {
//        StringBuilder result = new StringBuilder();
//        boolean first = true;
//
//        Set<Map.Entry<String, Object>> s=params.valueSet();
//        Iterator itr = s.iterator();
//
//        while(itr.hasNext())
//        {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//            Map.Entry me = (Map.Entry)itr.next();
//            String key = me.getKey().toString();
//            Object value =  me.getValue();
//
//            Log.d("DatabaseSync", "Key:"+key+", values:"+(String)(value == null?null:value.toString()));
//        }
//        for (ContentValues pair : params)
//        {
//            if (first)
//                first = false;
//            else
//                result.append("&");
//
//            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//            result.append("=");
//            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
//        }
//
//        return result.toString();
//    }
}


