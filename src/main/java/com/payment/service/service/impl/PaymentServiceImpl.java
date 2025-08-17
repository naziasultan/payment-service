package com.payment.service.service.impl;


import com.payment.service.model.Branch;
import com.payment.service.model.Path;
import com.payment.service.model.PaymentGraph;
import com.payment.service.service.PaymentService;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class PaymentServiceImpl implements PaymentService {
    // A shared, thread-safe graph holding the entire network of branches and connections.
    private final PaymentGraph paymentGraph;

    public PaymentServiceImpl(PaymentGraph paymentGraph) {
        this.paymentGraph = paymentGraph;
    }

    /**
     * Finds the cheapest path from an origin branch to a destination branch using Dijkstra's algorithm.
     * The method is thread-safe as all its core data structures are local to each call.
     *
     * @param originName      The name of the starting branch.
     * @param destinationName The name of the destination branch.
     * @return A comma-separated string of branch names representing the cheapest path, or null if no path exists.
     */
    @Override
    public String processPayment(String originName, String destinationName) {
        // Step 1: Retrieve branch objects and perform initial validation.
        Branch origin = paymentGraph.getBranch(originName);
        Branch destination = paymentGraph.getBranch(destinationName);

        if (origin == null || destination == null) {
            return null;
        }

        if (origin.equals(destination)) {
            return originName;
        }

        // Step 2: Initialize data structures for Dijkstra's algorithm.
        // The PriorityQueue stores paths, ordered by their total cost (cheapest first).
        PriorityQueue<Path> pq = new PriorityQueue<>();
        // The HashMap stores the cheapest cost found so far to reach each branch.
        Map<String, Integer> cheapestCosts = new HashMap<>();

        // Step 3: Start the algorithm from the origin branch.
        // Create the initial path with a cost of 0 and add it to the priority queue.
        List<String> initialPath = new ArrayList<>();
        initialPath.add(originName);
        pq.add(new Path(origin, initialPath, 0));
        // Record the cost to the origin in the map.
        cheapestCosts.put(originName, 0);

        // Step 4: The main loop of Dijkstra's algorithm.
        // Continue as long as there are potential paths to explore.
        while (!pq.isEmpty()) {
            // Get the path with the lowest total cost from the priority queue.
            Path currentPath = pq.poll();
            Branch currentBranch = currentPath.getBranch();

            // Step 5: Check for termination condition.
            // If the current branch is the destination, we have found the cheapest path.
            if (currentBranch.getName().equals(destinationName)) {
                return String.join(",", currentPath.getBranchNamesInPath());
            }

            // Step 6: Optimization.
            // If we've already found a cheaper path to this branch, skip this path.
            // This avoids redundant processing.
            if (currentPath.getTotalCost() > cheapestCosts.getOrDefault(currentBranch.getName(), Integer.MAX_VALUE)) {
                continue;
            }

            // Step 7: Explore neighbors.
            // Iterate through all branches connected to the current branch.
            for (Branch neighbor : currentBranch.getConnections()) {
                // Calculate the cost to reach the neighbor through the current path.
                int newCost = currentPath.getTotalCost() + currentBranch.getCost();

                // If this new path is cheaper than any previously found path to the neighbor...
                if (newCost < cheapestCosts.getOrDefault(neighbor.getName(), Integer.MAX_VALUE)) {
                    // Update the cheapest cost to the neighbor.
                    cheapestCosts.put(neighbor.getName(), newCost);
                    // Add the new, cheaper path to the priority queue for future exploration.
                    pq.add(new Path(neighbor, currentPath));
                }
            }
        }

        // Step 8: Return null if no path is found.
        // This is reached if the loop completes without finding the destination, meaning it's unreachable.
        return null;
    }
}
