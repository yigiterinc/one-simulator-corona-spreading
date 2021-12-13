package TemporalBehaviour;

import core.*;
import movement.MovementModel;
import movement.MovementVector;
import movement.Path;

import java.util.*;

/**
 * Created by Matthias on 18.11.2015.
 */
public class DailyBehaviour {

    private RoomPlans roomPlans;
    private ArrayList<Lecture> selectedLectures = new ArrayList<>();
    private DTNHost host;
    private MovementModel movementModel;

    public static double START_BLOCK1 = 2000;//2000;
    public static double HOUR = 2000;//3200;
    public static double START_BLOCK2 = 2*HOUR+START_BLOCK1;
    public static double START_BLOCK3 = 2*HOUR+START_BLOCK2;
    public static double START_BLOCK4 = 2*HOUR+START_BLOCK3;
    public static double START_BLOCK5 = 2*HOUR+START_BLOCK4;
    public static double START_BLOCK6 = 2*HOUR+START_BLOCK5;
    public static double LECTURE_LENGHT = 1.0*HOUR;//1.5*HOUR;

    private Coord location = new Coord(0,0);
    private Coord destination;
    private double speed;

    private int arrivalTime = 0;
    private int departureTime = 0;

    //private List<MovementListener> movListeners;


    public MovementModel getMovement(){
        return movementModel;
    }

    private State state;
    public State getState() {
        return state;
    }

    public void addConnection(DTNHost host){
        state.initConnection(host);
    }
    public void removeConnection(DTNHost host){
        state.removeConnection(host);
    }

    public DailyBehaviour(DTNHost host, MovementModel movement, List<MovementListener> movLs){
        //Settings s = new Settings(SimScenario.SCENARIO_NS);
        //this.movementModel = new MyProhibitedPolygonRwp(s);
        this.movementModel = movement.replicate();

        //if(counter < 50) {
        //    this.state = new FreetimeState(this, new InitState(this, null));
        //}
        //else {
            this.state = new IdleState(this, new InitState(this, null));
        //}

        Random random = new Random();
        //arrivalTime = 0;
        arrivalTime = (random.nextInt(20) * 100) + 100;
        departureTime = (random.nextInt(30) * 100) + 23000;

        this.roomPlans = RoomPlans.getRoomPlans();
        this.host = host;
        setInitialLocation();
    }
    private void setEntryLocation(){
        Random rng = new Random();
        int random = rng.nextInt(10);
        if (host.isInfected()) {
            this.location.setLocation(450.0, 90.0);
        } else if (random < 5) {
            this.location.setLocation(UBahnDepartureState.UBAHN_COORDS.getX(),UBahnDepartureState.UBAHN_COORDS.getY());
        } else {
            this.location.setLocation(490.0,220.0);
        }
    }


    public void changeState(State state){
        this.state = state;
    }
    public DTNHost getHost(){
        return host;
    }

    public void chooseLectures() {
        Random random = new Random();

        //block1
        ArrayList<Lecture> lectureList = roomPlans.getAllLecturesAtTime(START_BLOCK1);
        if(lectureList.size() > 0)
            selectedLectures.add(lectureList.get(random.nextInt(lectureList.size())));
        //block2
        lectureList = roomPlans.getAllLecturesAtTime(START_BLOCK2);
        if(lectureList.size() > 0 && random.nextDouble()<0.8)
            selectedLectures.add(lectureList.get(random.nextInt(lectureList.size())));
        //block3
        lectureList = roomPlans.getAllLecturesAtTime(START_BLOCK3);
        if(lectureList.size() > 0 && random.nextDouble()<0.5)
            selectedLectures.add(lectureList.get(random.nextInt(lectureList.size())));
        //block4
        lectureList = roomPlans.getAllLecturesAtTime(START_BLOCK4);
        if(lectureList.size() > 0 && random.nextDouble()<0.8)
            selectedLectures.add(lectureList.get(random.nextInt(lectureList.size())));
        //block5
        lectureList = roomPlans.getAllLecturesAtTime(START_BLOCK5);
        if(lectureList.size() > 0 && random.nextDouble()<0.2)
            selectedLectures.add(lectureList.get(random.nextInt(lectureList.size())));
    }


