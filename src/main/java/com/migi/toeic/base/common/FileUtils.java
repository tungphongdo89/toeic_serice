package com.migi.toeic.base.common;

import com.migi.toeic.dto.FileDTO;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.utils.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FileUtils {

	private Path fileStorageAudioLocation;
	private Path fileStorageImageLocation;
	private Path fileStorageTextLocation;

	public String uploadFile(MultipartFile file, Long idCate, Long idQuestion) {
		createUploadFileLocation(idCate);
		FileDTO fileDTO = storeFile(file, idCate, idQuestion);
		int index = fileDTO.getPathLocation().indexOf("assest");
		// Format path file lưu vào database
		String pathFile = "";
		try {
//            pathFile = "toeic_web\\" + fileDTO.getPathLocation().substring(index);
			pathFile = fileDTO.getPathLocation().substring(index);
		} catch (Exception ex) {
			throw new BusinessException(MessageUtils.getMessage("error_create_location"), ex);
		}
		return pathFile;
	}

	// Tạo nơi chứa các file người dùng upload
	public void createUploadFileLocation(Long idCate) throws BusinessException {
		if (idCate != null) {
			this.fileStorageAudioLocation = Paths.get(MessageUtils.getMessage("uploadAudioDir") + "/filesOfCategory" + "/De_bai_ID_" + idCate + "/audio")
					.toAbsolutePath().normalize();
			this.fileStorageImageLocation = Paths.get(MessageUtils.getMessage("uploadImageDir") + "/filesOfCategory" + "/De_bai_ID_" + idCate + "/image")
					.toAbsolutePath().normalize();
			this.fileStorageTextLocation = Paths.get(MessageUtils.getMessage("uploadTextDir") + "/filesOfCategory" + "/De_bai_ID_" + idCate + "/text")
					.toAbsolutePath().normalize();
		} else {
			this.fileStorageAudioLocation = Paths.get(MessageUtils.getMessage("uploadAudioDir") + "/filesOfGeneral" + "/audio")
					.toAbsolutePath().normalize();
			this.fileStorageImageLocation = Paths.get(MessageUtils.getMessage("uploadImageDir") + "/filesOfGeneral" + "/image")
					.toAbsolutePath().normalize();
			this.fileStorageTextLocation = Paths.get(MessageUtils.getMessage("uploadTextDir") + "/filesOfGeneral" + "/text")
					.toAbsolutePath().normalize();
		}
		try {
			Files.createDirectories(this.fileStorageAudioLocation);
			Files.createDirectories(this.fileStorageImageLocation);
			Files.createDirectories(this.fileStorageTextLocation);
		} catch (Exception ex) {
			throw new BusinessException(MessageUtils.getMessage("error_create_location"), ex);
		}
	}

	public FileDTO storeFile(MultipartFile file, Long idCate, Long idQuestion) throws BusinessException {
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmssddMMyyyy");
		String formatDateTime = localDateTime.format(formatter);

		String fileType = StringUtils.cleanPath(file.getContentType());

		// Format tên file lưu vào folder
		String fileNameFormat = "";
		if (idCate != null && idQuestion == null) {
			fileNameFormat = "De_bai_ID_" + idCate + "_" + formatDateTime + "_" + StringUtils.cleanPath(file.getOriginalFilename());
		} else if (idCate != null && idQuestion != null) {
			fileNameFormat = "De_bai_ID_" + idCate + "_Cau_hoi_ID_" + idQuestion + "_" + formatDateTime + "_" + StringUtils.cleanPath(file.getOriginalFilename());
		} else {
			fileNameFormat = formatDateTime + "_" + StringUtils.cleanPath(file.getOriginalFilename());
		}

		if (fileNameFormat.contains("..")) {
			throw new BusinessException(MessageUtils.getMessage("error_name_file") + fileNameFormat + "");
		}
		if (file.getSize() > 5242880) {
			throw new BusinessException(MessageUtils.getMessage("error_size_file") + file.getSize() + "");
		}

		try {
			Long fileSize = file.getSize();
			Path targetLocation = Paths.get("");

			// Lưu file upload vào nơi đã tạo
			if (fileType.contains("audio")) {
				targetLocation = this.fileStorageAudioLocation.resolve(fileNameFormat);
			}
			if (fileType.contains("image")) {
				targetLocation = this.fileStorageImageLocation.resolve(fileNameFormat);
			}
			if (fileType.contains("text")) {
				targetLocation = this.fileStorageTextLocation.resolve(fileNameFormat);
			}
			FileDTO fileDTO = new FileDTO(fileNameFormat, fileType, fileSize, String.valueOf(targetLocation));
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileDTO;
		} catch (FileSystemException e){
			throw new BusinessException(MessageUtils.getMessage("max_length_file_name_upload"), e);
		}
		catch (IOException ex) {
			throw new BusinessException(MessageUtils.getMessage("error_save_file_upload") + fileNameFormat + "", ex);
		}
	}


	// ----------------------------Download-----------------------------

	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws BusinessException {
		//Load file as Resource
		Resource resource = loadFileAsResource(fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			throw new BusinessException(MessageUtils.getMessage("error_download_file"));
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	public Resource loadFileAsResource(String fileName) throws BusinessException {
		try {
			Path fileAudioPath = this.fileStorageAudioLocation.resolve(fileName).normalize();
			Resource resourceAudio = new UrlResource(fileAudioPath.toUri());

			Path fileImagePath = this.fileStorageImageLocation.resolve(fileName).normalize();
			Resource resourceImage = new UrlResource(fileImagePath.toUri());

			Path fileTextPath = this.fileStorageTextLocation.resolve(fileName).normalize();
			Resource resourceText = new UrlResource(fileTextPath.toUri());

			if (resourceAudio.exists()) {
				return resourceAudio;
			} else if (resourceImage.exists()) {
				return resourceImage;
			} else if (resourceText.exists()) {
				return resourceText;
			} else {
				throw new BusinessException(MessageUtils.getMessage("error_download_file") + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new BusinessException(MessageUtils.getMessage("error_download_file") + fileName, ex);
		}
	}

	public FileDTO storeFile2(Long idCate, Long idQuestion, String contentType, String fileName, long size, InputStream ips) throws BusinessException {
		createUploadFileLocation(idCate);
		LocalDateTime localDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmssddMMyyyy");
		String formatDateTime = localDateTime.format(formatter);

		String fileType = StringUtils.cleanPath(contentType);

		// Format tên file lưu vào folder
		String fileNameFormat = "testCsv";
//        if(idCate != null && idQuestion == null){
//            fileNameFormat = "De_bai_ID_" + idCate + "_" + formatDateTime + "_" + StringUtils.cleanPath(fileName);
//        }
//        else if(idCate != null && idQuestion != null){
//            fileNameFormat = "De_bai_ID_" + idCate + "_Cau_hoi_ID_" + idQuestion + "_" + formatDateTime + "_" + StringUtils.cleanPath(fileName);
//        }
//        else {
//            fileNameFormat = formatDateTime + "_" + StringUtils.cleanPath(fileName);
//        }
//
//        if(fileNameFormat.contains("..")) {
//            throw new BusinessException(MessageUtils.getMessage("error_name_file") + fileNameFormat + "");
//        }
		if (size > 5000000) {
			throw new BusinessException(MessageUtils.getMessage("error_size_file") + size + "");
		}

		try {
			Long fileSize = size;
			Path targetLocation = Paths.get("");

			// Lưu file upload vào nơi đã tạo
			if (fileType.contains("audio")) {
				targetLocation = this.fileStorageAudioLocation.resolve(fileNameFormat);
			}
			if (fileType.contains("image")) {
				targetLocation = this.fileStorageImageLocation.resolve(fileNameFormat);
			}
			if (fileType.contains("text")) {
				targetLocation = this.fileStorageTextLocation.resolve(fileNameFormat);
			} else {
				targetLocation = this.fileStorageTextLocation.resolve(fileNameFormat);
			}

			FileDTO fileDTO = new FileDTO(fileNameFormat, fileType, fileSize, String.valueOf(targetLocation));
			Files.copy(ips, targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileDTO;
		} catch (IOException ex) {
			throw new BusinessException(MessageUtils.getMessage("error_save_file_upload") + fileNameFormat + "", ex);
		}
	}
}
