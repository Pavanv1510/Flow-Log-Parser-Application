package com.demo.service.impl;

import com.demo.model.LookupPojo;
import com.demo.service.FlowLogProcessor;
import com.demo.utils.IPProtocols;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Stream;

import static java.nio.file.Files.writeString;

public class FlowLogProcessorImpl implements FlowLogProcessor {

    private final ConcurrentHashMap<String, LookupPojo> lookupDataMap;
    private final ConcurrentHashMap<String, AtomicInteger> tagCount;
    // this is where you declare the file location to generate reports
    String reportFolder = "D:\\Illumio_Project\\FlowLogParserApplication\\outputReports\\";

    public FlowLogProcessorImpl() {
        this.lookupDataMap = new ConcurrentHashMap<>();
        this.tagCount = new ConcurrentHashMap<>();
    }

    //  This method reads the lookup data from a comma-separated text file
    @Override
    public void loadLookUpData(String filePath) throws IOException {
        logger.log(Level.INFO, "Reading/Processing lookup table file, start");
        AtomicInteger lineCounter = new AtomicInteger();
        long startTime = System.currentTimeMillis();
        
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.map(l -> l.trim().split(LOOKUP_FILE_SEPARATOR))
                    .forEach(a -> {
                        lineCounter.incrementAndGet();
                        
                        if (a.length < 2) {
                            logger.log(Level.WARNING, "Invalid lookup data format at line: " + lineCounter.get());
                            return;  // Skip this invalid entry
                        }

                        String dstport = a[0].trim();
                        String protocol = a[1].trim().toUpperCase(Locale.ROOT);

                        // Validate protocol (only TCP and UDP are allowed)
                        if (!protocol.equals("TCP") && !protocol.equals("UDP")) {
                            logger.log(Level.WARNING, "Invalid protocol found at line " + lineCounter.get() + ": " + protocol);
                            return;  // Skip invalid protocol entry
                        }

                        // Use "untagged" if no tag is provided
                        String tag = a.length == 3 ? a[2].trim().toLowerCase() : "untagged";

                        lookupDataMap.put(dstport + "-" + protocol, new LookupPojo(tag));
                    });

            logger.log(Level.INFO, "Lookup file read/processed successfully. Time_taken [" +
                    (System.currentTimeMillis() - startTime) + "]ms, Total Records[" + lineCounter + "], Unique key[" + lookupDataMap.size() + "]");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_101: File not found: {0}", filePath);
            throw new FileNotFoundException("Lookup file not found: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_102: Error reading lookup file: {0}", e.getMessage());
            throw new IOException("Error reading lookup file: " + filePath);
        }
    }

    //  Method to process the input data file
     
    @Override
    public void processFlowLogsData(String filePath) throws IOException {
        AtomicInteger counter = new AtomicInteger();
        long startTime = System.currentTimeMillis();
        
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.map(l -> l.trim().split(INPUT_FILE_SEPARATOR))
                    .forEach(a -> {
                        counter.incrementAndGet();
                        if (a.length != 14) {
                            logger.log(Level.WARNING, "ERROR_CODE_103: Invalid number of columns at line: " + counter.get());
                        } else if (!"2".equals(a[0].trim())) {
                            logger.log(Level.WARNING, "ERROR_CODE_104: Unsupported version [" + a[0] + "] at line: " + counter.get());
                        } else {
                            String protocol = IPProtocols.getProtocolName(Integer.parseInt(a[7].trim()));
                            String key = a[6].trim() + "-" + protocol;
                            LookupPojo pojo = lookupDataMap.computeIfAbsent(key, k -> new LookupPojo("NO_LOOKUP_EXIST"));
                            pojo.incrementCounter();
                            AtomicInteger count = tagCount.computeIfAbsent(pojo.getTag(), k -> new AtomicInteger());
                            count.incrementAndGet();
                        }
                    });
            logger.log(Level.INFO, "Flow log file processing completed successfully. Time_taken [" +
                    (System.currentTimeMillis() - startTime) + "]ms, Total Records[" + counter.intValue() + "]");
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_105: Flow log file not found: {0}", filePath);
            throw new FileNotFoundException("Flow log file not found: " + filePath);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_106: Error reading flow log file: {0}", e.getMessage());
            throw new IOException("Error reading flow log file: " + filePath);
        }
    }

    //  Print the reports
    @Override
    public void generateReport() throws Exception {
        logger.log(Level.INFO, "====Generating Port/Protocol Combination & tag report======");
        Path portCountPath = Path.of(reportFolder + System.currentTimeMillis() + "_port.csv");
        Path tagCountPath = Path.of(reportFolder + System.currentTimeMillis() + "_tag.csv");
    
        try {
            File portFile = new File(portCountPath.toUri());
            File tagFile = new File(tagCountPath.toUri());
            
            if (!portFile.createNewFile() || !tagFile.createNewFile()) {
                logger.log(Level.SEVERE, "ERROR_CODE_107: Unable to create report files in: {0}", reportFolder);
                throw new IOException("Unable to create report files in: " + reportFolder);
            }
            
            writeString(portCountPath, "dstport, protocol, Count", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            lookupDataMap.forEach((k, v) -> {
                try {
                    if (v.getCounter().intValue() > 0) {
                        String data = "\n" + k.replace("-", ",") + "," + v.getCounter().intValue();
                        writeString(portCountPath, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "ERROR_CODE_108: Error writing to port report file: {0}", e.getMessage());
                }
            });
    
            writeString(tagCountPath, "Tag, Tag Counts", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            tagCount.forEach((k, v) -> {
                try {
                    writeString(tagCountPath, "\n" + k + ", " + v.intValue(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "ERROR_CODE_109: Error writing to tag report file: {0}", e.getMessage());
                }
            });
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "ERROR_CODE_110: Report generation failed: {0}", e.getMessage());
            throw new IOException("Report generation failed: " + e.getMessage());
        }
    }

    public ConcurrentHashMap<String, LookupPojo> getLookupDataMap() {
        return lookupDataMap;
    }

    public ConcurrentHashMap<String, AtomicInteger> getTagCountMap() {
        return tagCount;
    }
}
