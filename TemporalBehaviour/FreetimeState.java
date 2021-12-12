package TemporalBehaviour;

import core.Coord;
import core.DTNHost;
import core.SimClock;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matthias on 18.11.2015.
 */
public class FreetimeState extends State {

    private double stateEnterTime = 0;

    public FreetimeState(DailyBehaviour dailyBehaviour, State state){
        super(dailyBehaviour, state);
        stateEnterTime = SimClock.getTime();

    }
    private Coord c;

    @Override
    public Coord getDestination() {
        var host = this.dailyBehaviour.getHost();
        host.overrideNameWith("FT");
        destinationChanged = false;
        if(c == null)
            reachedDestination();   //generate new random position
        return c;
    }

    @Override
    public void reachedDestination() {
        c = dailyBehaviour.getMovement().randomCoord();
        if(random.nextDouble() < 0.02){
            dailyBehaviour.changeState(new StudyState(dailyBehaviour, this));
        }
        //ArrayList<Lecture> lectures= dailyBehaviour.getLecturesAtTime(SimClock.getTime());
        //if( lectures.size() > 0){
        //    dailyBehaviour.changeState(new LectureState(dailyBehaviour, this, lectures.get(0)));
        //}
    }

    @Override
    public void update() {
        ArrayList<Lecture> lectures= dailyBehaviour.getLecturesAtTime(SimClock.getTime());
        if( lectures.size() > 0){
            dailyBehaviour.changeState(new LectureState(dailyBehaviour, this, lectures.get(0)));
        }
    }

    public int distributionTime = 150;// 200;

    @Override
    public void initConnection(DTNHost otherHost) {
        if (SimClock.getTime() > stateEnterTime + distributionTime) {
            dailyBehaviour.getHost().isStudent();
        }
    }

    @Override
    public void removeConnection(DTNHost otherHost) {
        this.connectedHosts.remove(otherHost);

        if (connectedHosts.size() == 0){
            this.dailyBehaviour.getMovement().setActive(true);
        }
    }
}
