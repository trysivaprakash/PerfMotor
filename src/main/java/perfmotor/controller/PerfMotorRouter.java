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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import perfmotor.beans.TestingDetails;
import perfmotor.gatling.PerfMotorEnvHolder;
import perfmotor.util.PerfMotorException;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RestController
public class PerfMotorRouter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerfMotorRouter.class);
    private static final String reportPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "resources";
    private static final String dataDirectory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "data";
    private static final String simulationClass = "perfmotor.gatling.PerfMotorSimulation";

    @Value("${perfMotor.enable}")
    private boolean perfMotorEnabled;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Sample method
     */
    @RequestMapping(method = RequestMethod.GET, path = "/samplePerfMotorGetCars")
    @ResponseBody
    public String justAGet() {
        System.out.println("Mustang, Fusion, GT ... etc");
        return "Mustang, Fusion, GT ... etc";
    }

    @RequestMapping(value = "/getSwaggerDocDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public synchronized String getSwaggerDocDetails(@RequestParam("swaggerUrl") String swaggerUrl) {
        LOGGER.info("To get swagger url");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(swaggerUrl, String.class);
        return responseEntity.getBody();
    }

    @RequestMapping(value = "/runPerfMotor", method = RequestMethod.POST)
    public synchronized void runPerformanceTest(@RequestBody TestingDetails testingDetails)
            throws PerfMotorException {

        LOGGER.info("Requested Http Method: " + testingDetails.getMethod());
        LOGGER.info("Requested Http URL : " + testingDetails.getUrl());
        if (perfMotorEnabled) {
            LOGGER.info("PERF MOTOR enabled and gonna execute!");
            preActions();
            convertDetailsForScala(testingDetails);
            executeRun();
        } else {
            LOGGER.info("PERF MOTOR disabled and not executed!");
        }
    }

    @RequestMapping(value = "/lastPerfMotorGeneratedReport", method = RequestMethod.GET, produces = "text/html")
    public synchronized String getLastGeneratedReport() throws PerfMotorException {
        LOGGER.info("To get last generated report");
        return getPerfTestReportPath();
    }

    @RequestMapping(value = "/perf-motor/js/{jsFileName}", method = RequestMethod.GET, produces = "application/javascript")
    public synchronized String getJsFile(@PathVariable("jsFileName") String jsFileName) throws PerfMotorException {
        LOGGER.info("To get javascript(js) file. Name - " + jsFileName);
        if (!jsFileName.endsWith(".js")) {
            jsFileName = jsFileName + ".js";
        }
        return loadJsFile(jsFileName);
    }

    private String loadJsFile(String expJsFileName) throws PerfMotorException {
        FileReader fileReader = null;
        File file;
        try {
            file = new File(reportPath);
            String[] listOfSubfolders = file.list();
            file = new File(reportPath + "/" + listOfSubfolders[0] + "/js");
            String[] listOfJs = file.list();
            for (String jsFile : listOfJs) {
                if (jsFile.equals(expJsFileName)) {
                    fileReader = new FileReader(reportPath + "/" + listOfSubfolders[0] + "/js/" + jsFile);
                    return IOUtils.toString(fileReader);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception while reading report html", e);
            throw new PerfMotorException("Exception while reading report html", e);
        } catch (IOException e) {
            LOGGER.error("Exception while converting html report to string", e);
            throw new PerfMotorException("Exception while converting html report to string", e);
        } finally {
            try {
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                LOGGER.error("Exception while closing the generated report", e);
                throw new PerfMotorException("Exception while closing the generated report", e);
            }
        }
        return null;
    }

    /**
     * Scan and get gatling generated report path html.
     *
     * @return String - Generated report path.
     */
    private String getPerfTestReportPath() throws PerfMotorException {
        String reportHtml;
        FileReader fileReader = null;
        File file;
        try {
            file = new File(reportPath);
            String[] listOfSubfolders = file.list();
            if (null == listOfSubfolders) {
                return "<!DOCTYPE html><html><head><title>PERF-MOTOR</title></head><body><h1>Report not available. Please check \"perfMotor.enable\" property and run PERF-MOTOR again!</h1></body></html>";
            }
            fileReader = new FileReader(reportPath + "/" + listOfSubfolders[0] + "/index.html");
            reportHtml = IOUtils.toString(fileReader);
            reportHtml = reportHtml.startsWith("\n") ? reportHtml.substring(1) : reportHtml;
            reportHtml = reportHtml.replace("<script type=\"text/javascript\" src=\"js/", "<script type=\"text/javascript\" src=\"perf-motor/js/");
            reportHtml = reportHtml.replace("<div class=\"item \"><a id=\"details_link\" href=\"#\">DETAILS</a></div>", "");
        } catch (FileNotFoundException e) {
            LOGGER.error("Exception while reading report html", e);
            throw new PerfMotorException("Exception while reading report html", e);
        } catch (IOException e) {
            LOGGER.error("Exception while converting html report to string", e);
            throw new PerfMotorException("Exception while converting html report to string", e);
        } finally {
            try {
                if (null != fileReader) {
                    fileReader.close();
                }
            } catch (IOException e) {
                LOGGER.error("Exception while closing the generated report", e);
                throw new PerfMotorException("Exception while closing the generated report", e);
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
        if (null != testingDetails.getBody()) {
            PerfMotorEnvHolder.body_$eq(testingDetails.getBody());
        } else {
            PerfMotorEnvHolder.body_$eq("");
        }


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
            LOGGER.info("File content provided");
            PerfMotorEnvHolder.dataDirectory_$eq(writeDataToFile(testingDetails.getFileContent()));
        } else {
            LOGGER.info("File content not provided");
            PerfMotorEnvHolder.dataDirectory_$eq("");
        }
    }

    /**
     * Do preliminary actions before executing a run.
     */
    private void preActions() throws PerfMotorException {
        try {
            FileUtils.deleteDirectory(new File(reportPath));
            FileUtils.deleteDirectory(new File(dataDirectory));
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
        props.outputDirectoryBaseName("PerfMotorSimulation");

        Gatling.fromMap(props.build());
    }

    /**
     * Write the given file content into local for Gatling to read.
     *
     * @param feederData Given file content.
     * @return String - Path where file content written in file.
     */
    private String writeDataToFile(String feederData) throws PerfMotorException {
        FileWriter writer = null;
        String fileAbsPath = dataDirectory + File.separator + "data-" + Math.random() * 100 + ".csv";
        LOGGER.info("Writing file content in path - " + fileAbsPath);
        try {
            File dataDirFile = new File(dataDirectory);
            if (!dataDirFile.exists()) {
                LOGGER.info("Data directory not exists. Creating - " + dataDirectory);
                boolean isDataDirFileCreated = dataDirFile.mkdirs();
                LOGGER.info("isDataDirFileCreated - " + isDataDirFileCreated);
            }
            File file = new File(fileAbsPath);
            if (!file.exists()) {
                LOGGER.info("File does not exists. Creating - " + fileAbsPath);
                file.createNewFile();
            }
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
