package com.architecture.springboot.restcontroller;

import com.architecture.springboot.response.ApiResponse;
import com.architecture.springboot.response.ResMessage;
import com.architecture.springboot.response.ResponseError;
import com.architecture.springboot.service.SampleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SampleRestController {
    private final SampleService sampleService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ApiResponse<ResMessage> restTest(HttpServletRequest request) {
        ResMessage resMessage = new ResMessage();
        resMessage.put("test", 1);
        log.info("resMessage : {}", resMessage);
        return ApiResponse.success(resMessage);
    }

    @RequestMapping(value = "/test/error", method = RequestMethod.GET)
    public ApiResponse<ResMessage> errorTest(HttpServletRequest request) {
        return ApiResponse.error(new ResponseError("Code 1", "error Test"));
    }

    @RequestMapping(value = "/test/errors", method = RequestMethod.GET)
    public ApiResponse<ResMessage> errorsTest(HttpServletRequest request) {
        return ApiResponse.error(List.of(
                new ResponseError("Code 1", "error Test"),
                new ResponseError("Code 2", "error Test")
        ));
    }

    @RequestMapping(value = "/test/fail", method = RequestMethod.GET)
    public ApiResponse<ResMessage> failTest(HttpServletRequest request) {
        return ApiResponse.fail(new ResponseError("Code 1", "error Test"));
    }

    @RequestMapping(value = "/test/fails", method = RequestMethod.GET)
    public ApiResponse<ResMessage> failsTest(HttpServletRequest request) {
        return ApiResponse.fail(List.of(
                new ResponseError("Code 1", "error Test"),
                new ResponseError("Code 2", "error Test")
        ));
    }

    @RequestMapping(value = "/test/upload", method = RequestMethod.POST)
    public ApiResponse<ResMessage> fileUploadTest(@RequestPart MultipartFile file, HttpServletRequest request) {
        ResMessage resMessage = new ResMessage();
        log.info("file : {} / {} / {} / {}", file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getResource());
        resMessage.put("file", file.getOriginalFilename());
        resMessage.put("valid", !file.isEmpty());
        return ApiResponse.success(resMessage);
    }

    @RequestMapping(value = "/test/upload/aws", method = RequestMethod.POST)
    public ApiResponse<ResMessage> fileAWSUploadTest(@RequestPart MultipartFile file, HttpServletRequest request) {
        ResMessage resMessage = new ResMessage();
        log.info("file : {} / {} / {} / {}", file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getResource());
        resMessage.put("file", file.getOriginalFilename());
        resMessage.put("valid", !file.isEmpty());
        resMessage.put("url", sampleService.testAWSUpload(file));
        return ApiResponse.success(resMessage);
    }

    @RequestMapping(value = "/test/upload/big", method = RequestMethod.POST)
    public ResponseEntity<String> fileBigUploadTest(@RequestParam("chunk") MultipartFile file,
                                            @RequestParam("chunkNumber") int chunkNumber,
                                            @RequestParam("totalChunks") int totalChunks) throws IOException {
        boolean isDone = sampleService.chunkUpload(file, chunkNumber, totalChunks);
        log.info("file : {} / {} / {} / {}", file.getOriginalFilename(), file.getSize(), file.getContentType(), file.getResource());
        return isDone ?
                ResponseEntity.ok("File uploaded successfully") :
                ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
    }
}
