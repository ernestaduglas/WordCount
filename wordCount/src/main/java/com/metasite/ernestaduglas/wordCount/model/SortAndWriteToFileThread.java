package com.metasite.ernestaduglas.wordCount.model;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SortAndWriteToFileThread implements Runnable {
	private Map<String, Integer> allFilesWordCountMap;
	private char from;
	private char to;
	private String completedFilesDir;

	public SortAndWriteToFileThread(Map<String, Integer> allFilesWordCountMap, char from, char to,
			String completedFilesDir) {
		this.allFilesWordCountMap = allFilesWordCountMap;
		this.from = from;
		this.to = to;
		this.completedFilesDir = completedFilesDir;
	}

	// sort and write to file
	@Override
	public void run() {
		Pattern pattern = Pattern.compile("[" + from + "-" + to + "]\\w*");
		Map<String, Integer> sortedMap = allFilesWordCountMap.entrySet().stream()
				.filter(entry -> pattern.matcher(entry.getKey()).matches())
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
		sortedMap = new TreeMap<>(sortedMap);
		this.writeWordCountMapToFile(sortedMap);
	}
	
	public void writeWordCountMapToFile(Map<String, Integer> sortedMap) {
		List<String> wordCountList = sortedMap.entrySet().stream()
				.map(entry -> entry.getKey() + " " + entry.getValue() + "\n").collect(Collectors.toList());
		String outputFile = completedFilesDir + "WordCount " + from + "-" + to + ".txt";
		try {
			Files.write(Paths.get(outputFile), wordCountList, Charset.defaultCharset());
		} catch (IOException e) {
			e.getMessage();
		}

	}
}
