# Springboot 2 to Springboot 3 migration

## Start 2023.08.16

## Version Logs

### version 1. 2023.08.16
```
- Springboot Version 3.1.2
- Maven Settings
- Database Configurations (MyBatis X JPA)
    -> ChainedTransactionManager 수정 필요
- Log4j2 Update
- Basic Settings
```

### version 1-1. 2023.08.17
```
- utility Update
- AWS 및 S3 SET
- WebConfiguration add
- Interceptor add
- Rest Message Reformat
```

### version 1-2. 2023.08.18
```
- ResponseEntity 사용방식 변경 (ApiResponse)
    -> 기존 DefaultRes Deprecated
- 파일 업로드 분할 방식 (대용량 파일 업로드 대비)
- AWS 업로드 방식 테스트
- Controller Advice 추가 (더 개선 필요)
```