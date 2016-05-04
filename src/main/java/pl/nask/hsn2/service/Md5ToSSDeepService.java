/*
 * Copyright (c) NASK, NCSC
 *
 * This file is part of HoneySpider Network 2.1.
 *
 * This is a free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	protected final CommandLineParams newCommandLineParams() {
		return new MTSCommandLineParams();
	}

	@Override
	protected final void prepareService() {
		SSDeepHashGenerator.initialize("libfuzzy.so.2","whitelist.ssdeep");

	}

	@Override
	protected final Class<? extends TaskFactory> initializeTaskFactory() {
		Md5ToSSDeepTaskFactory.prepereForAllThreads((MTSCommandLineParams)getCommandLineParams());
		return Md5ToSSDeepTaskFactory.class;
	}

}
