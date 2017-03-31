import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class Encoder
{
    public static void main(String args[])
    {
        String filePath  = args[0];
        int    charCount = Integer.parseInt(args[1]);
        int    sum       = 0;
        char   currChar  = 'a';

        Map<Object, Integer> frequencyMap = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)))
        {
            for (String line; (line = bufferedReader.readLine()) != null; )
            {
                Integer currentInt = Integer.parseInt(line);
                frequencyMap.put(currChar++, currentInt);
                sum += currentInt;
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Error while reading file: " + ioe.toString());
            System.exit(-1);
        }

        HuffmanTree tree = HuffmanCode.buildTree(frequencyMap);

        Map<Object, String> mapping = new HashMap<>(frequencyMap.size());
        HuffmanCode.fillMappingTable(tree, new StringBuffer(), mapping);

        for (Object o : mapping.keySet())
        {
            System.out.println(o + " : " + mapping.get(o));
        }

        printRandomCharacters(charCount, "testText", frequencyMap);
    }

    public static void printRandomCharacters(int k,
                                             String filePath,
                                             Map<Object, Integer> frequencyMap)
    {
        RandomSelector randomSelector = new RandomSelector<>(frequencyMap);
        try (FileWriter fileWriter = new FileWriter(filePath))
        {
            StringBuilder sb = new StringBuilder(k);
            for (int i = 0; i < k; i++)
            {
                sb.append(randomSelector.next().toString().toCharArray());
            }

            Map<Object, Integer> counts = new HashMap<>(4);
            counts.put('a', 0);
            counts.put('b', 0);
            counts.put('c', 0);
            counts.put('d', 0);

            char c;
            for (int i = 0; i < k; i++)
            {
                c = sb.charAt(i);
                counts.put(c, counts.get(c) + 1);
            }

            for (Object o : counts.keySet())
            {
                System.out.println(o + " : " + counts.get(o));
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while writing random text: " + ioe);
        }
    }
}