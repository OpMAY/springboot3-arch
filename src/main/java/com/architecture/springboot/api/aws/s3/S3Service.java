package com.architecture.springboot.api.aws.s3;

import com.architecture.springboot.util.Time;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedFileUpload;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.progress.LoggingTransferListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
@PropertySource("classpath:aws.properties")
public class S3Service {
    private final S3Client s3Client;
    private final Environment environment;
    private final Region DEFAULT_REGION = Region.AP_NORTHEAST_2;
    private String defaultURL;
    private String bucketName;
    private String serverFilePath;

    /*
      TODO
      1. PutObject
      2. GetObject
      3. Delete Object
      4. (Optional) Put/Get Objects
      */
    @PostConstruct
    public void init() {
        bucketName = environment.getProperty("aws.bucketName");
        defaultURL = getBucketUrl();
        serverFilePath = environment.getProperty("server.uploadPath");
    }

    /**
     * @param file          (MultipartFile)
     * @param fileFolderURL (String)
     * @return String
     * <p>putObject - AWS S3 버킷에 파일 업로드</p>
     **/
    public String putObject(MultipartFile file, String fileFolderURL) {
        try {
            log.info("put Object file : {}, url : {}", file, fileFolderURL);
            log.info("before upload : {}", Time.TimeFormatHMS());
            String fileName = UUID.randomUUID() + file.getOriginalFilename();
            String contentType = file.getContentType();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileFolderURL + fileName)
                    .contentType(contentType)
                    .contentLength(file.getSize())
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            log.info("putObjectResponse : {}", response.sdkHttpResponse());
            if (response.sdkHttpResponse().statusText().orElse("FAIL").equals("OK")) {
                log.info("after upload : {}", Time.TimeFormatHMS());
                return getFileUrl(fileFolderURL, fileName);
            }

        } catch (IOException ie) {
            log.error("파일을 읽어들이는데 에러가 발생했습니다.");
            log.error(ie.getMessage());
            throw new RuntimeException(ie.getMessage());
        } catch (S3Exception ae) {
            log.error("AWS와 통신에 문제가 발생했습니다.");
            log.error(ae.awsErrorDetails().errorMessage());
            throw new RuntimeException(ae.getMessage());
        } catch (IllegalStateException se) {
            log.error("AWS와 통신에 문제가 발생했습니다.");
            log.error(se.getMessage());
            throw new RuntimeException(se.getMessage());
        }
        return null;
    }


    /**
     * @param fileURL (String)
     * @return File
     * <p>getObject - AWS S3 버킷에 있는 파일 다운로드</p>
     **/
    public File getObject(String fileURL) {
        String path = getFilePathURL(fileURL);
        try {
            GetObjectRequest objectRequest =
                    GetObjectRequest.builder()
                            .key(path)
                            .bucket(bucketName)
                            .build();
            ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(objectRequest);
            File file = new File(serverFilePath + getFileNameFromURL(fileURL));
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                outputStream.write(response.asByteArray());
            } catch (IOException e) {
                log.error("파일 경로가 맞지 않거나 파일이 존재하지 않습니다.");
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
            return file;
        } catch (S3Exception se) {
            log.error("AWS와 통신에 문제가 발생했습니다.");
            log.error(se.awsErrorDetails().errorMessage());
            throw new RuntimeException(se.getMessage());
        }
    }

    /**
     * @param filePath (String)
     * @return boolean
     * <p>deleteObject - AWS S3 버킷에 있는 파일 삭제</p>
     **/
    public boolean deleteObject(String filePath) {
        try {
            List<ObjectIdentifier> bucketObjects = List.of(ObjectIdentifier.builder().key(filePath).build());
            Delete del = Delete.builder()
                    .objects(bucketObjects)
                    .build();
            DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(del)
                    .build();
            s3Client.deleteObjects(multiObjectDeleteRequest);
            return true;
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    /**
     * @param filePaths (List<String>)
     * @return boolean
     * <p>deleteObjects - AWS S3 버킷에 있는 파일들 삭제</p>
     **/
    public boolean deleteObjects(List<String> filePaths) {
        try {
            List<ObjectIdentifier> bucketObjects = new ArrayList<>();
            for (String s : filePaths) {
                bucketObjects.add(ObjectIdentifier.builder().key(s).build());
            }
            Delete del = Delete.builder()
                    .objects(bucketObjects)
                    .build();
            DeleteObjectsRequest multiObjectDeleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(del)
                    .build();
            s3Client.deleteObjects(multiObjectDeleteRequest);
            return true;
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            return false;
        }
    }


    private String getBucketUrl() {
        return "https://" + bucketName + ".s3." + DEFAULT_REGION + ".amazonaws.com/";
    }

    private String getFileUrl(String fileFolderURL, String fileName) {
        return defaultURL + fileFolderURL + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }

    private String getFilePathURL(String fullURL) {
        return fullURL.replace(defaultURL, "");
    }

    private String getFileNameFromURL(String url) {
        if (url.lastIndexOf("/") != -1) {
            return url.substring(url.lastIndexOf("/") + 1);
        } else {
            return url;
        }
    }

}
