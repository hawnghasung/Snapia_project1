package com.snapia.domain.service;

import lombok.extern.log4j.Log4j2;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@Log4j2
public class FileService {

    private final String uploadPath = "C:/snapia/upload/";
    private final String FFMPEG_PATH  = "C:/ffmpeg/bin/ffmpeg";
    private final String FFPROBE_PATH = "C:/ffmpeg/bin/ffprobe";

    private static final java.util.Set<String> IMAGE_TYPES = java.util.Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final java.util.Set<String> VIDEO_TYPES = java.util.Set.of(
            "video/mp4", "video/quicktime", "video/avi", "video/x-matroska"
    );

    /**
     * 파일 타입 판별 (IMAGE / VIDEO)
     */
    public String getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return "UNKNOWN";
        if (IMAGE_TYPES.contains(contentType)) return "IMAGE";
        if (VIDEO_TYPES.contains(contentType)) return "VIDEO";
        return "UNKNOWN";
    }

    /**
     * 이미지 저장
     */
    public String saveFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        File saveFile = new File(uploadPath, fileName);

        try {
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            file.transferTo(saveFile);
            log.info("파일 저장 완료: {}", fileName);
            return fileName;
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 동영상 저장 (인코딩 없이 바로 저장 + 썸네일만 추출)
     */
    public VideoSaveResult saveVideo(MultipartFile file, int maxDurationSeconds) {
        if (file == null || file.isEmpty()) return null;

        String uuid = UUID.randomUUID().toString();
        String originalExt = getExtension(file.getOriginalFilename());
        String outputFileName = uuid + "." + originalExt; // ✅ 인코딩 없이 원본 그대로
        String thumbFileName  = uuid + "_thumb.jpg";

        File outputFile = new File(uploadPath, outputFileName);
        File thumbFile  = new File(uploadPath, thumbFileName);

        try {
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            // ✅ 인코딩 없이 바로 저장
            file.transferTo(outputFile);
            log.info("동영상 저장 완료 (인코딩 스킵): {}", outputFileName);

            // 길이 체크
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);
            FFmpegProbeResult probeResult = ffprobe.probe(outputFile.getAbsolutePath());
            double duration = probeResult.getFormat().duration;

            if (duration > maxDurationSeconds) {
                outputFile.delete();
                throw new IllegalArgumentException(
                        "동영상은 " + maxDurationSeconds + "초 이하만 가능합니다. (현재: " + (int)duration + "초)"
                );
            }

            // ✅ 썸네일만 추출 (1초 지점)
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            // ✅ 이걸로 교체
            // 첫 프레임 추출 (가장 안전)
            FFmpegBuilder thumbBuilder = new FFmpegBuilder()
                    .setInput(outputFile.getAbsolutePath())
                    .overrideOutputFiles(true)
                    .addOutput(thumbFile.getAbsolutePath())
                    .setFrames(1)
                    .done();

            new FFmpegExecutor(ffmpeg, ffprobe).createJob(thumbBuilder).run();
            log.info("썸네일 추출 완료: {}", thumbFileName);

            return new VideoSaveResult(outputFileName, thumbFileName, (int) duration);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            log.error("동영상 저장 중 오류: {}", e.getMessage());
            if (outputFile.exists()) outputFile.delete();
            if (thumbFile.exists())  thumbFile.delete();
            throw new RuntimeException("동영상 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * 파일 삭제
     */
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;

        File file = new File(uploadPath, fileName);
        if (file.exists()) {
            if (file.delete()) {
                log.info("파일 삭제 완료: {}", fileName);
            } else {
                log.warn("파일 삭제 실패: {}", fileName);
            }
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "mp4";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static class VideoSaveResult {
        public final String videoFileName;
        public final String thumbFileName;
        public final int    duration;

        public VideoSaveResult(String videoFileName, String thumbFileName, int duration) {
            this.videoFileName = videoFileName;
            this.thumbFileName = thumbFileName;
            this.duration      = duration;
        }
    }
}