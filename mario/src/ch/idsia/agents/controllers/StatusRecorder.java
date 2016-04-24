package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.agents.ObservableAgent;
import ch.idsia.agents.controllers.human.HumanKeyboardAgent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import weka.core.*;
import weka.core.converters.*;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.converters.ArffLoader.ArffReader;

import java.io.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.testng.util.Strings;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;

public class StatusRecorder {
	
	public static final List<String> attrbuteNames = Arrays.asList("","","");
	
	public static final int fieldLength = 19;
	
	public static final int fieldRowsStart = 3;
	public static final int fieldRowsEnd = fieldLength;
	public static final int fieldColsStart = 3;
	public static final int fieldColsEnd = fieldLength - fieldColsStart;
	public static final int fieldSize = (fieldRowsEnd-fieldRowsStart)*(fieldColsEnd-fieldColsStart);
	public static final int MarioParamsNumber = 4;
	public static final int EnemiesNumber = 10;
	public static final int FeaturesCount = fieldSize+MarioParamsNumber+EnemiesNumber*3;
	public static final int DataCapacity = 5000;
	public static  ArrayList<Attribute> attributes = new ArrayList<Attribute>();
	static{
		for(int i=0;i<FeaturesCount;i++){
			Attribute attribute = new Attribute("attr"+i);
			attributes.add(attribute);
		}
		ArrayList<String> classes = new ArrayList<String>();
		for(int i=0;i<16;i++){
			classes.add(Integer.toString(i));
		}
		
		Attribute classAttribute = new Attribute("class",classes);
		attributes.add(classAttribute);
		
	}
	private static Instances defaultInstances = new Instances("default", attributes, DataCapacity);
	
	static{
		defaultInstances.setClassIndex(FeaturesCount);
	}
	
	
	public static Instances getDefaultInstances(){
		return defaultInstances;
	}
	
	private String filePath;
	
	
			
	private ObservableAgent currentAgent;
	private Environment currentEnvironment;
	
	
	
	public void createDataFile(){
		
	}
	
	public String getKeyPressClass(boolean[] keyStatus){
		String output;
		//boolean[] keyStatus = currentAgent.getAction();
		output = Integer.toString((int)( 
		booleanToFloat(keyStatus[Mario.KEY_JUMP])+
		booleanToFloat(keyStatus[Mario.KEY_LEFT])*2+
		booleanToFloat(keyStatus[Mario.KEY_RIGHT])*4+
		booleanToFloat(keyStatus[Mario.KEY_SPEED])*8));
		
		
		return output;
	}
	
	public boolean[] classToKeys(String keyClass){
		boolean[] output = new boolean[6];
		try{
			int keyClassInt = Integer.parseInt(keyClass);
			output[Mario.KEY_DOWN]=false;
			output[Mario.KEY_UP]=false;
			output[Mario.KEY_SPEED]=(keyClassInt/8) >0;
			keyClassInt %= 8;
			output[Mario.KEY_RIGHT] = (keyClassInt/4)>0;
			keyClassInt %= 4;
			output[Mario.KEY_LEFT] = (keyClassInt/2)>0;
			keyClassInt %= 2;
			output[Mario.KEY_JUMP] = keyClassInt>0;
			
			return output;
			
		}
		catch(NumberFormatException e){
			return null;
		}
		
	}
	
	
	
	public float booleanToFloat(boolean input){
		return input?1:0;
	}
	
