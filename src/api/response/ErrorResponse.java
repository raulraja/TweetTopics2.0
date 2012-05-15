package api.response;

import com.javielinux.tweettopics2.Utils;
import twitter4j.RateLimitStatus;

public class ErrorResponse implements BaseResponse {
    private int typeError = Utils.NOERROR;
    private RateLimitStatus rateError = null;

    private boolean hasError;
    private Throwable error;
    private String msgError;

    public Throwable getError() {
        return error;
    }

    public String getMsgError() {
        return msgError;
    }


    public boolean hasError() {
        return hasError;
    }

    public void setError(Throwable error, String msgError) {
        this.hasError = true;
        this.error = error;
        this.msgError = msgError;
    }


    public int getTypeError() {
        return typeError;
    }

    public void setTypeError(int typeError) {
        this.typeError = typeError;
    }

    public RateLimitStatus getRateError() {
        return rateError;
    }

    public void setRateError(RateLimitStatus rateError) {
        this.rateError = rateError;
    }

}