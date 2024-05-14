//package com.gl.eirs.hlrfulldump.hlrDao;
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
//    private static final Logger logger = LogManager.getLogger(ConfigurationDao.class);
//    private final static int retryCount = Integer.parseInt(ProcessConfiguration.getProperty("retryCount").trim());
//    private static final String JDBC_DRIVER = CommonConfiguration.getProperty("jdbc_driver").trim();
//    public String fetchConfigMessage(final String tag , final Connection conn) {
//
//        ResultSet resultSet =null;
//        int count = 0;
//        String message = null;
//
//        try {
//            logger.info("Fetching message configuration for the tag = " + tag);
//            String query="select value from app.msg_cfg  where tag='"+tag+"'";
//            logger.info("Query to fetch message configuration = "+query);
//            while (count <= retryCount) {
//                if (count == retryCount) {
//                    logger.error("Retry count is exceeded for query = "+ query + " , exiting from system.");
//                    System.exit(501);
//                }
//                Class.forName(JDBC_DRIVER);
//                try (Statement stmt = conn.createStatement();) {
//                    resultSet =	stmt.executeQuery(query);
//                    if (resultSet.next())
//                    {
//                        message=  resultSet.getString("value");
//                        logger.info("Query execution result to fetch message configuration = " + message);
//                        count = retryCount + 1;
//                    }
//                    else {
//                        count++;
//                    }
//                    /* conn.commit(); */
//                } catch (SQLException exception) {
//                    count++;
//                    logger.error("Retry count --" + count);
//                    logger.error("Fetching of message configuration failed with" + exception);
//                }
//            }
//        }catch (Exception e) {
//            logger.error("Exception while fetching message configuration : "+e);
//            return  null;
//        }
//        return message;
//    }
//}
