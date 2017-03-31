import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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

        ArrayList<Pair<Character, String>> mapping = new ArrayList<>(frequencyArray.length);

        HuffmanTree tree = HuffmanCode.buildTree(frequencyArray);
        HuffmanCode.fillMappingTable(tree, new StringBuffer(), mapping);

        for (Pair<Character, String> pair : mapping)
        {
            System.out.println(pair.getKey() + " : " + pair.getValue());
        }
    }
}