package org.traffichunter.plugin.spring.autoconfigure.rest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.traffichunter.plugin.spring.insrumentation.SpringWebInstrumentation;

import java.util.List;

/**
 * @author JuSeong
 * @version 1.1.0
 */
final class RestTemplateBeanPostProcessor implements BeanPostProcessor {


    RestTemplateBeanPostProcessor() {
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof RestTemplate restTemplate) {

            ClientHttpRequestInterceptor instrumentationInterceptor = SpringWebInstrumentation.restTemplateInstance();
            List<ClientHttpRequestInterceptor> restTemplateInterceptors = restTemplate.getInterceptors();

            if (restTemplateInterceptors.stream()
                    .noneMatch(interceptor -> interceptor.getClass() == instrumentationInterceptor.getClass()))
                restTemplateInterceptors.add(instrumentationInterceptor);

            return restTemplate;
        }
        return bean;

    }
}
