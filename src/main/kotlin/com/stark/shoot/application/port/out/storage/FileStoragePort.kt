package com.stark.shoot.application.port.out.storage

import org.springframework.web.multipart.MultipartFile

/**
 * 파일 저장소 포트
 *
 * 파일 업로드, 삭제, URL 생성 등의 기능을 제공합니다.
 */
interface FileStoragePort {

    /**
     * 파일을 저장하고 접근 가능한 URL을 반환합니다.
     *
     * @param file 업로드할 파일
     * @param directory 저장할 디렉토리 (예: "profile", "attachments")
     * @return 저장된 파일의 URL
     * @throws FileStorageException 파일 저장 실패 시
     */
    fun store(file: MultipartFile, directory: String): String

    /**
     * URL로부터 파일을 삭제합니다.
     *
     * @param fileUrl 삭제할 파일의 URL
     * @throws FileStorageException 파일 삭제 실패 시
     */
    fun delete(fileUrl: String)

    /**
     * 파일 존재 여부를 확인합니다.
     *
     * @param fileUrl 확인할 파일의 URL
     * @return 파일이 존재하면 true
     */
    fun exists(fileUrl: String): Boolean
}
