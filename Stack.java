/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LR0_parser;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 *
 * @author admin
 */
public class Stack<E> {
   	private ArrayList<E> al;
 
	public Stack() {
 		al = new ArrayList<E>();
 	}
 
	public void push(E item) {
 		al.add(item);
 	}
 
	public E pop() {
 		if (!isEmpty())
 			return al.remove(size()-1);
 		else
 			throw new EmptyStackException();
 	}
 
	public boolean isEmpty() {
 		return (al.size() == 0);
 	}
	
	public E peek() {
 		if (!isEmpty())
 			return al.get(size()-1);
 		else
 			throw new EmptyStackException();
 	}
 
	public int size() {
 		return al.size();
 	}
 
	@Override
	public String toString() {
		return "MyStack [al=" + al.toString() + "]";
 
 	}
}
