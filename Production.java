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
public class Production {
    char lhs;
    String rhs;
    //boolean nullable;
    Production(char l, String h){
        lhs=l;
        rhs=h;
        /*if(rhs.contains("e"))
            nullable = true;
        else 
            nullable = false;*/
    }
    
    Production (Item item){  // Constructing used in constructing table only
        if(item!=null){
            lhs = item.lhs;
            rhs = item.rhs;
        }
    }
}
