package perfmotor.controller;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import perfmotor.beans.PerfMotorExecVars;
import perfmotor.beans.ServiceDetails;
import perfmotor.gatling.PerfMotorEnvHolder;
import perfmotor.util.Assister;
import perfmotor.util.PerfMotorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class PerfMotorRouter {

    private final String reportPath = System.getProperty("user.dir") + "/src/main/webapp/resources";
    //private final String simulationClass = "com.myorg.perfmotor.perfmotor.gatling.PerfMotorSimulation";
    private final String dataDirectory = System.getProperty("user.dir") + "/src/main/webapp/data";

    @RequestMapping(method = RequestMethod.GET, path = "/cars")
    @ApiOperation(value = "Just a GET",
            notes = "To return the message", response = String.class, httpMethod = "GET")
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Unprocessable Entity (Invalid request content)."),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public String justAGet() {
        System.out.println("Mustang, Fusion, GT ... etc");
        return "Mustang, Fusion, GT ... etc";
    }

    @RequestMapping(value = "/pm", method = RequestMethod.GET)
    public String home(Map<String, Object> model) {
        model.put("message", "HowToDoInJava Reader !!");
        return "perf-motor";
    }

    @ApiOperation(value = "This is POST",
            notes = "To return the message", response = String.class, httpMethod = "POST")
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Unprocessable Entity (Invalid request content)."),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/runPerfMotor", method = RequestMethod.POST)
    @ResponseBody
    public synchronized String runPerformanceTest(@RequestBody String message,
                                                  HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws PerfMotorException {

        try {
            FileUtils.deleteDirectory(new File(reportPath));
        } catch (IOException e) {
            throw new PerfMotorException("Exception while deleting existing report file", e);
        }

        System.out.println(">>>>>>> received message : " + message);

        JSONObject jsonMessage = new JSONObject(message);

        System.out.println("httpMethod from the jsonObject : " + jsonMessage.getString("method"));
        System.out.println("url from the jsonObject : " + jsonMessage.getString("url"));
        System.out.println("nbrOfReq from the jsonObject : " + jsonMessage.getString("nbrOfReq"));
        System.out.println("nbrOfLoops from the jsonObject : " + jsonMessage.getString("nbrOfLoops"));

        //System.out.println("bodyPart from the jsonObject : "+jsonMessage.getString("body"));
        //System.out.println("fileContent from the jsonObject : "+jsonMessage.getBoolean("fileContent"));


        //System.out.println("fileContent from the jsonObject : "+jsonMessage.getBoolean("fileContent"));


        String baseUrl = "http://localhost:8082/ford/cars";
        String endPointUrl = jsonMessage.getString("url");
        String httpMethod = jsonMessage.getString("method");
        int loopCount = Integer.parseInt(jsonMessage.getString("nbrOfLoops"));
        int requestNumber = Integer.parseInt(jsonMessage.getString("nbrOfReq"));
        String contentType = "application/json";
        String feederDataFileName = dataDirectory + "/dataNew.csv";
        //String jsonBody = "{\"content\":\"${carName}\"}";  // to test passing a reference in the json body
        String jsonBody = null;
        try {
            jsonBody = jsonMessage.getString("body");
            System.out.println("nbrOfLoops from the jsonObject : " + jsonBody);
        } catch (Exception exception) {

        }

        try {
            System.out.println(">>>>>>>>> decoded url : " + URLDecoder.decode(endPointUrl, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            System.out.println(">>>>>>>>> decoded url faild ");
            //e.printStackTrace();
        }

        PerfMotorExecVars perfMotorExecVars = new PerfMotorExecVars();

        if (jsonMessage.get("fileContent") instanceof String) {
            writeDataToFile(jsonMessage);
            PerfMotorEnvHolder.dataDirectory_$eq(feederDataFileName);
        } else {
            PerfMotorEnvHolder.dataDirectory_$eq("defaultDataPath.csv");
        }


        String simulationClass = "perfmotor.gatling.PerfMotorSimulation";
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();
        props.simulationClass(simulationClass);
        props.resultsDirectory(reportPath);
        props.dataDirectory(dataDirectory);

        System.out.println(">>>>>>dat dir " + dataDirectory);


        //PerfMotorExecVars perfMotorExecVars = new PerfMotorExecVars();
        perfMotorExecVars.setBaseUrl(baseUrl);
        perfMotorExecVars.setMaxRespTime(50);
        perfMotorExecVars.setRequestName("Sample Request Name");
        perfMotorExecVars.setScenarioName("Sample Scenario Name");

        try {
            PerfMotorEnvHolder.baseUrl_$eq(URLDecoder.decode(endPointUrl, StandardCharsets.UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        PerfMotorEnvHolder.maxRespTime_$eq(500);
        PerfMotorEnvHolder.requestName_$eq("Sample_req");
        PerfMotorEnvHolder.scenarioName_$eq("sample scen");
        PerfMotorEnvHolder.loopCount_$eq(loopCount);
        PerfMotorEnvHolder.rampUp_$eq(requestNumber);
        PerfMotorEnvHolder.token_$eq("beare " + httpServletRequest.getHeader("Authorization"));
//      PerfMotorEnvHolder.dataDirectory_$eq(feederDataFileName);
        PerfMotorEnvHolder.httpMethod_$eq(httpMethod);
        PerfMotorEnvHolder.test_$eq(jsonBody);


        executeRun(props);
        perfMotorExecVars = null;


        File file = new File(System.getProperty("user.dir") + "/src/main/webapp/resources");
        String[] listOfSubfolders = file.list();
        String actualReportFolder = listOfSubfolders[0];
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>... Actual report folder name: " + actualReportFolder);


        return "resources/" + actualReportFolder + "/index.html";
    }


    @RequestMapping(method = RequestMethod.GET, value = "/perfMotor")
    public String loadServiceDetails(HttpServletRequest request) {
        StringBuilder url = getUrl(request);
        try {
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(true);
            scanner.addIncludeFilter(
                    new AnnotationTypeFilter(Controller.class));

            Class<?> clazz = null;
            ServiceDetails serviceDetails;
            for (BeanDefinition bd : scanner.findCandidateComponents("com.myorg.samplespringboot")) {
                serviceDetails = new ServiceDetails();
                try {
                    clazz = Class.forName(bd.getBeanClassName());
                    String basePath = Assister.getBasePath(clazz);
                    if (null != basePath) {
                        serviceDetails.setBasePath(basePath);
                        Assister.getEndPointDetails(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new PerfMotorException("Exception in finding base path", e);
                }
            }
        } catch (PerfMotorException e) {
            //TODO implement
        }
        return "perf-motor";
    }

    /*@RequestMapping(value = "/runPerformanceTest", method = RequestMethod.GET)
    //@ResponseBody
    public String runPerformanceTest(HttpServletRequest httpServletRequest) throws PerfMotorException {
    	
        //String baseUrl = "http://localhost:8080/students/${USER_ID}";
        //String baseUrl = "http://localhost:8082/ford/cars/colors/${carName}";
    	String baseUrl = "http://localhost:8082/ford/cars";
        String httpMethod = "POST";
        String contentType = "application/json";
        String feederDataFileName = "placeHoldeFeeder.csv";
        //String jsonBody = "{\"content\":\"John\"}";  // to test with plian json body only
        String jsonBody = "{\"content\":\"${carName}\"}";  // to test passing a reference in the json body

        String simulationClass = "com.myorg.perfmotor.perfmotor.gatling.PerfMotorSimulation";
        GatlingPropertiesBuilder props = new GatlingPropertiesBuilder();
        props.simulationClass(simulationClass);
        props.resultsDirectory(reportPath);
        props.dataDirectory(dataDirectory);
        //props.outputDirectoryBaseName("hello");
        
        System.out.println(">>>>>>dat dir "+dataDirectory);
        

        PerfMotorExecVars perfMotorExecVars = new PerfMotorExecVars();
        perfMotorExecVars.setBaseUrl(baseUrl);
        perfMotorExecVars.setMaxRespTime(50);
        perfMotorExecVars.setRequestName("Sample Request Name");
        perfMotorExecVars.setScenarioName("Sample Scenario Name");

        PerfMotorEnvHolder.baseUrl_$eq(baseUrl);
        PerfMotorEnvHolder.maxRespTime_$eq(500);
        PerfMotorEnvHolder.requestName_$eq("Sample_req");
        PerfMotorEnvHolder.scenarioName_$eq("sample scen");
        PerfMotorEnvHolder.loopCount_$eq(2);
        PerfMotorEnvHolder.rampUp_$eq(10);
        PerfMotorEnvHolder.token_$eq("beare "+httpServletRequest.getHeader("Authorization"));
        PerfMotorEnvHolder.dataDirectory_$eq(feederDataFileName);
        PerfMotorEnvHolder.httpMethod_$eq(httpMethod);
        PerfMotorEnvHolder.test_$eq(jsonBody);


        executeRun(props);
        perfMotorExecVars = null;
        
        
        File file = new File(System.getProperty("user.dir") + "/src/main/webapp/resources");
        String[] listOfSubfolders = file.list();
        String actualReportFolder = listOfSubfolders[0];
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>... Actual report folder name: "+actualReportFolder);

        
        return  actualReportFolder + "/index";
  }*/

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

    private void executeRun(GatlingPropertiesBuilder props) throws PerfMotorException {

        //      FileUtils.deleteDirectory(new File(reportPath));
        Gatling.fromMap(props.build());
        props = null;
    }

    private void clearFileDirectory(String filePath) {
        try {
            FileUtils.deleteDirectory(new File(filePath + "/data"));
            System.out.println(">>>>>>> data directory cleared");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeDataToFile(JSONObject feederData) {

        FileWriter writer = null;
        try {
            writer = new FileWriter(dataDirectory + "/dataNew.csv", false);
            writer.append(feederData.getString("fileContent"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (null != writer) {
                try {
                    System.out.println(">>>>>>>>>>>>>>> flushing the writer");
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

}
