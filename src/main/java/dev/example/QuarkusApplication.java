package dev.example;

import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.control.ActivateRequestContext;
import java.util.Scanner;

@QuarkusMain
public class QuarkusApplication implements io.quarkus.runtime.QuarkusApplication {

    private final CustomerSupportAgent agent;

    public QuarkusApplication(CustomerSupportAgent agent) {
        this.agent = agent;
    }

    @Override
    @ActivateRequestContext
    public int run(String... args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("[User]: ");
            String userMessage = scanner.nextLine();
            String agentResponse = agent.chat(userMessage);
            System.out.println("[Agent]: " + agentResponse);
        }
    }
}
