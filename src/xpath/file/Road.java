/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.file;


/**
 * This class represents a road in level graph, which means it's basically a
 * graph edge.
 *
 * @author Richard McKenna
 */
public class Road  {

    // THESE ARE THE EDGE'S NODES
    Intersection node1;
    Intersection node2;

    // false IF IT'S TWO-WAY, true IF IT'S ONE WAY
    boolean oneWay;
    
    private boolean closed;

    // ROAD SPEED LIMIT
    int speedLimit;

    // ACCESSOR METHODS
    public Intersection getNode1() {
        return node1;
    }

    public Intersection getNode2() {
        return node2;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    // MUTATOR METHODS
    public void setNode1(Intersection node1) {
        this.node1 = node1;
    }

    public void setNode2(Intersection node2) {
        this.node2 = node2;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public void setSpeedLimit(int speedLimit) {
        if (speedLimit < 10) {
            this.speedLimit = 10;
        } else if (speedLimit > 100) {
            this.speedLimit = 100;
        } else {
            this.speedLimit = speedLimit;
        }
    }

    /**
     * Builds and returns a textual representation of this road.
     */
    @Override
    public String toString() {
        return node1 + " - " + node2 + "(" + speedLimit + ":" + oneWay + ")";
    }

    /**
     * @return the closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @param closed the closed to set
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
