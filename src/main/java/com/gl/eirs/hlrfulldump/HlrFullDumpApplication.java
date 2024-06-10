package com.gl.eirs.hlrfulldump;


import com.gl.eirs.hlrfulldump.connection.MySQLConnection;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.gl.eirs.hlrfulldump.configuration.AppConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@EnableEncryptableProperties
@SpringBootApplication
@ComponentScan("com.gl.eirs.hlrfulldump")
public class HlrFullDumpApplication {
	@Autowired
	HlrDumpProcessorMain hlrDumpProcessorMain;

	@Autowired
	MySQLConnection connection;

	@Autowired
	AppConfig appConfig;


	public static void main(String[] args) {
		SpringApplication.run(HlrFullDumpApplication.class, args);
	}
}

@Component
class HlrFullDumpApplication1 implements CommandLineRunner {

	@Autowired
	HlrDumpProcessorMain hlrDumpProcessorMain;

	@Autowired
	AppConfig appConfig;



	@Override
	public void run(String... args) throws Exception {
		System.out.println("** Program Started!**");
		System.out.println("** Received Arguments: " + String.join(", ", args) + " **"); // Print all arguments
		if (args.length == 3) {
			try {
				int intParam = Integer.parseInt(args[0]);
				String addFileName;
				String delFileName;
				String operator = appConfig.getOperator();
				if (intParam == 1) {
					// Take filenames from command line arguments
					addFileName = args[1];
					delFileName = args[2];
				} else {
					// Generate filenames based on code logic
					LocalDateTime dateObj = LocalDateTime.now();
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					addFileName = "hlr_full_dump_diff_add_" + operator + "_" + dateFormatter.format(dateObj) + ".csv";
					delFileName = "hlr_full_dump_diff_del_" + operator + "_" + dateFormatter.format(dateObj) + ".csv";
				}
				hlrDumpProcessorMain.startFunction(intParam, addFileName, delFileName);
				            // Call the method to fill missing IMSI and MSISDN


			} catch (NumberFormatException e) {
				System.err.println("Invalid value for intParam. Please provide an integer.");
			}
		} else {
			System.err.println("Usage: java HlrDumpProcessorMain <intParam> <addFileName> <delFileName>");
		}
	}
}
