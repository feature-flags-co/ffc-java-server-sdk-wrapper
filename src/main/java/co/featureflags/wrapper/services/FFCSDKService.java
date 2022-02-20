package co.featureflags.wrapper.services;


import co.featureflags.commons.model.EvalDetail;
import co.featureflags.commons.model.FFCUser;
import co.featureflags.wrapper.model.Message;

public interface FFCSDKService {
    EvalDetail<String> variationDetail(String flagKeyId, FFCUser user, String defaultValue);

    Message initializeFromExternalJson(String json);
}
