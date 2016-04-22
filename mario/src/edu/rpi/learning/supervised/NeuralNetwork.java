package edu.rpi.learning.supervised;

import java.util.*;

import weka.classifiers.*;

import java.lang.Math;

import edu.rpi.math.*;

import org.jblas.*;

public class NeuralNetwork {
	
	private int layerCount;
	
	private List<Integer> nodesCount;
	
	private List<DoubleMatrix> weights;
	
	private List<DoubleMatrix> signalsIn;
	
	private List<DoubleMatrix> signalsOut;
	
	private List<DoubleMatrix> deltas;
	
	private List<DoubleMatrix> gradients;
	
	private DoubleMatrix y;
	
	private MathRPI.singleVarFunction transformationFunction;
	
	private MathRPI.singleVarFunction transformationFunctionPrime;
	
	private double learningRate;
	
	private double inSampleError;
	
	private Random random;
	
	private List<List<List<Double> > > trainingData;
	
	private List<List<List<Double> > > testingData;
		
	private DoubleMatrix a;
	
	private int epoch;
	
	private void transferLayer(int layerDepth){
		DoubleMatrix augmentedOut = DoubleMatrix.concatVertically(DoubleMatrix.eye(1),signalsOut.get(layerDepth-1)); 
		signalsIn.get(layerDepth).copy((weights.get(layerDepth)).mmul(augmentedOut));
		int nodesCountInThisLayer = nodesCount.get(layerDepth);
		
		//transformation function
		DoubleMatrix tempVector = mapFunctionToMatrix(transformationFunction, signalsIn.get(layerDepth));
		
		signalsOut.set(layerDepth, tempVector);
		//signalsOut.get(layerDepth).copy(DoubleMatrix.concatVertically(DoubleMatrix.eye(1),tempVector));
		
	}
	
	
	private void setNodesCount(int layerDepth, int nodesCount){
		this.nodesCount.set(layerDepth, nodesCount);
	}
	
	public void fillMatrixArray(List<DoubleMatrix> matrixArray, int count){
		for(int i=0;i<count;i++){
			matrixArray.add(new DoubleMatrix());
		}
	}
	
	private void initLayer(int layerDepth){
		int rows, columns;
		rows = nodesCount.get(layerDepth);
		signalsOut.set(layerDepth, new DoubleMatrix(rows));
		signalsIn.set(layerDepth, new DoubleMatrix(rows));
		if(layerDepth > 0){
			columns = nodesCount.get(layerDepth - 1) + 1;
			DoubleMatrix matrix = DoubleMatrix.rand(rows, columns).mmul(0.01);
			weights.set(layerDepth, matrix);
			gradients.set(layerDepth, matrix.mul(0));
			//deltas.set(layerDepth, new DoubleMatrix(rows));
		}
	}
	
	
	public DoubleMatrix mapFunctionToMatrix(MathRPI.singleVarFunction foo, DoubleMatrix input){
		int rows, columns;
		rows = input.getRows();
		columns = input.getColumns();
		DoubleMatrix output = DoubleMatrix.ones(rows, columns);
		for(int i=0;i<rows*columns;i++){
			output.put(i,foo.operation(input.get(i)));
		}	
		return output;
	}
	
	public void setHiddenLayerNodesCount(int layerDepth, int nodesCount){
		if(layerDepth!= 0 && layerDepth != layerCount){
			setNodesCount(layerDepth, nodesCount);
		}
	}

	public NeuralNetwork() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Construction of a feed-forward neural network instance
	 * @param layerCount the number of layers in the network, including input and output layers.  
	 * @param nodesCount the number of nodes in each layer
	 */
	public NeuralNetwork(int layerCount, List<Integer> nodesCount){
		this.layerCount = layerCount;
		this.nodesCount = new ArrayList<Integer>(nodesCount);		
		this.signalsIn = new ArrayList<DoubleMatrix>();
		fillMatrixArray(signalsIn, layerCount);
		this.signalsOut = new ArrayList<DoubleMatrix>();
		fillMatrixArray(signalsOut, layerCount);
		this.weights = new ArrayList<DoubleMatrix>();
		fillMatrixArray(weights, layerCount);
		this.deltas = new ArrayList<DoubleMatrix>();
		fillMatrixArray(deltas, layerCount);
		this.gradients = new ArrayList<DoubleMatrix>();
		fillMatrixArray(gradients, layerCount);
		random = new Random();
		
	}
	public void init(){
		transformationFunction = MathRPI.tanH;
		transformationFunctionPrime = MathRPI.singleVarDerivatives.get(transformationFunction);
		inSampleError = 0;
		org.jblas.util.Random.seed(1000);
		//set all weight matrices to small random numbers
		for(int i=1;i<layerCount;i++){
			initLayer(i);
		}
		epoch = 0;
		
		random.setSeed(1000);
	}
	
