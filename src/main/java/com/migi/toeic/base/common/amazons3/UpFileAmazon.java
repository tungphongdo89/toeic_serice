package com.migi.toeic.base.common.amazons3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.migi.toeic.base.common.FileUtils;
import com.migi.toeic.dto.FileDTO;
import com.migi.toeic.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.net.URL;

import static org.reflections.Reflections.log;


//@PropertySource("classpath:application.properties")
@Component
public class UpFileAmazon implements EnvironmentAware {

    FileUtils fileUtils = new FileUtils();

    @Autowired
    private Environment env;

    public static Environment environment;

    public static String getConfigProp(String key) {
        return environment.getProperty(key);
    }
    @Override
    public void setEnvironment(Environment arg0) {
        environment=arg0;
    }
//    public String getAccessKey() { return env.getProperty("accessKey"); }
//    public String getSecretKey() { return env.getProperty("secretKey"); }
//    public String getBucketName() { return env.getProperty("bucketName"); }
//    public String getWithregion() { return env.getProperty("withregion"); }
//    public String getStorage() { return env.getProperty("storage"); }
//    public String getAddressUrlAmazons3() { return env.getProperty("addressUrlAmazons3"); }

    public UpFileAmazon() {
    }

    public String UpLoadFile(MultipartFile file , Long idCate , Long idQuestion) throws BusinessException {
        try {
            String accessKey= UpFileAmazon.getConfigProp("accessKey");
            String secretKey= UpFileAmazon.getConfigProp("secretKey");
            String bucketName= UpFileAmazon.getConfigProp("bucketName");
            String withregion= UpFileAmazon.getConfigProp("withregion");
            String storage= UpFileAmazon.getConfigProp("storage");
//            String addressUrlAmazons3= UpFileAmazon.getConfigProp("addressUrlAmazons3");

            fileUtils.createUploadFileLocation(idCate);
            FileDTO fileDTO = fileUtils.storeFile(file , idCate , idQuestion);
            String localpath = fileDTO.getPathLocation();
            String fileType = fileDTO.getFileType();

            BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(withregion).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
            int lastIndexOf = localpath.lastIndexOf("\\");
            String filename = localpath.substring(lastIndexOf + 1);
            String folderAWS;
            if(fileType.contains("audio")){
                folderAWS  = "audio";
            }else if (fileType.contains("image")){
                folderAWS = "image";
            }else {
                folderAWS = "txt";
            }
            PutObjectRequest request = new PutObjectRequest(bucketName, folderAWS + "/" + filename, new File(localpath));
            s3Client.putObject(request.withCannedAcl(CannedAccessControlList.PublicRead));
//            String urlFile = addressUrlAmazons3 + storage + "/" + folderAWS + "/" + filename;
            URL urlFile =  s3Client.getUrl(bucketName, storage + "/" +folderAWS + "/" + filename);
            String urlFileSub = urlFile.toString().substring(0,8) + "s3-" +urlFile.toString().substring(31);
            System.out.println(urlFileSub);
            return urlFileSub;
        } catch (BusinessException e) {
            log.error("Lỗi upload ảnh ở đây ==> " + e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }
}
