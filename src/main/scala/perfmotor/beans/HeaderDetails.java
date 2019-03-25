package perfmotor.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeaderDetails {

    private String accept;
    private String Authorization;
    @JsonProperty("Content-Type")
    private String contentType;

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAuthorization() {
        return Authorization;
    }

    public void setAuthorization(String authorization) {
        Authorization = authorization;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
