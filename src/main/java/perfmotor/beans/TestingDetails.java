package perfmotor.beans;

public class TestingDetails {

    private HeaderDetails headers;
    private String url;
    private String method;
    private String body;
    private int expectedMaxResponseTime;
    private int expectedStatus;
    private int atOnceUsers;
    private int rampUsers;
    private String rampUsersOver;
    private int constantUsersPerSec;
    private String constantUsersPerSecDuring;
    private int rampUsersPerSecRate1;
    private int rampUsersPerSecRate2;
    private String rampUsersPerSecDuring;
    private String fileContent;

    public HeaderDetails getHeaders() {
        return headers;
    }

    public void setHeaders(HeaderDetails headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getExpectedMaxResponseTime() {
        return expectedMaxResponseTime;
    }

    public void setExpectedMaxResponseTime(int expectedMaxResponseTime) {
        this.expectedMaxResponseTime = expectedMaxResponseTime;
    }

    public int getExpectedStatus() {
        return expectedStatus;
    }

    public void setExpectedStatus(int expectedStatus) {
        this.expectedStatus = expectedStatus;
    }

    public int getAtOnceUsers() {
        return atOnceUsers;
    }

    public void setAtOnceUsers(int atOnceUsers) {
        this.atOnceUsers = atOnceUsers;
    }

    public int getRampUsers() {
        return rampUsers;
    }

    public void setRampUsers(int rampUsers) {
        this.rampUsers = rampUsers;
    }

    public String getRampUsersOver() {
        return rampUsersOver;
    }

    public void setRampUsersOver(String rampUsersOver) {
        this.rampUsersOver = rampUsersOver;
    }

    public int getConstantUsersPerSec() {
        return constantUsersPerSec;
    }

    public void setConstantUsersPerSec(int constantUsersPerSec) {
        this.constantUsersPerSec = constantUsersPerSec;
    }

    public String getConstantUsersPerSecDuring() {
        return constantUsersPerSecDuring;
    }

    public void setConstantUsersPerSecDuring(String constantUsersPerSecDuring) {
        this.constantUsersPerSecDuring = constantUsersPerSecDuring;
    }

    public int getRampUsersPerSecRate1() {
        return rampUsersPerSecRate1;
    }

    public void setRampUsersPerSecRate1(int rampUsersPerSecRate1) {
        this.rampUsersPerSecRate1 = rampUsersPerSecRate1;
    }

    public int getRampUsersPerSecRate2() {
        return rampUsersPerSecRate2;
    }

    public void setRampUsersPerSecRate2(int rampUsersPerSecRate2) {
        this.rampUsersPerSecRate2 = rampUsersPerSecRate2;
    }

    public String getRampUsersPerSecDuring() {
        return rampUsersPerSecDuring;
    }

    public void setRampUsersPerSecDuring(String rampUsersPerSecDuring) {
        this.rampUsersPerSecDuring = rampUsersPerSecDuring;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }
}
