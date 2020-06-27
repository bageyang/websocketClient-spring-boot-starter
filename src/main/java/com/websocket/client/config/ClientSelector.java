package com.websocket.client.config;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * TODO neverUsedClass
 * @author yang
 */
@Component
public class ClientSelector implements ImportSelector, Ordered {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[0];
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
