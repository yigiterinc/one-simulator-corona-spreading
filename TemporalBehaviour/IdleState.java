package TemporalBehaviour;

import core.Coord;
import core.DTNHost;
import core.SimClock;

import java.util.ArrayList;
import java.util.Random;

public class IdleState extends State {

    public IdleState(DailyBehaviour dailyBehaviour, State state){
        super(dailyBehaviour, state);
        dailyBehaviour.getMovement().setActive(false);
    }

    @Override
    public Coord getDestination() {
        return null;
    }

    @Override
    public void reachedDestination() {
        dailyBehaviour.changeState(new UBahnArrivalState(dailyBehaviour, this));
    }

    @Override
    public void update() {

    }

    @Override
    public void initConnection(DTNHost otherHost) { /* DO NOTHING */}

    @Override
    public void removeConnection(DTNHost otherHost) { /* DO NOTHING */}

}
