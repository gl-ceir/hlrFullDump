package com.gl.eirs.hlrfulldump.fileProcess;


import com.gl.eirs.hlrfulldump.alert.AlertDto;
import com.gl.eirs.hlrfulldump.alert.AlertManagement;
import com.gl.eirs.hlrfulldump.audit.AuditManagement;
import com.gl.eirs.hlrfulldump.configuration.AppConfig;
//import com.gl.eirs.hlrfulldump.configuration.ProcessConfiguration;
//import com.gl.eirs.hlrfulldump.pgmDao.PGMDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Component
public class DeltaFileProcessAdd2 {

    private static final Logger logger = LogManager.getLogger(DeltaFileProcessAdd2.class);
//    final private static PGMDao pgmDao = new PGMDao();
//    private static final AuditManagement auditManagement = new AuditManagement();
//    private static final AlertManagement alertManagement = new AlertManagement();

    @Autowired
    AlertManagement alertManagement;
    @Autowired
    AuditManagement auditManagement;

    //    private static final Integer batchCount = Integer.valueOf(ProcessConfiguration.getProperty("batchCount"));
//    private static final String fileSeparator = ProcessConfiguration.getProperty("fileSeparator");
//    private static final String deltaFilePath = ProcessConfiguration.getProperty("deltaFilePath");
//    private static final String operator = ProcessConfiguration.getProperty("operator");
    private static String moduleName = "HLR_Full_Dump";
    private static String featureName = "HLR_Full_Dump_Processor";
    private static long executionFinishTime;
    private static long executionFinalTime;

