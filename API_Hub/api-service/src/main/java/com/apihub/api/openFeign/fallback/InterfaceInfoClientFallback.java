package com.apihub.api.openFeign.fallback;

import com.apihub.api.model.domain.InterfaceInfo;
import com.apihub.api.openFeign.client.InterfaceInfoServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class InterfaceInfoClientFallback implements FallbackFactory<InterfaceInfoServiceClient> {

    @Override
    public InterfaceInfoServiceClient create(Throwable cause) {
        return new InterfaceInfoServiceClient() {
            @Override
            public InterfaceInfo queryItemById(long id) {
                InterfaceInfo interfaceInfo = new InterfaceInfo();
                interfaceInfo.setId(0L);
                return interfaceInfo;
            }
        };
    }
}
