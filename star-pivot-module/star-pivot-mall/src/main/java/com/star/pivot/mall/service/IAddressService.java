package com.star.pivot.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.star.pivot.mall.domain.bo.AddressVO;
import com.star.pivot.mall.domain.dto.AddressDTO;
import com.star.pivot.mall.domain.dto.AddressQueryDTO;
import com.star.pivot.mall.domain.entity.Address;

import java.util.List;

/**
 * 省市区地址服务
 */
public interface IAddressService extends IService<Address> {

    /** 懒加载：查询某父级下的直接子节点 */
    List<AddressVO> listChildren(String parentCode);

    /** 搜索（扁平列表，最多 200 条） */
    List<AddressVO> searchAddress(AddressQueryDTO queryDTO);

    AddressVO selectAddressById(Long id);

    void insertAddress(AddressDTO addressDTO);

    void updateAddress(AddressDTO addressDTO);

    void deleteAddressByIds(List<Long> ids);
}
