package com.gl.eirs.hlrfulldump.fileProcess;

import com.gl.eirs.hlrfulldump.alert.AlertManagement;
import com.gl.eirs.hlrfulldump.audit.AuditManagement;
import com.gl.eirs.hlrfulldump.configuration.AppConfig;
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
import java.util.List;

@Component
public class DeltaFileProcessDel2 {

    private static final Logger logger = LogManager.getLogger(DeltaFileProcessDel2.class);

    @Autowired
    AlertManagement alertManagement;
    @Autowired
    AuditManagement auditManagement;
    private static String moduleName = "HLR_Full_Dump";
    private static String featureName = "HLR_Full_Dump_Processor";
    private static long executionFinishTime;
    private static long executionFinalTime;

    @Autowired
    AppConfig appConfig;

    public void deltaFileProcess(final Connection conn, final long executionStartTime, String delFileName, String operator) throws Exception {
        Integer batchCount = appConfig.getBatchCount();
        String fileSeparator = appConfig.getFileSeparator();
        String deltaFilePath = appConfig.getDeltaFilePath();

        logger.info("Inside delta file deletion process function.");
        moduleName = moduleName + "_" + operator;
        LocalDateTime dateObj = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        File hlrDeltaFile = new File(deltaFilePath + delFileName);
        final String delInHlr = "DELETE FROM app.active_msisdn_list WHERE imsi = ? AND msisdn = ?";
        final String insertInHis = "INSERT INTO app.active_msisdn_list_his (imsi, msisdn, operator, remarks, operation) VALUES (?, ?, ?, ?, 0)";
        ArrayList<String> sqlQueries = new ArrayList<>();
        logger.info("Starting to read the delta file deletion for processing.");
        int line = 0;
        long delFileCount = 0;
        long failureCount = 0;
        conn.setAutoCommit(false);

        List<String[]> hlrRecords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(hlrDeltaFile));
             PreparedStatement delInHlrSt = conn.prepareStatement(delInHlr);
             PreparedStatement insertInHisSt = conn.prepareStatement(insertInHis)) {

            String nextLine;
            while ((nextLine = reader.readLine()) != null) {
                if (nextLine.isEmpty()) {
                    continue;
                }
                delFileCount++;

                String[] hlrRecord = nextLine.split(fileSeparator, -1);
                logger.info("Active Msisdn record is {}", (Object) Arrays.stream(hlrRecord).toArray());
                hlrRecords.add(hlrRecord);
                String imsi = hlrRecord[0].trim();
                String msisdn = hlrRecord[1].trim();
                String remarks = "Sim Change";
                if (imsi.equalsIgnoreCase("IMSI") || msisdn.equalsIgnoreCase("MSISDN")) continue;

                delInHlrSt.setString(1, imsi);
                delInHlrSt.setString(2, msisdn);
                delInHlrSt.addBatch();

                line++;
                if (line % batchCount == 0) {
                    logger.info("Executing batch statements for deletion {} entries.", batchCount);

                    try {
                        int[] delInDev = delInHlrSt.executeBatch();
                        for (int i = 0; i < delInDev.length; i++) {
                            if (delInDev[i] > 0) {
                                String[] record = hlrRecords.get(i);
                                insertInHisSt.setString(1, record[0].trim());
                                insertInHisSt.setString(2, record[1].trim());
                                insertInHisSt.setString(3, operator);
                                insertInHisSt.setString(4, remarks);
                                insertInHisSt.addBatch();
                                sqlQueries.add("INSERT INTO app.active_msisdn_list_his (imsi, msisdn, operator, remarks, operation) VALUES (" + record[0].trim() + ", " + record[1].trim() + ", " + operator + ", " + remarks + ", 0)");
                            }
                        }
                        insertInHisSt.executeBatch();
                        conn.commit();
                        logger.info("Total entries processed for delete {}", batchCount);
                        for (int kld : delInDev) {
                            if (kld == 0) {
                                logger.error("Delete statement to delete a record in hlr_full_dump table failed.");
                                logger.error("The record is " + sqlQueries.get(kld));
                                failureCount++;
                            }
                        }
                    } catch (BatchUpdateException e) {
                        alertManagement.raiseAnAlert("alert5215", delFileName, operator, 0);
                        logger.error("Delete statement to delete in active_msisdn_list failed for this batch." + e.getLocalizedMessage());
                        int[] cnt = e.getUpdateCounts();
                        for (int j = 0; j < cnt.length; j++) {
                            if (cnt[j] <= 0) {
                                logger.error("The query failed is " + sqlQueries.get(j));
                                failureCount++;
                            }
                        }
                    }
                    sqlQueries.clear();
                    hlrRecords.clear();
                }
            }

            if (line % batchCount != 0) {
                logger.info("Executing batch statements for deletion {} entries.", line);
                try {
                    int[] delInDev = delInHlrSt.executeBatch();
                    for (int i = 0; i < delInDev.length; i++) {
                        if (delInDev[i] > 0) {
                            String[] record = hlrRecords.get(i);
                            insertInHisSt.setString(1, record[0].trim());
                            insertInHisSt.setString(2, record[1].trim());
                            insertInHisSt.setString(3, operator);
                            insertInHisSt.setString(4, "Sim Change");
                            insertInHisSt.addBatch();
                            sqlQueries.add("INSERT INTO app.active_msisdn_list_his (imsi, msisdn, operator, remarks, operation) VALUES (" + record[0].trim() + ", " + record[1].trim() + ", " + operator + ", " + "Sim Change" + ", 0)");
                        }
                    }
                    insertInHisSt.executeBatch();
                    conn.commit();
                    logger.info("Total entries processed for delete {}", line);
                    for (int kld : delInDev) {
                        if (kld == 0) {
                            logger.error("Delete statement to delete a record in active_msisdn_list table failed.");
                            logger.error("The record is " + sqlQueries.get(kld));
                            failureCount++;
                        }
                    }
                } catch (BatchUpdateException e) {
                    alertManagement.raiseAnAlert("alert5215", delFileName, operator, 0);
                    logger.error("Delete statement to delete a record in active_msisdn_list table failed for this batch." + e.getLocalizedMessage());
                    int[] cnt = e.getUpdateCounts();
                    for (int j = 0; j < cnt.length; j++) {
                        if (cnt[j] <= 0) {
                            logger.error("The query failed is " + sqlQueries.get(j));
                            failureCount++;
                        }
                    }
                }
            }
            conn.setAutoCommit(true);
        } catch (Exception exception) {
            logger.error("Exception " + exception.getMessage());
            final Date finishDate = new Date();
            executionFinishTime = finishDate.getTime();
            executionFinalTime = executionFinishTime - executionStartTime;
            logger.info("Execution finish time " + executionFinalTime);
            long insertCount = 0;
            long deletedCount = delFileCount;
            alertManagement.raiseAnAlert("alert5202", exception.getMessage(), operator, 0);
            auditManagement.updateAudit(501, "FAIL", featureName, moduleName, insertCount, "",
                    executionFinalTime, deletedCount, failureCount, "The diff file processing failed for file " + delFileName, conn);
            System.exit(1);
        }
    }
}
