package pl.nask.hsn2.service;

import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonController;
import org.apache.commons.daemon.DaemonInitException;

import pl.nask.hsn2.CommandLineParams;
import pl.nask.hsn2.ServiceMain;
import pl.nask.hsn2.service.SSDeepHashGenerator;
import pl.nask.hsn2.task.TaskFactory;

public class Md5ToSSDeepService extends ServiceMain {

	public static void main(final String[] args) throws DaemonInitException, InterruptedException {
		ServiceMain mts = new Md5ToSSDeepService();
		mts.init(new DaemonContext() {
			public DaemonController getController() {
				return null;
			}
			public String[] getArguments() {
				return args;
			}
		});
		mts.start();
		mts.getServiceRunner().join();
	}
	
	@Override
	protected CommandLineParams newCommandLineParams() {
		return new MTSCommandLineParams();
	}
	
	@Override
	protected void prepareService() {
		SSDeepHashGenerator.initialize("libfuzzy.so.2","whitelist.ssdeep");
		
	}

	@Override
	protected TaskFactory createTaskFactory() {
		return new Md5ToSSDeepTaskFactory((MTSCommandLineParams)getCommandLineParams());
	}

}
