/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run.mycode.untiednations.map;

import com.hoten.delaunay.geom.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bdahl
 */
public class Center {
    
    public boolean city = false;
    public PoliticalMap.PoliticalArea owner = null;
    public boolean surrounded = false;
    
    
    public int index;
    public Point loc;
    public List<Corner> corners = new ArrayList<>();//good
    public List<Center> neighbors = new ArrayList<>();//good
    public List<Edge> borders = new ArrayList<>();
    public boolean border, ocean, water, coast;
    public double elevation;
    public double moisture;
    public Enum biome;
    public double area;

    public Center() {
    }

    public Center(Point loc) {
        this.loc = loc;
    }
}
