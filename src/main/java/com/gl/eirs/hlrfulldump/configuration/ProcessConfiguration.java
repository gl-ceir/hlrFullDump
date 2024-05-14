//package com.gl.eirs.hlrfulldump.configuration;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Properties;
//
//public class ProcessConfiguration {
//
//    private static final Properties properties = new Properties();
//    private static final Logger logger = LogManager.getLogger(ProcessConfiguration.class);
//
//    static {
//        try {
//            String currentDirectory = System.getProperty("user.dir");
//            String propFileName = currentDirectory + "/configuration.properties";
//            FileInputStream input = new FileInputStream(propFileName);
//            if(input != null) {
//                properties.load(input);
//            } else {
//                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
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
//
//    }
//
//    public static int getIntProperty(String key) {
//        return Integer.parseInt(properties.getProperty(key));
//    }
//
//}
