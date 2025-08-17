package com.payment.service.service;

import com.payment.service.model.Branch;
import com.payment.service.model.PaymentGraph;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

public class GraphConfigService {
    // The shared graph instance that will be populated with branches and connections.
    private final PaymentGraph paymentGraph;

    // Injects a Map of branch names to their costs from the application.properties file.
    // The SpEL expression "#{${branch.costs}}" is used to parse the string value into a Map.
    @Value("#{${branch.costs}}")
    private Map<String, Integer> branchCosts;

    // Injects a Map of branch names to their connections as a comma-separated string.
    // The SpEL expression "#{${branch.connections}}" is used for parsing.
    @Value("#{${branch.connections}}")
    private Map<String, String> branchConnections;

    public GraphConfigService(PaymentGraph paymentGraph) {
        this.paymentGraph = paymentGraph;
    }

    /**
     * Initializes the payment graph by populating it with branches and connections.
     * This method is automatically called by Spring after the bean has been constructed
     * and all its dependencies (@Value, @Autowired) have been injected.
     */
    @PostConstruct
    public void setupGraph() {
        // Step 1: Add branches to the graph.
        // The code iterates through the branchCosts map and creates a new Branch object for each entry.
        // It then adds the newly created branch to the paymentGraph.
        branchCosts.forEach((name, cost) -> paymentGraph.addBranch(new Branch(name, cost)));

        // Step 2: Add connections to the branches.
        // The code iterates through the branchConnections map.
        branchConnections.forEach((origin, destinations) -> {
            // Split the comma-separated string of destination names into an array.
            String[] destArray = destinations.split(",");
            // Iterate over each destination in the array.
            for (String dest : destArray) {
                // Add a connection from the origin branch to the destination branch.
                // The trim() method removes any leading/trailing whitespace.
                paymentGraph.addConnection(origin.trim(), dest.trim());
            }
        });
    }
}
