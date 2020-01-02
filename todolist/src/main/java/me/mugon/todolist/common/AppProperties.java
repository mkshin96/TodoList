package me.mugon.todolist.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app-properties")
@Getter @Setter
public class AppProperties {

    private String testEmail;

    private String testPassword;
}
