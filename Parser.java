/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LR0_parser;

import com.google.common.collect.HashBasedTable;
import static java.lang.Character.isUpperCase;
import java.util.ArrayList;
import java.util.HashSet;

import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author admin
 */

public class Parser {
    ArrayList<Production> productions = null;
    ArrayList<State> states = null;
    ArrayList<Character> symbol = null;
    int stateIndex = 0;
    Table<Integer, Character, String> table = HashBasedTable.create(); // Parsing table ( stateNumber, symbol, actionString )
    Stack<State> stack = null; // Stack that contains states, use for parsing
    boolean LR0 = true; // Signal an error if turned on - not that kind of turned on - but you know what i mean lol
    int numOfStartSymbolProd; // This variabl signifies the number of start symbol productions in the grammar,
                            // used to compute closure of the initial set of items
    int R_R_CONFLICT = -2;
    Parser(ArrayList<String> listOfProductions){ // initialize the parser by passing in a list of productions
        productions = new ArrayList<>();        
        HashSet<Character> temp = new HashSet<>();
        symbol = new ArrayList<>();
        
        numOfStartSymbolProd = Integer.valueOf(listOfProductions.get(0)); 
        
        for(int i=1;i<listOfProductions.size();i++){ // Starting from 1 because line 0 is for numOfStartSymbolProd
            String currentProduction = listOfProductions.get(i);
            char leftHandSide = currentProduction.charAt(0);
            String rightHandSide = currentProduction.split(":")[1]; // a production has the form A:w   
            productions.add(new Production(leftHandSide,rightHandSide));
            
            temp.add(leftHandSide);
            char[] rhs = rightHandSide.toCharArray();
            for(int j=0;j<rhs.length;j++){
                temp.add(rhs[j]);
            }              
        } 
        symbol.addAll(temp);                         
    }

    public State closure(ArrayList<Item> initial){ 
        
        ArrayList<Item> setOfItems = new ArrayList<>();        
        setOfItems.addAll(initial);
        boolean changed = true;
        
       while(changed){ // fixed-point approach to compute closure
            int oldSize = setOfItems.size();
            for(int i=0;i<setOfItems.size();i++)
            {
                if(setOfItems.get(i).visited == false){ // If an item is not visited
                    setOfItems.get(i).visited(); // Mark it as visited
                    Item curItem = setOfItems.get(i);
                    
                    if(curItem.dotPos < curItem.rhs.length()){ // only process if dot is not at the end of rhs
                        char dotSymbol = curItem.rhs.charAt(curItem.dotPos);
                        for(int j=0;j<productions.size();j++)
                        {
                            Production curProd = productions.get(j);
                            if(isUpperCase(dotSymbol) && curProd.lhs == dotSymbol){ // if dotSymbol is a terminal
                                                                                    // add all productions whose
                                                                                    // first symbol of rhs matches dotSymbol
                                Item newItem = new Item(curProd.lhs,curProd.rhs);
                                if(!setOfItems.contains(newItem))                   // If this new item is not yet in the set
                                    setOfItems.add(newItem);                         // Add it to the set
                          }
                        }
                    }
                }
            }
            changed = (setOfItems.size() != oldSize);
       }
      return new State(setOfItems);    
    }
       
    public State Goto(State state, char symbol){        
        ArrayList<Item> initial = new ArrayList<>();         
        
        for(int i=0;i<state.items.size();i++){            
            Item item = state.items.get(i);            
            if(item.dotPos < item.rhs.length()){                
                char dotSymbol = item.rhs.charAt(item.dotPos);               
                
                if(dotSymbol == symbol) { // if there is an item whose dotSymbol is equal to the non-terminal in interest
                                           // move the dot left one unit and add it to the initial set of item for closure                                       
                    
                    Item newItem = new Item(item.lhs,item.rhs, item.dotPos);    // Has to create  a new item
                                                                // Otherwise the dotPos of the original item will also be
                                                                //  moved one position right, leading to incorrect results
                    newItem.dotPos++;   // Move the dot over
                    initial.add(newItem);                   
                }            
            }
        }        
        return closure(initial);
    }
    
