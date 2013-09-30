package pl.nask.hsn2.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

public class GeneratorsTest {
	
	private static final String MD5_HASH = "e4bac1121e54d70b38c3fe9e91c11458";
	private static final String SSDEEP_HASH = "100 6:2LGFxfoJY6oRW1XRd7mmOnRRAgO9lboiW5W6vFBfK96MgAqM8M:2Axfo/oRWlRRv6RubA/tBi6O98M";
	private static final String JS_PATH = "src/test/resources/js";
	private static final String TEST_PATH = "src/test/resources/test";
	@Test
	public void md5HashGeneratorTest() throws FileNotFoundException, IOException{
		File file = new File(JS_PATH);
		Set<String> whitelist = new HashSet<>();
		whitelist.add(MD5_HASH);
		
		boolean resultTrue = Md5HashGenerator.chceckFile(file, whitelist);
		Assert.assertTrue(resultTrue);
		
		boolean resultFalse = Md5HashGenerator.chceckFile(file, whitelist);
		Assert.assertFalse(resultFalse);
	}
	
	@Test
	public void ssdeepHashGeneratorTest() throws IOException{
		new File(TEST_PATH).deleteOnExit();
		SSDeepHashGenerator.initialize("libfuzzy.so.2", TEST_PATH);
		SSDeepHashGenerator.addNewHashForFile(JS_PATH);
		SSDeepHashGenerator.addNewHashForFile(JS_PATH);
		
		try (BufferedReader reader = new BufferedReader(new FileReader(TEST_PATH))){
			for(String line = reader.readLine(); line != null; line = reader.readLine())
				Assert.assertEquals(line, SSDEEP_HASH);
		}
	}
	
}
