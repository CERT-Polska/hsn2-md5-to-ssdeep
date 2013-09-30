package pl.nask.hsn2.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.nask.hsn2.InputDataException;
import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.ResourceException;
import pl.nask.hsn2.StorageException;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.protobuff.Resources.JSContext;
import pl.nask.hsn2.protobuff.Resources.JSContextList;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class Md5ToSSDeepTask implements Task {

	private TaskContext jobContext;
	private Long jsContextId;
	private Set<String> whitelist;
	private static final Logger LOGGER = LoggerFactory.getLogger(Md5ToSSDeepTask.class);
	private static final int MD5_STRING_LENGTH = 32;
	
	public Md5ToSSDeepTask(TaskContext jobContext, ParametersWrapper parameters, ObjectDataWrapper data, MTSCommandLineParams cmd) {
		this.jobContext = jobContext;
		prepareWhitelist(cmd.getWhitelistPath());
		jsContextId = data.getReferenceId("js_context_list");
	}
	
	private void prepareWhitelist(String whitelistPath) {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(whitelistPath);
			br = new BufferedReader(fr);
			String readLine;
			whitelist = new HashSet<String>();
			while ((readLine = br.readLine()) != null) {
				readLine = readLine.trim();
				// MD5 hash hex string is always 32 characters length.
				if (readLine.length() == MD5_STRING_LENGTH) {
					whitelist.add(readLine);
				}
			}
		} catch (IOException e) {
			LOGGER.warn("Cannot access whitelist file.");
			LOGGER.debug(e.getMessage(), e);
		} finally {
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				LOGGER.warn("Cannot close whitelist buffered reader.");
				LOGGER.debug(e.getMessage(), e);
			}
		}
	}
	
	@Override
	public boolean takesMuchTime() {
		return false;
	}

	@Override
	public void process() throws ParameterException, ResourceException, StorageException, InputDataException {
		if (jsContextId != null) {
			try {
				JSContextList contextList = downloadJsContextList();

				for (JSContext context : contextList.getContextsList()) {
					File tempFile = prepareTempJsSource(context);

					if (Md5HashGenerator.chceckFile(tempFile, whitelist)){
						SSDeepHashGenerator.addNewHashForFile(tempFile.getCanonicalPath());
					}
					if (!tempFile.delete()) {
						LOGGER.warn("Could not delete temp file: {}", tempFile);
					}
				}
			}
			catch (IOException e) {
				LOGGER.error(e.getMessage(), e);
			}
			finally{
				SSDeepHashGenerator.flush();
			}
		}
		else {
			LOGGER.info("Task skipped, not js");
		}
	}
	
	/**
	 * Creates temporary file for JS context.
	 * 
	 * @param context
	 *            JS context.
	 * @return Temporary file object.
	 * @throws IOException
	 *             When there is some issue with IO operations.
	 */
	private File prepareTempJsSource(JSContext context) throws IOException {
		// Create unique path to file.
		File f = new File(System.getProperty("java.io.tmpdir"));
		String tempFileName = f.getAbsolutePath() + File.separator + "hsn2-js-sta_" + context.getId() + System.currentTimeMillis();
		while (true) {
			f = new File(tempFileName);
			if (!f.exists()) {
				break;
			}
			tempFileName += "-";
		}

		// Write source to file.
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {
			bw.write(context.getSource());
		}

		// Return path file.
		return f;
	}

	private JSContextList downloadJsContextList() throws StorageException, IOException {
		InputStream is = null;
		try {
			is = jobContext.getFileAsInputStream(jsContextId);
			return JSContextList.parseFrom(is);
		}
		finally {
			if (is != null) {
				is.close();
			}
		}
	}
}
