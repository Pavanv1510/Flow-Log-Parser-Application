package com.demo.testcase;

import com.demo.service.impl.FlowLogProcessorImpl;
import com.demo.model.LookupPojo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FlowLogProcessorTest {

    public static void main(String[] args) {
        try {
            testValidInput();
            testMissingLookupFile();
            testCaseInsensitiveMatching();
            testUntaggedFlows();
            testInvalidProtocolHandling();  // New test for invalid protocol handling
        } catch (Exception e) {
            System.out.println("Error running tests: " + e.getMessage());
        }
    }

    public static void testValidInput() throws IOException, URISyntaxException {
        System.out.println("Running: testValidInput");
        FlowLogProcessorImpl processor = new FlowLogProcessorImpl();
        Path lookupFile = Paths.get(FlowLogProcessorTest.class.getResource("lookup.csv").toURI());
        Path logFile = Paths.get(FlowLogProcessorTest.class.getResource("input.txt").toURI());

        try {
            processor.loadLookUpData(lookupFile.toString());
            processor.processFlowLogsData(logFile.toString());
            processor.generateReport();
            System.out.println("testValidInput passed.");
        } catch (Exception e) {
            System.out.println("testValidInput failed: " + e.getMessage());
        }
    }

    public static void testMissingLookupFile() {
        System.out.println("Running: testMissingLookupFile");
        try {
            FlowLogProcessorImpl processor = new FlowLogProcessorImpl();
            processor.loadLookUpData("missing_lookup.csv");
            System.out.println("testMissingLookupFile failed (no exception thrown).");
        } catch (IOException e) {
            System.out.println("testMissingLookupFile passed: " + e.getMessage());
        }
    }

    public static void testCaseInsensitiveMatching() throws IOException, URISyntaxException {
        System.out.println("Running: testCaseInsensitiveMatching");
        FlowLogProcessorImpl processor = new FlowLogProcessorImpl();
        Path lookupFile = Paths.get(FlowLogProcessorTest.class.getResource("lookup_case.csv").toURI());

        try {
            processor.loadLookUpData(lookupFile.toString());
            LookupPojo result = processor.getLookupDataMap().get("443-TCP");
            if (result != null && result.getTag().equalsIgnoreCase("sv_P2")) {
                System.out.println("testCaseInsensitiveMatching passed.");
            } else {
                System.out.println("testCaseInsensitiveMatching failed.");
            }
        } catch (Exception e) {
            System.out.println("testCaseInsensitiveMatching failed: " + e.getMessage());
        }
    }

    public static void testUntaggedFlows() throws IOException, URISyntaxException {
        System.out.println("Running: testUntaggedFlows");
        FlowLogProcessorImpl processor = new FlowLogProcessorImpl();
        Path lookupFile = Paths.get(FlowLogProcessorTest.class.getResource("lookup.csv").toURI());

        try {
            processor.loadLookUpData(lookupFile.toString());
            processor.generateReport();
            
            String unmatchedKey = "999-UDP";
            LookupPojo result = processor.getLookupDataMap().getOrDefault(unmatchedKey, new LookupPojo("untagged"));
            if (result.getTag().equalsIgnoreCase("untagged")) {
                System.out.println("testUntaggedFlows passed.");
            } else {
                System.out.println("testUntaggedFlows failed.");
            }
        } catch (Exception e) {
            System.out.println("testUntaggedFlows failed: " + e.getMessage());
        }
    }

    public static void testInvalidProtocolHandling() throws IOException, URISyntaxException {
        System.out.println("Running: testInvalidProtocolHandling");
        FlowLogProcessorImpl processor = new FlowLogProcessorImpl();
        Path lookupFile = Paths.get(FlowLogProcessorTest.class.getResource("lookup_invalid_protocol.csv").toURI());

        try {
            processor.loadLookUpData(lookupFile.toString());
            // Ensure that the invalid entry (ftp) is not included in the lookup map
            if (processor.getLookupDataMap().containsKey("25-FTP")) {
                System.out.println("testInvalidProtocolHandling failed: Invalid protocol found in map.");
            } else {
                System.out.println("testInvalidProtocolHandling passed: Invalid protocol skipped.");
            }
        } catch (Exception e) {
            System.out.println("testInvalidProtocolHandling failed: " + e.getMessage());
        }
    }
}
