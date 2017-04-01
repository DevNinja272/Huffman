import java.awt.dnd.InvalidDnDOperationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.lang.Math;
import java.util.*;

public class Encoder
{
    private static String randomTextFilePath   = "testText";
    private static String decodedFileExtension = ".dec1";
    private static String encodedFileExtension = ".enc1";
    private static String decodedTwoFileExtension = ".dec2";
    private static String encodedTwoFileExtension = ".enc2";

    public static void main(String args[])
    {
        /* Parse arguments */
        String filePath  = args[0];
        int    charCount = Integer.parseInt(args[1]);

        int j = 2;
        if (args.length >= 3)
        {
            j = Integer.parseInt(args[2]);
        }

        /* Generate first mapping and encoding series */
        Map<String, Integer> frequencyMap = new HashMap<>();
        ReadFrequenciesFromFileIntoMap(frequencyMap, filePath);

        HuffmanTree tree = HuffmanCode.buildTree(frequencyMap);

        Map<String, String> mapping = new HashMap<>(frequencyMap.size());
        HuffmanCode.fillMappingTable(tree, new StringBuffer(), mapping);
        WriteRandomlyToFile(charCount, randomTextFilePath, frequencyMap);
        EncodeFile(randomTextFilePath, mapping, encodedFileExtension);

        PrintActualEntropy(frequencyMap);

        Map<String, String> reverseMapping = new HashMap<>(mapping.size());
        PrintEncodingAndGenerateReverseMapping(mapping, reverseMapping);
        DecodeFile(randomTextFilePath, reverseMapping, encodedFileExtension, decodedFileExtension);

        PrintOneSymbolEncodingEntropy(mapping, frequencyMap);

        /* Generate expanded version of encoding */
        Map<String, Integer> expandedFrequencyMap = ExpandEncodingForDeriveAlphabet(j,
                                                                                    frequencyMap);
        HuffmanTree expandedTree = HuffmanCode.buildTree(expandedFrequencyMap);

        Map<String, String> expandedMapping = new HashMap<>(expandedFrequencyMap.size());
        HuffmanCode.fillMappingTable(expandedTree, new StringBuffer(), expandedMapping);

        Map<String, String> expandedReverseMapping = new HashMap<>(expandedMapping.size());
        PrintEncodingAndGenerateReverseMapping(expandedMapping, expandedReverseMapping);
        EncodeFile(randomTextFilePath, expandedMapping, encodedTwoFileExtension);
        DecodeFile(randomTextFilePath, expandedReverseMapping, encodedTwoFileExtension, decodedTwoFileExtension);
        PrintTwoSymbolEncodingEntropy(expandedMapping, expandedFrequencyMap);
    }

    public static void PrintActualEntropy(Map<String, Integer> frequencyMap)
    {
        float sum = 0;
        for(Integer value : frequencyMap.values())
        {
            sum += value;
        }

        float entropy = 0;
        for(Integer value : frequencyMap.values())
        {
            entropy += ((value/sum) * (Math.log(value/sum)/Math.log(2))); 
        }
        entropy = -entropy;
        System.out.println("Actual Entropy: " + entropy);
    }

    public static void PrintOneSymbolEncodingEntropy(Map<String, String> mapping, Map<String, Integer> frequencyMap)
    {
        float originalSum = 0;
        ArrayList<Integer> frequencyArray = new ArrayList<Integer>();
        for(Integer value : frequencyMap.values())
        {
            frequencyArray.add(value);
            originalSum += value;
        }

        float encodingSum = 0;
        int index = 0;
        for(String code : mapping.values())
        {
            encodingSum += code.length() + frequencyArray.get(index);
            index++;
        }

        System.out.println("1-Symbol Encoding Efficiency compared to Original: " + encodingSum/originalSum);
    }

    public static void PrintTwoSymbolEncodingEntropy(Map<String, String> expandedMapping, Map<String, Integer> expandedFrequencyMap)
    {
        float originalSum = 0;
        ArrayList<Integer> frequencyArray = new ArrayList<Integer>();
        for(Integer value : expandedFrequencyMap.values())
        {
            frequencyArray.add(value);
            originalSum += value;
        }

        float encodingSum = 0;
        int index = 0;
        for(String code : expandedMapping.values())
        {
            encodingSum += code.length() + frequencyArray.get(index);
            index++;
        }

        System.out.println("2-Symbol Encoding Efficiency compared to 1-Symbol Efficiency: " + encodingSum/originalSum);
    }

