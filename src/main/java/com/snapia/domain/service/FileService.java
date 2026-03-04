package com.snapia.domain.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    private final String uploadPath = "C:/snapia/upload/";

    /**
     * 파일을 저장하고 저장된 파일명을 반환
     * @param file 업로드할 파일
     * @return 저장된 파일명 (UUID_원본파일명)
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(uploadPath, fileName);

        try {
            // 폴더가 없으면 생성
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }

            file.transferTo(saveFile); // 실제 하드디스크에 저장
            log.info("파일 저장 완료: {}", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 파일 삭제
     * @param fileName 삭제할 파일명
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        File file = new File(uploadPath, fileName);
        if (file.exists()) {
            if (file.delete()) {
                log.info("파일 삭제 완료: {}", fileName);
            } else {
                log.warn("파일 삭제 실패: {}", fileName);
            }
        }
    }
}