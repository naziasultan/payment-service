package com.payment.service.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Branch {
    private final String name;
    private final int cost;
    private final List<Branch> connections;

    public Branch(String name, int cost) {
        this.name = name;
        this.cost = cost;
        this.connections = new CopyOnWriteArrayList<>();
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public List<Branch> getConnections() {
        return connections;
    }

    public void addConnection(Branch branch) {
        this.connections.add(branch);
    }
}