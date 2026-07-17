package com.star.pivot.framework.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModuleApiProvider {

    private OperLogApi operLogApi;

    private I18nApi i18nApi;

    public Optional<OperLogApi> getOperLogApi() {
        return Optional.ofNullable(operLogApi);
    }

    public Optional<I18nApi> getI18nApi() {
        return Optional.ofNullable(i18nApi);
    }

    @Autowired(required = false)
    public void setOperLogApi(OperLogApi operLogApi) {
        this.operLogApi = operLogApi;
    }

    @Autowired(required = false)
    public void setI18nApi(I18nApi i18nApi) {
        this.i18nApi = i18nApi;
    }
}
