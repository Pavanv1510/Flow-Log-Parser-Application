package com.demo.service;

import java.io.IOException;
import java.util.logging.Logger;

public interface FlowLogProcessor {
    public static final Logger logger = Logger.getLogger(FlowLogProcessor.class.getName());
    public static final String INPUT_FILE_SEPARATOR = " ";
    public static final String LOOKUP_FILE_SEPARATOR = ",";
    public void loadLookUpData(String filePath) throws IOException;
    public void processFlowLogsData(String filePath) throws IOException;
    public void generateReport() throws Exception;
}
