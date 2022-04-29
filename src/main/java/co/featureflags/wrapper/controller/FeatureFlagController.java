package co.featureflags.wrapper.controller;

import co.featureflags.commons.model.AllFlagStates;
import co.featureflags.commons.model.FlagState;
import co.featureflags.commons.model.VariationParams;
import co.featureflags.wrapper.model.Message;
import co.featureflags.wrapper.services.FFCSDKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/feature-flag")
public class FeatureFlagController {

    private final static Logger LOG = LoggerFactory.getLogger(FeatureFlagController.class);

    private final FFCSDKService sdkService;

    @Value("${ffc.spring.env-secret}")
    private String envSecret;

    private final static String INVALID_REQUEST = "Unauthorized";
    private final static Message INVALID_MSG = Message.ERROR("Unauthorized");

    @Autowired
    public FeatureFlagController(FFCSDKService sdkService) {
        this.sdkService = sdkService;
    }

    @PostMapping(value = "/variation")
    public ResponseEntity<FlagState<String>> variation(@RequestHeader("envSecret") String envSecret, @RequestBody VariationParams params) {
        if (!this.envSecret.equals(envSecret)) {
            LOG.warn("env secret not match, received {}", envSecret);
            return new ResponseEntity<>(FlagState.Empty(INVALID_REQUEST), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(sdkService.flagValue(params.getFeatureFlagKeyName(), params.getUser()),
                HttpStatus.OK);
    }

    @PostMapping(value = "/variations")
    public ResponseEntity<AllFlagStates<String>> variations(@RequestHeader("envSecret") String envSecret, @RequestBody VariationParams params) {
        if (!this.envSecret.equals(envSecret)) {
            LOG.warn("env secret not match, received {}", envSecret);
            return new ResponseEntity<>(AllFlagStates.empty(INVALID_REQUEST), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(sdkService.allLatestFlagValues(params.getUser()), HttpStatus.OK);
    }

    @PostMapping(value = "/init")
    public ResponseEntity<Message> init(@RequestHeader("envSecret") String envSecret, @RequestBody String json) {
        if (!this.envSecret.equals(envSecret)) {
            LOG.warn("env secret not match, received {}", envSecret);
            return new ResponseEntity<>(INVALID_MSG, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(sdkService.initializeFromExternalJson(json), HttpStatus.OK);
    }

}
