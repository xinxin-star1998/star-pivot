package com.star.pivot.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.star.pivot.file.constant.FileBizConstants;
import com.star.pivot.file.constant.FileCategory;
import com.star.pivot.file.domain.dto.SysFileFolderDTO;
import com.star.pivot.file.domain.entity.SysFileFolder;
import com.star.pivot.file.domain.vo.FileCategoryNodeVo;
import com.star.pivot.file.domain.vo.SysFileFolderVo;
import com.star.pivot.file.mapper.SysFileFolderMapper;
import com.star.pivot.file.mapper.SysFileMapper;
import com.star.pivot.file.service.ISysFileFolderService;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.framework.utils.validation.AssertUtils;
import com.star.pivot.security.context.SecurityContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysFileFolderServiceImpl extends ServiceImpl<SysFileFolderMapper, SysFileFolder>
        implements ISysFileFolderService {

    private final SysFileMapper sysFileMapper;

    @Override
    public List<FileCategoryNodeVo> listTree(String category) {
        List<FileCategory> categories = StringUtils.hasText(category)
                ? List.of(FileCategory.of(category))
                : Arrays.asList(FileCategory.values());

        List<FileCategoryNodeVo> nodes = new ArrayList<>();
        for (FileCategory cat : categories) {
            FileCategoryNodeVo node = new FileCategoryNodeVo();
            node.setCategory(cat.getCode());
            node.setCategoryLabel(cat.getLabel());
            node.setDefaultFolderId(cat.getDefaultFolderId());

            List<SysFileFolder> folders = list(new LambdaQueryWrapper<SysFileFolder>()
                    .eq(SysFileFolder::getCategory, cat.getCode())
                    .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL)
                    .orderByAsc(SysFileFolder::getOrderNum)
                    .orderByAsc(SysFileFolder::getFolderId));

            List<SysFileFolderVo> children = folders.stream()
                    .map(this::toFolderVo)
                    .sorted(Comparator.comparing(SysFileFolderVo::getOrderNum, Comparator.nullsLast(Integer::compareTo)))
                    .collect(Collectors.toList());
            node.setChildren(children);
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysFileFolderDTO dto) {
        FileCategory.of(dto.getCategory());
        if (dto.getFolderName() == null || dto.getFolderName().isBlank()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件夹名称不能为空");
        }

        long exists = count(new LambdaQueryWrapper<SysFileFolder>()
                .eq(SysFileFolder::getCategory, dto.getCategory())
                .eq(SysFileFolder::getFolderName, dto.getFolderName())
                .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
        if (exists > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "同分类下文件夹名称已存在");
        }

        SysFileFolder folder = new SysFileFolder();
        folder.setCategory(dto.getCategory());
        folder.setFolderName(dto.getFolderName());
        folder.setParentId(0L);
        folder.setOrderNum(dto.getOrderNum() != null ? dto.getOrderNum() : 0);
        folder.setStatus(StringUtils.hasText(dto.getStatus()) ? dto.getStatus() : "0");
        folder.setDelFlag(FileBizConstants.DEL_FLAG_NORMAL);
        folder.setRemark(dto.getRemark());
        folder.setCreateBy(SecurityContextUtils.getUsername());
        folder.setCreateTime(LocalDateTime.now());
        save(folder);
        return folder.getFolderId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysFileFolderDTO dto) {
        AssertUtils.notNull(dto.getFolderId(), ErrorCode.PARAM_INVALID, "文件夹ID不能为空");
        SysFileFolder folder = getActiveFolder(dto.getFolderId());
        AssertUtils.notNull(folder, ErrorCode.NOT_FOUND, "文件夹不存在");

        if (StringUtils.hasText(dto.getFolderName()) && !dto.getFolderName().equals(folder.getFolderName())) {
            long exists = count(new LambdaQueryWrapper<SysFileFolder>()
                    .eq(SysFileFolder::getCategory, folder.getCategory())
                    .eq(SysFileFolder::getFolderName, dto.getFolderName())
                    .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL)
                    .ne(SysFileFolder::getFolderId, dto.getFolderId()));
            if (exists > 0) {
                throw new BizException(ErrorCode.PARAM_INVALID, "同分类下文件夹名称已存在");
            }
            folder.setFolderName(dto.getFolderName());
        }
        if (dto.getOrderNum() != null) {
            folder.setOrderNum(dto.getOrderNum());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            folder.setStatus(dto.getStatus());
        }
        if (dto.getRemark() != null) {
            folder.setRemark(dto.getRemark());
        }
        folder.setUpdateBy(SecurityContextUtils.getUsername());
        folder.setUpdateTime(LocalDateTime.now());
        updateById(folder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long folderId) {
        SysFileFolder folder = getActiveFolder(folderId);
        AssertUtils.notNull(folder, ErrorCode.NOT_FOUND, "文件夹不存在");
        if (FileBizConstants.DEFAULT_FOLDER_NAME.equals(folder.getFolderName())) {
            throw new BizException(ErrorCode.PARAM_INVALID, "默认文件夹不可删除");
        }
        long fileCount = sysFileMapper.countActiveByFolderId(folderId);
        if (fileCount > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "文件夹下存在文件，无法删除");
        }
        update(new LambdaUpdateWrapper<SysFileFolder>()
                .eq(SysFileFolder::getFolderId, folderId)
                .set(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_RECYCLE)
                .set(SysFileFolder::getUpdateBy, SecurityContextUtils.getUsername())
                .set(SysFileFolder::getUpdateTime, LocalDateTime.now()));
    }

    private SysFileFolder getActiveFolder(Long folderId) {
        if (folderId == null) {
            return null;
        }
        return getOne(new LambdaQueryWrapper<SysFileFolder>()
                .eq(SysFileFolder::getFolderId, folderId)
                .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
    }

    private SysFileFolderVo toFolderVo(SysFileFolder folder) {
        SysFileFolderVo vo = new SysFileFolderVo();
        BeanUtils.copyProperties(folder, vo);
        vo.setFileCount(sysFileMapper.countActiveByFolderId(folder.getFolderId()));
        return vo;
    }
}