    public void initializeParser(){ // the main function that builds the automata + build the parsing table
        System.out.println(productions.size());        
        ArrayList<Item> initialItem = new ArrayList<>();
        
        for(int i=0;i<numOfStartSymbolProd;i++){
            Production firstProduction = productions.get(i); // By default the initial production is the initial item

            initialItem.add(new Item(firstProduction.lhs, firstProduction.rhs));
        }
        
        states = new ArrayList<>();
        State initialState = closure(initialItem);   // Compute the initial state by taking closure
                                                        // of set of items       
        initialState.stateIndex = states.size(); // indexing the initial state                                                
        states.add(initialState);   // Add the initial state to the automata ( set of states ) 
                
        boolean changed = true;   

        while(changed){            // fixed point approach to compute the automata 
            int oldSize = states.size();            
            for(int i=0;i<states.size();i++)
            {
                if(states.get(i).visited == false){
                    states.get(i).visited();
                    State fromState = states.get(i);    

                    for(int j=0;j<symbol.size();j++){  // Compute GOTO for all the nonterminals   
                        String actionToTake = "";
                        State toState = Goto(fromState, symbol.get(j));
                         if(!toState.isNull())  // if the destination state is an accepting state then signals the reduction
                                                 // on empty string , retrieve the appropriate production to reduce
                        {
                             toState.stateIndex = states.size();     // Indexing the state    
                             states.add(toState);                    // add it to the set of states    
                            if(toState.accept)
                            {
                                if(fromState.accept) // if both states signal a reduce and there is a transition between them
                                                    // then this is a shift-reduce conflict
                                {
                                    System.out.println("Shift-reduce conflict, not LR(0) grammar");
                                    LR0 = false;
                                    return;
                                }
                                int prodToReduce = itemToFinalProduction(stateToFinalItem((toState)));
                                if(prodToReduce == R_R_CONFLICT){
                                    System.out.println("Reduce-reduce conflict, not LR(0) grammar");
                                    LR0 = false;
                                    return;
                                }
                               
                                actionToTake = "r" + prodToReduce;
                                table.put(toState.stateIndex, 'e', actionToTake ); // put reduction move in ( toState, 'e' )
                            
                            }

                            if(isUpperCase(symbol.get(j)))          // if the transition symbol is nonTerm then fill integer                           
                                actionToTake = String.valueOf(toState.stateIndex);                            

                            else                                    // Otherwise fill "s" + destination_state
                                actionToTake = "s"+toState.stateIndex;  
                           
                            table.put(fromState.stateIndex,symbol.get(j), actionToTake); // Fill in the action for the entry    
                                                                                                       
                        }
                    }      
                }
            }
            changed = (states.size() != oldSize);
        } // done constructing automata + parsing table 
        
        table.put(0, 'S',"accept"); // Special entry that signals acceptance of sentence
        
    }    
    
