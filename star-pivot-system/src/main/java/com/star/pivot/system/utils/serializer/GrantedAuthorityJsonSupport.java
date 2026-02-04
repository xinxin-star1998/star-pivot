package com.star.pivot.system.utils.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GrantedAuthority 集合的 JSON 序列化/反序列化（集中定义，供 LoginUser 等使用）
 * <p>序列化：集合 → 字符串数组；反序列化：字符串数组/单字符串 → SimpleGrantedAuthority 集合，兼容 Redis 缓存格式
 */
public final class GrantedAuthorityJsonSupport {

    private GrantedAuthorityJsonSupport() {}

    /** 序列化器：GrantedAuthority 集合 → 字符串数组，不写类型信息 */
    public static class Serializer extends JsonSerializer<Collection<? extends GrantedAuthority>> {
        @Override
        public void serialize(Collection<? extends GrantedAuthority> authorities,
                              JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (authorities == null) {
                gen.writeNull();
                return;
            }
            gen.writeStartArray();
            for (GrantedAuthority a : authorities) {
                gen.writeString(a.getAuthority());
            }
            gen.writeEndArray();
        }

        @Override
        public void serializeWithType(Collection<? extends GrantedAuthority> authorities,
                                     JsonGenerator gen, SerializerProvider serializers,
                                     TypeSerializer typeSer) throws IOException {
            serialize(authorities, gen, serializers);
        }
    }

    /** 反序列化器：字符串数组或单字符串 → SimpleGrantedAuthority 集合，兼容带类型信息的旧格式 */
    public static class Deserializer extends JsonDeserializer<Collection<? extends GrantedAuthority>> {
        @Override
        public Collection<? extends GrantedAuthority> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            if (node == null || node.isNull()) return new ArrayList<>();

            if (node.isArray()) {
                if (node.size() >= 2) {
                    JsonNode first = node.get(0);
                    if (first.isTextual()) {
                        String firstText = first.asText();
                        if (firstText.contains(".") && !firstText.contains(":") && !firstText.contains("/")) {
                            JsonNode second = node.get(1);
                            if (second.isArray()) return parseAuthorityArray(second);
                            List<GrantedAuthority> list = new ArrayList<>();
                            for (int i = 1; i < node.size(); i++) {
                                JsonNode item = node.get(i);
                                if (item.isTextual()) {
                                    String s = item.asText();
                                    if (s != null && !s.trim().isEmpty())
                                        list.add(new SimpleGrantedAuthority(s));
                                }
                            }
                            return list;
                        }
                    }
                }
                return parseAuthorityArray(node);
            }

            if (node.isTextual()) {
                String s = node.asText();
                if (s != null && !s.trim().isEmpty()
                        && !(s.contains(".") && !s.contains(":") && !s.contains("/"))) {
                    List<GrantedAuthority> list = new ArrayList<>();
                    list.add(new SimpleGrantedAuthority(s));
                    return list;
                }
            }
            return new ArrayList<>();
        }

        @Override
        public Collection<? extends GrantedAuthority> deserializeWithType(JsonParser p, DeserializationContext ctxt,
                                                                           TypeDeserializer typeDeserializer) throws IOException {
            return deserialize(p, ctxt);
        }

        private List<GrantedAuthority> parseAuthorityArray(JsonNode arrayNode) {
            List<GrantedAuthority> list = new ArrayList<>();
            for (JsonNode n : arrayNode) {
                if (n.isTextual()) {
                    String s = n.asText();
                    if (s != null && !s.trim().isEmpty()) {
                        boolean isTypeInfo = s.contains(".") && !s.contains(":") && !s.contains("/")
                                && (s.startsWith("java.") || s.startsWith("com.") || s.startsWith("org.") || s.startsWith("net."));
                        if (!isTypeInfo) list.add(new SimpleGrantedAuthority(s));
                    }
                }
            }
            return list;
        }
    }
}
