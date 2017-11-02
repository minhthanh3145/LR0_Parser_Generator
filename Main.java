/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LR0_parser;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author admin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
               
        ArrayList<String> Productions = new ArrayList<>();
        

        Scanner stdin = new Scanner(new BufferedInputStream(System.in)); // read the grammar productions
         try {

            File f = new File("grammar.txt");
            BufferedReader b = new BufferedReader(new FileReader(f));
            String readLine = "";
            System.out.println("Reading file using Buffered Reader");
            while ((readLine = b.readLine()) != null) {
                System.out.println(readLine);
                Productions.add(readLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }       
        // Initialize the parser
        Parser parser = new Parser(Productions);
        parser.initializeParser();

//        parser.printStates(); // Un-comment to see the details of states
        char[] input =  read("input.txt");  // Read the input file\        
        parser.parsing(input);     
        
        
        
    }
    
    public static char[] read(String name) throws IOException {
    
        File file = new File(name);
        
        CharSource source = Files.asCharSource(file, Charsets.UTF_8); 
            
        String result = source.read().replaceAll("\r\n",""); // remove the unnecessary char     
        
        char[] res = result.toCharArray();
   
        return res;
    }   
    
}
