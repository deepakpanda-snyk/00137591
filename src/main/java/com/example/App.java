package com.example;

/**
 * Minimal placeholder class - only exists so this is a real, buildable Maven
 * project and not just a bare manifest. Used purely to reproduce Snyk PR
 * check / SCA scanning behavior for case 00137591 (New York Life).
 *
 * No Spring Boot or New Relic code paths are exercised here - the point of
 * the fixture is the dependency versions in pom.xml, not runtime behavior.
 * Do not deploy this anywhere reachable.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("PR check repro fixture - see README.md for the test scenario.");
    }
}
