package pl.iodkovskaya.leaveRequestSystem.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronConfig {
    @Bean
    public String vacationIncrementCron(@Value("${vacation.increment.cron}") String cronExpression) {
        return cronExpression;
    }
}
