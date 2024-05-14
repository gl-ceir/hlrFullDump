//package com.gl.eirs.hlrfulldump.pgmDao;
//
//import com.gl.eirs.hlrfulldump.configuration.CommonConfiguration;
//import com.gl.eirs.hlrfulldump.configuration.ProcessConfiguration;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//public class ConfigurationDao {
//
//    private static final int retryCount = Integer.parseInt(ProcessConfiguration.getProperty("retryCount").trim());
//    private static final String JDBC_DRIVER = CommonConfiguration.getProperty("jdbc_driver").trim();
//    private static final Logger logger = LogManager.getLogger(ConfigurationDao.class);
//
//    public Integer instanceCount(final Connection connection) throws ClassNotFoundException {
//
//        int count = 0;
//        final String query = "select value from app.sys_param where tag = 'PGM_NO_OF_INSTANCES'";
//        int instanceCount = -1;
//        logger.info("Query to fetch pgm instances count = " + query);
//        while (count <= retryCount) {
//            if (count == retryCount) {
//                logger.error("Retry count is exceeded for query = " + query + " , exiting from system.");
//                System.exit(100);
//            }
//
//            Class.forName(JDBC_DRIVER);
//            try (Statement stmt = connection.createStatement();) {
//                ResultSet resultSet = stmt.executeQuery(query);
//                logger.info("Query execution result for pgm instance count = " + resultSet);
//                if(resultSet.next()) {
//                    instanceCount = Integer.parseInt(resultSet.getString("value"));
//                    logger.info("No of PGM Instance = " + instanceCount);
//                    count = retryCount + 1;
//                }
//                else count++;
//            } catch (SQLException exception) {
//                count++;
//                logger.error("Fetching statement failed with " + exception.getLocalizedMessage());
//            }
//        }
//        return instanceCount;
//    }
//
//    public String instanceName(final String instanceNumber, final Connection connection) throws ClassNotFoundException {
//
//        int count = 0;
//        final String query = "select * from app.sys_param where tag = '"+instanceNumber+"' ";
//        logger.info("Query is = " + query);
//        String instanceName = "";
//        try {
//
//            while (count <= retryCount) {
//                if (count == retryCount) {
//                    logger.error("Retry count is exceeded for query = " + query + " , exiting from system.");
//                    System.exit(100);
//                }
//
//                Class.forName(JDBC_DRIVER);
//                try (Statement stmt = connection.createStatement();) {
//                    ResultSet resultSet = stmt.executeQuery(query);
//                    logger.info("Query execution result for fetching PGM name = " + resultSet);
//                    if (resultSet.next()) {
//                        instanceName = resultSet.getString("value");
//                        logger.info("Name of PGM Instance = " + instanceName);
//                        count = retryCount + 1;
//                    } else count++;
//                } catch (SQLException exception) {
//                    count++;
//                    logger.error("Fetching statement failed with " + exception.getLocalizedMessage());
//                }
//            }
//        } catch (Exception exception) {
//
//        }
//        return instanceName;
//    }
//}
