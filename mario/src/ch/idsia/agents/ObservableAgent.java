package ch.idsia.agents;

public interface ObservableAgent extends Agent{
	
	float[] getMarioFloatPos ();
    float[] getEnemiesFloatPos() ;

    //protected int[] getMarioState;

    int getMarioStatus();
    int getMarioMode();
    boolean isMarioOnGround();
    boolean isMarioAbleToJump();
    boolean isMarioAbleToShoot();
    boolean isMarioCarrying();
    byte[][] getLevelScene();
    
}
