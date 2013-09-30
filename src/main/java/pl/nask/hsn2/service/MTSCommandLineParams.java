package pl.nask.hsn2.service;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.CommandLineParams;

public class MTSCommandLineParams extends CommandLineParams {
	private static final OptionNameWrapper WHITELIST_PATH = new OptionNameWrapper("wp", "whitelistPath");
    private static final Logger LOGGER = LoggerFactory.getLogger(MTSCommandLineParams.class);
    
    @Override
    public void initOptions() {
        super.initOptions();
        addOption(WHITELIST_PATH, "path", "Path to whitelist files");
	}
    
    public String getWhitelistPath() {
		return getOptionValue(WHITELIST_PATH);
	}
    
    @Override
	protected void initDefaults() {
	    super.initDefaults();
	    setDefaultValue(WHITELIST_PATH, "whitelist.md5");
	    setDefaultMaxThreads(1);
	    setDefaultServiceNameAndQueueName("md5-to-ssdeep");
	    setDefaultDataStoreAddress("http://127.0.0.1:8080");
    }
    
    @Override
	protected void validate(){
		super.validate();
		String msg = "";
		if (!new File(getWhitelistPath()).exists()){
			msg += "Whitelist file not exists!\n";
			LOGGER.error("Whitelist file does not exist! Path used: {}", getWhitelistPath());
		}
		if (!msg.equals("")){
			throw new IllegalStateException(msg);
		}
	}
    
    @Override
    public Integer getMaxThreads() {
    	return 1;
    }
}
