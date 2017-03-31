import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

        String filePath             = "testText";
        String decodedFileExtension = "dec1";

        WriteRandomlyToFile(charCount, filePath, frequencyMap);
        EncodeFile();
        DecodeFile();
    }

    public static void WriteRandomlyToFile(int count,
                                           String filePath,
                                           Map<Object, Integer> frequencyMap)
    {
        RandomSelector randomSelector = new RandomSelector<>(frequencyMap);
        try (FileWriter fileWriter = new FileWriter(filePath))
        {
            for (int i = 0; i < count; i++)
            {
                fileWriter.write(randomSelector.next().toString().toCharArray());
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while writing random text: " + ioe);
        }
    }

    public static void EncodeFile(String filePath, Map<Object, String> encoding)
    {
        String encodedFileExtension = ".enc1";
        //@formatter:off
        try (FileReader fileReader = new FileReader(filePath);
             FileWriter fileWriter = new FileWriter(filePath + encodedFileExtension))
        {//@formatter:on

            int min = Integer.MAX_VALUE;
            int max = 0;
            for (Object o : encoding.keySet())
            {
                if ()
            }

            for (int c = fileReader.read(); c > -1; c = fileReader.read())
            {
                if (stringBuilder.)
                fileWriter.write();
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while writing random text: " + ioe);
        }
    }
}