package perfmotor.gatling

import io.gatling.core.Predef.{Simulation, configuration, _}
import io.gatling.http.Predef.{http, _}

import scala.concurrent.duration._

class PerfMotorSimulation extends Simulation {
  val httpConf = http(configuration).baseURL(PerfMotorEnvHolder.baseUrl).disableWarmUp
  var feederFileGiven = false

  val header = Map(
    "Authorization" -> PerfMotorEnvHolder.token
  )

  if (PerfMotorEnvHolder.dataDirectory.equals("")) {
    feederFileGiven = false;
  } else {
    try {
      println("Checking feeder file on path : " + PerfMotorEnvHolder.dataDirectory)
      csv(PerfMotorEnvHolder.dataDirectory).circular
      feederFileGiven = true;
    }
    catch {
      case exception: Exception => {
        feederFileGiven = false;
        println("Exception occurred while reading feeder file", exception)
      }
    }
  }

  var perfMotorScenario = scenario("");

  if ((!feederFileGiven) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .exec(http(PerfMotorEnvHolder.requestName)
        .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
        .headers(header)
        .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((feederFileGiven) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
        .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
        .headers(header)
        .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if (feederFileGiven && ((("PUT".equals(PerfMotorEnvHolder.httpMethod))) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
        .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
        .headers(header)
        .body(StringBody(PerfMotorEnvHolder.body)).asJSON
        .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((!feederFileGiven) && (("PUT".equals(PerfMotorEnvHolder.httpMethod)) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .exec(
        http(PerfMotorEnvHolder.requestName)
          .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
          .headers(header)
          .body(StringBody(PerfMotorEnvHolder.body)).asJSON
          .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((feederFileGiven) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
        .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
        .headers(header)
        .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((!feederFileGiven) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .exec(http(PerfMotorEnvHolder.requestName)
        .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
        .headers(header)
        .check(status.is(PerfMotorEnvHolder.expectedStatus)).check(responseTimeInMillis lessThan PerfMotorEnvHolder.expectedMaxResponseTime))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  }

  var perfMotorSimulation = List(perfMotorScenario.inject(
    atOnceUsers(PerfMotorEnvHolder.atOnceUsers),
    rampUsers(PerfMotorEnvHolder.rampUsers) over Duration.apply(PerfMotorEnvHolder.rampUsersOver).asInstanceOf[FiniteDuration],
    constantUsersPerSec(PerfMotorEnvHolder.constantUsersPerSec) during Duration.apply(PerfMotorEnvHolder.constantUsersPerSecDuring).asInstanceOf[FiniteDuration],
    rampUsersPerSec(PerfMotorEnvHolder.rampUsersPerSecRate1) to (PerfMotorEnvHolder.rampUsersPerSecRate2) during Duration.apply(PerfMotorEnvHolder.rampUsersPerSecDuring).asInstanceOf[FiniteDuration]
  ))

  setUp(perfMotorSimulation).protocols(httpConf);
}
