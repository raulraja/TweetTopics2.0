package api;


import adapters.RowResponseList;
import com.javielinux.tweettopics2.Utils;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Tweet;

import java.util.ArrayList;
import java.util.HashMap;

public class APIResult {

    /*
    ERROR
     */

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

    /*
    ARRAYS
     */

    public HashMap<String,ArrayList<Object>> arrays = new HashMap<String,ArrayList<Object>>();

    public void addArrayParameter(String name, ArrayList<Object> obj) {
        arrays.put(name, obj);
    }

    public ArrayList<Object> getArrayParameter(String name) {
        if (arrays.containsKey(name)) {
            return arrays.get(name);
        }
        return null;
    }

    public boolean existArrayParameter(String name) {
        if (arrays.containsKey(name)) {
            return true;
        }
        return false;
    }

    /*
    OBJECTS
     */

    public HashMap<String,Object> parameters = new HashMap<String,Object>();

    public void addParameter(String name, Object obj) {
        parameters.put(name, obj);
    }

    public Object getParameter(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name);
        }
        return null;
    }

    public String getString(String name) {
        if (parameters.containsKey(name)) {
            return parameters.get(name).toString();
        }
        return null;
    }

    public Integer getInteger(String name) {
        if (parameters.containsKey(name)) {
            return (Integer) parameters.get(name);
        }
        return null;
    }

    public Boolean getBoolean(String name) {
        if (parameters.containsKey(name)) {
            return (Boolean) parameters.get(name);
        }
        return null;
    }

    public Long getLong(String name) {
        if (parameters.containsKey(name)) {
            return (Long) parameters.get(name);
        }
        return null;
    }

    public Double getDouble(String name) {
        if (parameters.containsKey(name)) {
            return (Double) parameters.get(name);
        }
        return null;
    }

    public Status getStatus(String name) {
        if (parameters.containsKey(name)) {
            return (Status) parameters.get(name);
        }
        return null;
    }

    public Tweet getTweet(String name) {
        if (parameters.containsKey(name)) {
            return (Tweet) parameters.get(name);
        }
        return null;
    }

    public RowResponseList getRowResponseList(String name) {
        if (parameters.containsKey(name)) {
            return (RowResponseList) parameters.get(name);
        }
        return null;
    }

    public boolean existParameter(String name) {
        if (parameters.containsKey(name)) {
            return true;
        }
        return false;
    }

}
