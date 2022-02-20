package co.featureflags.wrapper.controller;

import co.featureflags.commons.model.EvalDetail;
import co.featureflags.commons.model.VariationParams;
import co.featureflags.wrapper.model.Message;
import co.featureflags.wrapper.services.FFCSDKService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeatureFlagController {

    private final FFCSDKService sdkService;

    @Autowired
    public FeatureFlagController(FFCSDKService sdkService) {
        this.sdkService = sdkService;
    }

    @PostMapping(value = "/api/variation")
    public EvalDetail<String> variationDetail(@RequestBody VariationParams params) {
        return sdkService.variationDetail(params.getFeatureFlagKeyName(), params.getUser(), "NOT_FOUND");
    }

    @PostMapping(value = "/api/init")
    public ResponseEntity<Message> variationDetail(@RequestBody String json) {
        Message msg = sdkService.initializeFromExternalJson(json);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

}
