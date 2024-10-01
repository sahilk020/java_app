package com.pay10.easebuzz;

public class Esaebuzzencry {

	 public static void main(String[] args) {        
	        StringBuilder result = getBinary("hi there", 2);        
	        System.out.println(result.toString());      
	        }

	    public static StringBuilder getBinary(String str, int numberOfCharactersWanted) {
	        StringBuilder result = new StringBuilder();
	        byte[] byt = str.getBytes();       
	        for (int i = 0; i < numberOfCharactersWanted; i++) {        
	            result.append(String.format("%8s", Integer.toBinaryString(byt[i])).replace(' ', '0')).append(' ');
	        }
			return result;}
}
