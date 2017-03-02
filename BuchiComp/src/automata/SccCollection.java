/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package automata;

import automata.SCC;
import java.util.ArrayList;

/**
 *
 * @author joel
 */
public class SccCollection{
    
    private final ArrayList<SCC> array;

    public SccCollection() {
        array = new ArrayList();
    }
    
    public ArrayList<SCC> getArray(){
        return array;
    }
    
    public void addOrJoinSCC(SCC scc){
        SCC union = new SCC();
        union.addAll(scc);
        for(int i=0; i<array.size(); i++){
            /* if an element intersects the new scc */
            if(array.get(i).intersects(union)){
                /* add to union */
                union.addAll(array.get(i));
                
                /* remove scc from array */
                array.remove(i);
                i++;
            } 
                
            
        }
        
        /* add union to array */
        array.add(union);
        
    }

    
    public void addOrJoinSCCs(SccCollection sccs){
        
        for(SCC scc : sccs.getArray()){
            this.addOrJoinSCC(scc);
        }
        
    }
    
    @Override
    public String toString(){
        String s="[";
        for(SCC scc : array){
            s=s+ scc.getString() + " ";
        }
        s=s+ "]";
        return s;
    }
   
    
}
