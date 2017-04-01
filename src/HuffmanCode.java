/*
* RETRIEVED FROM: https://rosettacode.org/wiki/Huffman_coding#Java
* */

import java.util.Map;
import java.util.PriorityQueue;

abstract class HuffmanTree implements Comparable<HuffmanTree>
{
    public final int frequency;

    public HuffmanTree(int freq) { frequency = freq; }

    public int compareTo(HuffmanTree tree)
    {
        return frequency - tree.frequency;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof HuffmanTree)) { return false; }

        HuffmanTree that = (HuffmanTree) o;

        return frequency == that.frequency;
    }

    @Override
    public int hashCode()
    {
        return frequency;
    }
}

class HuffmanLeaf extends HuffmanTree
{
    public final String value;

    public HuffmanLeaf(int freq, String val)
    {
        super(freq);
        value = val;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof HuffmanLeaf)) { return false; }
        if (!super.equals(o)) { return false; }

        HuffmanLeaf that = (HuffmanLeaf) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}

class HuffmanNode extends HuffmanTree
{
    public final HuffmanTree left, right; // subtrees

    public HuffmanNode(HuffmanTree l, HuffmanTree r)
    {
        super(l.frequency + r.frequency);
        left = l;
        right = r;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) { return true; }
        if (!(o instanceof HuffmanNode)) { return false; }
        if (!super.equals(o)) { return false; }

        HuffmanNode that = (HuffmanNode) o;

        if (!left.equals(that.left)) { return false; }
        return right.equals(that.right);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + left.hashCode();
        result = 31 * result + right.hashCode();
        return result;
    }
}

public class HuffmanCode
{
    // input is an array of frequencies, indexed by character code
    public static HuffmanTree buildTree(Map<String, Integer> frequencyMap)
            throws IllegalArgumentException
    {
        PriorityQueue<HuffmanTree> trees = new PriorityQueue<>();

        // initially, we have a forest of leaves
        // one for each non-empty character
        for (Map.Entry<String, Integer> entry : frequencyMap.entrySet())
        {
            int freq = entry.getValue();
            if (freq < 0)
            {
                throw new IllegalArgumentException("Frequency must be non-negative.");
            }
            else if (freq > 0)
            {
                trees.offer(new HuffmanLeaf(freq, entry.getKey()));
            }
        }

        // loop until there is only one tree left
        while (trees.size() > 1)
        {
            // two trees with least frequency
            HuffmanTree a = trees.poll();
            HuffmanTree b = trees.poll();

            // put into new node and re-insert into queue
            trees.offer(new HuffmanNode(a, b));
        }

        return trees.poll();
    }

    public static void fillMappingTable(HuffmanTree tree,
                                        StringBuffer prefix,
                                        Map<String, String> mapping) throws IllegalArgumentException
    {
        if (tree == null)
        {
            throw new IllegalArgumentException("Tree cannot be null.");
        }

        if (tree instanceof HuffmanLeaf)
        {
            HuffmanLeaf leaf = (HuffmanLeaf) tree;
            mapping.put(leaf.value, prefix.toString());
        }
        else if (tree instanceof HuffmanNode)
        {
            HuffmanNode node = (HuffmanNode) tree;

            // traverse left
            prefix.append('0');
            fillMappingTable(node.left, prefix, mapping);
            prefix.deleteCharAt(prefix.length() - 1);

            // traverse right
            prefix.append('1');
            fillMappingTable(node.right, prefix, mapping);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }
}