    // store no of instance count and type of instances in an array. This will save mutliplt calls to pgm tables.
    @Autowired
    AppConfig appConfig;
    public void deltaFileProcess(final Connection conn, final long executionStartTime, String addFileName, String operator) throws Exception {

        logger.info("Inside delta file addition process function.");
        Integer batchCount = appConfig.getBatchCount();
        String fileSeparator = appConfig.getFileSeparator();

        String deltaFilePath = appConfig.getDeltaFilePath();


        moduleName = moduleName+"_"+operator;
        LocalDateTime dateObj = LocalDateTime.now();
        File hlrDeltaFile = new File(deltaFilePath + addFileName);
        long addFileCount = 0;
        long failureCount=0;
        final String addInHlr = "INSERT INTO app.active_msisdn_list (imsi, msisdn, activation_date, operator, remarks)" +
                "VALUES(?,?,?,?,?)";


        ArrayList<String> sqlQueries = new ArrayList<>();
        logger.info("Starting to read the delta file addition for processing.");
        int i = 0;

        conn.setAutoCommit(false);

        int line = 0;
        try(BufferedReader reader = new BufferedReader(new FileReader(hlrDeltaFile));
            PreparedStatement addInHlrSt = conn.prepareStatement(addInHlr);
            PreparedStatement insertIntoHisStmt = conn.prepareStatement("INSERT INTO app.active_msisdn_list_his (imsi, msisdn, activation_date, operator, remarks, operation) VALUES (?, ?, ?, ?, ?, 0)");
        ) {
            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                if (nextLine.isEmpty()) {
                    continue;
                }
                addFileCount++;
//                final String[] hlrRecord = nextLine.split(fileSeparator);
                String[] hlrRecord = nextLine.split(fileSeparator, -1);
                logger.info("HLR record is {}", (Object) Arrays.stream(hlrRecord).toArray());
                String imsi = hlrRecord[0].trim();
                String msisdn = hlrRecord[1].trim();
                String activationDate = hlrRecord[2].trim();
                String remarks = "SIM change";
                if(activationDate.isBlank()) {
                    activationDate = null;
                }
//                logger.error(hlrRecord);
//                if(activationDate.i   sEmpty()) activationDate="null";
//                logger.error("Activation date is {}", activationDate);
                if (imsi.equalsIgnoreCase("IMSI") || msisdn.equalsIgnoreCase("MSISDN")) continue;


                addInHlrSt.setString(1, imsi);
                addInHlrSt.setString(2, msisdn);
                addInHlrSt.setString(3, activationDate);
                addInHlrSt.setString(4, operator);
                addInHlrSt.setString(5, remarks);

                addInHlrSt.addBatch();
//                sqlQueries.add("INSERT INTO app.device_sync_request (identity, instance_name, operation, request_date, status, imsi, msisdn, no_of_retry) VALUES(HLR_DATA," + instanceName +", ADD, " + LocalDateTime.now() + ", NEW, " + imsi + ", " + msisdn + ",0)");
                sqlQueries.add("INSERT INTO app.active_msisdn_list (imsi, msisdn, activation_date, operator, remarks) VALUES (" + imsi + "," + msisdn + "," + activationDate + "," + operator + "," + remarks + ")");
                logger.info("Query added to batch for insert: INSERT INTO app.active_msisdn_list (imsi, msisdn, activation_date, operator, remarks) VALUES({}, {}, {}, {}, {})", imsi, msisdn, activationDate, operator, remarks);
                line++;
                if (line % batchCount == 0) {
                    logger.info("Executing batch statements for addition {} entries.", batchCount);
                    try {
                        int[] addInDev = addInHlrSt.executeBatch();
                        conn.commit();
                        logger.info("Total entries processed for insert {}", batchCount);
                        for (int kld: addInDev) {
                            if (kld == 0) {
                                logger.error("Insert statement to create a record in active_msisdn_list table failed.");
                                logger.error("The record is " + sqlQueries.get(kld));
                                failureCount++;
                            }
                            else {
                                // Insert deleted record into hlr_dump_his
                                insertIntoHisStmt.setString(1, imsi);
                                insertIntoHisStmt.setString(2, msisdn);
                                assert activationDate != null;
                                insertIntoHisStmt.setString(3, String.valueOf(java.sql.Date.valueOf(activationDate)));
                                insertIntoHisStmt.setString(4, operator);
                                insertIntoHisStmt.setString(5, remarks);
                                insertIntoHisStmt.addBatch();
                            }

                        }
                        insertIntoHisStmt.executeBatch();
                        conn.commit();
                    } catch (BatchUpdateException e) {
                        alertManagement.raiseAnAlert("alert5215", addFileName, operator, 0);
                        logger.error("Insert statement to create a record in active_msisdn_list table failed for this batch." + e.getLocalizedMessage());
                        int cnt[] = e.getUpdateCounts();
                        for(int j=0;j<cnt.length;j++) {
                            if(cnt[j] <= 0) {
                                logger.error("The query failed is " + sqlQueries.get(j));
                                failureCount++;
                            }
                        }
                    }
                    sqlQueries.removeAll(sqlQueries);
                }

            }

            if (line % batchCount != 0) {
                logger.info("Executing batch statements for insert {} entries.", line);
                try {
                    int[] addInDev = addInHlrSt.executeBatch();
                    conn.commit();
                    logger.info("Total entries processed for insert {}", line);
                    for (int kld: addInDev) {
                        if (kld == 0) {
                            logger.error("Insert statement to create a record in active_msisdn_list table failed.");
                            logger.error("The records are" + sqlQueries.get(kld));
                            failureCount++;
                        }
                    }
                } catch (BatchUpdateException e) {
                    alertManagement.raiseAnAlert("alert5215", addFileName, operator, 0);
                    logger.error("Insert statement to create a record in active_msisdn_list table failed for insert status for this batch." + e.getLocalizedMessage());
                    logger.error(sqlQueries);
                    int cnt[] = e.getUpdateCounts();
                    for(int j=0;j<cnt.length;j++) {
                        if(cnt[j] <= 0) {
                            logger.error("The query failed is " + sqlQueries.get(j));
                            failureCount++;
                        }
                    }
                }

            }
            conn.setAutoCommit(true);
//            logger.info("Total entries processed for insert {}", line);

        } catch (Exception exception) {

            logger.error("Exception " + exception.getMessage());
            final Date finishDate = new Date();
            executionFinishTime = finishDate.getTime();
            logger.info("First Execution Finish Time " + executionFinishTime);
//            logger.error("Subtract Execution Finish Time " + Math.subtractExact(executionFinishTime, executionStartTime));
            executionFinalTime = executionFinishTime - executionStartTime;
            logger.info("Execution Finish Time " + executionFinalTime);
            long insertCount =addFileCount;
            long deletedCount = 0;
//            final int failureCount=0;
            alertManagement.raiseAnAlert("alert5202", exception.getMessage(), operator, 0);
            auditManagement.updateAudit(501, "FAIL", featureName, moduleName, insertCount,"",
                    executionFinalTime, deletedCount, failureCount, "The diff file processing failed for file " + addFileName, conn);

            System.exit(1);
        }
    }

}
