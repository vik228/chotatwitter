package com.chotatwitter.utils.networkutils;

/**
 * Created by vikas-pc on 21/11/15.
 */
public class NetworkException extends Exception {

    private static final String TAG = "NetworkException";

    public static int HTTP_STATUS_NOT_AVAILABLE = -1;
    private int statusCode,failureType;
    private String message,messageFromServer;
    private Throwable cause;

    public NetworkException (int statusCode, int failureType,String message,String messageFromServer, Throwable cause) {
        super(message,cause);
        this.statusCode = statusCode;
        this.failureType = failureType;
        this.message = message;
        this.messageFromServer = messageFromServer;
        this.cause = cause;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public int getFailureType() {
        return failureType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getMessageFromServer() {
        return messageFromServer;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public static class Builder {
        private  String message;
        private int httpStatusCode = HTTP_STATUS_NOT_AVAILABLE;
        private int failureType = FailureType.REQUEST_FAILURE;
        private String messageFromServer;
        private Throwable cause;

        public Builder() {

        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public Builder httpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
            return this;
        }
        public Builder failureType (int failureType) {
            this.failureType = failureType;
            return this;
        }
        public Builder messageFromServer (String messageFromServer) {
            this.messageFromServer = messageFromServer;
            return this;
        }
        public Builder cause (Throwable cause) {
            this.cause = cause;
            return this;
        }

        public NetworkException build() {
            return new NetworkException(httpStatusCode,failureType,message,messageFromServer,cause);
        }
    }
}
