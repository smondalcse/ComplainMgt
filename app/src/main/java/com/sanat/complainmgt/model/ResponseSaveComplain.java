package com.sanat.complainmgt.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResponseSaveComplain implements Serializable {
    @SerializedName("HttpStatusCode")
    @Expose
    private Integer httpStatusCode;
    @SerializedName("Msg")
    @Expose
    private String msg;
    @SerializedName("Success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<ComplainModel> data;

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<ComplainModel> getData() {
        return data;
    }

    public void setData(List<ComplainModel> data) {
        this.data = data;
    }

}
