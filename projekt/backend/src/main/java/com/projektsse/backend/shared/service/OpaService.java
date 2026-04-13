package com.projektsse.backend.shared.service;

import com.projektsse.backend.shared.exceptions.OpaEvaluationException;
import com.projektsse.backend.shared.models.opa.OpaInput;
import io.github.open_policy_agent.opa.OPAClient;
import io.github.open_policy_agent.opa.OPAException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpaService {
    private final OPAClient opaClient;

    public OpaService(@Value("${opa.url}") String opaURL) {
        opaClient = new OPAClient(opaURL);
    }

    public boolean check(String policy, OpaInput input) {
        try {
            return opaClient.check(policy, input);
        } catch (OPAException e) {
            throw new OpaEvaluationException(e);
        }
    }
}
