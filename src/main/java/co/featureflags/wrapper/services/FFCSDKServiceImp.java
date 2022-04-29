package co.featureflags.wrapper.services;

import co.featureflags.commons.model.AllFlagStates;
import co.featureflags.commons.model.FFCUser;
import co.featureflags.commons.model.FlagState;
import co.featureflags.server.exterior.FFCClient;
import co.featureflags.wrapper.model.Message;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FFCSDKServiceImp implements FFCSDKService, InitializingBean {

    private final static Logger LOG = LoggerFactory.getLogger(FFCSDKServiceImp.class);

    private final static String DEFAULT_VALUE = "NOT FOUND";

    @Value("${ffc.spring.offline}")
    private boolean offline;

    @Value("${ffc.dataFile}")
    private String dataFile;

    private final FFCClient client;

    @Autowired
    public FFCSDKServiceImp(FFCClient client) {
        this.client = client;
    }

    @Override
    public FlagState<String> flagValue(String flagKeyId, FFCUser user) {
        return client.variationDetail(flagKeyId, user, DEFAULT_VALUE);
    }

    @Override
    public AllFlagStates<String> allLatestFlagValues(FFCUser user) {
        return client.getAllLatestFlagsVariations(user);
    }

    @Override
    public Message initializeFromExternalJson(String json) {
        if (!offline) {
            return Message.ERROR("Operation not supported in online mode");
        }
        try {
            client.initializeFromExternalJson(json);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(dataFile, false));
                writer.write(json);
                writer.close();
            } catch (Exception unexpected) {
                LOG.warn("data updating persistence error: {}", unexpected.getMessage());
            }
            return Message.OK("Initialization well done");
        } catch (Exception unexpected) {
            return Message.ERROR(unexpected.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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
    }

}
