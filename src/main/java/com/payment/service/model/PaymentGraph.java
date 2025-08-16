package com.payment.service.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PaymentGraph {
    private final Map<String, Branch> branches = new ConcurrentHashMap<>();

    public void addBranch(Branch branch) {
        this.branches.put(branch.getName(), branch);
    }

    public void addConnection(String originName, String destinationName) {
        Branch origin = branches.get(originName);
        Branch destination = branches.get(destinationName);
        if (origin != null && destination != null) {
            origin.addConnection(destination);
        }
    }

    public Branch getBranch(String name) {
        return branches.get(name);
    }
}
