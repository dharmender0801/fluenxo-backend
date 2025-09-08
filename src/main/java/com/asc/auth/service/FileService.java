package com.asc.auth.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.asc.auth.dto.FileUploadDto;
import com.asc.auth.exception.BadRequestException;
import com.asc.auth.utils.Utils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {

	@Value("${file.uploads.base.path:/var/www/html/files/assets/file-uploads/}")
	String fileUploadBasePath;

	@Value("${file.access.base.domain.url:https://fluenxo.dharmenderkumar.com/file-uploads/}")
	String fileAccessBaseUrl;

	@Value("${replace.path:/var/www/html/files/}")
	private String replacePath;

	public String uploadFile(MultipartFile file) throws Exception {
		String currentMonthYears = Utils.formatDateString(new Date(), "MM-yyyy");
		String filePath = String.format(fileUploadBasePath + "%s/", currentMonthYears);
		FileUploadDto fileUploadDto = fileUpload(filePath, file);
		return fileUploadDto.getAbsolutePath().replace(replacePath, fileAccessBaseUrl);
	}

	public FileUploadDto fileUpload(String destPath, MultipartFile file) throws Exception {
		FileUploadDto fileUploadDto = new FileUploadDto();
		String originalFileName = file.getOriginalFilename();
		String fileExtension = "";
		int lastDotIndex = originalFileName.lastIndexOf(".");
		if (lastDotIndex != -1) {
			fileExtension = originalFileName.substring(lastDotIndex);
			originalFileName = originalFileName.substring(0, lastDotIndex);
		}
		String timeStamp = Utils.formatDateString(new Date(), "yyyyMMdd_HHmmss");
		String fileName = String.format("%s_%s%s", originalFileName, timeStamp, fileExtension);
		log.info("Saving the uploaded file: {}", fileName);
		log.info("file to be saved at: {}", destPath);
		if (!new File(destPath).exists()) {
			log.info("Directory doesn't exist");
			new File(destPath).mkdirs();
		}
		File dest = new File(destPath, fileName);
		log.info("Destination fetched: {}", dest.getAbsolutePath());
		if (!dest.exists()) {
			try {
				log.info("File Created {}", dest.createNewFile());
			} catch (IOException e) {
				throw new BadRequestException("File Error: " + e.getMessage());
			}
		}
		log.info("Multipart File: {}", file);
		try {
			file.transferTo(dest);
			log.info("file successfully uploaded to location: {}", dest);
			Utils.setFilePermissions(String.valueOf(dest));
			fileUploadDto.setAbsolutePath(String.valueOf(dest));
		} catch (IllegalStateException | IOException e) {
			throw new BadRequestException("File Transfer Error: " + e.getMessage());
		}
		fileUploadDto.setFileName(fileName);
		return fileUploadDto;
	}
}
