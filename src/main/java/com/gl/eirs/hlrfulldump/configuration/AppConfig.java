package com.gl.eirs.hlrfulldump.configuration;


import com.gl.eirs.hlrfulldump.HlrDumpProcessorMain;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Configuration
@Data
@Service
public class AppConfig {
    @Bean
    public HlrDumpProcessorMain hlrDumpProcessorMain() {
        return new HlrDumpProcessorMain();
    }

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

    @Value("jdbc:mysql://localhost:3306/aud")
    String dbUrl;

    @Value("com.mysql.cj.jdbc.Driver")
    String jdbcDriver;

    @Value("${spring.datasource.password}")
    String springDatasourcePassword;

    @Value("root")
    String dbUsername;

}
