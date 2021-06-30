package com.migi.toeic.rest;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.migi.toeic.exception.BusinessException;
import com.migi.toeic.service.ImportDataService;
import com.migi.toeic.service.impl.ImportDataServiceImpl;
import com.migi.toeic.utils.UFile;
import io.swagger.annotations.Api;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Collections;

@RestController
@Api(description = "API CATEGORY MANAGEMENT")

@RequestMapping("/v1/upload/")
@JsonRootName("snapshot")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@PropertySource("classpath:i18n/messages.properties")
public class CommonUploadData {

	@Autowired
	private Environment env;
	@Autowired
	ImportDataService importDataService;

	@PostMapping(value = "importDataPart1", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importData(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart1((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}

	@PostMapping(value = "importDataPart2", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart2(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart2((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}

	@PostMapping(value = "importDataPart3", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart3(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart3((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}
	@PostMapping(value = "importDataPart4", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart4(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart4((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}


	@PostMapping(value = "importDataPart5", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart5(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart5((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}

	@PostMapping(value = "importDataPart6", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart6(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart6((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}

	@PostMapping(value = "importDataPart7", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart7(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart7((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}

	@PostMapping(value = "importDataPart8", consumes = MediaType.MULTIPART_FORM_DATA, produces = MediaType.TEXT_HTML)
	public Response importDataPart8(@Multipart(value = "file") MultipartFile file, HttpServletRequest request) throws Exception {
		// TODO Auto-generated method stub
		String filePath = "";
		String folderUpload = env.getProperty("folderUpload");
//		DataHandler dataHandler = attachments.getDataHandler();

		// get filename to be uploaded
		String fileName = file.getOriginalFilename();

		// write & upload file to server
		try (InputStream inputStream = file.getInputStream();) {
			filePath = UFile.writeToFileServerATTT2(inputStream, fileName, "", folderUpload);
//			filePathReturn = UEncrypt.encryptFileUploadPath(filePath);
		} catch (Exception ex) {
			throw new BusinessException("Loi khi save file", ex);
		}
		try {
			try {
				return Response.ok(importDataService.importDataPart8((folderUpload + filePath))).build();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
			}

		} catch (IllegalArgumentException e) {
			return Response.ok().entity(Collections.singletonMap("error", e.getMessage())).build();
		}
	}
}
