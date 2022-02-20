package co.featureflags.wrapper.services;

import co.featureflags.commons.model.EvalDetail;
import co.featureflags.commons.model.FFCUser;
import co.featureflags.server.exterior.FFCClient;
import co.featureflags.wrapper.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;

@Service
@PropertySource("classpath:sdk.properties")
public class FFCSDKServiceImp implements FFCSDKService {

    private final static Logger LOG = LoggerFactory.getLogger(FFCSDKServiceImp.class);

    @Value("${offline}")
    private boolean offline;

    @Value("${dataFile}")
    private String dataFile;

    private final FFCClient client;

    @Autowired
    public FFCSDKServiceImp(FFCClient client) {
        this.client = client;
    }

    @Override
    public EvalDetail<String> variationDetail(String flagKeyId, FFCUser user, String defaultValue) {
        return client.variationDetail(flagKeyId, user, defaultValue);
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
}
