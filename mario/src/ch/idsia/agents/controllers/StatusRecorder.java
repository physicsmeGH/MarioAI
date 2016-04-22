package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.core.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;

public class StatusRecorder {
	
	public static final List<String> attrbuteNames = Arrays.asList("","","");
	
	private String filePath;
	
	public static  ArrayList<Attribute> attributes;
			
	private BasicMarioAIAgent currentAgent;
	private Environment currentEnvironment;
	
	public Instance encodeStatus(){
		
	}
	
	public void createDataFile(){
		
	}
	public void writeStatusToFile(){
		
	}
	
	
	public float booleanToFloat(boolean input){
		return input?1:0;
	}
	
	public List<Float> getTenClosestEnemies(){
		List<List<Float>> enemiesGrouped = new ArrayList<List<Float>>();
		for(int i=0; i<currentAgent.enemiesFloatPos.length;i+=3){
			List<Float> temp = Arrays.asList(currentAgent.enemiesFloatPos[i],currentAgent.enemiesFloatPos[i+1],currentAgent.enemiesFloatPos[i+2]);
			enemiesGrouped.add(temp);
		}
		Comparator<List<Float>> myCompare = (List<Float> a, List<Float> b)->{
			return Float.compare(a.get(1), b.get(1));
		};
		enemiesGrouped.sort(myCompare);
		enemiesGrouped = enemiesGrouped.stream().limit(10).collect(Collectors.toList());
		BinaryOperator<List<Float>> listAppend = (List<Float> a, List<Float>b)->{
			a.addAll(b);
			return a;
		};
		List<Float> enemiesLinear = enemiesGrouped.stream().reduce(listAppend).get();
		return enemiesLinear;
		
		
	}
	
	
	public List<Float> getNearEnviroment(){
		int height = currentAgent.levelScene.length;
		int width = currentAgent.levelScene[0].length;
		List<Float> output = new ArrayList<Float>();
		for(int i=0; i<height;i++){
			for(int j=0;j<width;j++){
				output.add((float)currentAgent.levelScene[i][j]);
			}
		}
		return output;
	}
	
	public double[] listToArray(List<Float> input){
		int length = input.size();
		double[] output = new double[length];
		for(int i =0;i<length;i++){
			output[i] = input.get(i);
		}
		return output;
	}
	
	public int[] getActionFromClassifier(){
		
	}
	
	public List<Float> statusToList(){
		List<Float> output = new ArrayList<Float>();
		//enemy pos
		output.addAll(getTenClosestEnemies());
		
		//Mario status
		output.add(booleanToFloat(currentAgent.isMarioAbleToJump));
		output.add(booleanToFloat(currentAgent.isMarioAbleToShoot));
		output.add(booleanToFloat(currentAgent.isMarioOnGround));
		output.add(booleanToFloat(currentAgent.isMarioCarrying));
		output.add((float)currentAgent.marioStatus);
		output.add((float)currentAgent.marioMode);
		
		//levelScene
		output.addAll(getNearEnviroment());
		
		
		
		
		return output;
		MultilayerPerceptron myClassifier;
		Instances dataset = new Instances("eval",attributes,0);
		output.toArray(new double[0])
		Instance data = new DenseInstance(weight, attValues)
		myClassifier.classifyInstance(instance)
		
		
		
	}
	
	public StatusRecorder(BasicMarioAIAgent agent) {
		currentAgent = agent;
		filePath = "";
		
		// TODO Auto-generated constructor stub
	}

}
