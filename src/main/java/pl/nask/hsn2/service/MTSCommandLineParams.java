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

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.CommandLineParams;

public class MTSCommandLineParams extends CommandLineParams {
	private static final OptionNameWrapper WHITELIST_PATH = new OptionNameWrapper("wp", "whitelistPath");
	private static final Logger LOGGER = LoggerFactory.getLogger(MTSCommandLineParams.class);

	@Override
	public final void initOptions() {
		super.initOptions();
		addOption(WHITELIST_PATH, "path", "Path to whitelist files");
	}

	public final String getWhitelistPath() {
		return getOptionValue(WHITELIST_PATH);
	}

	@Override
	protected final void initDefaults() {
		super.initDefaults();
		setDefaultValue(WHITELIST_PATH, "whitelist.md5");
		setDefaultMaxThreads(1);
		setDefaultServiceNameAndQueueName("md5-to-ssdeep");
		setDefaultDataStoreAddress("http://127.0.0.1:8080");
	}

	@Override
	protected final void validate(){
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
	public final Integer getMaxThreads() {
		return 1;
	}
}
