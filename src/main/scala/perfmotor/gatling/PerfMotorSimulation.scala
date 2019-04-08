package perfmotor.gatling

import io.gatling.core.Predef.{Simulation, configuration, _}
import io.gatling.http.Predef.{http, _}

import scala.concurrent.duration._

class PerfMotorSimulation extends Simulation {
  val httpConf = http(configuration).baseUrl(PerfMotorEnvHolder.baseUrl)
  val header = Map(
    "Authorization" -> PerfMotorEnvHolder.token
  )

//  var myFeeder = Iterator[Map[String, String]]

  before {
    println("Simulation is about to start!")
//    if (!PerfMotorEnvHolder.dataDirectory.equals("")) {
//      myFeeder = RecordSeqFeederBuilder(csv(PerfMotorEnvHolder.dataDirectory).records).circular
//    }
  }

  after {
    println("Simulation is finished!")
  }

  try {
    var perfMotorScenario = scenario("")

    if (PerfMotorEnvHolder.dataDirectory.equals("")) {
      perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
        .exec(http(PerfMotorEnvHolder.requestName)
          .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
          .headers(header)
          .body(StringBody(PerfMotorEnvHolder.body)).asJson
          .check(status.is(PerfMotorEnvHolder.expectedStatus))
          .check(responseTimeInMillis lte PerfMotorEnvHolder.expectedMaxResponseTime))
        .exec(flushHttpCache).exec(flushCookieJar).exec(flushSessionCookies)
    } else {
      val myFeeder = csv(PerfMotorEnvHolder.dataDirectory).batch.circular

      perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
        .feed(myFeeder)
        .exec(http(PerfMotorEnvHolder.requestName)
          .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
          .headers(header)
          .body(StringBody(PerfMotorEnvHolder.body)).asJson
          .check(status.is(PerfMotorEnvHolder.expectedStatus))
          .check(responseTimeInMillis lte PerfMotorEnvHolder.expectedMaxResponseTime))
        .exec(flushHttpCache).exec(flushCookieJar).exec(flushSessionCookies)
    }

    val perfMotorSimulation = perfMotorScenario.inject(
      atOnceUsers(PerfMotorEnvHolder.atOnceUsers),
      rampUsers(PerfMotorEnvHolder.rampUsers) during Duration.apply(PerfMotorEnvHolder.rampUsersOver).asInstanceOf[FiniteDuration],
      constantUsersPerSec(PerfMotorEnvHolder.constantUsersPerSec) during Duration.apply(PerfMotorEnvHolder.constantUsersPerSecDuring).asInstanceOf[FiniteDuration],
      rampUsersPerSec(PerfMotorEnvHolder.rampUsersPerSecRate1) to (PerfMotorEnvHolder.rampUsersPerSecRate2) during Duration.apply(PerfMotorEnvHolder.rampUsersPerSecDuring).asInstanceOf[FiniteDuration]
    )

    setUp(perfMotorSimulation).protocols(httpConf);
  }
  catch {
    case exception: Exception => {
      println("Exception occurred while gatling execution.", exception)
    }
  }
}
