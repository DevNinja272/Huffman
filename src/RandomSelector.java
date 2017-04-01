import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by HP User on 3/30/2017.
 */
public class RandomSelector<T>
{
    private class AccumulationTable
    {
        private final ArrayList<T>       items;
        private final ArrayList<Integer> accumulations;

        AccumulationTable(Map<T, Integer> frequencyMap)
        {
            int size = frequencyMap.size();
            this.items = new ArrayList<>(size);
            this.accumulations = new ArrayList<>(size);
            updateAccumulationTable(frequencyMap);
        }

        private ArrayList<T> getItems()
        { return items; }

        private ArrayList<Integer> getAccumulations()
        { return accumulations; }

        private int getTotal()
        {
            //@formatter:off
            ArrayList<Integer> accumulations = this.getAccumulations();
            return !(accumulations == null || accumulations.isEmpty())
                   ? accumulations.get(accumulations.size() - 1)
                   : 0;
            //@formatter:on
        }

        private void updateAccumulationTable(Map<T, Integer> frequencyMap)
        {
            List<T>       items         = this.getItems();
            List<Integer> accumulations = this.getAccumulations();

            int accumulation = this.getTotal();
            for (Map.Entry<T, Integer> entry : frequencyMap.entrySet())
            {
                int frequency = entry.getValue();

                if (frequency == 0)
                { continue; }

                accumulation += frequency;

                items.add(entry.getKey());
                accumulations.add(accumulation);
            }

            assert accumulation == this.getTotal();
            assert items.size() == accumulations.size();
        }

        T get(int index)
        { return this.getItems().get(index); }

        int getAccumulation(int index)
        { return this.getAccumulations().get(index); }

        int getAccumulation(T item)
        {
            int index = this.getItems().indexOf(item);
            return this.getAccumulations().get(index);
        }

        public T getItemWithLowestAccumulationAtLeast(int accumulation)
        {
            List<Integer> accumulations = this.getAccumulations();

            if (accumulation < 0)
            {
                throw new IllegalArgumentException("Accumulation must be non-negative.");
            }
            else if (accumulations == null || accumulations.isEmpty())
            {
                return null;
            }
            else if (accumulation == 0)
            {
                return get(0);
            }

            long upperBound = 0;
            for (int i = 0; i < accumulations.size(); i++)
            {
                upperBound = accumulations.get(i);
                if (accumulation <= upperBound)
                {
                    T item = this.get(i);
                    return item;
                }
            }

            return null;
        }
    }

    private final AccumulationTable accumulationTable;
    private final Random            random;

    public RandomSelector(Map<T, Integer> frequencyMap)
    {
        this.accumulationTable = new AccumulationTable(frequencyMap);
        this.random = generateFreshRandom();
    }

    private AccumulationTable getAccumulationTable()
    { return this.accumulationTable; }

    private Random getRandom()
    { return this.random; }

    private Random generateFreshRandom()
    {
        SecureRandom secureRandom      = new SecureRandom();
        byte[]       secureRandomBytes = new byte[8];
        secureRandom.nextBytes(secureRandomBytes);

        long seed = 0;
        for (int i = 0; i < secureRandomBytes.length; i++)
        {
            seed |= secureRandomBytes[i] << (i * 8);
        }

        return new Random(seed | System.nanoTime());
    }

    public T next()
    {
        AccumulationTable accumulationTable = this.getAccumulationTable();
        int               bound             = accumulationTable.getTotal();
        int               accumulation      = this.getRandom().nextInt(Math.max(bound, 0)) + 1;

        return accumulationTable.getItemWithLowestAccumulationAtLeast(accumulation);
    }
}
