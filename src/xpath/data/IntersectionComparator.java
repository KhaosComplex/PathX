/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xpath.data;

import java.util.Comparator;
import xpath.file.Intersection;
import xpath.file.XPathModel;

/**
 *
 * @author Khaos
 */
public class IntersectionComparator implements Comparator<Intersection> {

    private XPathModel model;

    public IntersectionComparator(XPathModel initModel) {
        model = initModel;
    }

    @Override
    public int compare(Intersection o1, Intersection o2) {
        int currentX = model.getCurrentIntersection().x;
        int currentY = model.getCurrentIntersection().y;
        int x = currentX - o1.x;
        int y = currentY - o1.y;
        int x2 = currentX - o2.x;
        int y2 = currentY - o2.y;
        double c = Math.sqrt((x * x) + (y * y));
        double c2 = Math.sqrt((x2 * x2) + (y2 * y2));
        return (new Integer((int) c)).compareTo(new Integer((int) c2));
    }
}
