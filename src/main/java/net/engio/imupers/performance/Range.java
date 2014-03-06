package net.engio.imupers.performance;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An immutable set of values of type T. Values are pre-generated by a corresponding
 * {@link ValueGenerator} and can be accessed randomly.
 *
 *
 * @author bennidi
 *         Date: 2/13/14
 */
public class Range<T> {

    private T[] elements;

    private Random random;

    private AtomicInteger index=new AtomicInteger(0);

    public Range(ValueGenerator<T> generator, int size){
        super();
        elements = (T[])new Object[size];
        for(int i = 0; i < size ; i++){
           elements[i] = generator.next();
        }
        random = new Random();
    }

    private Range(T[] elements){
        this.elements = elements;
        random = new Random();
    }

    public Range<T> shuffle(){
        Range<T> clone = new Range<T>(Arrays.copyOf(elements, elements.length));
        shuffle(clone.elements);
        return clone;
    }

    private void shuffle(T[] elements){
        Random rnd = new Random();
        for (int i = elements.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            T a = elements[index];
            elements[index] = elements[i];
            elements[i] = a;
        }
    }

    /**
     * Get a range containing the same values but with a distinct Random
     * to avoid contention on the Random object in multi-threaded use.
     * @return
     */
    public Range<T> clone(){
        return new Range<T>(elements);
    }

    public T getRandomElement(){
        return elements[Math.abs(random.nextInt() % elements.length)];
    }


    public T getNext(){
        return index.get() < elements.length -1
                ? elements[index.getAndIncrement()] // get next element
                : elements[index.getAndSet(0)]; // reset when overflow
    }

    public T getElement(int index){
        return index < elements.length ? elements[index] : null;
    }

    public Range<T> startWithRandomIndex(){
        index.set(Math.abs(random.nextInt() % elements.length));
        return this;
    }

}
