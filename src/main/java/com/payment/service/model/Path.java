package com.payment.service.model;

import java.util.ArrayList;
import java.util.List;

public class Path implements Comparable<Path> {
    private final Branch branch;
    private final List<String> branchNamesInPath;
    private final int totalCost;

    public Path(Branch branch, List<String> branchNamesInPath, int totalCost) {
        this.branch = branch;
        this.branchNamesInPath = branchNamesInPath;
        this.totalCost = totalCost;
    }

    public Path(Branch branch, Path previousPath) {
        this.branch = branch;
        this.branchNamesInPath = new ArrayList<>(previousPath.getBranchNamesInPath());
        this.branchNamesInPath.add(branch.getName());
        this.totalCost = previousPath.getTotalCost() + previousPath.getBranch().getCost();
    }

    public Branch getBranch() {
        return branch;
    }

    public List<String> getBranchNamesInPath() {
        return branchNamesInPath;
    }

    public int getTotalCost() {
        return totalCost;
    }

    @Override
    public int compareTo(Path other) {
        return Integer.compare(this.totalCost, other.totalCost);
    }
}