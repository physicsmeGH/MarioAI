package ch.idsia.agents.controllers;

import java.io.*;

import ch.idsia.agents.Agent;
import ch.idsia.agents.ObservableAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.classifiers.*;
import weka.core.*;
import weka.core.converters.ConverterUtils.DataSource;


/**
 * 
 * @author Shuran Li
 *
 */

public class ClassifierAgent extends BasicMarioAIAgent implements ObservableAgent
{
	
	private StatusRecorder recorder;
	
	private Classifier classifier;

	public ClassifierAgent(String modelFileName)
	{	
		super("ClassifierAgent");
		try{
			ObjectInputStream modelReader = new ObjectInputStream(
		
					new FileInputStream(modelFileName));
		
			classifier = (Classifier) modelReader.readObject();
		}
		catch(Exception e){
			e.printStackTrace();
			return;
		}
		recorder = new StatusRecorder(this);
				
	}

	

	
	
	public boolean[] getAction()
	{
		// this Agent requires observation integrated in advance.
		boolean[] action;
		Instances dummySet = new Instances(StatusRecorder.getDefaultInstances());
		Instance currentStat = recorder.encodeStatus();
		currentStat.setDataset(dummySet);
		int currentClass = 0;
		try {
			 currentClass = (int)classifier.classifyInstance(currentStat);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		action = recorder.classToKeys(dummySet.classAttribute().value(currentClass));
		return action;
	}





	@Override
	public float[] getMarioFloatPos() {
		
		return marioFloatPos;
	}

	@Override
	public float[] getEnemiesFloatPos() {
		
		return enemiesFloatPos;
	}

	@Override
	public int getMarioStatus() {
		
		return marioStatus;
	}

	@Override
	public int getMarioMode() {
		
		return marioMode;
	}

	@Override
	public boolean isMarioOnGround() {
		
		return isMarioOnGround;
	}

	@Override
	public boolean isMarioAbleToJump() {
		
		return isMarioAbleToJump;
	}

	@Override
	public boolean isMarioAbleToShoot() {
		
		return isMarioAbleToShoot;
	}

	@Override
	public boolean isMarioCarrying() {
		
		return isMarioCarrying;
	}

	@Override
	public byte[][] getLevelScene() {
		return levelScene;
	}
}
