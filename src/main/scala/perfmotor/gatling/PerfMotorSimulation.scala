package perfmotor.gatling

import io.gatling.core.Predef.{Simulation, configuration, _}
import io.gatling.http.Predef.{http, _}

import scala.concurrent.duration._

class PerfMotorSimulation extends Simulation {
  val httpConf = http(configuration).baseURL(PerfMotorEnvHolder.baseUrl).disableWarmUp
  val maxRespTime = PerfMotorEnvHolder.maxRespTime
  
  val header = Map(
  "Authorization" -> PerfMotorEnvHolder.token
  )
  
  var flag = false;
  var jsonBOdyFlag = true;
  
  try {
    println(">>>>>>>>> checking feeder file on path : "+PerfMotorEnvHolder.dataDirectory)
   val bulkCsvRequestData  = csv(PerfMotorEnvHolder.dataDirectory).circular
  println(">>>>>>>>>>>>> userData in PerfMotor Simulation : "+bulkCsvRequestData)
    flag = true;
  }
  catch {
    case exception : Exception => {
      flag = false;
      println(">>>>>>exception occured while reading csv, probable cuase...no feeder data provided.")
    }
  }
  
  if("".equals(PerfMotorEnvHolder.test)) {
    jsonBOdyFlag = false;
  }
  println(">>>>>>> feederDataFlag : "+flag)
  println(">>>>>>> jsonBodyDataFlag : "+jsonBOdyFlag)
  println(">>>>>>> jsonBodyData : "+PerfMotorEnvHolder.test)
  println("request method : >>>>  " +  PerfMotorEnvHolder.httpMethod )
  println("evaluate : >>>>  " +  (flag && ("GET".equals(PerfMotorEnvHolder.httpMethod) )))
  
  
  //val temp = PerfMotorEnvHolder.loopCount*PerfMotorEnvHolder.rampUp
  
  //val feeder = Iterator.continually(Map("emailllllll" -> (Random.alphanumeric.take(temp).mkString + "@foo.com")))


  var perfMotorScenario = scenario("");
  
  if((!flag) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)

  } else if ((flag) && ("GET".equals(PerfMotorEnvHolder.httpMethod))) {
    println(">>>>>>>> here i am")
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if (flag && ((("PUT".equals(PerfMotorEnvHolder.httpMethod))) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
      .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .body(StringBody(PerfMotorEnvHolder.test)).asJSON
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  } else if ((!flag) && (("PUT".equals(PerfMotorEnvHolder.httpMethod)) || ("POST".equals(PerfMotorEnvHolder.httpMethod)))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
    .exec(
          http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .body(StringBody(PerfMotorEnvHolder.test)).asJSON
            .check(status.is(200))).exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)

  }else if ((flag) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
    perfMotorScenario = scenario(PerfMotorEnvHolder.scenarioName)
      .feed(csv(PerfMotorEnvHolder.dataDirectory).circular)
        .exec(http(PerfMotorEnvHolder.requestName)
            .httpRequest(PerfMotorEnvHolder.httpMethod, PerfMotorEnvHolder.baseUrl)
            .headers(header)
            .check(status.is(200)))
      .exec(flushHttpCache).exec(flushHttpCache).exec(flushSessionCookies)
  }else if ((!flag) && ("DELETE".equals(PerfMotorEnvHolder.httpMethod))) {
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
