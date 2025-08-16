package com.payment.service.service.impl;


import com.payment.service.model.Branch;
import com.payment.service.model.Path;
import com.payment.service.model.PaymentGraph;
import com.payment.service.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentGraph paymentGraph;

    public PaymentServiceImpl(PaymentGraph paymentGraph) {
        this.paymentGraph = paymentGraph;
    }

    @Override
    public String processPayment(String originName, String destinationName) {
        Branch origin = paymentGraph.getBranch(originName);
        Branch destination = paymentGraph.getBranch(destinationName);
        if (origin == null || destination == null) {
            return null;
        }
        if (origin.equals(destination)) {
            return originName;
        }

        PriorityQueue<Path> pq = new PriorityQueue<>();
        Map<String, Integer> cheapestCosts = new HashMap<>();

        List<String> initialPath = new ArrayList<>();
        initialPath.add(originName);
        pq.add(new Path(origin, initialPath, 0));
        cheapestCosts.put(originName, 0);

        while (!pq.isEmpty()) {
            Path currentPath = pq.poll();
            Branch currentBranch = currentPath.getBranch();

            if (currentBranch.getName().equals(destinationName)) {
                return String.join(",", currentPath.getBranchNamesInPath());
            }

            if (currentPath.getTotalCost() > cheapestCosts.getOrDefault(currentBranch.getName(), Integer.MAX_VALUE)) {
                continue;
            }

            for (Branch neighbor : currentBranch.getConnections()) {
                int newCost = currentPath.getTotalCost() + currentBranch.getCost();
                if (newCost < cheapestCosts.getOrDefault(neighbor.getName(), Integer.MAX_VALUE)) {
                    cheapestCosts.put(neighbor.getName(), newCost);
                    pq.add(new Path(neighbor, currentPath));
                }
            }
        }
        return null;
    }
}