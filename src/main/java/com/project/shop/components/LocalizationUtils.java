package com.project.shop.components;

import com.project.shop.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class LocalizationUtils {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    public String getLocalizedMessage(String messageKey, Object... params)
    {
        HttpServletRequest request = WebUtils.getCurrentRequest();
        Locale locale =localeResolver.resolveLocale(request);
        return messageSource.getMessage(messageKey,params,locale);
    }
}
