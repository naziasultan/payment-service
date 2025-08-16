package com.payment.service.service;

import com.payment.service.model.Branch;
import com.payment.service.model.PaymentGraph;
import com.payment.service.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentServiceThreadSafetyTest {

    private static final Map<String, String> EXPECTED_PATHS = new ConcurrentHashMap<>();
    private static final int NUMBER_OF_THREADS = 1000;
    private static final int NUMBER_OF_TEST_RUNS = 100;
    private PaymentGraph paymentGraph;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentGraph = new PaymentGraph();
        paymentService = new PaymentServiceImpl(paymentGraph);

        Map<String, Integer> branchCosts = Map.of(
                "A", 5, "B", 50, "C", 10, "D", 10, "E", 20, "F", 5
        );
        Map<String, List<String>> branchConnections = Map.of(
                "A", List.of("B", "C"),
                "C", List.of("B", "E"),
                "B", List.of("D"),
                "D", List.of("E", "F"),
                "E", List.of("D", "F")
        );

        branchCosts.forEach((name, cost) -> paymentGraph.addBranch(new Branch(name, cost)));
        branchConnections.forEach((originName, destinations) ->
                destinations.forEach(destName -> paymentGraph.addConnection(originName, destName))
        );

        // A -> C -> E -> D is the cheapest path (cost 35) vs A -> B -> D (cost 55).
        EXPECTED_PATHS.put("A,D", "A,C,E,D");
        EXPECTED_PATHS.put("A,E", "A,C,E");
        EXPECTED_PATHS.put("C,F", "C,E,F");
        EXPECTED_PATHS.put("A,F", "A,C,E,F");
    }

    @Test
    void testProcessPayment_ThreadSafety_WithConcurrentRequests() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(NUMBER_OF_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
        List<Callable<Void>> tasks = new ArrayList<>();
        AtomicInteger successfulRuns = new AtomicInteger(0);

        IntStream.range(0, NUMBER_OF_THREADS).forEach(threadIndex -> {
            tasks.add(() -> {
                barrier.await();

                for (int i = 0; i < NUMBER_OF_TEST_RUNS; i++) {
                    String pathAD = paymentService.processPayment("A", "D");
                    assertNotNull(pathAD, "Path A->D should not be null");
                    assertEquals(EXPECTED_PATHS.get("A,D"), pathAD, "Incorrect path for A->D");

                    String pathAE = paymentService.processPayment("A", "E");
                    assertNotNull(pathAE, "Path A->E should not be null");
                    assertEquals(EXPECTED_PATHS.get("A,E"), pathAE, "Incorrect path for A->E");

                    String pathCF = paymentService.processPayment("C", "F");
                    assertNotNull(pathCF, "Path C->F should not be null");
                    assertEquals(EXPECTED_PATHS.get("C,F"), pathCF, "Incorrect path for C->F");

                    String pathAF = paymentService.processPayment("A", "F");
                    assertNotNull(pathAF, "Path A->F should not be null");
                    assertEquals(EXPECTED_PATHS.get("A,F"), pathAF, "Incorrect path for A->F");

                    successfulRuns.incrementAndGet();
                }
                return null;
            });
        });

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }

        int totalExpectedRuns = NUMBER_OF_THREADS * NUMBER_OF_TEST_RUNS;
        assertEquals(totalExpectedRuns, successfulRuns.get(), "Not all test runs completed successfully.");
    }
}
