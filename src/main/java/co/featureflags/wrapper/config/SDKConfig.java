package co.featureflags.wrapper.config;

import co.featureflags.server.FFCClientImp;
import co.featureflags.server.FFCConfig;
import co.featureflags.server.exterior.FFCClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@PropertySource("classpath:sdk.properties")
public class SDKConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SDKConfig.class);

    @Value("${envSecret}")
    private String envSecret;
    @Value("${offline}")
    private boolean offline;
    @Value("${dataFile}")
    private String dataFile;

    @Bean(destroyMethod = "close")
    public FFCClient client() {
        return getClient(offline);
    }

    protected FFCClient getClient(boolean offline) {
        FFCConfig config = new FFCConfig.Builder()
                .offline(offline)
                .build();
        FFCClient client = new FFCClientImp(envSecret, config);
        if (offline) {
            String json = null;
            Path path = Paths.get(dataFile);
            if (Files.isRegularFile(path)) {
                try {
                    json = Files.lines(path)
                            .reduce((s, line) -> s.concat(line))
                            .orElse(null);
                    if (StringUtils.isNotEmpty(json)) {
                        client.initializeFromExternalJson(json);
                        LOG.info("initializing from a file works well");
                    }
                } catch (IOException unexpected) {
                    LOG.warn("initializing from a file didn't work well: {}", unexpected.getMessage());
                }
            }
        }
        return client;
    }


}
