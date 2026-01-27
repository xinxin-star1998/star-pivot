package com.star.pivot.system.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.star.pivot.common.security.LoginUserInfo;
import com.star.pivot.system.domain.entity.SysUser;
import com.star.pivot.system.utils.serializer.GrantedAuthorityDeserializer;
import com.star.pivot.system.utils.serializer.GrantedAuthoritySerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

/**
 * 登录用户信息
 *
 * @author stardust
 * @since 2024-01-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails, LoginUserInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户信息
     */
    private SysUser user;

    /**
     * 权限列表
     * 
     * <p>使用自定义序列化器/反序列化器处理 SimpleGrantedAuthority 的序列化问题
     * 序列化时：将权限集合转换为字符串数组
     * 反序列化时：将字符串数组转换为 SimpleGrantedAuthority 集合
     * 
     * <p>自定义序列化器/反序列化器会处理类型信息，兼容新旧数据格式
     */
    @JsonSerialize(using = GrantedAuthoritySerializer.class)
    @JsonDeserialize(using = GrantedAuthorityDeserializer.class)
    private Collection<? extends GrantedAuthority> authorities;

    public LoginUser(SysUser user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.userId = user.getUserId();
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * 账户是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 账户是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 密码是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 账户是否激活
     */
    @Override
    public boolean isEnabled() {
        return "0".equals(user.getStatus());
    }
}

