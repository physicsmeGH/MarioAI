package edu.rpi.scenarios;

import ch.idsia.benchmark.tasks.BasicTask;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.agents.controllers.human.*;
import ch.idsia.agents.controllers.ClassifierAgent;
import ch.idsia.agents.controllers.StatusRecorder;


/**
 * Created by IntelliJ IDEA. User: Sergey Karakovskiy, sergey at idsia dot ch Date: Mar 17, 2010 Time: 8:28:00 AM
 * Package: ch.idsia.scenarios
 */
public final class Test
{
	/**
	 * Record human play data of a mario level
	 * @param args arguments for running a mario simulation
	 * use "-fn" to specify data storage file name
	 */
	public static void main(String[] args)
	{
		//        final String argsString = "-vis on";
		final CmdLineOptions cmdLineOptions = new CmdLineOptions(args);

		final BasicTask basicTask = new BasicTask(cmdLineOptions);
		
 		
		String modelFileName = cmdLineOptions.getParameterValue("-fn");	
		ClassifierAgent myAgent = new ClassifierAgent(modelFileName);
		cmdLineOptions.setAgent(myAgent);
		basicTask.reset(cmdLineOptions);

		basicTask.runOneEpisode();
		
		System.out.println(basicTask.getEnvironment().getEvaluationInfoAsString());
		//            } while (basicTask.getEnvironment().getEvaluationInfo().marioStatus != Environment.MARIO_STATUS_WIN);
		//        }
		//
		System.exit(0);
	}

}