    //nick
    public void update(){
        //In normal conditions, all nodes will start from idle state
        if(state instanceof IdleState && arrivalTime <= SimClock.getTime() && departureTime > SimClock.getTime())  {
            state.reachedDestination();
            this.movementModel.setActive(true);
        }

        //if departureTime -> depart
        if(departureTime <= SimClock.getTime() && !(state instanceof UBahnDepartureState)) {
            this.state = new UBahnDepartureState(this, this.state);
            //this.destination = this.state.getDestination();
        }
        state.update();
    }



    public void printLectures() {
        System.out.println("Host: "+host.getName());
        for(Lecture lecture : this.selectedLectures){
            System.out.println("\t- "+lecture.print());
        }
    }

    public void move(double timeIncrement){
        double possibleMovement;
        double distance;
        double dx, dy;

        if (!this.isMovementActive() || SimClock.getTime() < this.nextPathAvailable()) {
            return;
        }
        if (this.destination == null || state.destinationChanged()) {
            if (nextWaypointNotCalculated()) {
                return;
            }
        }


        possibleMovement = timeIncrement * state.getSpeed();
        distance = this.location.distance(this.destination);

        while (possibleMovement >= distance) {
            // node can move past its next destination
            this.location.setLocation(this.destination); // snap to destination
            //this.getState().reachedDestination();
            possibleMovement -= distance;
            distanceExceedsNextDestinationn = true;
            if (nextWaypointNotCalculated()) { // get a new waypoint
                return; // no more waypoints left
            }
            distance = this.location.distance(this.destination);
        }

        // move towards the point for possibleMovement amount
        dx = (possibleMovement/distance) * (this.destination.getX() -
                this.location.getX());
        dy = (possibleMovement/distance) * (this.destination.getY() -
                this.location.getY());

        this.location.translate(dx, dy);
    }
    /**
     * Sets the next destination and speed to correspond the next waypoint
     * on the path.
     * @return True if there was a next waypoint to set, false if node still
     * should wait
     */
    private Path path;
    private boolean distanceExceedsNextDestinationn = false;
    private boolean nextWaypointNotCalculated() {

        if(state.getDestination().equals(this.location) || (distanceExceedsNextDestinationn && (path == null || path.getCoords().size() == 0))){
            state.reachedDestination();
            distanceExceedsNextDestinationn = false;
        }

        if (path == null||!path.hasNext()) {
            Coord destination = state.getDestination();
            double speed = state.getSpeed();
            if (destination != null)
                path = movementModel.getPath(this.location, destination, speed);
            else
                return true;
        }

        this.destination = path.getNextWaypoint();
        this.speed = path.getSpeed();
        return false;
    }

    public ArrayList<Lecture> getLecturesAtTime(double time){
        ArrayList<Lecture>lecturesAtTime = new ArrayList<>();
        for(Lecture lecture : selectedLectures){
            if(lecture.lectureTakesPlace(time))
                lecturesAtTime.add(lecture);
        }
        return lecturesAtTime;

    }

    private MovementVector tempDestination = null;

    public MovementVector getTempDestination() {
        return this.tempDestination;
    }

    public void setTempDestination(MovementVector vec) {
        this.tempDestination = vec;
    }

    private MovementVector calculateNonIntersectingWay1(MovementVector vec){
        //save destination
        tempDestination = vec;
        //go to middle of main hall -> y = 310
        MovementVector middleCoord = new MovementVector((new Coord(location.getX(),310)),tempDestination.speed);
        return middleCoord;
    }

    private MovementVector calculateNonIntersectingWay2(MovementVector vec){
        if(vec != null){
            MovementVector middleCoord = new MovementVector((new Coord(vec.coord.getX(),310)),tempDestination.speed);
            return middleCoord;}
        return null;
    }

//    private Coord calculateNonIntersectingWay3(MovementVector vec){
//        intersectionAvoidingWay = true;
//        //go to middle of main hall -> y = 310
//        Coord middleCoord = new Coord(vec.coord.getX(),vec.coord.getX());
//        return middleCoord;
//    }

    public void setInitialLocation() {
        setEntryLocation();
        movementModel.setLastWayPoint(this.location);
    }

    public double nextPathAvailable() {
        return movementModel.nextPathAvailable();
    }

    public boolean isMovementActive() {
        //System.out.println("name: "+host.getName()+" isMovementActive: "+movementModel.isActive());
        return movementModel.isActive();
    }

    public Coord getLocation() {
        return location;
    }

    public void setLocation(Coord location) {
        this.location = location;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDepartureTime() {
        return departureTime;
    }
}
