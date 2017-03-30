import java.nio.file.Paths;
import java.util.*;
import java.io.*;

public class Encoder
{
    public static void main(String args[])
    {
        String filePath  = args[0];
        int    charCount = Integer.parseInt(args[1]);
        int    sum       = 0;

        ArrayList<Integer> frequencies = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)))
        {
            for (String line; (line = bufferedReader.readLine()) != null; )
            {
                Integer currentInt = Integer.parseInt(line);
                frequencies.add(currentInt);
                sum += currentInt;
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Error while reading file: " + ioe.toString());
            System.exit(-1);
        }

        int[] frequencyArray = new int[frequencies.size()];
        for (int i = 0; i < frequencyArray.length; frequencyArray[i] = frequencies.get(i++)) { }

        Huffman.R = frequencyArray.length;
        Huffman.Node huffmanTree = Huffman.buildTrie(frequencyArray);
        Huffman.writeTrie(huffmanTree);
    }
}