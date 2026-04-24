package com.stark.shoot.adapter.out.storage

import com.stark.shoot.application.port.out.storage.FileStoragePort
import com.stark.shoot.infrastructure.annotation.Adapter
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

/**
 * 로컬 파일 시스템 기반 파일 저장소 Adapter
 *
 * TODO: 프로덕션 환경에서는 클라우드 스토리지(S3, GCS 등)를 사용하도록 교체 필요
 * TODO: 파일 검증 추가 (MIME 타입, 파일 크기, 악성 파일 검사 등)
 * TODO: 이미지 리사이징/최적화 기능 추가
 * TODO: CDN 통합
 */
@Adapter
class LocalFileStorageAdapter(
    @Value("\${file.upload-dir:./uploads}")
    private val uploadDir: String,

    @Value("\${file.base-url:http://localhost:8080/uploads}")
    private val baseUrl: String
) : FileStoragePort {

    private val logger = KotlinLogging.logger {}
    private val rootLocation: Path = Paths.get(uploadDir)

    init {
        try {
            Files.createDirectories(rootLocation)
            logger.info { "파일 저장 디렉토리 초기화: $rootLocation" }
        } catch (e: IOException) {
            logger.error(e) { "파일 저장 디렉토리 생성 실패: $uploadDir" }
            throw IllegalStateException("Could not initialize storage", e)
        }
    }

    override fun store(file: MultipartFile, directory: String): String {
        if (file.isEmpty) {
            throw IllegalArgumentException("업로드할 파일이 비어있습니다")
        }

        try {
            // 파일명 생성 (UUID + 원본 확장자)
            val originalFilename = file.originalFilename ?: "unknown"
            val extension = originalFilename.substringAfterLast('.', "")
            val filename = "${UUID.randomUUID()}${if (extension.isNotEmpty()) ".$extension" else ""}"

            // 디렉토리 경로 생성
            val dirPath = rootLocation.resolve(directory)
            Files.createDirectories(dirPath)

            // 파일 저장
            val destinationFile = dirPath.resolve(filename)
            file.inputStream.use { input ->
                Files.copy(input, destinationFile, StandardCopyOption.REPLACE_EXISTING)
            }

            val fileUrl = "$baseUrl/$directory/$filename"
            logger.info { "파일 저장 완료: $fileUrl" }
            return fileUrl

        } catch (e: IOException) {
            logger.error(e) { "파일 저장 실패: ${file.originalFilename}" }
            throw FileStorageException("파일 저장에 실패했습니다", e)
        }
    }

    override fun delete(fileUrl: String) {
        try {
            // URL에서 파일 경로 추출
            val relativePath = fileUrl.removePrefix(baseUrl).trimStart('/')
            val filePath = rootLocation.resolve(relativePath)

            if (Files.exists(filePath)) {
                Files.delete(filePath)
                logger.info { "파일 삭제 완료: $fileUrl" }
            } else {
                logger.warn { "삭제할 파일이 존재하지 않음: $fileUrl" }
            }

        } catch (e: IOException) {
            logger.error(e) { "파일 삭제 실패: $fileUrl" }
            throw FileStorageException("파일 삭제에 실패했습니다", e)
        }
    }

    override fun exists(fileUrl: String): Boolean {
        return try {
            val relativePath = fileUrl.removePrefix(baseUrl).trimStart('/')
            val filePath = rootLocation.resolve(relativePath)
            Files.exists(filePath)
        } catch (e: Exception) {
            logger.error(e) { "파일 존재 여부 확인 실패: $fileUrl" }
            false
        }
    }
}

/**
 * 파일 저장소 관련 예외
 */
class FileStorageException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
