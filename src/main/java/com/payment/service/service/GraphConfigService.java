package com.payment.service.service;

import com.payment.service.model.Branch;
import com.payment.service.model.PaymentGraph;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GraphConfigService {
    private final PaymentGraph paymentGraph;

    @Value("#{${branch.costs}}")
    private Map<String, Integer> branchCosts;

    @Value("#{${branch.connections}}")
    private Map<String, String> branchConnections;

    public GraphConfigService(PaymentGraph paymentGraph) {
        this.paymentGraph = paymentGraph;
    }

    @PostConstruct
    public void setupGraph() {
        branchCosts.forEach((name, cost) -> paymentGraph.addBranch(new Branch(name, cost)));
        branchConnections.forEach((origin, destinations) -> {
            String[] destArray = destinations.split(",");
            for (String dest : destArray) {
                paymentGraph.addConnection(origin.trim(), dest.trim());
            }
        });
    }
}