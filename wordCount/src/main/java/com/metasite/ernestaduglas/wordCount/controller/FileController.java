package com.metasite.ernestaduglas.wordCount.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.metasite.ernestaduglas.wordCount.service.FileService;

@Controller
@RequestMapping(value = "/wordcount")
public class FileController {
	@Autowired
	private FileService fileService;

	@RequestMapping(method = RequestMethod.GET)
	public String getIndex() {
		return "index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String uploadFiles(@RequestParam("files") MultipartFile[] files) {
		fileService.uploadFiles(files);
		fileService.processFiles();
		return "redirect:/wordcount/download";

	}

	@RequestMapping(path = "/download", method = RequestMethod.GET)
	public @ResponseBody void getProcessedFiles(HttpServletResponse response) {
		File file = new File(FileService.getZippedFile());
		InputStream input = null;
		try {
			input = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.getMessage();
		}
		response.setContentType(FileService.getZippedFile());
		response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
		response.setHeader("Content-Length", String.valueOf(file.length()));
		try {
			FileCopyUtils.copy(input, response.getOutputStream());
		} catch (IOException e) {
			e.getMessage();
		}
	}
}
