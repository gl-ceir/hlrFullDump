package com.gl.eirs.hlrfulldump;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class HlrFullDumpApplication implements CommandLineRunner {

	@Autowired
	HlrDumpProcessorMain hlrDumpProcessorMain;
	public static void main(String[] args) {
		SpringApplication.run(HlrFullDumpApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		hlrDumpProcessorMain.startFunction(args);

	}
}