	private void calculateGradient(List<Double> dataX, List<Double> dataY){
		forwardPropagate(dataX);
		backwardPropagate(dataY);
		DoubleMatrix deltaY = this.y.sub(signalsOut.get(layerCount-1));
		
		inSampleError = inSampleError + deltaY.dot(deltaY)/y.getLength();
		for (int i=1;i<layerCount;i++){
			DoubleMatrix onePointGradient = signalsOut.get(i-1).mmul(deltas.get(i).transpose());
			gradients.set(i, onePointGradient);
			weights.set(i, weights.get(i).add(gradients.get(i)));
		}
	}
	
	private void gradientOneEpoch(){
		/*
		for(List<List<Double> > dataPair:trainingData){
			calculateGradient(dataPair.get(0),dataPair.get(1));
		}*/
		int sampleSize = trainingData.size();
		trainingData.stream().forEach(dataPair -> {calculateGradient(dataPair.get(0),dataPair.get(1));});
		inSampleError = inSampleError/sampleSize;
	}
	
	public void learn(){
		this.inSampleError = 0;
		for(int i=0;i<layerCount;i++){
			//gradients.set(i, DoubleMatrix.zeros(rows, columns))
		}
		
	}
	
	
	/**
	 * takes an input vector and forward propagates it through the network
	 * has no return value, output is stored in the Class' private member 
	 * @param input the input vector as a double list
	 */
	public void forwardPropagate(List<Double> input){
		ArrayList<Double> output = new ArrayList<Double>();
		//input 
		//Layer 0, i.e. input layer has no transformation function
		signalsOut.get(0).copy(new DoubleMatrix(input));
		for(int i=1; i<layerCount; i++){
			//transformation function
			transferLayer(i);				
		}
	}
	
	public List<Double> predict(List<Double> input){
		forwardPropagate(input);
		int outputCount =  nodesCount.get(layerCount-1);
		List<Double> output = new ArrayList<Double>();
		for(int i=0;i<outputCount;i++){
			output.add(signalsOut.get(layerCount-1).get(i));
		}
		return output;
	}
	
	
	private void backOneLayer(int layerDepth){
		DoubleMatrix thetaPrime = mapFunctionToMatrix(transformationFunctionPrime, signalsIn.get(layerDepth)); 
		DoubleMatrix weightedDelta = 
				(weights.get(layerDepth+1).transpose().mmul(deltas.get(layerDepth+1))).getRange(1, nodesCount.get(layerDepth)+1);
		deltas.set(layerDepth, thetaPrime.mul(weightedDelta));
		
	}
	
	public DoubleMatrix listToVector(List<Double> input){
		int length = input.size();
		DoubleMatrix output = DoubleMatrix.zeros(length);
		for(int i=0;i<length;i++){
			output.put(i, input.get(i));
		}
		return output;
	}
	
	public void backwardPropagate(List<Double> dataY){
		//calculate delta for weight matrix in the last layer
		this.y = listToVector(dataY);
		DoubleMatrix deltaY =(y.sub(signalsOut.get(layerCount-1)));
		
		deltas.set(layerCount-1, 
				deltaY.mul(
						mapFunctionToMatrix(
								this.transformationFunctionPrime,
								signalsIn.get(layerCount-1)
						)
				)
		);
		
		
		for(int i=layerCount-2;i>0;i--){
			backOneLayer(i);
		}
	}
	
	public static void main(String[] args){
		
		List<Integer> nodes = new ArrayList<Integer>();
		nodes.add(3);
		nodes.add(4);
		nodes.add(5);
		nodes.add(1);
		NeuralNetwork myNN = new NeuralNetwork(4,nodes);
		myNN.init();
		List<Double> input = new ArrayList<Double>();
		input.add(1.0);
		input.add(5.2);
		input.add(3.1);
		myNN.forwardPropagate(input);
		System.out.println(myNN.predict(input).toString());
		w
		
		myNN.y= new DoubleMatrix(1);
		myNN.y.put(0, 5.9);
		List<Double> output = new ArrayList<Double>();
		output.add(5.9);
		myNN.backwardPropagate(output);
		
	}
	
	
}
