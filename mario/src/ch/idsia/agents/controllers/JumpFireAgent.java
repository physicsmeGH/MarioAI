package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.benchmark.mario.environments.MarioEnvironment;

/**
 * Created by IntelliJ IDEA.
 * User: Sergey Karakovskiy
 * Date: Apr 25, 2009
 * Time: 12:27:07 AM
 * Package: ch.idsia.agents.controllers;
 */

public class JumpFireAgent extends BasicMarioAIAgent implements Agent
{
    public JumpFireAgent()
    {
        super("JumpFireAgent");
        reset();
    }

    public boolean[] getAction()
    {
    	int enemyState = (int)(dangerOfEnemy()[0]);
    	forward();
    	switch (enemyState){
    		case 0:
    			//no danger
    			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
    			break;
    		case 1:
    			
    			//enemy, lower or same height
    			if(isMarioAbleToShoot){
        			action[Mario.KEY_SPEED] = true;
        		}
        		else{
        			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
        		}
    			break;
    		case 2:
    			retreat();
    			
    			break;
    			
    			//enemy, higher
    			
    			    		
    	}
    	
        
        return action;
    }
    private float[] getClosestEnemyPos(){
    	float[] result = new float[3];
    	result[0] = (float)0;
    	result[1] = (float)20000000;
    	
    	for(int i=0; i<this.enemiesFloatPos.length; i+=3){
    		float relativeXPos = enemiesFloatPos[i+1];
    		float relativeYPos = enemiesFloatPos[i+2];
    		if(relativeXPos >0 && relativeXPos < result[1] ){
    			result[0] = 1;
    			result[1] = relativeXPos;
    			result[2] = relativeYPos;
    		}
    	}
    	
    	return result;
    	
    }
    private float[] dangerOfEnemy(){
    	float[] result = getClosestEnemyPos();
    	if((int)result[0]>0){
    		if(result[1]<20){
    			if(result[2]>0 || result[2]<-50){
    				//lower or higher but very far
    				result[0] = 0;
    			}
    			else if(result[2]> -10){	
    				//same height
    				result[0] = 1;
    			}
    			else{
    				//higher
    				result[0] = 2;
    			}
    		}
    		else{
    			result[0] = 0;
    		}
    	}
    	
    	//Float[] enemyPos = 
    	
    	
    	return result;
    }
    
    public void retreat(){
    	action[Mario.KEY_RIGHT] = false;
    	action[Mario.KEY_LEFT] = true;
    	action[Mario.KEY_SPEED] = true;
    	action[Mario.KEY_JUMP] = false;
    }
    
    public void forward(){
    	action[Mario.KEY_LEFT] = false;
    	action[Mario.KEY_RIGHT] = true;
    	
    }
    public void reset()
    {
        action = new boolean[Environment.numberOfButtons];
        action[Mario.KEY_RIGHT] = true;
        action[Mario.KEY_SPEED] = true;
    }
}
