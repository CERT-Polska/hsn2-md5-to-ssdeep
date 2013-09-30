package pl.nask.hsn2.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;

public class SSDeepHashGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SSDeepHashGenerator.class);
	private static NativeLibrary nativeLibrary;
	private final static int HASH_BYTE_ARRAY_LENGTH = 180;
	private static BufferedWriter writer;
	
	public static void initialize(String libName, String whitelistPath){
		LOGGER.info("Loading ssdeep library");
		nativeLibrary = NativeLibrary.getInstance(libName);
		LOGGER.info("ssdeep library loaded");
		try{
			writer = new BufferedWriter(new FileWriter(whitelistPath));
			LOGGER.info("Output file: " + whitelistPath);
		}
		catch (IOException e){
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private static String generateHashForFile(String path){
		synchronized (nativeLibrary){
			Function function = nativeLibrary.getFunction("fuzzy_hash_filename");
			byte[] result = new byte[HASH_BYTE_ARRAY_LENGTH];
			int i = function.invokeInt(new Object[] {path, result});
			if(i == 0){
				String hash = new String(result).trim();
				LOGGER.debug(hash);
				return hash;
			}
			else{
				throw new IllegalStateException("Can not generate hash for: " + path);
			}
		}
	}
	
	public static void addNewHashForFile(String path) throws IOException{
		String hash = generateHashForFile(path);
		writer.write("100 " + hash);
		writer.newLine();
	}
	
	public static void flush(){
		try{
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException("Could not save to file!", e);
		}
	}
}