	public List<Float> getTenClosestEnemies(){
		List<List<Float>> enemiesGrouped = new ArrayList<List<Float>>();
		for(int i=0; i<currentAgent.getEnemiesFloatPos().length;i+=3){
			List<Float> temp = Arrays.asList(currentAgent.getEnemiesFloatPos()[i],currentAgent.getEnemiesFloatPos()[i+1],currentAgent.getEnemiesFloatPos()[i+2]);
			enemiesGrouped.add(temp);
		}
		Comparator<List<Float>> myCompare = (List<Float> a, List<Float> b)->{
			return Float.compare(a.get(1), b.get(1));
		};
		enemiesGrouped.sort(myCompare);
		enemiesGrouped = enemiesGrouped.stream().limit(10).collect(Collectors.toList());
		BinaryOperator<List<Float>> listAppend = (List<Float> a, List<Float>b)->{
			List<Float> c = new ArrayList<Float>();
			c.addAll(a);
			c.addAll(b);
			return c;
		};
		List<Float> enemiesLinear ;
		try{
			enemiesLinear = new ArrayList(enemiesGrouped.stream().reduce(listAppend).get());
		}
		catch(NoSuchElementException e){
			enemiesLinear = new ArrayList<Float>();
		}
		while(enemiesLinear.size()<30){
			enemiesLinear.add((float)0);
		}
		return enemiesLinear;
		
		
	}
	
	
	private List<Float> getNearEnviroment(){
		int height = currentAgent.getLevelScene().length;
		int width = currentAgent.getLevelScene()[0].length;
		List<Float> output = new ArrayList<Float>();
		for(int i=fieldRowsStart; i<fieldRowsEnd;i++){
			for(int j=fieldColsStart;j<fieldColsEnd;j++){
				output.add((float)currentAgent.getLevelScene()[i][j]);
			}
		}
		return output;
	}
	
	private double[] listToArray(List<Float> input){
		int length = input.size();
		double[] output = new double[length];
		for(int i =0;i<length;i++){
			output[i] = input.get(i);
		}
		return output;
	}
	
	public int[] getActionFromClassifier(){
		//TODO:write function
		int[] output = new int[0];
		return output;
	}
	
	public Instance encodeStatus(){
		List<Float> input;
		input = statusToList();
		Instance output = new DenseInstance(FeaturesCount+1);
		for(int i=0;i<input.size();i++){
			output.setValue(i, input.get(i));
		}
		return output;
	}
	
	
	
	
	public List<Float> statusToList(){
		List<Float> output = new ArrayList<Float>();
		//enemy pos
		output.addAll(getTenClosestEnemies());
		
		//Mario status
		output.add(booleanToFloat(currentAgent.isMarioAbleToJump()));
		output.add(booleanToFloat(currentAgent.isMarioAbleToShoot()));
		output.add(booleanToFloat(currentAgent.isMarioOnGround()));
		//output.add(booleanToFloat(currentAgent.isMarioCarrying()));
		output.add((float)currentAgent.getMarioStatus());
		//output.add((float)currentAgent.getMarioMode());
		
		//levelScene
		output.addAll(getNearEnviroment());
		
		//class
		
		return output;
		/*MultilayerPerceptron myClassifier;
		Instances dataset = new Instances("eval",attributes,0);
		output.toArray(new double[0])
		Instance data = new DenseInstance(weight, attValues)
		myClassifier.classifyInstance(instance)*/
	}
	
	public static int appendToARFF(Instances dataSet, String fileName){
		ArffSaver saver = new ArffSaver();
		Instances toWrite = new Instances(dataSet);
		/*toWrite.delete();
		if(fromARFF(fileName)!=null){
			
			toWrite = dataSet;
		}
		else{
			toWrite = dataSet;
		}*/
		
		return saveToARFF(toWrite, fileName);
		
	}
	
	public static int saveToARFF(Instances dataset, String fileName){
		ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		try{
			saver.setFile(new File(fileName));
			
			saver.writeBatch();
			
		}
		catch(IOException e){
			return -1;
		}
		return 0;				
	}
	
	public static Instances fromARFF(String fileName){
		try{
			DataSource source  = new DataSource(fileName);
			Instances output = source.getDataSet();
			return output;
		}
		catch(Exception e){
			return null;
		}
	}
	
	
	
	public StatusRecorder(ObservableAgent agent){
		currentAgent = agent;
		
	}

}
