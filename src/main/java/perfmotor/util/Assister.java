package perfmotor.util;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

public class Assister {

    /**
     * To get basepath of the router.
     * @param clazz a class to be scanned for basePath
     * @return a String if basePath available, Else Null.
     * @throws PerfMotorException
     */
    public static String getBasePath(Class<?> clazz) {
        String basePath = null;

        RequestMapping requestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);
        if (null != requestMapping && null != requestMapping.path()) {
            basePath = requestMapping.path()[0];
        }
        return basePath;
    }

    /**
     * To get end point details from the router.
     * @param clazz a class to be scanned for end point details.
     */
    public static void getEndPointDetails(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);

        }
    }
}
