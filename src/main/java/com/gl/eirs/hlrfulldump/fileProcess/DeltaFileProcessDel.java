//package com.gl.eirs.hlrfulldump.fileProcess;//package com.gl.eirs.hlrfulldump.fileProcess;
//
//
//import com.gl.eirs.hlrfulldump.alert.AlertManagement;
//import com.gl.eirs.hlrfulldump.audit.AuditManagement;
//import com.gl.eirs.hlrfulldump.configuration.ProcessConfiguration;
//import com.gl.eirs.hlrfulldump.eir.PGMManagement;
//import com.gl.eirs.hlrfulldump.pgmDao.PGMDao;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Date;
//
//public class DeltaFileProcessDel {
//
//    private static final Logger logger = LogManager.getLogger(DeltaFileProcessDel.class);
//    private static final PGMManagement pgmManagement = new PGMManagement();
//    final private static PGMDao pgmDao = new PGMDao();
//    private static final AuditManagement auditManagement = new AuditManagement();
//    private static final AlertManagement alertManagement = new AlertManagement();
//    private static final Integer batchCount = Integer.valueOf(ProcessConfiguration.getProperty("batchCount"));
//    private static final String fileSeparator = ProcessConfiguration.getProperty("fileSeparator");
//    private static long executionFinishTime;
//    private static long executionFinalTime;
////    int line = 0;
//    // store no of instance count and type of instances in an array. This will save multiple calls to pgm tables.
//
//    public static void deltaFileProcess(final Connection pgmConn, final Connection conn, final long executionStartTime, final String errorFileDate,
//                                        final Writer writer, Path path) throws Exception {
//
//        logger.info("Inside delta file deletion process function.");
//        File hlrDeltaFile = new File("HLRDumpDeltaDeletion.csv");
//        final String delInDeviceSync = "INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry)" +
//                "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
//        logger.info("Fetching the pgm instances.");
//        ArrayList<String> instanceNames = pgmManagement.getAllInstanceName(pgmConn);
//        ArrayList<String> sqlQueries = new ArrayList<>();
//        logger.info("Starting to read the delta file deletion for processing.");
//        int i = 0;
//        int line = 0;
//        conn.setAutoCommit(false);
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(hlrDeltaFile));
//             PreparedStatement delInDeviceSyncSt = conn.prepareStatement(delInDeviceSync);
//        ) {
//            String nextLine;
//            while ((nextLine = reader.readLine()) != null) {
//                if (nextLine.isEmpty()) {
//                    continue;
//                }
//                final String[] hlrRecord = nextLine.split(fileSeparator);
//                String imsi = hlrRecord[0].trim();
//                String msisdn = hlrRecord[1].trim();
//                if (imsi.equalsIgnoreCase("IMSI") || msisdn.equalsIgnoreCase("MSISDN")) continue;
//                for (String instanceName : instanceNames) {
//                    delInDeviceSyncSt.setString(1, "HLR_DATA");
//                    delInDeviceSyncSt.setString(2, instanceName);
//                    delInDeviceSyncSt.setString(3, "DEL");
//                    delInDeviceSyncSt.setString(4, String.valueOf(LocalDateTime.now()));
//                    delInDeviceSyncSt.setString(5, "NEW");
//                    delInDeviceSyncSt.setString(6, imsi);
//                    delInDeviceSyncSt.setString(7, msisdn);
//                    delInDeviceSyncSt.setInt(8, 0);
//                    delInDeviceSyncSt.addBatch();
//                    sqlQueries.add("INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry) VALUES(HLR_DATA," + instanceName +", DEL, " + LocalDateTime.now() + ", NEW, " + imsi + ", " + msisdn + ",0)");
//                    logger.info("Query added to batch for delete: INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry) VALUES(HLR_DATA,\"" + instanceName + "\", DEL, \"" + LocalDateTime.now() + "\", NEW, \"" + imsi + "\", \"" + msisdn + "\",/0)");
//                }
//                line++;
//                if (line % batchCount == 0) {
//                    logger.info("Executing batch statements for deletion {} entries.", batchCount);
//
//                    try {
//                        int[] delInDev = delInDeviceSyncSt.executeBatch();
//                        conn.commit();
//                        logger.info("Total entries processed for delete {}", line);
//                        for (int kld: delInDev) {
//                            if (kld == 0) {
//                                logger.error("Insert statement to create a record in device sync request table failed for delete status.");
//                                writer.write("The query is " +sqlQueries.get(kld)+ "\n");
//                                logger.error("Writing queries error file " + imsi + " ," + msisdn);
//                            }
//                        }
//                    } catch (Exception e) {
////                        String msg = alertManagement.alertMessage("alert1105", conn);
//                        alertManagement.raiseAnAlert("alert1105", "", "HLR Dump File Process", 0);
//                        logger.error("Insert statement to create a record in device sync request table failed for delete status for this batch." + e.getLocalizedMessage());
//                        for (String kld: sqlQueries) {
//                            writer.write("The query is " + kld + "\n");
//                            logger.error("Writing queries in error file " + imsi + " ," + msisdn);
//                            }
//                        }
//
//                    }
//                sqlQueries.removeAll(sqlQueries);
//            }
//            if (line % batchCount != 0) {
//                logger.info("Executing batch statements for deletion {} entries.", batchCount);
//                try {
//                    int[] delInDev = delInDeviceSyncSt.executeBatch();
//                    conn.commit();
//                    logger.info("Total entries processed for delete {}", line);
//                    for (int kld: delInDev) {
//                        if (kld == 0) {
//                            logger.error("Insert statement to create a record in device sync request table failed for delete status.");
//                            writer.write("Writing query in error file. The query is " +sqlQueries.get(kld)+ "\n");
//
//                        }
//                    }
//                } catch (Exception e) {
////                    String msg = alertManagement.alertMessage("alert1018", conn);
//                    alertManagement.raiseAnAlert("alert1018", "", "HLR Dump File Process", 0);
//                    logger.error("Insert statement to create a record in device sync request table failed for delete status for this batch." + e.getLocalizedMessage());
//                    logger.error("Writing query in error file.");
//                    for (String kld: sqlQueries) {
//                        writer.write("The query is " + kld + "\n");
//                    }
//                }
//            }
//            conn.setAutoCommit(true);
//            logger.info("Total entries processed for delete {}", line);
//        } catch (Exception exception) {
//
//            logger.error("Exception " + exception.getMessage());
//            final Date finishDate = new Date();
//            executionFinishTime = finishDate.getTime();
//            logger.info("First Execution Finish Time " + executionFinishTime);
////            logger.error("Subtract Execution Finish Time " + Math.subtractExact(executionFinishTime, executionStartTime));
//            executionFinalTime = executionFinishTime - executionStartTime;
//            logger.info("Execution Finish Time " + executionFinalTime);
//            int insertCount = 0;
//            final int failureCount = (int) Files.lines(path).count();
//            int deletedCount = pgmDao.insertAndUpdatedCount(finishDate, conn, "DEL");
//            auditManagement.updateAudit(501, "FAIL", "HLR Dump File Process", insertCount, "HLR Dump File Processor",
//                    "Diff File Processing", executionFinalTime, deletedCount, failureCount, "hlrDiffProcessingFailed", conn);
//            writer.close();
//            System.exit(1);
//        }
//    }
//
//}
