package pl.iodkovskaya.leaveRequestSystem.configuration;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.configuration.JobRunrConfiguration;
import org.jobrunr.storage.StorageProvider;
import org.jobrunr.storage.sql.postgres.PostgresStorageProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("!test")
@ConditionalOnProperty(value = "org.jobrunr.background-job-server.enabled", havingValue = "true", matchIfMissing = true)
public class JobRunrConfig {
    @Autowired
    private DataSource dataSource;

    @Bean
    public StorageProvider storageProvider() {
        return new PostgresStorageProvider(dataSource);
    }

    @Bean
    public JobRunrConfiguration.JobRunrConfigurationResult jobRunr() {
        return JobRunr.configure()
                .useStorageProvider(storageProvider())
                .initialize();
    }

}
