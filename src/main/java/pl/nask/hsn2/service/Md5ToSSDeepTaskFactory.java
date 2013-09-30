package pl.nask.hsn2.service;

import pl.nask.hsn2.ParameterException;
import pl.nask.hsn2.TaskContext;
import pl.nask.hsn2.task.Task;
import pl.nask.hsn2.task.TaskFactory;
import pl.nask.hsn2.wrappers.ObjectDataWrapper;
import pl.nask.hsn2.wrappers.ParametersWrapper;

public class Md5ToSSDeepTaskFactory implements TaskFactory {

	private MTSCommandLineParams cmd;

	public Md5ToSSDeepTaskFactory(MTSCommandLineParams commandLineParams) {
		cmd = commandLineParams;
	}

	@Override
	public Task newTask(TaskContext jobContext, ParametersWrapper parameters, ObjectDataWrapper data) throws ParameterException {
		return new Md5ToSSDeepTask(jobContext, parameters, data, cmd);
	}

}
