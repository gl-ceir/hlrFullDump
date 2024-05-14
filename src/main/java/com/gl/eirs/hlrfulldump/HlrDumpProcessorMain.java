package com.gl.eirs.hlrfulldump;

import com.gl.eirs.hlrfulldump.alert.AlertManagement;
import com.gl.eirs.hlrfulldump.audit.AuditManagement;
//import com.gl.eirs.hlrfulldump.configuration.ProcessConfiguration;
import com.gl.eirs.hlrfulldump.configuration.AppConfig;
import com.gl.eirs.hlrfulldump.connection.MySQLConnection;
import com.gl.eirs.hlrfulldump.fileProcess.DeltaFileProcessAdd2;
import com.gl.eirs.hlrfulldump.fileProcess.DeltaFileProcessDel2;
import com.gl.eirs.hlrfulldump.validation.ConfigValidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
public class HlrDumpProcessorMain {

//    final private static MySQLConnection connection = new MySQLConnection();

    @Autowired
    MySQLConnection connection;

    @Autowired
    ConfigValidation configValidation;

    @Autowired
    AlertManagement alertManagement;

    @Autowired
    AuditManagement auditManagement;
//    final private static ConfigValidation configValidation = new ConfigValidation();
//    final private static AlertManagement alertManagement = new AlertManagement();
//    final private static AuditManagement auditManagement = new AuditManagement();
    final private static Logger logger = LogManager.getLogger(HlrDumpProcessorMain.class);
    private static long executionFinishTime;
    private static long executionFinalTime;

    private static String moduleName = "HLR_Full_Dump";
//    private static final String deltaFilePath = ProcessConfiguration.getProperty("deltaFilePath");
//    private static final String operator = ProcessConfiguration.getProperty("operator");
    private static String featureName = "HLR_Full_Dump_Processor";

    @Autowired
    AppConfig appConfig;

    @Autowired
    DeltaFileProcessAdd2 deltaFileProcessAdd2;
    @Autowired
    DeltaFileProcessDel2 deltaFileProcessDel2;

    public void startFunction(String args[]) throws Exception {

        String deltaFilePath = appConfig.getDeltaFilePath();
        String operator = appConfig.getOperator();
        logger.error(operator);
        Connection conn = null;
        moduleName = moduleName+"_"+operator;
        try {

            final Date date = new Date();
            final long executionStartTime = date.getTime();
            logger.info("Java process execution Start Time = " + executionStartTime);
            conn = connection.getConnection();
            // creating an entry in audit table.

            logger.info("Making an entry in audit table indicate start of HLR Dump Process.");
            auditManagement.createAudit(201, 0, featureName, conn, moduleName);

            // do config validation
            boolean configValidationStatus = configValidation.configValidation(alertManagement, auditManagement, executionStartTime, conn);
            if (!configValidationStatus) {
                System.exit(-1);
            }
            LocalDateTime dateObj = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

//            dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

            Date dateFinish = new Date();
            logger.info("Starting the process of reading the deletion diff file.");
            deltaFileProcessDel2.deltaFileProcess(conn, executionStartTime);
            Date finishDate = new Date();
            executionFinishTime = finishDate.getTime();
//            logger.info("First Execution Finish Time for deletion " + executionFinishTime);
//            logger.info("Subtract Execution Finish Time deletion " + Math.subtractExact(executionFinishTime, executionStartTime));
            executionFinalTime = executionFinishTime - executionStartTime;
            logger.info("Execution finish time for deletion file" + executionFinalTime);


            logger.info("Starting the process of diff of insertion file.");

            deltaFileProcessAdd2.deltaFileProcess(conn, executionStartTime);
            finishDate = new Date();
            executionFinishTime = finishDate.getTime();
//            logger.info("First Execution Finish Time for insertion " + executionFinishTime);
//            logger.info("Subtract Execution Finish Time for insertion " + Math.subtractExact(executionFinishTime, executionStartTime));
            executionFinalTime = executionFinishTime - executionStartTime;
            logger.info("Execution finish for time addition file " + executionFinalTime);

//            LocalDateTime dateObj = LocalDateTime.now();
//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String addFileName = "hlr_full_dump_diff_add_" + operator + "_" + dateFormatter.format(dateObj) + ".csv";
            String delFileName = "hlr_full_dump_diff_del_" + operator + "_" + dateFormatter.format(dateObj) + ".csv";
            File addHlrDeltaFile = new File(deltaFilePath + addFileName);
            File delHlrDeltaFile = new File(deltaFilePath + delFileName);

            Path addFile = Paths.get(addHlrDeltaFile.toURI());
            Path delFile = Paths.get(delHlrDeltaFile.toURI());
            long insertCount = Files.lines(addFile).count();
            long deletedCount = Files.lines(delFile).count();

            long failureCount = 0;
            auditManagement.updateAudit(200, insertCount, "", executionFinalTime, deletedCount, failureCount, "NA", moduleName, featureName, "SUCCESS", conn);
            logger.info("File Processing is successfully completed.");
        } catch (Exception exception) {
            logger.error(exception);
            alertManagement.raiseAnAlert("alert5202", exception.getMessage(), operator, 0);
            auditManagement.updateAudit(501, "FAIL", featureName, moduleName, 0, "",
                    executionFinalTime, 0, 0, "The process exited with exception" + exception.getMessage() + " for operator" + operator + ".", conn);
            System.exit(1);

        }
    }
}
