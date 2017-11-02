/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LR0_parser;

import java.util.ArrayList;

/**
 *
 * @author admin
 */
public class State {
    ArrayList<Item> items;    
    boolean accept = false;
    int stateIndex = -1; // variable used in computing automata only
    boolean visited = false; // variable used in computing closure only
    void visited(){visited=true;} // function used in computing closure only
    void setStateIndex(int index){stateIndex=index;} // function used in computng automata only 
    
    State(ArrayList<Item> setOfItems){
        items = setOfItems;

        for(int i=0;i<items.size();i++){
            if(items.get(i).dotPos == items.get(i).rhs.length()){
                accept = true;
                break;
            }
        }        
    }
    public boolean isNull(){
        if(items.isEmpty()) return true;
        return false;
    }
      @Override
    public boolean equals(Object object)
    {
        State state = (State)object;
        if(this.items.size() != state.items.size())
            return false;
        else{
            for(int i=0;i<state.items.size();i++)
            {
                if(!this.items.contains(state.items.get(i)))
                    return false;
            }
        }
        
        return true;
    }
}
