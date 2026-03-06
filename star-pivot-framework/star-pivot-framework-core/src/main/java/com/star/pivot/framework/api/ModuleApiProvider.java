package com.star.pivot.framework.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModuleApiProvider {

    private OperLogApi operLogApi;

    public Optional<OperLogApi> getOperLogApi() {
        return Optional.ofNullable(operLogApi);
    }

    @Autowired(required = false)
    public void setOperLogApi(OperLogApi operLogApi) {
        this.operLogApi = operLogApi;
    }
}
