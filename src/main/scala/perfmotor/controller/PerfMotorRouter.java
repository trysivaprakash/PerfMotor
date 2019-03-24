package perfmotor.controller;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import perfmotor.beans.TestingDetails;
import perfmotor.gatling.PerfMotorEnvHolder;
import perfmotor.util.PerfMotorException;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@ApiIgnore
@Controller
public class PerfMotorRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfMotorRouter.class);

    private final String reportPath = System.getProperty("user.dir") + "/src/main/webapp/resources";
    private final String dataDirectory = System.getProperty("user.dir") + "/src/main/webapp/data";
    private final String simulationClass = "perfmotor.gatling.PerfMotorSimulation";

    /**
     * Sample method to test
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, path = "/cars")
    public String justAGet() {
        System.out.println("Mustang, Fusion, GT ... etc");
        return "Mustang, Fusion, GT ... etc";
    }

    @RequestMapping(value = "/pm", method = RequestMethod.GET)
    public String home() {
        return "perf-motor";
    }

    @RequestMapping(value = "/runPerfMotor", method = RequestMethod.POST)
    @ResponseBody
    public synchronized String runPerformanceTest(@RequestBody TestingDetails testingDetails, HttpServletRequest httpServletRequest,
                                                  HttpServletResponse httpServletResponse) throws PerfMotorException {
        LOGGER.info("Requested Http Method: " + testingDetails.getMethod());
        LOGGER.info("Requested Http URL : " + testingDetails.getUrl());

        preActions();
        convertDetailsForScala(testingDetails);
        executeRun();
        return getPerfTestReportPath();
    }

    /**
     * Scan and get gatling generated report path html.
     *
     * @return String - Generated report path.
     */
    private String getPerfTestReportPath() {
        File file = new File(reportPath);
        String[] listOfSubfolders = file.list();
        String actualReportFolder = listOfSubfolders[0];
        return "resources/" + actualReportFolder + "/index.html";
    }

    /**
     * Assign given testing details into scala environment to do the execution.
     *
     * @param testingDetails Given testing details
     * @throws PerfMotorException
     */
    private void convertDetailsForScala(@RequestBody TestingDetails testingDetails) throws PerfMotorException {
        try {
            PerfMotorEnvHolder.baseUrl_$eq(URLDecoder.decode(testingDetails.getUrl(), StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Exception while decoding given url", e);
            throw new PerfMotorException("Exception while decoding given url", e);
        }
        PerfMotorEnvHolder.httpMethod_$eq(testingDetails.getMethod());
        PerfMotorEnvHolder.loopCount_$eq(testingDetails.getNbrOfLoops());
        PerfMotorEnvHolder.rampUp_$eq(testingDetails.getNbrOfReq());
        PerfMotorEnvHolder.test_$eq(testingDetails.getBody());

        if (null != testingDetails.getFileContent()
                && !testingDetails.getFileContent().equalsIgnoreCase("false")) {
            PerfMotorEnvHolder.dataDirectory_$eq(writeDataToFile(testingDetails.getFileContent()));
        } else {
            PerfMotorEnvHolder.dataDirectory_$eq("");
        }
        PerfMotorEnvHolder.maxRespTime_$eq(500);
        PerfMotorEnvHolder.requestName_$eq("For given request");
        PerfMotorEnvHolder.scenarioName_$eq("For given scenario");
        if (null != testingDetails.getHeaders().getAuthorization()) {
            PerfMotorEnvHolder.token_$eq(testingDetails.getHeaders().getAuthorization());
        } else {
            PerfMotorEnvHolder.token_$eq("");
        }
    }

    /**
     * Do preliminary actions before executing a run.
     *
     * @throws PerfMotorException
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
     * To identify the given host and port.
     *
     * @param request HttpServletRequest
     * @return StringBuilder - Host Details
     */
    private StringBuilder getUrl(HttpServletRequest request) {
        StringBuilder url = new StringBuilder()
                .append(request.getScheme())
                .append("://")
                .append(request.getServerName());
        if (-1 != request.getServerPort()) {
            url.append(":").append(request.getServerPort());
        }
        return url;
    }

    /**
     * Write the given file content into local for Gatling to read.
     *
     * @param feederData Given file content.
     * @return String - Path where file content written in file.
     * @throws PerfMotorException
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
