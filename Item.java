/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LR0_parser;

/**
 *
 * @author admin
 */
public class Item {
    char lhs;
    String rhs;
    int dotPos;
    
    boolean visited = false; // Similar to that described in state
    void visited(){visited=true;} // similar to that described in state
    Item(char l, String r){
        lhs=l;
        rhs=r;
        dotPos=0;
    }
    Item(char l, String r, int pos){
        lhs=l;
        rhs=r;
        dotPos=pos;
    }

    
       @Override
    public boolean equals(Object object)
    {
        Item item = (Item)object;
        if(this.lhs == item.lhs && this.rhs.equals(item.rhs) && this.dotPos == item.dotPos)
            return true;
        
        return false;
    }
}
