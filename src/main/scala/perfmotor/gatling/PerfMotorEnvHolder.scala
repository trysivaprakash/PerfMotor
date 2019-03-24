package perfmotor.gatling

object PerfMotorEnvHolder {

//  val perfMotorExecVars = new PerfMotorExecVars

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

//  val baseUrl = scala.util.Properties.envOrElse("baseURL", "")

  /*val baseURL = scala.util.Properties.envOrElse("baseURL", "")
  val maxResponseTime = scala.util.Properties.envOrElse("maxResponseTime", "240000") // in milliseconds*/
}
