package com.payment.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "branch")
public class PaymentConfigProperties {

    private Map<String, Integer> costs;
    private Map<String, String> connections;

    public Map<String, Integer> getCosts() {
        return costs;
    }

    public void setCosts(Map<String, Integer> costs) {
        this.costs = costs;
    }

    public Map<String, String> getConnections() {
        return connections;
    }

    public void setConnections(Map<String, String> connections) {
        this.connections = connections;
    }
}