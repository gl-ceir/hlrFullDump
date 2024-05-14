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
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Date;
//
//public class PGMDao {
//
//    private static final int retryCount = Integer.parseInt(ProcessConfiguration.getProperty("retryCount").trim());
//    private final static String JDBC_DRIVER = CommonConfiguration.getProperty("jdbc_driver");
//    private static final Logger logger = LogManager.getLogger(PGMDao.class);
//
//    public void insertInDeviceSyncRequest(final String instanceName, final String imsi, final String msisdn,
//                                          final Connection pgmConnection) throws ClassNotFoundException {
//        int count = 0;
//        final String query = "INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry)" +
//                " values('HLR_DATA', '" + instanceName + "', 'ADD', '" + LocalDateTime.now() + "', 'NEW', '"+ imsi +"', '" + msisdn +"', '0')";
//        logger.info("Insert statement to create a record in device sync request table = " + query);
//        while (count <= retryCount) {
//            if (count == retryCount) {
//                logger.error("Retry count is exceeded for query = " + query + " , exiting from system.");
//                System.exit(100);
//            }
//
//            Class.forName(JDBC_DRIVER);
//            try (Statement stmt = pgmConnection.createStatement();) {
//                int result = stmt.executeUpdate(query);
//                logger.info("Insert statement to crete a record in device sync request table result = " + result);
//                if(result == 1) {
//                    count = retryCount + 1;
//                }
//                else count++;
//            } catch (SQLException exception) {
//                count++;
//                logger.error("Retry count is -- " + count);
//                logger.error("Insert statement to crete a record in device sync request table failed with " + exception.getLocalizedMessage());
//            }
//        }
//
//    }
//
//    public void deleteInDeviceSyncRequest(final String instanceName, final String imsi, final String msisdn,
//                                          final Connection pgmConnection) throws ClassNotFoundException {
//
//        int count = 0;
//        final String query = "INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry)" +
//                " values('HLR_DATA', '" + instanceName + "', 'DEL', '" + LocalDateTime.now() + "', 'NEW', '"+ imsi +"', '" + msisdn +"', '0')";
//        logger.info("Delete statement to delete a record in device sync request table = " + query);
//
//        while (count <= retryCount) {
//            if (count == retryCount) {
//                logger.error("Retry count is exceeded for query = " + query + " , exiting from system.");
//                System.exit(100);
//            }
//            Class.forName(JDBC_DRIVER);
//            try (Statement stmt = pgmConnection.createStatement();) {
//                int result = stmt.executeUpdate(query);
//                logger.info("Delete statement to delete a record in device sync request table result = " + result);
//                if(result == 1) {
//                    count = retryCount + 1;
//                }
//                else count++;
//            } catch (SQLException exception) {
//                count++;
//                logger.error("Retry count is -- " + count);
//                logger.error("Delete statement to delete a record in device sync request table failed with " + exception.getLocalizedMessage());
//            }
//        }
//    }
//
//    public Integer insertAndUpdatedCount(Date currentDate , final Connection conn, final String action) throws ParseException {
//
//        ResultSet resultSet =null;
//        int count = 0;
//        int dbCount=0;
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        currentDate = sdf.parse(sdf.format(currentDate));
//        try {
//            logger.info("Fetching count of action {} for HLR_DATA from the date {}", action, currentDate);
//            String query="select count(*) from app.device_sync_request  where request_date LIKE '%"+ LocalDate.now() +"%' and operation='"+action+"' and identity='HLR_DATA'";
//
//            logger.info("Query to fetch : "+query);
//            while (count <= retryCount) {
//                if (count == retryCount) {
//                    logger.error("Retry count is exceeded for query = " + query + " , exiting from system.");
//                    System.exit(501);
//                }
//                Class.forName(JDBC_DRIVER);
//                try (Statement stmt = conn.createStatement();) {
//                    resultSet=	stmt.executeQuery(query);
//                    if (resultSet.next())
//                    {
//                        dbCount=  resultSet.getInt(1);
//                        logger.info("DB result count with action = " + action + " = "+ dbCount);
//                    }
//                    count = retryCount + 1;
//
//                    /* conn.commit(); */
//                } catch (SQLException exception) {
//                    count++;
//                    logger.error("Retry count is --" + count);
//                    logger.error("Fetching count from device_sync_request failed with " + exception);
//                }
//            }
//        }catch (Exception e) {
//            logger.error("Exception while fetching count: "+ e );
//            return  null;
//        }
//        return dbCount;
//    }
//}
