package perfmotor.gatling

object PerfMotorEnvHolder {
  var baseUrl = ""
  var scenarioName = ""
  var requestName = ""
  var httpMethod = ""
  var dataDirectory = ""
  var token = ""
  var body = ""

  var expectedMaxResponseTime = 0
  var expectedStatus = 0
  var atOnceUsers = 0
  var rampUsers = 0
  var rampUsersOver = ""
  var constantUsersPerSec = 0
  var constantUsersPerSecDuring = ""
  var rampUsersPerSecRate1 = 0
  var rampUsersPerSecRate2 = 0
  var rampUsersPerSecDuring = ""
}
