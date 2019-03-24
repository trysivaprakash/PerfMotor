package perfmotor.beans;

public class PerfMotorExecVars {

    private String baseUrl;
    private int maxRespTime;
    private String scenarioName;
    private String requestName;

    public String getRequestName() {
        return requestName;
    }

    public void setRequestName(String requestName) {
        this.requestName = requestName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }


    public int getMaxRespTime() {
        return maxRespTime;
    }

    public void setMaxRespTime(int maxRespTime) {
        this.maxRespTime = maxRespTime;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}
