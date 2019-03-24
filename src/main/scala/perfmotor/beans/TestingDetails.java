package perfmotor.beans;

public class TestingDetails {

    private String url;
    private String credentials;
    private HeaderDetails headers;
    private String method;
    private int nbrOfReq;
    private int nbrOfLoops;
    private String body;
    private String fileContent;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public HeaderDetails getHeaders() {
        return headers;
    }

    public void setHeaders(HeaderDetails headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getNbrOfReq() {
        return nbrOfReq;
    }

    public void setNbrOfReq(int nbrOfReq) {
        this.nbrOfReq = nbrOfReq;
    }

    public int getNbrOfLoops() {
        return nbrOfLoops;
    }

    public void setNbrOfLoops(int nbrOfLoops) {
        this.nbrOfLoops = nbrOfLoops;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
