package ch.idsia.agents.controllers.human;

import ch.idsia.agents.Agent;
import org.jblas.*;

public class HumanTeacherKeyboardAgent extends HumanKeyboardAgent implements Agent {

	public HumanTeacherKeyboardAgent() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void recordEnvironmentAndKeyPressed(){
		
	}
	
	public boolean[] getAction(){
		boolean[] actions =	super.getAction();
		
		
		return actions;
		
		
	}

}
