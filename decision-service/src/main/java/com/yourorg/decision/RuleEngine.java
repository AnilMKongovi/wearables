package com.yourorg.decision;

import org.springframework.stereotype.Service;

@Service
public class RuleEngine {

    public String evaluate(int heartRate) {
        if (heartRate > 120) {
            return "ALERT_HIGH_HEART_RATE";
        }
        return "NORMAL";
    }
}
