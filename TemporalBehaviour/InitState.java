package TemporalBehaviour;

import core.Coord;
import core.DTNHost;
import core.SimClock;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matthias on 18.11.2015.
 */
public class InitState extends State {

    public InitState(DailyBehaviour dailyBehaviour, State state){
        super(dailyBehaviour, state);
    }

    @Override
    public Coord getDestination() {
        return null;
    }

    @Override
    public void reachedDestination() {
    }

    @Override
    public void update() {

    }

    @Override
    public void initConnection(DTNHost otherHost) {
    }

    @Override
    public void removeConnection(DTNHost otherHost) {
    }
}