    public void parsing(char[] input){
        if(!LR0){
            System.out.println("Grammar is not LR(0), can't even begin to parse!");
            return;
        }
        stack = new Stack<>(); // initialize the stack
        stack.push(states.get(0));
        int i=0; // index of input
        
        while(i<=input.length)
        {
            char nextChar;
            String action ="none";            
            State top = stack.peek(); // Retrieve the topStack           
            
            
            if(top.accept)
                            { // If the top State is an accepting state, reduce by the reduction 
                            // indicated by the entry 
                nextChar='e';
                               
                action = table.get(top.stateIndex,'e'); // Get the action reduce on empty string
                int prodIndex = Character.getNumericValue(action.charAt(1));
                String rhs = productions.get(prodIndex).rhs; // Get the rhs of the production
                nextChar = productions.get(prodIndex).lhs; // The lhs of the production
                
                
                for(int j=0;j<rhs.length();j++) // Pop the number of states equivalent to rhs's length
                    stack.pop();            
               
                State fromState = stack.peek(); // This state is used to transition on the nextChar
                
                if(fromState.stateIndex==0 && nextChar=='e') break;
               
                
                // If entry signals acceptance, break out of the loop
                if( table.get(fromState.stateIndex, nextChar) == "accept" ) break;
                                
                   // Transitions on a nonterminal is definitely a integer ( GOTO entry )
                State toState = states.get(Integer.parseInt(table.get(fromState.stateIndex, nextChar)));                   
                stack.push(toState);
                
                System.out.println("reduce by " + nextChar + " -> " + rhs + "\t\t to state " + toState.stateIndex);
                              
              
            }else
            { // if the top State is not an accepting state, then read the next input to transition                
                nextChar = input[i];              
                                       
                
                action = table.get(top.stateIndex, nextChar);   
                
                State toState = states.get(Integer.valueOf(action.substring(1))); // Rtrieve the desintation state 
                                                                             // indicated by the action string
               
                stack.push(toState);
                i++;
                
                System.out.println("Shift from " + top.stateIndex + "\t on char '" + nextChar + "'\t to State " + toState.stateIndex + "\t action: s"+Integer.valueOf(action.substring(1)));
            }                             
        }          
        if( stack.peek().stateIndex!=0 )
        {
            System.out.println("cannot recognize this sentence, topState is "+stack.peek().stateIndex);
            return;
        }else
        {
            System.out.println("Parsed successfully \n\t" + Arrays.toString(input) + " does belong to the grammar described in grammar.txt");
        }
    }    
    

    
    // Utilites
    public Item stateToFinalItem(State state){ // Given an accepting state, return a final item
        if(!state.accept)
            return null;        
        int reduce_reduce_flag = 0; // flag for reduce-reduce-conflict
        int count=0;
        Item retItem = new Item('S',"");
        for(int i=0;i<state.items.size();i++){
            Item curItem = state.items.get(i);            
            if(curItem.dotPos == curItem.rhs.length())
            {
                 retItem = new Item(curItem.lhs, curItem.rhs,reduce_reduce_flag=(count>1)?(R_R_CONFLICT):(-1) ); // turn the flag if
                                                              // there are two productions in the same state that
                                                              // signal reductions
                 count++;
            }
          
        }
        return retItem;
    }
    public int itemToFinalProduction(Item item){
        if( item.dotPos >0 && item.dotPos!=item.rhs.length())
            return -1;
        
        if( item.dotPos == R_R_CONFLICT) return R_R_CONFLICT; // Signal reduce-reduce conflict
        
        for(int i=0;i<productions.size();i++)
        {
            Production curProd = productions.get(i);
            if(item.lhs == curProd.lhs && item.rhs == curProd.rhs)
                return i;
        }
        return -1;
    }
    public void printState(State currentState){
           
            for(int j=0;j<currentState.items.size();j++){
                Item curItem = currentState.items.get(j);
                System.out.println(" " + curItem.lhs + " -> " + curItem.rhs + " \tdotPos = "+ curItem.dotPos);                
            }
    }
    
    public void printStates(){
        System.out.println("Printing states: ");
        for(int i=0;i<states.size();i++){
            System.out.println("S"+ states.get(i).stateIndex + ": ");
            State currentState = states.get(i);
            printState(currentState);
        }        
        // Print the action entries for each state
        table.rowKeySet().stream().map((key) -> {
            // For each key,
            System.out.println("State " + key + ":");
            return key;
        }).forEachOrdered((key) -> {
            table.row(key).entrySet().forEach((row) -> {
                // For each entry of the entry set of that key
                System.out.println("\tsymbol :" + row.getKey() + " \taction :" + row.getValue()); // Print the entry
            });
        });        
    }        
}
