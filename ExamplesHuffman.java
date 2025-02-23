import huffman.*;
import tester.Tester;
import java.util.ArrayList;

 
class ExamplesHuffman {
  ArrayList<String> s1 = new ArrayList<String>();
  ArrayList<String> s2 = new ArrayList<String>();
  ArrayList<String> s3 = new ArrayList<String>();
  
  s2.add("some");
  
  ArrayList<Integer> num1 = new ArrayList<Integer>();
  
  ArrayList<Boolean> b1 = new ArrayList<Boolean>();
  
  Huffman h1 = new Huffman(s1, num1);
  
  
  // test constructor
  
  // test1 : basic functionality
  // test2 : one is longer than the other
  // test3 (?) : empty arrays
  
  // test encode
  // test1 : basic functionality
  // test2 : check for string containing any character not in alphabet
  
  // test decode
  // test1 : basic functionality
  // test2 : boolean list doesn't reach node
}