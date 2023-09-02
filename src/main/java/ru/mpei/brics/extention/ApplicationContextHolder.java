package ru.mpei.brics.extention;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApplicationContextHolder {

    @Getter
    private static ApplicationContext context;
    public ApplicationContextHolder() {
        log.info("Application context holder created");
    }

    @Autowired
    public ApplicationContextHolder(ApplicationContext applicationContext) {
        ApplicationContextHolder.context = applicationContext;
    }
}
