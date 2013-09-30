package pl.nask.hsn2.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private static Set<String> duplicate = new HashSet<>();
	private static String md5hashFromFile(BufferedInputStream bufferedInputStream) throws IOException {
		bufferedInputStream.reset();
		String result = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.reset();
			try (InputStream dis = new DigestInputStream(new WhiteListFileInputStream(bufferedInputStream), md)) {
				while (dis.read() != -1) {
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
	
	public static boolean chceckFile(File file, Set<String> whitelist) throws FileNotFoundException, IOException{
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file), 50000)) {
			bis.mark(Integer.MAX_VALUE);
			String md5hash = Md5HashGenerator.md5hashFromFile(bis);
			if (whitelist.contains(md5hash) && !duplicate.contains(md5hash)){
				duplicate.add(md5hash);
				return true;
			}
			else{
				return false;
			}
		}
	}
}
