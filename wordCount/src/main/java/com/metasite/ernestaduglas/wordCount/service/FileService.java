package com.metasite.ernestaduglas.wordCount.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.metasite.ernestaduglas.wordCount.model.FileWordCountThread;
import com.metasite.ernestaduglas.wordCount.model.SortAndWriteToFileThread;

@Service
public class FileService {
	private static final String UPLOADED_FILES_DIR = System.getProperty("user.home") + "//wordcount//uploaded//";
	private static final String COMPLETED_FILES_DIR = System.getProperty("user.home") + "//wordcount//completed//";
	private static final String ZIPPED_FILE = System.getProperty("user.home") + "//wordcount//wordcount.zip";
	private Map<String, Integer> allFilesWordCount = new HashMap<String, Integer>();
	@Autowired
	private ThreadPoolTaskExecutor threadPool;

	public void uploadFiles(MultipartFile[] files) {
		for (MultipartFile multipartFile : files) {
			File file = new File(UPLOADED_FILES_DIR + multipartFile.getOriginalFilename());
			try {
				multipartFile.transferTo(file);
			} catch (IllegalStateException | IOException e) {
				e.getMessage();
			}
		}
	}

	public void processFiles() {
		List<Future<Map<String, Integer>>> resultList = new ArrayList<>();
		for (String fileName : this.getFileNames()) {
			Future<Map<String, Integer>> result = threadPool.submit(new FileWordCountThread(fileName));
			resultList.add(result);
		}
		for (Future<Map<String, Integer>> result : resultList) {
			try {
				for (Map.Entry<String, Integer> singleFileWordCount : result.get().entrySet()) {
					allFilesWordCount.merge(singleFileWordCount.getKey(), singleFileWordCount.getValue(), Integer::sum);
				}
			} catch (InterruptedException | ExecutionException e) {
				e.getMessage();
			}
		}
		threadPool.execute(new SortAndWriteToFileThread(allFilesWordCount, 'a', 'g', COMPLETED_FILES_DIR));
		threadPool.execute(new SortAndWriteToFileThread(allFilesWordCount, 'h', 'n', COMPLETED_FILES_DIR));
		threadPool.execute(new SortAndWriteToFileThread(allFilesWordCount, 'o', 'u', COMPLETED_FILES_DIR));
		threadPool.execute(new SortAndWriteToFileThread(allFilesWordCount, 'v', 'z', COMPLETED_FILES_DIR));
		threadPool.shutdown();
		try {
			threadPool.getThreadPoolExecutor().awaitTermination(3, TimeUnit.SECONDS);
		} catch (IllegalStateException | InterruptedException e) {
			e.getMessage();
		}
		try {
			zipProcessedFiles();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public List<String> getFileNames() {
		List<String> fileNames = null;
		try {
			fileNames = Files.list(Paths.get(UPLOADED_FILES_DIR)).map(path -> path.toString())
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.getMessage();
		}
		return fileNames;
	}

	public void zipProcessedFiles() throws IOException {
		FileOutputStream fileOut = new FileOutputStream(ZIPPED_FILE);
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);

		for (File file : new File(COMPLETED_FILES_DIR).listFiles()) {
			FileInputStream fileIn = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fileIn.read(bytes)) >= 0) {
				zipOut.write(bytes, 0, length);
			}
			fileIn.close();
		}
		zipOut.close();
		fileOut.close();
	}

	public void createFilesDirectory(String path) {
		try {
			Files.createDirectories(Paths.get(path));
		} catch (IOException e) {
			e.getMessage();
		}
	}

	public static String getZippedFile() {
		return ZIPPED_FILE;
	}

}
