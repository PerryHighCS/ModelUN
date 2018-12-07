package run.mycode.untiednations.competition.ui.game.map;

import com.hoten.delaunay.geom.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Corner.java
 *
 * @author Connor
 */
public class Corner {

    public List<Center> touches = new ArrayList<>(); //good
    public List<Corner> adjacent = new ArrayList<>(); //good
    public List<Edge> protrudes = new ArrayList<>();
    public Point loc;
    public int index;
    public boolean border;
    public double elevation;
    public boolean water, ocean, coast;
    public Corner downslope;
    public int river;
    public double moisture;
}
