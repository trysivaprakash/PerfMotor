package perfmotor.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HeaderDetails {

    private String accept;
    @JsonProperty("Authorization")
    private String authorization;
    @JsonProperty("Content-Type")
    private String contentType;

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
