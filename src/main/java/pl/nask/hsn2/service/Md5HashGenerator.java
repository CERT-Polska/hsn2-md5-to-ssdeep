/*
 * Copyright (c) NASK, NCSC
 *
 * This file is part of HoneySpider Network 2.0.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.service.WhiteListFileInputStream;

public class Md5HashGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(Md5HashGenerator.class);
	private static final int BUFFER_SIZE = 50000;
	private static Set<String> duplicate = new HashSet<>();

	private Md5HashGenerator() {}

	private static String md5hashFromFile(BufferedInputStream bufferedInputStream) throws IOException {
		bufferedInputStream.reset();
		String result = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.reset();
			try (InputStream dis = new DigestInputStream(new WhiteListFileInputStream(bufferedInputStream), md)) {
				while (dis.read() != -1) { // NOPMD
					// Nothing to do.
				}
				char[] md5 = Hex.encodeHex(md.digest());
				result = String.valueOf(md5);
			}
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error("Could not create MD5 hash for whitelisting.\n{}", e);
			result = "";
		}
		return result;
	}

	public static boolean chceckFile(File file, Set<String> whitelist) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), BUFFER_SIZE)) {
			bis.mark(Integer.MAX_VALUE);
			String md5hash = Md5HashGenerator.md5hashFromFile(bis);
			if (whitelist.contains(md5hash) && !duplicate.contains(md5hash)) {
				duplicate.add(md5hash);
				return true;
			} else {
				return false;
			}
		}
	}
}
