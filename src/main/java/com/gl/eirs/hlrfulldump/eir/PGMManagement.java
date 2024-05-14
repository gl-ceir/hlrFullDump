//package com.gl.eirs.hlrfulldump.eir;
//
//import com.gl.eirs.hlrfulldump.pgmDao.ConfigurationDao;
//import com.gl.eirs.hlrfulldump.pgmDao.PGMDao;
//
//import java.sql.Connection;
//import java.util.ArrayList;
//
//public class PGMManagement {
//    final private static PGMDao pgmDao = new PGMDao();
//    final private static ConfigurationDao configurationDao = new ConfigurationDao();
//
//    ArrayList<String> instanceName = new ArrayList<>();
//
//    public ArrayList<String> getAllInstanceName(final Connection connection) throws ClassNotFoundException {
//
//        int instanceCount = configurationDao.instanceCount(connection);
//        for(int i=1;i<=instanceCount;i++) {
//            final String instanceNumber = "PGM_INSTANCE_NO_" + i;
//            final String localInstanceName = configurationDao.instanceName(instanceNumber, connection);
//            instanceName.add(localInstanceName);
//        }
//        return instanceName;
//    }
//
//    public void insert(final String imsi, final String msisdn, final ArrayList<String> instanceNames, final Connection connection) throws ClassNotFoundException {
////        int instanceCount = configurationDao.instanceCount(connection);
//        for(String instanceName: instanceNames) {
////            final String instanceNumber = "PGM_INSTANCE_NO_" + i;
////            final String instanceName = configurationDao.instanceName(instanceNumber, connection);
//            pgmDao.insertInDeviceSyncRequest(instanceName, imsi, msisdn, connection);
//        }
//    }
//
//    public void delete(final String imsi, final String msisdn, final ArrayList<String> instanceNames, final Connection connection) throws ClassNotFoundException {
////        int instanceCount = configurationDao.instanceCount(connection);
//        for(String instanceName: instanceNames) {
////            final String instanceNumber = "PGM_INSTANCE_NO_" + i;
////            final String instanceName = configurationDao.instanceName(instanceNumber, connection);
//            pgmDao.deleteInDeviceSyncRequest(instanceName, imsi, msisdn, connection);
//        }
//    }
//
//}
