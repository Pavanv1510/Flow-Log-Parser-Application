import com.demo.service.FlowLogProcessor;
import com.demo.service.impl.FlowLogProcessorImpl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {
    public static final Logger logger = Logger.getLogger(Main.class.getName());
    public static void main(String[] args) {
        FlowLogProcessorImpl impl = new FlowLogProcessorImpl();
        try {
            if(args.length<2 ){
                throw new RuntimeException("Missing lookup and input file path in command prompt ");
            }
            impl.loadLookUpData(args[0]);
            impl.processFlowLogsData(args[1]);
            impl.generateReport();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "ERROR_CODE_200: ", e.getMessage());
        }

    }
}