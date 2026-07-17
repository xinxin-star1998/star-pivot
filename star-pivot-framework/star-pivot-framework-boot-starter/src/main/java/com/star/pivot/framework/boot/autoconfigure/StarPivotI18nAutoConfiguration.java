package com.star.pivot.framework.boot.autoconfigure;

import com.star.pivot.framework.utils.MessageUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.Locale;

/**
 * 后端接口文案国际化：MessageSource + X-Lang Locale
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class StarPivotI18nAutoConfiguration {

    public static final String HEADER_X_LANG = "X-Lang";

    @Bean
    @ConditionalOnMissingBean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:i18n/messages");
        source.setDefaultEncoding("UTF-8");
        source.setFallbackToSystemLocale(false);
        source.setCacheSeconds(3600);
        MessageUtils.setMessageSource(source);
        return source;
    }

    @Bean
    @ConditionalOnMissingBean(LocaleResolver.class)
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return resolver;
    }

    @Bean
    public OncePerRequestFilter localeContextFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain) throws ServletException, IOException {
                Locale previous = LocaleContextHolder.getLocale();
                try {
                    LocaleContextHolder.setLocale(resolveLocale(request));
                    filterChain.doFilter(request, response);
                } finally {
                    LocaleContextHolder.setLocale(previous);
                }
            }

            private Locale resolveLocale(HttpServletRequest request) {
                String xLang = request.getHeader(HEADER_X_LANG);
                if (StringUtils.hasText(xLang)) {
                    return toLocale(xLang.trim());
                }
                String accept = request.getHeader("Accept-Language");
                if (StringUtils.hasText(accept)) {
                    String first = accept.split(",")[0].trim();
                    if (StringUtils.hasText(first)) {
                        return toLocale(first);
                    }
                }
                return Locale.SIMPLIFIED_CHINESE;
            }

            private Locale toLocale(String lang) {
                String value = lang.replace('_', '-');
                String primary = value;
                int idx = value.indexOf('-');
                if (idx > 0) {
                    primary = value.substring(0, idx);
                }
                primary = primary.toLowerCase(Locale.ROOT);
                return switch (primary) {
                    case "en" -> Locale.ENGLISH;
                    case "zh" -> {
                        if (value.toLowerCase(Locale.ROOT).contains("tw")
                                || value.toLowerCase(Locale.ROOT).contains("hk")) {
                            yield Locale.TRADITIONAL_CHINESE;
                        }
                        yield Locale.SIMPLIFIED_CHINESE;
                    }
                    case "ja" -> Locale.JAPANESE;
                    case "ko" -> Locale.KOREAN;
                    default -> Locale.forLanguageTag(value);
                };
            }
        };
    }
}
