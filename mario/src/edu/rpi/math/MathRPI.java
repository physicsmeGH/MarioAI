package edu.rpi.math;

import java.lang.Math;
import java.util.*;

public class MathRPI {
	
	public static interface singleVarFunction{
		public double operation(double input);
	}
	
	public static Map<singleVarFunction, singleVarFunction> singleVarDerivatives = 
			new HashMap<singleVarFunction,singleVarFunction>();
	
	public static singleVarFunction tanH = (double input) ->{return Math.tanh(input);};
		
	public static singleVarFunction derivativeOfTanH = 
			(double input)->{return 1 - Math.pow(Math.tanh(input), 2);};
			
	static{
		singleVarDerivatives.put(tanH, derivativeOfTanH);
	}
			
	
	public MathRPI() {
		// TODO Auto-generated constructor stub
	}

}
