package com.gl.eirs.hlrfulldump.configuration;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Data
@Service
public class AppConfig {


    @Value("${batch.count}")
    int batchCount;

    @Value("${file.separator.parameter}")
    String fileSeparator;

    @Value("${operator}")
    String operator;

    @Value("${delta.file.path}")
    String deltaFilePath;

    @Value("${alert.url}")
    String alertUrl;


    @Value("${password.decryptor}")
    String passwordDecryptor;

    @Value("${db_url}")
    String dbUrl;

    @Value("${jdbc_driver}")
    String jdbcDriver;

    @Value("${spring.datasource.password}")
    String springDatasourcePassword;

    @Value("${dbUsername}")
    String dbUsername;

}
