package net.engio.pips.data.utils;

import net.engio.pips.data.IDataProcessor;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author bennidi
 *         Date: 3/25/14
 */
public class Sensor<V> {

    // use thread-safe init-on-demand idiom for Timer singleton
    private static class TimerHolder{
        private static final Timer Timer = new Timer("Sensors", true);
    }

    private static Timer getTimer(){
        return TimerHolder.Timer;
    }

    private int frequency;

    private TimeUnit unit;

    private IDataProcessor<V, ?> target;

    private Generator<V> generator;

    private TimerTask scheduledTask;

    public Sensor(int frequency, TimeUnit unit) {
        this.frequency = frequency;
        this.unit = unit;
    }

    public static Sensor Each(int frequency, TimeUnit unit){
       return new Sensor(frequency, unit);
    }

    /**
     * Specify where the sensor will get its values from
     *
     * @param gen The source for the values this sensor will push to its destination
     * @return  This sensor
     */
    public Sensor<V> pipe(Generator<V> gen){
        this.generator = gen;
        return this;
    }

    /**
     * Specify the destination that will receive the values from the generator
     *
     * @param target  The destination to receive the values
     * @return This sensor
     */
    public Sensor<V> into(IDataProcessor<V, ?> target){
        this.target = target;
        return this;
    }

    /**
     * Starting a sensor will schedule a timer task that pushes values from the
     * generator to the target at the specified rate of this sensor.
     */
    public void start(){
        scheduledTask = new TimerTask() {
            @Override
            public void run() {
                target.receive(generator.next());
            }
        };
        getTimer().scheduleAtFixedRate(scheduledTask ,0, unit.convert(frequency, TimeUnit.MILLISECONDS));
    }

    /**
     * Stop a formerly started sensor. The running timer task will be cancelled and according to the
     * semantics of the TimerTask.cancel() method
     */
    public void stop(){
        if(scheduledTask != null)
            scheduledTask.cancel();
    }

}
