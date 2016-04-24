package ch.idsia.agents.controllers.human;

import ch.idsia.agents.Agent;
import ch.idsia.agents.ObservableAgent;
import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.agents.controllers.StatusRecorder;
import weka.core.*;


import java.util.Arrays;

import org.jblas.*;

public class HumanTeacherKeyboardAgent extends HumanKeyboardAgent implements ObservableAgent {
	
	
	private Instances recordedData;
	private String dataFileName;
	private StatusRecorder recorder;
	private boolean[] previousKeyStatus;
	
	public Instances getRecordedData(){
		return recordedData;
	}
	
	public StatusRecorder getRecorder(){
		return recorder;
	}
	
	public HumanTeacherKeyboardAgent(String FileName) {
		// TODO Auto-generated constructor stub
		super();
		this.setName("HumanTeacherKeyboardAgent");
		dataFileName = FileName;
		recorder = new StatusRecorder(this);
		recordedData = new Instances(StatusRecorder.getDefaultInstances());
		recordedData.setRelationName("Human Decisions");
		
	}
	
	
	public void recordEnvironmentAndKeyPressed(){
		
	}
	
	public boolean[] getAction(){
		boolean[] actions =	super.getAction();
		if(!Arrays.equals(actions, previousKeyStatus)){
			//key status changed
			Instance currentData = recorder.encodeStatus();
			currentData.setDataset(recordedData);
			currentData.setValue(recorder.FeaturesCount, recorder.getKeyPressClass(actions));
			recordedData.add(currentData);
		}
		previousKeyStatus = actions.clone();
		return actions;
		
		
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
