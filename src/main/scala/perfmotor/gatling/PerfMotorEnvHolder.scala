package perfmotor.gatling

object PerfMotorEnvHolder {
  var baseUrl = ""
  var maxRespTime = 0
  var scenarioName = ""
  var requestName = ""
  
  var token = ""
  var loopCount = 20
  var rampUp = 100
  var httpMethod = ""
  var dataDirectory = "defaultEmptyData.csv"
  var test = ""
  var jsonBody = ""
  var rampUserOver = "5";
}
