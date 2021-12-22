package TemporalBehaviour;

import core.Coord;
import core.DTNHost;
import core.SimClock;

import java.util.ArrayList;

public class UBahnArrivalState extends State {

    public static Coord ENTRANCE_COORDS = new Coord(107, 40);

    public UBahnArrivalState(DailyBehaviour dailyBehaviour, State state){
        super(dailyBehaviour, state);
    }

    @Override
    public Coord getDestination() {
        destinationChanged = false;
        return ENTRANCE_COORDS;
    }

    @Override
    public void reachedDestination() {
        dailyBehaviour.changeState(new FreetimeState(dailyBehaviour, this));
    }

    @Override
    public void update() {

    }

    @Override
    public void initConnection(DTNHost otherHost) { }

    @Override
    public void removeConnection(DTNHost otherHost) { }

}
