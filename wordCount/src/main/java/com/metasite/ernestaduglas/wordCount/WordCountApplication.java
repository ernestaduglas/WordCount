package com.metasite.ernestaduglas.wordCount;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.metasite.ernestaduglas.wordCount.service.FileService;

@SpringBootApplication
public class WordCountApplication {


	public static void main(String[] args) {
		SpringApplication.run(WordCountApplication.class, args);
	
		FileService service = new FileService();
		service.createFilesDirectory(System.getProperty("user.home") + "//wordcount//uploaded//");
		service.createFilesDirectory(System.getProperty("user.home") + "//wordcount//completed//");
		
	}
}
