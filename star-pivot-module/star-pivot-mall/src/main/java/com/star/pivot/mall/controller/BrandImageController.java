package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.FileUploadVO;
import com.star.pivot.mall.support.BrandImageConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商城品牌 Logo 上传
 */
@Slf4j
@RestController
@RequestMapping("/mall/brand/image")
@RequiredArgsConstructor
@Tag(name = "商城-品牌图片", description = "品牌 Logo 上传")
public class BrandImageController {

    private final FileStorageService fileStorageService;

    /**
     * 上传品牌 Logo
     *
     * @param file    图片文件
     * @param brandId 品牌 ID（编辑时可传，用于分目录存储）
     */
    @Log(title = "品牌Logo上传", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "品牌Logo上传")
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('mall:brand:edit')")
    public Result<FileUploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "brandId", required = false) Long brandId) throws Exception {
        String businessId = brandId != null ? brandId.toString() : null;
        FileUploadVO vo = FileUploadVO.from(fileStorageService.uploadFile(
                file,
                BrandImageConstants.CATEGORY,
                businessId,
                BrandImageConstants.ALLOWED_CONTENT_TYPES,
                BrandImageConstants.MAX_SIZE_BYTES));
        log.info("品牌Logo上传成功，brandId={}, objectName={}", brandId, vo.getObjectName());
        return Result.success("上传成功", vo);
    }
}
