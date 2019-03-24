package perfmotor.gatling

import io.gatling.core.Predef.{Simulation, configuration, _}
import io.gatling.http.Predef.{http, _}

import scala.concurrent.duration._

class PerfMotorSimulation extends Simulation {
  val httpConf = http(configuration).baseURL(PerfMotorEnvHolder.baseUrl).disableWarmUp
  val maxRespTime = PerfMotorEnvHolder.maxRespTime
  var feederFileGiven = false
  var jsonBOdyFlag = true

  val header = Map(
  "Authorization" -> PerfMotorEnvHolder.token
  )

  if (PerfMotorEnvHolder.dataDirectory.equals("")) {
    feederFileGiven = false;
  } else {
    try {
      println("Checking feeder file on path : "+PerfMotorEnvHolder.dataDirectory)
      csv(PerfMotorEnvHolder.dataDirectory).circular
      feederFileGiven = true;
    }
    catch {
      case exception : Exception => {
        feederFileGiven = false;
        println("Exception occurred while reading feeder file", exception)
      }
    }
  }

  var perfMotorScenario = scenario("");

  if((!feederFileGiven) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((feederFileGiven) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if (feederFileGiven && ((("PUT".equals(PerfMotorEnvHolder.httpMethod))) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .body(StringBody(PerfMotorEnvHolder.test)).asJSON
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((!feederFileGiven) && (("PUT".equals(PerfMotorEnvHolder.httpMethod)) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
    .exec(
          http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .body(StringBody(PerfMotorEnvHolder.test)).asJSON
            .check(status.is(200))).exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  }else if ((feederFileGiven) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
        .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  }else if ((!feederFileGiven) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
        .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  }

  var perfMotorSimulation = List(perfMotorScenario.inject(
      atOnceUsers(PerfMotorEnvHolder.rampUp),
      constantUsersPerSec(PerfMotorEnvHolder.loopCount) during Duration.apply("1 seconds").asInstanceOf[FiniteDuration]
    ))

  setUp(perfMotorSimulation).protocols(httpConf);
}
