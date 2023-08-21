package com.architecture.springboot.service;

import com.architecture.springboot.api.aws.s3.S3Service;
import com.architecture.springboot.dao.TestDao;
import com.architecture.springboot.model.dto.TestDto;
import com.architecture.springboot.model.entity.TestEntity;
import com.architecture.springboot.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class SampleService {
    private final S3Service s3service;
    private final TestRepository testRepository;
    private final TestDao testDao;

    public String testAWSUpload(MultipartFile file) {
        return s3service.putObject(file, "test/");
    }

    public boolean chunkUpload(MultipartFile file, int chunkNumber, int totalChunks) throws IOException {
        // 파일 업로드 위치
        String uploadDir = "D:\\Projects\\springboot3\\upload";

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 임시 저장 파일 이름
        String filename = file.getOriginalFilename() + ".part" + chunkNumber;

        Path filePath = Paths.get(uploadDir, filename);
        // 임시 저장
        Files.write(filePath, file.getBytes());

        // 마지막 조각이 전송 됐을 경우
        if (chunkNumber == totalChunks - 1) {
            String[] split = file.getOriginalFilename().split("\\.");
            String outputFilename = UUID.randomUUID() + "." + split[split.length - 1];
            Path outputFile = Paths.get(uploadDir, outputFilename);
            Files.createFile(outputFile);

            // 임시 파일들을 하나로 합침
            for (int i = 0; i < totalChunks; i++) {
                Path chunkFile = Paths.get(uploadDir, file.getOriginalFilename() + ".part" + i);
                Files.write(outputFile, Files.readAllBytes(chunkFile), StandardOpenOption.APPEND);
                // 합친 후 삭제
                Files.delete(chunkFile);
            }
            log.info("File uploaded successfully");
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void dbTest() {
        TestDto testDto = new TestDto(1, "name");
        TestEntity testEntity = new TestEntity(null);
        log.info(1);
        testRepository.deleteAll();
        log.info(2);
        testDao.insertTest(testDto);
        log.info(3);
        testRepository.save(testEntity);
        log.info(4);
        log.info(testRepository.findAll());
        log.info("mybatis : {}", testDao.getTests());
    }
}