    public static void WriteRandomlyToFile(int count,
                                           String filePath,
                                           Map<String, Integer> frequencyMap)
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
            System.exit(-1);
        }
    }

    public static void ReadFrequenciesFromFileIntoMap(Map<String, Integer> frequencyMap,
                                                      String filePath)
    {
        char currChar = 'a';
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath)))
        {
            for (String line; (line = bufferedReader.readLine()) != null; )
            {
                Integer currentInt = Integer.parseInt(line);
                frequencyMap.put("" + currChar++, currentInt);
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Error while reading file: " + ioe.toString());
            System.exit(-1);
        }
    }

    public static void PrintEncodingAndGenerateReverseMapping(Map<String, String> mapping,
                                                              Map<String, String> reverseMapping)
    {
        System.out.println("Mapping table (character : encoding)");
        for (Map.Entry<String, String> entry : mapping.entrySet())
        {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            reverseMapping.put(entry.getValue(), entry.getKey());
        }
    }

    public static void EncodeFile(String filePath, Map<String, String> encoding, String extension)
    {
        //@formatter:off
        try (
                FileReader fileReader = new FileReader(filePath);
                FileWriter fileWriter = new FileWriter(filePath + extension);
            )
        {//@formatter:on
            for (int c = fileReader.read(); c > -1; c = fileReader.read())
            {
                String encoded = encoding.get((char) c);
                if (encoded != null)
                {
                    fileWriter.write(encoded.toCharArray());
                }
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while writing random text: " + ioe);
        }
    }

    public static void DecodeFile(String filePath, Map<String, String> decoding, String encodeExtension, String decodeExtension)
    {
        //@formatter:off
        try (
                FileReader fileReader = new FileReader(filePath + encodeExtension);
                FileWriter fileWriter = new FileWriter(filePath + decodeExtension);
            )
        {//@formatter:on
            // Get max length to impose practical limit
            int tempLengeth, maxLength = 0;
            for (Map.Entry<String, String> entry : decoding.entrySet())
            {
                tempLengeth = entry.getKey().length();
                if (tempLengeth > maxLength)
                {
                    maxLength = tempLengeth;
                }
            }

            // Read in characters and generate match at earliest occurrence
            // Terminate if exceeded practical limit
            StringBuilder sb = new StringBuilder(maxLength);
            String        temp;
            for (int c = fileReader.read(); c > -1; c = fileReader.read())
            {
                // Could mimic StringBuilder with array of size maxLength for efficiency
                sb.append((char) c);
                temp = sb.toString();

                if (temp.length() > maxLength)
                {
                    // Also consider writing to a temp file and then renaming
                    // if decoding is successful
                    throw new InvalidDnDOperationException("Sequence "
                                                           + sb.toString()
                                                           + " does "
                                                           + "not "
                                                           + "have a valid decoding.");
                }

                Object decodedCharacter = decoding.get(temp);
                if (decodedCharacter != null)
                {
                    fileWriter.write(((String) decodedCharacter));
                    sb = new StringBuilder(maxLength);
                    temp = "";
                }
            }
        }
        catch (IOException ioe)
        {
            System.out.println("Exception while writing random text: " + ioe);
        }
    }

    public static Map<String, Integer> ExpandEncodingForDeriveAlphabet(int j,
                                                                       Map<String, Integer>
                                                                               encoding)
    {
        if (j <= 0 || encoding.isEmpty())
        {
            return encoding;
        }

        Map<String, Integer> expandedMapping = new HashMap<>((int) Math.pow(encoding.size(), j));

        // Copy over contents from original Map
        for (Map.Entry<String, Integer> entry : encoding.entrySet())
        {
            expandedMapping.put(entry.getKey(), entry.getValue());
        }

        // Expand by repeated cross joining
        while (--j > 0)
        {
            String[] keysArray = new String[expandedMapping.size()];
            keysArray = expandedMapping.keySet().toArray(keysArray);
            for (String key : keysArray)
            {
                Integer value = expandedMapping.get(key);
                expandedMapping.remove(key);

                for (Map.Entry<String, Integer> originalEntry : encoding.entrySet())
                {
                    expandedMapping.put(key + originalEntry.getKey(),
                                        value * originalEntry.getValue());
                }
            }
        }

        return expandedMapping;
    }
}