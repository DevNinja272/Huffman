import java.util.*;
import java.io.*;

public class Encoder
{
	public static void main(String args[])
	{
		String nameOfFile = args[0];
		int kCharacters = Integer.parseInt(args[1]);
		int sum = 0;

		ArrayList<Integer> probabilities = new ArrayList();

		try(BufferedReader inputstream = new BufferedReader(new FileReader(nameOfFile))) 
		{
			for(String line; (line = inputstream.readLine()) != null;) 
			{
				Integer currentInt = Integer.parseInt(line);
				probabilities.add(currentInt);
				sum += currentInt;
			}
		}
		catch(IOException ioe)
		{
			System.out.println("Error while reading file: " + ioe.toString());
			return;
		}

		int[] probArray = new int[probabilities.size()];
		int index = 0;
		for (Integer freq : probabilities) 
		{
			probArray[index++] = freq;
		}

		Huffman.Node huffmanTree = Huffman.buildTrie(probArray);
		Huffman.writeTrie(huffmanTree);
	}
}