package com.chotatwitter.utils.networkutils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chotatwitter.common.UnObfuscable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by vikas-pc on 21/11/15.
 */
public class NetworkUtils {

    private static final String TAG = "NetworkUtils";
    public static final int TOKEN_EXPIRED_CODE = 401;

    public static final String CONTENT_TYPE_TAG = "Content-Type";
    public static final String CONTENT_TYPE_JSON = "application/json";


    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static String getNetworkTypeIfConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return activeNetworkInfo.getTypeName();
        }else {
            return null;
        }
    }

    public static boolean isConnectedToWifi (Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context
                .CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && (networkInfo.getType() == ConnectivityManager
                .TYPE_WIFI);
    }

    public static <R extends UnObfuscable> void doPostCallAsync(String url, String requestJSON,
            NetworkAsyncCallback<R> cb) {
        Logger.logd(NetworkUtils.class, requestJSON);
        OkHttpClient client = getStandardOkHttpClient();
        RequestBody body = RequestBody.create(JSON, requestJSON);
        Request request = new Request.Builder().url(url)
                .header(CONTENT_TYPE_TAG,CONTENT_TYPE_JSON)
                .post(body).build();
        Call call = client.newCall(request);
        call.enqueue(cb);
    }

    public static <R extends UnObfuscable> void doGetCallAsync(String url, NetworkAsyncCallback<R> cb) {
        OkHttpClient client = getStandardOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(cb);
    }

     /*
        Do get call sync...
     */

    public static <R extends UnObfuscable> R doGetCallSync(String url, Class<R> rClass) throws NetworkException{
        OkHttpClient client = getStandardOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException ioe) {
            throw NetworkUtils.wrapIOException(request, ioe);
        }
        return NetworkUtils.handleResponse(response, rClass);
    }


    /*
        Do post call sync...
     */

    public static <R extends UnObfuscable> R doPostCallSync(String url, String jsonRequestObject, Class<R> rClass)
            throws NetworkException{
        OkHttpClient client = getStandardOkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonRequestObject);
        Request request = new Request.Builder().url(url)
                .header(CONTENT_TYPE_TAG, CONTENT_TYPE_JSON)
                .post(body).build();
        Call call = client.newCall(request);
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException ioe) {
            throw NetworkUtils.wrapIOException(request, ioe);
        }
        return NetworkUtils.handleResponse(response, rClass);
    }

    public static OkHttpClient getStandardOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(15, TimeUnit.SECONDS);
        return client;
    }

    public static <T> T parseResponse(Response response, Class<T> tClass) throws NetworkException {
        Gson gson = new Gson();
        String responseString = null;
        try {
            responseString = response.body().string();
        } catch (IOException ioe) {
            String message = "IOException while converting response body to string: " + ioe.getMessage();
            throw new NetworkException.Builder().cause(ioe).message(message).httpStatusCode(response.code()).build();
        }
        try {
            return gson.fromJson(responseString, tClass);
        } catch (JsonSyntaxException jsonSyntaxException) {
            String message = "JsonSyntaxException while parsing response string to " + tClass.getSimpleName();
            throw new NetworkException.Builder().cause(jsonSyntaxException).message(message).httpStatusCode(response
                    .code()).build();
        }
    }

    public static <T> T handleResponse (Response response, Class<T> responseWrapperObject) throws NetworkException {
        T wrapperObj = null;
        if (null != response) {
            if (response.isSuccessful()) {
                wrapperObj = parseResponse(response, responseWrapperObject);
                return wrapperObj;
            } else {
                String messageFromServer = response.message();
                ServerErrorResponse errorResponse = parseResponse(response,ServerErrorResponse.class);
                if (errorResponse != null) {
                    messageFromServer = errorResponse.getMessage();
                }
                String why = null;
                int failureType;
                if (TOKEN_EXPIRED_CODE == response.code()) {
                    // Login auth token expired, special handling
                    why = "Login Auth Token expired";
                    failureType = FailureType.TOKEN_EXPIRED;
                } else {
                    String requestUrl = response.request().urlString();
                    String method = response.request().method();
                    why = method + " response not successful for URL: " + requestUrl;
                    failureType = FailureType.RESPONSE_FAILURE;
                }

                throw new NetworkException.Builder().message(why).failureType(failureType)
                        .httpStatusCode(response.code()).messageFromServer(messageFromServer).build();
            }
        }else {
            String why = "Null Response obtained from URL: " + response.request().urlString();
            throw new NetworkException.Builder().message(why).failureType(FailureType.RESPONSE_FAILURE).build();
        }
    }

    public static NetworkException wrapIOException(Request request, IOException ioe){
        String requestUrl = request.urlString();
        String method = request.method();
        return new NetworkException.Builder().cause(ioe).failureType(FailureType.REQUEST_FAILURE)
                .message(method + " request failed for URL: " + requestUrl).build();
    }


}
