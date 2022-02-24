package co.featureflags.wrapper.services;


import co.featureflags.commons.model.AllFlagStates;
import co.featureflags.commons.model.EvalDetail;
import co.featureflags.commons.model.FFCUser;
import co.featureflags.commons.model.FlagState;
import co.featureflags.wrapper.model.Message;

import java.util.List;

public interface FFCSDKService {
    FlagState<String> flagValue(String flagKeyId, FFCUser user);

    AllFlagStates<String> allLatestFlagValues(FFCUser user);

    Message initializeFromExternalJson(String json);
}
