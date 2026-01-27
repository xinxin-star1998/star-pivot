package com.star.pivot.system.utils.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GrantedAuthority 集合反序列化器
 * 
 * <p>将字符串数组反序列化为 SimpleGrantedAuthority 集合，用于从 Redis 缓存中恢复
 * 
 * <p>支持的数据格式：
 * <ul>
 *   <li>字符串数组（新格式）：["permission1", "permission2"]</li>
 *   <li>带类型信息的数组（旧格式）：["java.util.ArrayList", ["permission1", "permission2"]]</li>
 *   <li>单个字符串："permission1"（向后兼容）</li>
 * </ul>
 * 
 * @author stardust
 * @since 2026-01-27
 */
public class GrantedAuthorityDeserializer extends JsonDeserializer<Collection<? extends GrantedAuthority>> {

    @Override
    public Collection<? extends GrantedAuthority> deserialize(
            JsonParser p, 
            DeserializationContext ctxt) throws IOException {
        
        JsonNode node = p.getCodec().readTree(p);
        
        if (node == null || node.isNull()) {
            return new ArrayList<>();
        }
        
        // 处理数组格式
        if (node.isArray()) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            
            // 检查是否是带类型信息的格式：["java.util.ArrayList", [...]]
            // 或者 ["java.util.ArrayList", "permission1", "permission2", ...]
            if (node.size() >= 2) {
                JsonNode firstNode = node.get(0);
                // 如果第一个元素是字符串且看起来像类名（包含点号），则可能是类型信息
                if (firstNode.isTextual()) {
                    String firstText = firstNode.asText();
                    // 检查是否是 Java 类名格式（包含包路径分隔符）
                    if (firstText.contains(".") && !firstText.contains(":") && !firstText.contains("/")) {
                        // 可能是类型信息，跳过第一个元素，处理后续元素
                        for (int i = 1; i < node.size(); i++) {
                            JsonNode itemNode = node.get(i);
                            if (itemNode.isArray()) {
                                // 如果第二个元素是数组，则递归处理该数组
                                return parseAuthorityArray(itemNode);
                            } else if (itemNode.isTextual()) {
                                // 如果后续元素是字符串，直接处理
                                String authority = itemNode.asText();
                                if (authority != null && !authority.trim().isEmpty()) {
                                    authorities.add(new SimpleGrantedAuthority(authority));
                                }
                            }
                        }
                        return authorities;
                    }
                }
            }
            
            // 标准数组格式：["permission1", "permission2", ...]
            return parseAuthorityArray(node);
        }
        
        // 处理单个字符串（向后兼容）
        if (node.isTextual()) {
            String authority = node.asText();
            // 检查是否是类型信息（类名格式）
            if (authority.contains(".") && !authority.contains(":") && !authority.contains("/")) {
                // 可能是类型信息，返回空集合
                return new ArrayList<>();
            }
            if (authority != null && !authority.trim().isEmpty()) {
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(authority));
                return authorities;
            }
        }
        
        // 如果无法解析，返回空集合（避免抛出异常）
        return new ArrayList<>();
    }
    
    /**
     * 带类型信息的反序列化方法
     * 
     * <p>当 Jackson 启用了类型信息处理时，会调用此方法而不是 deserialize 方法。
     * 此方法会忽略类型信息，直接调用 deserialize 方法进行反序列化。
     * 
     * @param p JSON 解析器
     * @param ctxt 反序列化上下文
     * @param typeDeserializer 类型反序列化器（不使用，直接忽略）
     * @return 权限集合
     * @throws IOException 反序列化异常
     */
    @Override
    public Collection<? extends GrantedAuthority> deserializeWithType(
            JsonParser p,
            DeserializationContext ctxt,
            TypeDeserializer typeDeserializer) throws IOException {
        // 直接调用 deserialize 方法，忽略类型信息处理
        // 这样可以避免 Jackson 尝试解析类型信息导致的错误
        return deserialize(p, ctxt);
    }
    
    /**
     * 解析权限数组
     * 
     * @param arrayNode 数组节点
     * @return 权限集合
     */
    private List<GrantedAuthority> parseAuthorityArray(JsonNode arrayNode) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (JsonNode authorityNode : arrayNode) {
            if (authorityNode.isTextual()) {
                String authority = authorityNode.asText();
                if (authority != null && !authority.trim().isEmpty()) {
                    // 判断是否是类型信息（Java 类名格式）
                    // 类型信息通常是完整的类名，包含包路径，如 "java.util.ArrayList"
                    // 权限字符串通常包含冒号，如 "system:menu:list"，或者不包含点号
                    boolean isTypeInfo = authority.contains(".") 
                        && !authority.contains(":") 
                        && !authority.contains("/")
                        && (authority.startsWith("java.") 
                            || authority.startsWith("com.") 
                            || authority.startsWith("org.") 
                            || authority.startsWith("net."));
                    
                    if (!isTypeInfo) {
                        authorities.add(new SimpleGrantedAuthority(authority));
                    }
                }
            }
        }
        return authorities;
    }
}
