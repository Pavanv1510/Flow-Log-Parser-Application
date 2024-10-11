Flow Log Data Parsing Program

Overview

This project parses flow log data from an input file and maps each row to a tag based on a lookup table. The lookup table defines which port/protocol combinations correspond to specific tags, allowing for easy classification of flow log entries. The program processes the logs, generates reports, and handles errors such as missing files, invalid data, and unsupported protocols gracefully.

Features

- Input Files:
   - A flow log file containing flow log entries (up to 10 MB in size).
   - A lookup table in CSV format (up to 10,000 entries) containing port/protocol/tag mappings.

- Case-Insensitive Matching: The program matches `protocol` values (e.g., "tcp", "udp") in a case-insensitive manner.
  
- IP Protocol Standards: The program utilizes IP protocols as defined by the **IANA standards**, making it reliable for handling standard network protocols.

- Invalid Protocol Handling: Any protocol that is not recognized (e.g., "ftp") is skipped and logged as a warning.

- Output Generation: Two output reports are generated:
  1. Tag Counts: A summary of how many times each tag was matched in the flow log.
  2. Port/Protocol Combination Counts: A summary of the number of times each unique `port/protocol` combination was found in the flow log.


Example Output

Tag Counts:

| Tag      | Count |
|----------|-------|
| sv_P2    | 2     |
| sv_P1    | 2     |
| email    | 3     |
| Untagged | 9     |

Port/Protocol Combination Counts:

| Port  | Protocol | Count |
|-------|----------|-------|
| 22    | tcp      | 1     |
| 25    | tcp      | 1     |
| 110   | tcp      | 1     |
| 143   | tcp      | 1     |
| 443   | tcp      | 1     |

Assumptions

1. File Paths: 
    - The paths for the input files (lookup and flow log) and the output reports are hardcoded in the program. You must adjust these paths directly in the code if necessary.
    - Example hardcoded path for output: `D:\\Illumio_Project\\FlowLogParserApplication\\outputReports\\`.

2. Protocol Matching: 
    - The program only supports the `TCP` and `UDP` protocols. Any other protocols will be logged as invalid and ignored.
  
3. Log Format:
    - The program assumes that all flow log entries follow the default format and only version 2 logs are supported.
  
4. Handling Missing or Invalid Data:
    - If any rows in the lookup file or flow log contain invalid data (e.g., incorrect column count or unsupported protocol), these rows will be skipped and logged, allowing the program to continue processing the rest of the data.
  
5. Untagged Flows:
    - Any flow that does not match any entry in the lookup table will be labeled as "untagged."

Installation and Setup

To run the project locally, follow these steps:

Prerequisites:
- Ensure that Java 8 or higher is installed on your machine.

Steps:

1. Clone the Repository:
   
   git clone <your-repo-link>
   cd FlowLogParserApplication/src

2. Place Input Files:
   - Ensure that the input files (lookup.csv and flow_log.txt) are placed in the correct directories as defined in the code.

3. Compile the Program:
   
   javac Main.java

4. Run the Program:

   java Main <path_to_lookuptable_file> <path_to_input_file>

5. Output:
   - The output files (`port.csv` and `tag.csv`) will be generated in the `outputReports` folder. 

Running the Tests

1. To run the test cases that validate the functionality of the program

- Compile the Java Files

   javac com/demo/service/impl/FlowLogProcessorImpl.java
   javac com/demo/testcase/FlowLogProcessorTest.java

- Run the Program

   java com.demo.testcase.FlowLogProcessorTest


Error Handling

The program logs the following errors:

- File Not Found: If the lookup or flow log file is missing, the program will log an error and terminate gracefully.
- Invalid Protocols: If the lookup table contains an invalid protocol (e.g., "ftp"), the program will skip the entry, log the error, and continue processing.
- Malformed Data: Any rows with missing or extra columns are skipped, and the error is logged.
  
Test Cases

The following test cases have been implemented:

1. testValidInput(): Verifies the program correctly processes valid input files.
2. testMissingLookupFile(): Ensures the program handles missing lookup files gracefully.
3. testCaseInsensitiveMatching(): Verifies that protocols are matched case-insensitively.
4. testInvalidProtocolHandling(): Confirms that invalid protocols in the lookup file are logged and skipped.
5. testUntaggedFlows(): Ensures unmatched flows are labeled as "untagged."

