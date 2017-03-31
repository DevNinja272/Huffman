import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by HP User on 3/30/2017.
 */
public class RandomSelector<T>
{
    private class Frequency
    {
        private final T       item;
        private final Integer frequency;

        public Frequency(T item, Integer frequency)
        {
            if (frequency < 0)
            {
                throw new IllegalArgumentException("Frequency must have non-negative value.");
            }

            this.item = item;
            this.frequency = frequency;
        }

        public T getItem()
        { return this.item; }

        public Integer getFrequency()
        { return this.frequency; }

        @Override
        public int hashCode()
        {
            int result = getItem().hashCode();
            result = 31 * result + getFrequency().hashCode();
            return result;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            { return true; }

            if (o instanceof RandomSelector.Frequency)
            {
                Frequency other = (Frequency) o;
                return getItem().equals(other.getItem())
                       && getFrequency().equals(other.getFrequency());
            }

            return false;
        }
    }

    private class AccumulationTable
    {
        private final ArrayList<T>       items;
        private final ArrayList<Integer> accumulations;

        AccumulationTable(Collection<Frequency> frequencies)
        {
            int size = frequencies.size();
            this.items = new ArrayList<>(size);
            this.accumulations = new ArrayList<>(size);
            updateAccumulationTable(frequencies);
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

        private void updateAccumulationTable(Collection<Frequency> frequencies)
        {
            List<T>       items         = this.getItems();
            List<Integer> accumulations = this.getAccumulations();

            int accumulation = this.getTotal();
            for (Frequency frequency : frequencies)
            {
                T   item           = frequency.getItem();
                int frequencyValue = frequency.getFrequency();

                if (frequency.getFrequency() == 0)
                { continue; }

                accumulation += frequencyValue;

                items.add(item);
                accumulations.add(accumulation);
            }

            assert accumulation == this.getTotal();
            assert items.size() == accumulations.size();
        }

        T get(int index)
        { return this.getItems().get(index); }

        long getAccumulation(int index)
        { return this.getAccumulations().get(index); }

        long getAccumulation(T item)
        {
            int index = this.getItems().indexOf(item);
            return this.getAccumulations().get(index);
        }

        public T getItemWithLowestAccumulationAtLeast(long accumulation)
        {
            ArrayList<Integer> accumulations = this.getAccumulations();

            int  i = 0;
            long leftBound, rightBound;

            rightBound = accumulations.get(i);
            for (i += 1; i < accumulations.size(); i++)
            {
                leftBound = rightBound;
                rightBound = accumulations.get(i);

                if (leftBound < accumulation && rightBound > accumulation)
                {
                    return this.getItems().get(i);
                }
            }

            return null;
        }
    }

    private final AccumulationTable accumulationTable;
    private final Random            random;

    public RandomSelector(List<T> items, List<Integer> frequencies)
    {
        int size = items.size();

        if (size != frequencies.size())
        {
            throw new IllegalArgumentException("Number of items must equal number of frequencies.");
        }

        Collection<Frequency> frequencyList = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            frequencyList.add(new Frequency(items.get(i), frequencies.get(i)));
        }

        this.accumulationTable = new AccumulationTable(frequencyList);
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

        return new Random(seed);
    }

    public T next()
    {
        AccumulationTable accumulationTable = this.getAccumulationTable();
        int accumulation = this.getRandom().nextInt(accumulationTable.getTotal() - 1) + 1;
        return accumulationTable.getItemWithLowestAccumulationAtLeast(accumulation);
    }
}
