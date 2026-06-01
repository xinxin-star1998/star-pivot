package com.star.pivot.mall.controller;

import com.star.pivot.framework.annotation.Log;
import com.star.pivot.framework.domain.AppConstants;
import com.star.pivot.framework.domain.Result;
import com.star.pivot.framework.storage.FileStorageService;
import com.star.pivot.framework.storage.FileUploadVO;
import com.star.pivot.mall.support.GoodsImageConstants;
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
 * 商城商品图片上传
 */
@Slf4j
@RestController
@RequestMapping("/mall/goods/image")
@RequiredArgsConstructor
@Tag(name = "商城-商品图片", description = "商品 SPU/SKU 图片上传")
public class GoodsImageController {

    private final FileStorageService fileStorageService;

    /**
     * 上传商品图片
     *
     * @param file    图片文件
     * @param goodsId 商品 ID（编辑时可传，用于分目录存储）
     */
    @Log(title = "商品图片上传", businessType = AppConstants.BusinessType.INSERT)
    @Operation(summary = "商品图片上传")
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('mall:product:edit')")
    public Result<FileUploadVO> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "goodsId", required = false) Long goodsId) throws Exception {
        String businessId = goodsId != null ? goodsId.toString() : null;
        FileUploadVO vo = FileUploadVO.from(fileStorageService.uploadFile(
                file,
                GoodsImageConstants.CATEGORY,
                businessId,
                GoodsImageConstants.ALLOWED_CONTENT_TYPES,
                GoodsImageConstants.MAX_SIZE_BYTES));
        log.info("商品图片上传成功，goodsId={}, objectName={}", goodsId, vo.getObjectName());
        return Result.success("上传成功", vo);
    }
}
