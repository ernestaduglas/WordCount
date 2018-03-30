package com.metasite.ernestaduglas.wordCount.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWordCountThread implements Callable<Map<String,Integer>> {
	private String filename;
	private Map<String, Integer> countedWords;

	public FileWordCountThread(String filename) {
		this.filename = filename;
		this.countedWords = new HashMap<String, Integer>();
	}

	@Override
	public Map<String,Integer> call() {
			try {
				countedWords = Files.lines(Paths.get(filename)).flatMap(line -> Stream.of(line.split(" ")))
						.map(word -> word.replaceAll("[^A-Za-z]", "").toLowerCase()).filter(word -> word.length() > 0)
						.collect(Collectors.groupingBy(word -> word, Collectors.summingInt(word->1)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		return countedWords;
	}
}
