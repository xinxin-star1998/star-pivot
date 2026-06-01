package com.star.pivot.framework.storage.integration;

import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.StoragePathValidator;
import com.star.pivot.framework.storage.UploadResult;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件上传校验与路径安全集成测试
 */
class FileUploadIntegrationTest {

    private static final String[] IMAGE_TYPES = {"image/png", "image/jpeg"};

    private final FileStorageService fileStorageService = new StubFileStorageService();

    @Test
    void storagePathValidator_shouldAllowWhitelistedPrefixes() {
        assertTrue(StoragePathValidator.isAllowedPresignedPath("avatar/1.png"));
        assertTrue(StoragePathValidator.isAllowedPresignedPath("goods/2026/01/spu.png"));
        assertTrue(StoragePathValidator.isAllowedPresignedPath("editor/2026/note.png"));
    }

    @Test
    void storagePathValidator_shouldRejectUnsafePaths() {
        assertFalse(StoragePathValidator.isAllowedPresignedPath("../avatar/x.png"));
        assertFalse(StoragePathValidator.isAllowedPresignedPath("/avatar/x.png"));
        assertFalse(StoragePathValidator.isAllowedPresignedPath("secret/key.txt"));
        assertFalse(StoragePathValidator.isAllowedPresignedPath(""));
    }

    @Test
    void uploadFile_shouldRejectEmptyFile() {
        MockMultipartFile empty = new MockMultipartFile("file", "a.png", "image/png", new byte[0]);

        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.uploadFile(empty, "goods", "1", IMAGE_TYPES, 1024L));
    }

    @Test
    void uploadFile_shouldRejectOversizedFile() {
        byte[] content = new byte[2048];
        MockMultipartFile large = new MockMultipartFile("file", "a.png", "image/png", content);

        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.uploadFile(large, "goods", "1", IMAGE_TYPES, 1024L));
    }

    @Test
    void uploadFile_shouldRejectUnsupportedContentType() {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", "hello".getBytes());

        assertThrows(IllegalArgumentException.class,
                () -> fileStorageService.uploadFile(file, "goods", "1", IMAGE_TYPES, 1024L));
    }

    @Test
    void uploadFile_shouldSanitizeMaliciousImageExtension() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "evil.php.jpg", "image/png", "x".getBytes());

        UploadResult result = fileStorageService.uploadFile(file, "goods", "100", IMAGE_TYPES, 1024L * 1024);

        assertTrue(result.getObjectName().startsWith("goods/"));
        assertTrue(result.getObjectName().contains("/100/"));
        assertFalse(result.getObjectName().contains(".php"));
    }

    private static final class StubFileStorageService implements FileStorageService {
        @Override
        public String uploadAvatar(org.springframework.web.multipart.MultipartFile file, String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String uploadAvatarWithUrl(org.springframework.web.multipart.MultipartFile file, String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String uploadAvatarWithPresignedUrl(org.springframework.web.multipart.MultipartFile file, String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void deleteAvatar(String userId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPresignedUrl(String objectName) {
            return "https://example.test/" + objectName;
        }

        @Override
        public String getPermanentUrl(String objectName) {
            return "https://example.test/" + objectName;
        }

        @Override
        public String uploadEditorImageWithUrl(org.springframework.web.multipart.MultipartFile file) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void uploadFileInternal(org.springframework.web.multipart.MultipartFile file, String objectName) {
            // no-op stub
        }
    }
}
