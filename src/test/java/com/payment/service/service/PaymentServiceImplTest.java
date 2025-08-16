package com.payment.service.service;

import com.payment.service.model.Branch;
import com.payment.service.model.PaymentGraph;
import com.payment.service.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentServiceImplTest {
    private PaymentServiceImpl paymentService;
    private PaymentGraph testGraph;

    @BeforeEach
    void setup() {
        // Re-create the graph setup logic for independent unit testing
        testGraph = new PaymentGraph();
        testGraph.addBranch(new Branch("A", 5));
        testGraph.addBranch(new Branch("B", 50));
        testGraph.addBranch(new Branch("C", 10));
        testGraph.addBranch(new Branch("D", 10));
        testGraph.addBranch(new Branch("E", 20));
        testGraph.addBranch(new Branch("F", 5));
        testGraph.addBranch(new Branch("G", 15)); // A new, isolated branch for testing

        testGraph.addConnection("A", "B");
        testGraph.addConnection("A", "C");
        testGraph.addConnection("C", "B");
        testGraph.addConnection("B", "D");
        testGraph.addConnection("C", "E");
        testGraph.addConnection("D", "E");
        testGraph.addConnection("E", "D");
        testGraph.addConnection("D", "F");
        testGraph.addConnection("E", "F");

        this.paymentService = new PaymentServiceImpl(testGraph);
    }

    // --- Functional Test Cases ---

    @Test
    void testFindCheapestPath_ShortestPathExists() {
        // The actual cheapest path is A -> C -> E -> D (Cost: 5 + 10 + 20 = 35)
        String cheapestPath = paymentService.processPayment("A", "D");
        assertEquals("A,C,E,D", cheapestPath);
    }

    @Test
    void testFindCheapestPath_LongerButCheaperPathExists() {
        // Test case where a longer path is actually cheaper due to lower costs
        // Path C -> E -> F (Cost: 10 + 20 = 30)
        // Path C -> B -> D -> F (Cost: 10 + 50 + 10 = 70)
        String cheapestPath = paymentService.processPayment("C", "F");
        assertEquals("C,E,F", cheapestPath);
    }

    @Test
    void testFindCheapestPath_PathWithCycle() {
        // Test that the algorithm correctly finds the cheapest path without getting
        // stuck in a cycle (D <-> E)
        // D -> E -> F (Cost: 10 + 20 = 30)
        // D -> F (Cost: 10)
        String cheapestPath = paymentService.processPayment("D", "F");
        assertEquals("D,F", cheapestPath);
    }

    @Test
    void testFindCheapestPath_SameOriginAndDestination() {
        // The path from a branch to itself should just be the branch name
        String cheapestPath = paymentService.processPayment("A", "A");
        assertEquals("A", cheapestPath);
    }

    @Test
    void testFindCheapestPath_NoPathAvailable() {
        // Test case with a new isolated branch 'G' that is not connected to the network
        String cheapestPath = paymentService.processPayment("A", "G");
        assertNull(cheapestPath);
    }

    @Test
    void testFindCheapestPath_NonExistentBranches() {
        // Test with non-existent origin or destination branches
        String cheapestPath1 = paymentService.processPayment("X", "A");
        String cheapestPath2 = paymentService.processPayment("A", "Y");
        assertNull(cheapestPath1);
        assertNull(cheapestPath2);
    }
}