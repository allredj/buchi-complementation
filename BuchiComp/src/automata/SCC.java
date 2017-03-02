package automata;

import java.util.HashSet;

/**
 *
 * @author joel
 */
public class SCC extends HashSet<State>{

    public boolean intersects(SCC scc){
        for(State stateHere : this){
            if(scc.contains(stateHere)){
                return true;
            }
        }
        return false;
    }
    
    public String getString(){
        String s="{";
        State[] set = new State[this.size()];
        for(State state :  this.toArray(set)){
            s=s+state.getLabel() + " ";
        }
            
        s=s+"}";
        return s;
    }
  
    
}

