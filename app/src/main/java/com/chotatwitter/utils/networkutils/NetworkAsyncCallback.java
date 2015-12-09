package com.chotatwitter.utils.networkutils;

import android.os.Handler;
import android.os.Message;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by vikas-pc on 21/11/15.
 */
public abstract class NetworkAsyncCallback<Wrapper> extends Handler implements Callback {

    private static final String TAG = "NetworkAsyncCallback";
    private Class<Wrapper> mWrapper;
    private static final int MESSAGE_RESPONSE_SUCCESS = 0;
    private static final int MESSAGE_RESPONSE_TOKEN_EXPIRED = 1;
    private static final int MESSAGE_RESPONSE_FAILURE = 3;

    public NetworkAsyncCallback (Class<Wrapper> wrapper) {
        this.mWrapper = wrapper;
    }
    @Override
    public void onFailure(Request request, IOException e) {

        obtainMessage(MESSAGE_RESPONSE_FAILURE, NetworkUtils.wrapIOException(request,e)).sendToTarget();
        Logger.logd(NetworkAsyncCallback.this, NetworkUtils.wrapIOException(request,e).getMessage());
    }

    @Override
    public void onResponse(Response response) throws IOException {

        try {
            Wrapper responseObject = NetworkUtils.handleResponse(response, mWrapper);
            obtainMessage(MESSAGE_RESPONSE_SUCCESS, responseObject).sendToTarget();
        } catch (NetworkException ne) {
            int what = MESSAGE_RESPONSE_FAILURE;
            switch (ne.getFailureType()) {
                case FailureType.TOKEN_EXPIRED:
                    what = MESSAGE_RESPONSE_TOKEN_EXPIRED;
                    break;
                case FailureType.RESPONSE_FAILURE:
                    what = MESSAGE_RESPONSE_FAILURE;
                    break;
            }
            obtainMessage(what, ne).sendToTarget();
        }

    }

    @Override
    public void handleMessage(Message msg) {

        if (msg.what == MESSAGE_RESPONSE_SUCCESS) {
            Wrapper responseObject = (Wrapper) msg.obj;
            onNetworkSuccess(responseObject);
        } else if (isFailureMessage(msg.what)) {
            NetworkException ne = (NetworkException) msg.obj;
            if (msg.what == MESSAGE_RESPONSE_TOKEN_EXPIRED) {
                Logger.logd(NetworkAsyncCallback.this, ne.getMessage());
                onNetworkTokenExpired(ne);
            } else {
                onNetworkFailure(ne);
            }
        }

    }

    private boolean isFailureMessage(int msgWhat) {
        return (msgWhat == MESSAGE_RESPONSE_TOKEN_EXPIRED || msgWhat == MESSAGE_RESPONSE_SUCCESS || msgWhat == MESSAGE_RESPONSE_FAILURE);
    }

    public abstract void onNetworkSuccess(Wrapper wrapper );
    public abstract void onNetworkFailure(NetworkException ne);


    public void onNetworkTokenExpired(NetworkException ne) {
        //TODO: Login Auth Token Expired, initiate Logout Flow.
        // Classes extending NetworkAsyncCallback might not necessarily implement this method
        onNetworkFailure(ne);

    }



}
