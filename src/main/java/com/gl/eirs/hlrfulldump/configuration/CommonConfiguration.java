//package com.gl.eirs.hlrfulldump.configuration;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Properties;
//
//@Service
//public class CommonConfiguration {
//
//    @Autowired
//    AppConfig appConfig;
//
////    private static final String commonConfigFileName = ProcessConfiguration.getProperty("commonConfigFileName").trim();
////    private static String commonConfigFilePath = ProcessConfiguration.getProperty("commonConfigFilePath").trim();
//    private static final Properties properties = new Properties();
//    private static final Logger logger = LogManager.getLogger(CommonConfiguration.class);
//
//    static {
//        try {
//            String commonConfigFileName = appConfig.getCommonConfigFilePath().trim();
//            String commonConfigFilePath = ProcessConfiguration.getProperty("commonConfigFilePath").trim();
//            commonConfigFilePath = commonConfigFilePath.replace("${APP_HOME}", System.getenv("APP_HOME"));
//            String propFileName = commonConfigFilePath + "/" + commonConfigFileName;
//            FileInputStream input = new FileInputStream(propFileName);
//            if(input != null) {
//                properties.load(input);
//            } else {
//                throw new FileNotFoundException("Property File '" + propFileName + "' not found in the classpath.");
//            }
//        } catch (IOException io) {
//            logger.error(io.toString(), (Throwable) io);
//        }
//    }
//
//    public static String getProperty(String key) {
//
//        String value = properties.getProperty(key);
//        if(value == null) {
//            try {
//                throw new Exception(key + " not found!");
//            } catch (Exception e) {
//                logger.error(e.getMessage());
//            }
//        }
//        return value;
//    }
//
//    public static int getIntProperty(String key) {
//        return Integer.parseInt(properties.getProperty(key));
//    }
//
//}
