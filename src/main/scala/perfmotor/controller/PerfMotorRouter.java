package perfmotor.controller;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import perfmotor.beans.TestingDetails;
import perfmotor.gatling.PerfMotorEnvHolder;
import perfmotor.util.PerfMotorException;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class PerfMotorRouter {

  private static final Logger LOGGER = LoggerFactory.getLogger(PerfMotorRouter.class);

  private final String reportPath = System.getProperty("user.dir") + "/src/main/webapp/resources";
  private final String dataDirectory = System.getProperty("user.dir") + "/src/main/webapp/data";
  private final String simulationClass = "perfmotor.gatling.PerfMotorSimulation";

  /**
   * Sample method to body
   */
  @ApiIgnore
  @RequestMapping(method = RequestMethod.GET, path = "/perfMotor/cars")
  public String justAGet() {
    System.out.println("Mustang, Fusion, GT ... etc");
    return "Mustang, Fusion, GT ... etc";
  }

  @ApiIgnore
  @RequestMapping(value = "/runPerfMotor", method = RequestMethod.POST)
  public synchronized void runPerformanceTest(@RequestBody TestingDetails testingDetails)
      throws PerfMotorException {
    LOGGER.info("Requested Http Method: " + testingDetails.getMethod());
    LOGGER.info("Requested Http URL : " + testingDetails.getUrl());

    preActions();
    convertDetailsForScala(testingDetails);
    executeRun();
    //return getPerfTestReportPath();
  }

  @ApiIgnore
  @RequestMapping(value = "/getLastGeneratedReport", method = RequestMethod.GET, produces = "text/html")
  public synchronized String getLastGeneratedReport() throws PerfMotorException {
    LOGGER.info("To get last generated report");
    return getPerfTestReportPath();
  }

  /**
   * Scan and get gatling generated report path html.
   *
   * @return String - Generated report path.
   */
  private String getPerfTestReportPath() throws PerfMotorException {
    String reportHtml;
    File file = new File(reportPath);
    String[] listOfSubfolders = file.list();
    //String reportHtml =  "resources/" + listOfSubfolders[0] + "/index.html";
    FileReader fileReader = null;
    try {
      fileReader = new FileReader(reportPath + "/" + listOfSubfolders[0] + "/index.html");
      reportHtml = IOUtils.toString(fileReader);
      reportHtml = reportHtml.startsWith("\n") ? reportHtml.substring(1) : reportHtml;
    } catch (FileNotFoundException e) {
      LOGGER.error("Exception while reading report html", e);
      throw new PerfMotorException("Exception while reading report html", e);
    } catch (IOException e) {
      LOGGER.error("Exception while converting html report to string", e);
      throw new PerfMotorException("Exception while converting html report to string", e);
    } finally {
      if (null != fileReader) {
        try {
          fileReader.close();
        } catch (IOException e) {
          LOGGER.error("Exception while closing the generated report", e);
          throw new PerfMotorException("Exception while closing the generated report", e);
        }
      }
    }
    return reportHtml;
  }

  /**
   * Assign given testing details into scala environment to do the execution.
   *
   * @param testingDetails Given testing details
   */
  private void convertDetailsForScala(@RequestBody TestingDetails testingDetails)
      throws PerfMotorException {
    try {
      PerfMotorEnvHolder.baseUrl_$eq(
          URLDecoder.decode(testingDetails.getUrl(), StandardCharsets.UTF_8.toString()));
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Exception while decoding given url", e);
      throw new PerfMotorException("Exception while decoding given url", e);
    }
    PerfMotorEnvHolder.requestName_$eq("For given request");
    PerfMotorEnvHolder.scenarioName_$eq("For given scenario");
    if (null != testingDetails.getHeaders().getAuthorization()) {
      PerfMotorEnvHolder.token_$eq(testingDetails.getHeaders().getAuthorization());
    } else {
      PerfMotorEnvHolder.token_$eq("");
    }
    PerfMotorEnvHolder.httpMethod_$eq(testingDetails.getMethod());
    PerfMotorEnvHolder.body_$eq(testingDetails.getBody());

    PerfMotorEnvHolder.expectedMaxResponseTime_$eq(testingDetails.getExpectedMaxResponseTime());
    PerfMotorEnvHolder.expectedStatus_$eq(testingDetails.getExpectedStatus());

    PerfMotorEnvHolder.atOnceUsers_$eq(testingDetails.getAtOnceUsers());
    PerfMotorEnvHolder.rampUsers_$eq(testingDetails.getRampUsers());
    if (null != testingDetails.getRampUsersOver()) {
      PerfMotorEnvHolder.rampUsersOver_$eq(testingDetails.getRampUsersOver());
    } else {
      PerfMotorEnvHolder.rampUsersOver_$eq("0 seconds");
    }

    PerfMotorEnvHolder.constantUsersPerSec_$eq(testingDetails.getConstantUsersPerSec());

    if (null != testingDetails.getConstantUsersPerSecDuring()) {
      PerfMotorEnvHolder
          .constantUsersPerSecDuring_$eq(testingDetails.getConstantUsersPerSecDuring());
    } else {
      PerfMotorEnvHolder.constantUsersPerSecDuring_$eq("0 seconds");
    }

    PerfMotorEnvHolder.rampUsersPerSecRate1_$eq(testingDetails.getRampUsersPerSecRate1());
    PerfMotorEnvHolder.rampUsersPerSecRate2_$eq(testingDetails.getRampUsersPerSecRate2());
    if (null != testingDetails.getRampUsersPerSecDuring()) {
      PerfMotorEnvHolder.rampUsersPerSecDuring_$eq(testingDetails.getRampUsersPerSecDuring());
    } else {
      PerfMotorEnvHolder.rampUsersPerSecDuring_$eq("0 seconds");
    }

    if (null != testingDetails.getFileContent()
        && !testingDetails.getFileContent().equalsIgnoreCase("false")) {
      PerfMotorEnvHolder.dataDirectory_$eq(writeDataToFile(testingDetails.getFileContent()));
    } else {
      PerfMotorEnvHolder.dataDirectory_$eq("");
    }
  }

  /**
   * Do preliminary actions before executing a run.
   */
  private void preActions() throws PerfMotorException {
    try {
      FileUtils.deleteDirectory(new File(reportPath));
    } catch (IOException e) {
      LOGGER.error("Exception while deleting existing report file", e);
      throw new PerfMotorException("Exception while deleting existing report file", e);
    }
  }

  /**
   * Execute scala-gatling
   */
  private void executeRun() {
    GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();
    props.simulationClass(simulationClass);
    props.resultsDirectory(reportPath);
    props.dataDirectory(dataDirectory);

    Gatling.fromMap(props.build());
  }

  /**
   * Write the given file content into local for Gatling to read.
   *
   * @param feederData Given file content.
   * @return String - Path where file content written in file.
   */
  private String writeDataToFile(String feederData) throws PerfMotorException {
    String fileAbsPath = dataDirectory + "/dataNew.csv";
    FileWriter writer = null;
    try {
      writer = new FileWriter(fileAbsPath, false);
      writer.append(feederData);
    } catch (IOException e) {
      LOGGER.error("Unable to write feederData", e);
      throw new PerfMotorException("Unable to write feederData", e);
    } finally {
      if (null != writer) {
        try {
          writer.flush();
          writer.close();
        } catch (IOException e) {
          LOGGER.error("Unable to flush feederData", e);
          throw new PerfMotorException("Unable to flush feederData", e);
        }
      }

    }
    return fileAbsPath;
  }
}
