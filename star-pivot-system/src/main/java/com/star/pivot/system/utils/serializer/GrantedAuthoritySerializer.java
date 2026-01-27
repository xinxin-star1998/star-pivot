package com.star.pivot.system.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.Collection;

/**
 * GrantedAuthority 集合序列化器
 * 
 * <p>将 GrantedAuthority 集合序列化为字符串数组，用于 Redis 缓存
 * 
 * <p>注意：此序列化器直接序列化为字符串数组，不包含类型信息。
 * 由于 Redis 配置中启用了类型信息处理，Jackson 会调用 serializeWithType 方法，
 * 此序列化器实现了该方法，明确禁用类型信息处理，直接输出字符串数组。
 * 
 * @author stardust
 * @since 2026-01-27
 */
public class GrantedAuthoritySerializer extends JsonSerializer<Collection<? extends GrantedAuthority>> {

    @Override
    public void serialize(Collection<? extends GrantedAuthority> authorities, 
                         JsonGenerator gen, 
                         SerializerProvider serializers) throws IOException {
        if (authorities == null) {
            gen.writeNull();
            return;
        }
        
        // 将权限集合序列化为字符串数组
        // 直接写入数组内容，不包含类型信息
        gen.writeStartArray();
        for (GrantedAuthority authority : authorities) {
            gen.writeString(authority.getAuthority());
        }
        gen.writeEndArray();
    }

    /**
     * 带类型信息的序列化方法
     * 
     * <p>当 Jackson 启用了类型信息处理时，会调用此方法而不是 serialize 方法。
     * 此方法明确禁用类型信息处理，直接调用 serialize 方法，避免类型信息相关的错误。
     * 
     * @param authorities 权限集合
     * @param gen JSON 生成器
     * @param serializers 序列化提供者
     * @param typeSer 类型序列化器（不使用，直接忽略）
     * @throws IOException 序列化异常
     */
    @Override
    public void serializeWithType(Collection<? extends GrantedAuthority> authorities,
                                  JsonGenerator gen,
                                  SerializerProvider serializers,
                                  TypeSerializer typeSer) throws IOException {
        // 直接调用 serialize 方法，不添加类型信息
        // 这样可以避免 Jackson 尝试为 ArrayList 添加类型信息导致的错误
        serialize(authorities, gen, serializers);
    }
}
