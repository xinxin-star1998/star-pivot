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
import java.util.*;
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

            node.setChildren(buildFolderTree(folders));
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

        Long parentId = normalizeParentId(dto.getParentId());
        if (parentId > 0) {
            SysFileFolder parent = getActiveFolder(parentId);
            AssertUtils.notNull(parent, ErrorCode.NOT_FOUND, "父文件夹不存在");
            if (!dto.getCategory().equals(parent.getCategory())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "父文件夹与业务分类不一致");
            }
        }

        assertUniqueName(dto.getCategory(), parentId, dto.getFolderName(), null);

        SysFileFolder folder = new SysFileFolder();
        folder.setCategory(dto.getCategory());
        folder.setFolderName(dto.getFolderName().trim());
        folder.setParentId(parentId);
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

        Long parentId = folder.getParentId() == null ? 0L : folder.getParentId();
        if (dto.getParentId() != null) {
            parentId = normalizeParentId(dto.getParentId());
            if (parentId.equals(dto.getFolderId())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "不能将文件夹设为自己的子级");
            }
            if (parentId > 0) {
                SysFileFolder parent = getActiveFolder(parentId);
                AssertUtils.notNull(parent, ErrorCode.NOT_FOUND, "父文件夹不存在");
                if (!folder.getCategory().equals(parent.getCategory())) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "父文件夹与业务分类不一致");
                }
                if (isDescendant(dto.getFolderId(), parentId)) {
                    throw new BizException(ErrorCode.PARAM_INVALID, "不能移动到自己的子文件夹下");
                }
            }
            folder.setParentId(parentId);
        }

        if (StringUtils.hasText(dto.getFolderName()) && !dto.getFolderName().equals(folder.getFolderName())) {
            assertUniqueName(folder.getCategory(), parentId, dto.getFolderName(), dto.getFolderId());
            folder.setFolderName(dto.getFolderName().trim());
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
        if (FileBizConstants.DEFAULT_FOLDER_NAME.equals(folder.getFolderName())
                && (folder.getParentId() == null || folder.getParentId() == 0L)) {
            throw new BizException(ErrorCode.PARAM_INVALID, "默认文件夹不可删除");
        }
        long childCount = count(new LambdaQueryWrapper<SysFileFolder>()
                .eq(SysFileFolder::getParentId, folderId)
                .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL));
        if (childCount > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "请先删除子文件夹");
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

    private List<SysFileFolderVo> buildFolderTree(List<SysFileFolder> folders) {
        Map<Long, SysFileFolderVo> map = new LinkedHashMap<>();
        for (SysFileFolder folder : folders) {
            map.put(folder.getFolderId(), toFolderVo(folder));
        }
        List<SysFileFolderVo> roots = new ArrayList<>();
        for (SysFileFolderVo vo : map.values()) {
            Long parentId = vo.getParentId() == null ? 0L : vo.getParentId();
            if (parentId == 0L || !map.containsKey(parentId)) {
                roots.add(vo);
            } else {
                map.get(parentId).getChildren().add(vo);
            }
        }
        sortTree(roots);
        return roots;
    }

    private void sortTree(List<SysFileFolderVo> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        nodes.sort(Comparator.comparing(SysFileFolderVo::getOrderNum, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(SysFileFolderVo::getFolderId, Comparator.nullsLast(Long::compareTo)));
        for (SysFileFolderVo node : nodes) {
            sortTree(node.getChildren());
        }
    }

    private void assertUniqueName(String category, Long parentId, String folderName, Long excludeId) {
        LambdaQueryWrapper<SysFileFolder> wrapper = new LambdaQueryWrapper<SysFileFolder>()
                .eq(SysFileFolder::getCategory, category)
                .eq(SysFileFolder::getParentId, parentId)
                .eq(SysFileFolder::getFolderName, folderName.trim())
                .eq(SysFileFolder::getDelFlag, FileBizConstants.DEL_FLAG_NORMAL);
        if (excludeId != null) {
            wrapper.ne(SysFileFolder::getFolderId, excludeId);
        }
        if (count(wrapper) > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "同级目录下文件夹名称已存在");
        }
    }

    /** 判断 candidateId 是否是 ancestorId 的子孙 */
    private boolean isDescendant(Long ancestorId, Long candidateId) {
        Long current = candidateId;
        Set<Long> visited = new HashSet<>();
        while (current != null && current > 0 && visited.add(current)) {
            if (current.equals(ancestorId)) {
                return true;
            }
            SysFileFolder folder = getActiveFolder(current);
            if (folder == null) {
                return false;
            }
            current = folder.getParentId();
        }
        return false;
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null || parentId < 0 ? 0L : parentId;
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
        vo.setChildren(new ArrayList<>());
        vo.setFileCount(sysFileMapper.countActiveByFolderId(folder.getFolderId()));
        return vo;
    }
}
