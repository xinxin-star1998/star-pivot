package com.star.pivot.mall.integration;

import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.FileUploadVO;
import com.star.pivot.framework.storage.UploadResult;
import com.star.pivot.mall.controller.GoodsImageController;
import com.star.pivot.mall.support.GoodsImageConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 商品图片上传集成测试（Controller + FileStorageService 协作）
 */
@ExtendWith(MockitoExtension.class)
class GoodsImageControllerIntegrationTest {

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private GoodsImageController goodsImageController;

    @Test
    void upload_shouldReturnFileUploadVoWithGoodsCategory() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", "img".getBytes());
        UploadResult uploadResult = UploadResult.builder()
                .objectName("goods/2026/06/01/100/uuid.png")
                .permanentUrl("https://bucket.example.com/goods/2026/06/01/100/uuid.png")
                .presignedUrl("https://bucket.example.com/goods/2026/06/01/100/uuid.png?sig=abc")
                .build();
        when(fileStorageService.uploadFile(
                eq(file),
                eq(GoodsImageConstants.CATEGORY),
                eq("100"),
                eq(GoodsImageConstants.ALLOWED_CONTENT_TYPES),
                eq(GoodsImageConstants.MAX_SIZE_BYTES)
        )).thenReturn(uploadResult);

        Result<FileUploadVO> response = goodsImageController.upload(file, 100L);

        assertEquals(200, response.getCode());
        assertNotNull(response.getData());
        assertEquals("goods/2026/06/01/100/uuid.png", response.getData().getObjectName());
        assertEquals(uploadResult.getPresignedUrl(), response.getData().getDisplayUrl());
    }

    @Test
    void upload_withoutGoodsId_shouldUseNullBusinessId() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", "img".getBytes());
        UploadResult uploadResult = UploadResult.builder()
                .objectName("goods/2026/06/01/uuid.png")
                .presignedUrl("https://bucket.example.com/goods/2026/06/01/uuid.png?sig=abc")
                .build();
        when(fileStorageService.uploadFile(
                eq(file),
                eq(GoodsImageConstants.CATEGORY),
                isNull(),
                eq(GoodsImageConstants.ALLOWED_CONTENT_TYPES),
                eq(GoodsImageConstants.MAX_SIZE_BYTES)
        )).thenReturn(uploadResult);

        Result<FileUploadVO> response = goodsImageController.upload(file, null);

        assertEquals(200, response.getCode());
        assertTrue(response.getData().getObjectName().startsWith("goods/"));
    }

    @Test
    void upload_shouldPassGoodsImageConstraintsToStorageService() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", "img".getBytes());
        UploadResult uploadResult = UploadResult.builder()
                .objectName("goods/2026/06/01/1/uuid.png")
                .presignedUrl("https://example.com/presigned")
                .build();
        when(fileStorageService.uploadFile(
                eq(file),
                eq(GoodsImageConstants.CATEGORY),
                eq("1"),
                eq(GoodsImageConstants.ALLOWED_CONTENT_TYPES),
                eq(GoodsImageConstants.MAX_SIZE_BYTES)
        )).thenReturn(uploadResult);

        goodsImageController.upload(file, 1L);

        ArgumentCaptor<String> categoryCaptor = ArgumentCaptor.forClass(String.class);
        verify(fileStorageService).uploadFile(
                eq(file),
                categoryCaptor.capture(),
                eq("1"),
                eq(GoodsImageConstants.ALLOWED_CONTENT_TYPES),
                eq(GoodsImageConstants.MAX_SIZE_BYTES)
        );
        assertEquals(GoodsImageConstants.CATEGORY, categoryCaptor.getValue());
    }
}
