//package com.gl.eirs.hlrfulldump.hlrDao;
//
//import com.gl.eirs.hlrfulldump.configuration.CommonConfiguration;
//import com.gl.eirs.hlrfulldump.configuration.ProcessConfiguration;
//import com.gl.eirs.hlrfulldump.entity.AlertEntity;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//
//public class AlertDao {
//
//    private final static Logger logger = LogManager.getLogger(AlertDao.class);
//    private final static int retryCount = Integer.parseInt(ProcessConfiguration.getProperty("retryCount").trim());
//    private final static String JDBC_DRIVER = CommonConfiguration.getProperty("jdbc_driver");
//    public void saveInRunningAlertTable(String  alertId, String description, Connection conn) throws ClassNotFoundException {
//
//        int count = 0;
//        String query = "insert into app.sys_generated_alert (alert_id,description,STATUS,USER_ID,USERNAME) "
//                + "values('" + alertId + "','" + description + "',0,0,'NULL')";
//        logger.info("Insert statement to create a record in running alert table = " + query);
//        while (count <= this.retryCount) {
//            if (count == this.retryCount) {
//                logger.error("Retry count is exceeded for query = "+ query + " , exiting from system.");
//                System.exit(100);
//            }
//            Class.forName(JDBC_DRIVER);
//            try (Statement stmt = conn.createStatement();) {
//                int result=	stmt.executeUpdate(query);
//                logger.info("Query execution result in running alert table = "+ result);
//                if(result == 1)  {
//                    count = retryCount + 1;
//                }
//                else count++;
//            } catch (SQLException exception) {
//                count++;
//                logger.error("Retry count is -- " + count);
//                logger.error("Insert in running alert table failed with = " + exception);
//            }
//        }
//    }
//
//    public AlertEntity getAlertMessage(String  alertId , Connection conn) {
//
//        ResultSet resultSet =null;
//        int count = 0;
//        AlertEntity alertEntity = new AlertEntity();
//        try {
//            logger.info("Getting Alert Message for this alert id = "+alertId);
//            String query="select * from app.cfg_feature_alert  where alert_id='"+alertId+"'";
//            logger.error("Query to fetch alert message with an alert id is : "+query);
//            while (count <= this.retryCount) {
//                if (count == this.retryCount) {
//                    logger.info("Retry count is exceeded for query = "+ query + " , exiting from system");
//                    System.exit(501);
//                }
//                Class.forName(JDBC_DRIVER);
//                try (Statement stmt = conn.createStatement();) {
//                    resultSet=	stmt.executeQuery(query);
//                    if(resultSet.next())
//                    {
//                        alertEntity.setAlertId(resultSet.getString("alert_id"));
//                        alertEntity.setMessage(resultSet.getString("description"));
//                        logger.info("Query execution result to get Alert Message is successful.");
//                        count = retryCount + 1;
//                    }
//                    else {
//                        count++;
//                    }
//
//                    /* conn.commit(); */
//                } catch (SQLException exception) {
//                    count++;
//                    logger.error("Retry count is -- " + count);
//                    logger.error("Fetching from alert message table failed with " + exception);
//                }
//            }
//        }catch (Exception e) {
//            logger.info("Exception while fetching alert message details : "+e);
//            return null;
//        }
//        return alertEntity;
//    }
//}
