package edu.rpi.learning.supervised;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import ch.idsia.agents.controllers.StatusRecorder;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.*;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class Classifications {

	public Classifications() {
		// TODO Auto-generated constructor stub
		
	}
	
	public static void makeNNClassifier(String fileName){
		MultilayerPerceptron nnClassifier = new MultilayerPerceptron();
		nnClassifier.setAutoBuild(true);
		nnClassifier.setNormalizeAttributes(true);
		nnClassifier.setGUI(true);
		
		Instances dataSet = fromARFF(fileName);
		try {
			nnClassifier.buildClassifier(dataSet);
			ObjectOutputStream modelSaver = new ObjectOutputStream(
					new FileOutputStream("NeuralNetwork.model"));
			modelSaver.writeObject(nnClassifier);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		try {
			Evaluation eva = new Evaluation(dataSet);
			eva.evaluateModel(nnClassifier, dataSet);
			System.out.println(eva.toSummaryString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
	
	private static Instances fromARFF(String fileName){
		try{
			DataSource source  = new DataSource(fileName);
			Instances output = source.getDataSet();
			output.setClassIndex(output.numAttributes()-1);
			return output;
		}
		catch(Exception e){
			return null;
		}
	}
	
	public static void main(String[] argv){
		makeNNClassifier("dataSave.arff");
	}

}
