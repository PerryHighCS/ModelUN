package run.mycode.untiednations.competition.ui.game.map;

import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.groundshapes.Blob;
import com.hoten.delaunay.voronoi.groundshapes.HeightAlgorithm;
import com.hoten.delaunay.voronoi.groundshapes.Perlin;
import com.hoten.delaunay.voronoi.groundshapes.Radial;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.LineSegment;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * VoronoiGraph.java
 *
 * @author Connor
 */
public class PoliticalMap {
    public static PoliticalMap createMap(List<String> countryNames, int size) {
            
            /** Number of pieces for the graph. */
            final int SITES_AMOUNT = 8_000;

            /**
             * Each time a relaxation step is performed, the points are left in a slightly more even distribution:
             * closely spaced points move farther apart, and widely spaced points move closer together.
             */
            final int LLOYD_RELAXATIONS = 3;

            /** You can make it false if you want to check some changes in code or image/graph size. */
            final boolean RANDOM_SEED = false;
            final long SEED;
            
            /** Random, radial, blob, etc. See {@link #getAlgorithmImplementation(Random, String)} */
            final String ALGORITHM = "perlin";

            if (RANDOM_SEED) {
                SEED = System.nanoTime();
            }
            else {
                SEED = countryNames.hashCode();
            }            
            
            final Random r = new Random(SEED);
            HeightAlgorithm algorithm = getAlgorithmImplementation(r, ALGORITHM);

            //make the intial underlying voronoi structure
            final Voronoi v = new Voronoi(SITES_AMOUNT, size, size, r, null);

            //assemble the voronoi strucutre into a usable graph object representing a map
            final PoliticalMap graph = new PoliticalMap(v, LLOYD_RELAXATIONS, r, algorithm, countryNames);

            return graph;        
    }
    
    public static enum Biome {
        OCEAN(0x44447a), LAKE(0x336699), BEACH(0xa09077), SNOW(0xffffff),
        TUNDRA(0xbbbbaa), BARE(0x888888), SCORCHED(0x555555), TAIGA(0x99aa77),
        SHURBLAND(0x889977), TEMPERATE_DESERT(0xc9d29b),
        TEMPERATE_RAIN_FOREST(0x448855), TEMPERATE_DECIDUOUS_FOREST(0x679459),
        GRASSLAND(0x88aa55), SUBTROPICAL_DESERT(0xd2b98b), SHRUBLAND(0x889977),
        ICE(0x99ffff), MARSH(0x2f6666), TROPICAL_RAIN_FOREST(0x337755),
        TROPICAL_SEASONAL_FOREST(0x559944), COAST(0x33335a),
        LAKESHORE(0x225588), RIVER(0x225588);
        public Color color;

        Biome(int color) {
            this.color = new Color(color);
        }
    }
    
    protected static class PoliticalArea {
        public String name;
        public Color color;
        public Center capital;
        public List<Center> land;
    }
    
    final private List<Edge> edges = new ArrayList<>();
    final private List<Corner> corners = new ArrayList<>();
    final private List<Center> centers = new ArrayList<>();
    final private Rectangle bounds;
    final private Random r;
    final private Color OCEAN, RIVER, BARE;
    final private BufferedImage pixelCenterMap;
    
    final private List<PoliticalArea> countries;
    final private Map<String, PoliticalArea> countryNames;
    
    public PoliticalMap(Voronoi v, int numLloydRelaxations, Random r, HeightAlgorithm algorithm, List<String> countryNames) {
        this(v, numLloydRelaxations, r, algorithm);
         
        
        for (int i = 0; i < countryNames.size(); i++) {
            // Initialize the country with a name and a color
            PoliticalArea pa = new PoliticalArea();
            pa.name = countryNames.get(i);
            pa.color = Color.getHSBColor((float)i / countryNames.size(), 0.3f, 0.75f);
            
            // Pick a random location for the country's capital
            pa.capital = findSeed(centers, r);
            pa.capital.city = true;
            pa.capital.owner = pa;
            
            // Create a list to hold all of the country's land, and add it's capital
            pa.land = new ArrayList<>();
            pa.land.add(pa.capital);
            
            countries.add(pa);
            this.countryNames.put(pa.name, pa);
        }
        
        // Find all land areas
        List<Center> land = centers.stream()
                .filter(c -> !(c.water || c.ocean || c.city))
                .collect(Collectors.toList());
        
        // Loop until no more land is grabbed (Some might be isolated on an island)
        int lastsize = -1;
        while(land.size() != lastsize) { 
            lastsize = land.size();
            countries.forEach(c -> expand(c, land, r)); // Allow each country to grab some adjacent land
        }
    }
    
    public PoliticalMap(Voronoi v, int numLloydRelaxations, Random r, HeightAlgorithm algorithm) {
        // Create a list of all of the countries
        this.countries = new ArrayList<>();
        this.countryNames = new HashMap<>();
        
         // Setup display colors
        RIVER = OCEAN = new Color(0x225588);
        BARE = new Color(0x888888);
        
        this.r = r;
        bounds = v.get_plotBounds();
        for (int i = 0; i < numLloydRelaxations; i++) {
            ArrayList<Point> points = v.siteCoords();
            for (Point p : points) {
                ArrayList<Point> region = v.region(p);
                double x = 0;
                double y = 0;
                for (Point c : region) {
                    x += c.x;
                    y += c.y;
                }
                x /= region.size();
                y /= region.size();
                p.x = x;
                p.y = y;
            }
            v = new Voronoi(points, null, v.get_plotBounds());
        }
        buildGraph(v);
        improveCorners();

        assignCornerElevations(algorithm);
        assignOceanCoastAndLand();
        redistributeElevations(landCorners());
        assignPolygonElevations();

        calculateDownslopes();
        //calculateWatersheds();
        createRivers();
        assignCornerMoisture();
        redistributeMoisture(landCorners());
        assignPolygonMoisture();
        assignBiomes();

        pixelCenterMap = new BufferedImage((int) bounds.width, (int) bounds.width, BufferedImage.TYPE_4BYTE_ABGR);
    }
    /**
     * Get the coordinates of a country's capital on the map
     * 
     * @param country the name of the country whose capital should be found
     * @return The location of the capital on the map
     */
    public Point getCapitalLocation(String country) {
        return countryNames.get(country).capital.loc;
    }
    
    /**
     * Get a point inside of a particular country
     * 
     * @param country the name of the country
     * @return A location inside the country
     */
    public Point getRandomPoint(String country) {
        List<Center> land = countryNames.get(country).land;
        return land.get((int)(Math.random() * land.size())).loc;
    }
    
    /**
     * Get the color assigned to a particular country in the map
     * 
     * @param country the name of the country
     * @return the color the country is colored on the map
     */
    public Color getColor(String country) {
        return countryNames.get(country).color;
    }
    
    private Color getColor(Center p, boolean countryColors) {
        if (countryColors && p.owner != null && !p.water) {
            return p.owner.color;
        }
        return ((Biome) p.biome).color;
    }
    
    private static HeightAlgorithm getAlgorithmImplementation(Random r, String name) {
        switch (name) {
            case "radial":
                return new Radial(1.07,
                    r.nextInt(5) + 1,
                    r.nextDouble() * 2 * Math.PI,
                    r.nextDouble() * 2 * Math.PI,
                    r.nextDouble() * .5 + .2);
            case "blob":
                return new Blob();
            case "perlin":
                return new Perlin(r, 7, 256, 256);
            default: 
                throw new RuntimeException("Invalid Height Algorithm Selected." +
                    "Check getAlgorithmImplementation and use one of the algorithms named.");
        }
    }

    private Enum getBiome(Center p) {
        if (p.ocean) {
            return Biome.OCEAN;
        } else if (p.water) {
            if (p.elevation < 0.1) {
                return Biome.MARSH;
            }
            if (p.elevation > 0.8) {
                return Biome.ICE;
            }
            return Biome.LAKE;
        } else if (p.coast) {
            return Biome.BEACH;
        } else if (p.elevation > 0.8) {
            if (p.moisture > 0.50) {
                return Biome.SNOW;
            } else if (p.moisture > 0.33) {
                return Biome.TUNDRA;
            } else if (p.moisture > 0.16) {
                return Biome.BARE;
            } else {
                return Biome.SCORCHED;
            }
        } else if (p.elevation > 0.6) {
            if (p.moisture > 0.66) {
                return Biome.TAIGA;
            } else if (p.moisture > 0.33) {
                return Biome.SHRUBLAND;
            } else {
                return Biome.TEMPERATE_DESERT;
            }
        } else if (p.elevation > 0.3) {
            if (p.moisture > 0.83) {
                return Biome.TEMPERATE_RAIN_FOREST;
            } else if (p.moisture > 0.50) {
                return Biome.TEMPERATE_DECIDUOUS_FOREST;
            } else if (p.moisture > 0.16) {
                return Biome.GRASSLAND;
            } else {
                return Biome.TEMPERATE_DESERT;
            }
        } else {
            if (p.moisture > 0.66) {
                return Biome.TROPICAL_RAIN_FOREST;
            } else if (p.moisture > 0.33) {
                return Biome.TROPICAL_SEASONAL_FOREST;
            } else if (p.moisture > 0.16) {
                return Biome.GRASSLAND;
            } else {
                return Biome.SUBTROPICAL_DESERT;
            }
        }
    }
    
    private Center findSeed(List<Center> centers, Random r) {
        Center c = null;
        
        while (c == null) {
            int pos = r.nextInt(centers.size());
            
            c = centers.get(pos);
            
            if (c.water || c.ocean || c.city)  {
                c = null;
            }
            else {
                // No neighboring cities
                for (Center n : c.neighbors) {
                    if (n.city) {
                        c = null;
                    }
                }
            }
        }
        return c;
    }
    
    private void expand(PoliticalArea pa, List<Center> land, Random r) {
        // Find a landmass in the country
        List<Center> taken = new ArrayList<>(pa.land);
        
        while (!taken.isEmpty()) {
            Center c = taken.get(r.nextInt(taken.size()));
            
            if (!c.surrounded) {
                List<Center> neighbors = c.neighbors;

                for (int i = 0; i < neighbors.size(); i++) {
                    Center candidate = neighbors.get(i);
                    if (candidate.owner == null &&
                        !candidate.ocean) {

                        candidate.owner = pa;
                        land.remove(candidate);
                        pa.land.add(candidate);
                        
                        return;
                    }
                }
                c.surrounded = true;
            }
            
            taken.remove(c);
        }
    }

    private void improveCorners() {
        Point[] newP = new Point[corners.size()];
        corners.forEach((c) -> {
            if (c.border) {
                newP[c.index] = c.loc;
            } else {
                double x = 0;
                double y = 0;
                for (Center center : c.touches) {
                    x += center.loc.x;
                    y += center.loc.y;
                }
                newP[c.index] = new Point(x / c.touches.size(), y / c.touches.size());
            }
        });
        corners.stream().forEach((c) -> {
            c.loc = newP[c.index];
        });
        edges.stream().filter((e) -> (e.v0 != null && e.v1 != null)).forEach((e) -> {
            e.setVornoi(e.v0, e.v1);
        });
    }

    private Edge edgeWithCenters(Center c1, Center c2) {
        for (Edge e : c1.borders) {
            if (e.d0 == c2 || e.d1 == c2) {
                return e;
            }
        }
        return null;
    }

    private void drawTriangle(Graphics2D g, Corner c1, Corner c2, Center center) {
        int[] x = new int[3];
        int[] y = new int[3];
        x[0] = (int) center.loc.x;
        y[0] = (int) center.loc.y;
        x[1] = (int) c1.loc.x;
        y[1] = (int) c1.loc.y;
        x[2] = (int) c2.loc.x;
        y[2] = (int) c2.loc.y;
        g.fillPolygon(x, y, 3);
    }

    private boolean closeEnough(double d1, double d2, double diff) {
        return Math.abs(d1 - d2) <= diff;
    }

    public BufferedImage createMap() {
        int width = (int) bounds.width;
        int height = (int) bounds.height;

        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();

        paint(g);

        return img;
    }
    
    
    public BufferedImage createMap(boolean countryColors, String selectedCountry) {
        return createMap(countryColors, selectedCountry, true);
    }
    
    public BufferedImage createMap(boolean countryColors, String selectedCountry, boolean showTerrain) {
        int width = (int) bounds.width;
        int height = (int) bounds.height;

        final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = img.createGraphics();

        PoliticalArea selected = countryNames.get(selectedCountry);
        paint(g, countryColors, selected, !showTerrain);

        return img;
    }

    public void paint(Graphics2D g) {
        paint(g, true, true, false, false, false, true, true, null);
    }
    
    
    public void paint(Graphics2D g, boolean countryColors, PoliticalArea hilightCountry) {
        paint(g, true, true, false, false, false, true, true, hilightCountry);
    }
    
    public void paint(Graphics2D g, boolean countryColors, PoliticalArea hilightCountry, boolean fillCountries) {
        paint(g, fillCountries, true, false, false, false, true, true, hilightCountry);
    }


    private void drawPolygon(Graphics2D g, Center c, Color color) {
        g.setColor(color);

        //only used if Center c is on the edge of the graph. allows for completely filling in the outer polygons
        Corner edgeCorner1 = null;
        Corner edgeCorner2 = null;
        c.area = 0;
        for (Center n : c.neighbors) {
            Edge e = edgeWithCenters(c, n);

            if (e.v0 == null) {
                //outermost voronoi edges aren't stored in the graph
                continue;
            }

            //find a corner on the exterior of the graph
            //if this Edge e has one, then it must have two,
            //finding these two corners will give us the missing
            //triangle to render. this special triangle is handled
            //outside this for loop
            Corner cornerWithOneAdjacent = e.v0.border ? e.v0 : e.v1;
            if (cornerWithOneAdjacent.border) {
                if (edgeCorner1 == null) {
                    edgeCorner1 = cornerWithOneAdjacent;
                } else {
                    edgeCorner2 = cornerWithOneAdjacent;
                }
            }

            drawTriangle(g, e.v0, e.v1, c);
            c.area += Math.abs(c.loc.x * (e.v0.loc.y - e.v1.loc.y)
                    + e.v0.loc.x * (e.v1.loc.y - c.loc.y)
                    + e.v1.loc.x * (c.loc.y - e.v0.loc.y)) / 2;
        }

        //handle the missing triangle
        if (edgeCorner2 != null) {
            //if these two outer corners are NOT on the same exterior edge of the graph,
            //then we actually must render a polygon (w/ 4 points) and take into consideration
            //one of the four corners (either 0,0 or 0,height or width,0 or width,height)
            //note: the 'missing polygon' may have more than just 4 points. this
            //is common when the number of sites are quite low (less than 5), but not a problem
            //with a more useful number of sites. 
            //TODO: find a way to fix this

            if (closeEnough(edgeCorner1.loc.x, edgeCorner2.loc.x, 1)) {
                drawTriangle(g, edgeCorner1, edgeCorner2, c);
            } else {
                int[] x = new int[4];
                int[] y = new int[4];
                x[0] = (int) c.loc.x;
                y[0] = (int) c.loc.y;
                x[1] = (int) edgeCorner1.loc.x;
                y[1] = (int) edgeCorner1.loc.y;

                //determine which corner this is
                x[2] = (int) ((closeEnough(edgeCorner1.loc.x, bounds.x, 1) || closeEnough(edgeCorner2.loc.x, bounds.x, .5)) ? bounds.x : bounds.right);
                y[2] = (int) ((closeEnough(edgeCorner1.loc.y, bounds.y, 1) || closeEnough(edgeCorner2.loc.y, bounds.y, .5)) ? bounds.y : bounds.bottom);

                x[3] = (int) edgeCorner2.loc.x;
                y[3] = (int) edgeCorner2.loc.y;

                g.fillPolygon(x, y, 4);
                c.area += 0; //TODO: area of polygon given vertices
            }
        }
    }

    //also records the area of each voronoi cell
    public void paint(Graphics2D g, boolean fillCountries, boolean drawRivers, 
            boolean drawSites, boolean drawCorners, boolean drawDelaunay,
            boolean drawVoronoi, boolean countryColors, PoliticalArea selected) {
        final int numSites = centers.size();

        Color[] defaultColors = null;
        if (fillCountries) {
            defaultColors = new Color[numSites];
            for (int i = 0; i < defaultColors.length; i++) {
                defaultColors[i] = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
            }
        }

        Graphics2D pixelCenterGraphics = pixelCenterMap.createGraphics();

        g.setColor(OCEAN);
        g.fillRect((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
        
        //draw via triangles
        for (Center c : centers) {
            drawPolygon(g, c, getColor(c, !fillCountries || c.owner == selected));
            drawPolygon(pixelCenterGraphics, c, new Color(c.index));
        }

        for (Edge e : edges) {
            if (drawDelaunay) {
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.YELLOW);
                g.drawLine((int) e.d0.loc.x, (int) e.d0.loc.y, (int) e.d1.loc.x, (int) e.d1.loc.y);
            }
            
            // Draw political borders
            if (e.d0.owner != e.d1.owner) {
                if (selected != null && 
                     (e.d0.owner == selected ||
                      e.d1.owner == selected)) {
                    g.setStroke(new BasicStroke(5));
                    g.setColor(Color.WHITE);
                }
                else {
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.BLACK);
                }
                g.drawLine((int) e.v0.loc.x, (int) e.v0.loc.y, (int) e.v1.loc.x, (int) e.v1.loc.y);
            }
            
            if (drawRivers && e.river > 0) {
                g.setStroke(new BasicStroke(1 + (int) Math.sqrt(e.river * 2)));
                g.setColor(RIVER);
                g.drawLine((int) e.v0.loc.x, (int) e.v0.loc.y, (int) e.v1.loc.x, (int) e.v1.loc.y);
            }
        }

        if (drawSites) {
            g.setColor(Color.BLACK);
            centers.stream().forEach((s) -> {
                g.fillOval((int) (s.loc.x - 2), (int) (s.loc.y - 2), 4, 4);
            });
        }
        else {
            g.setColor(Color.BLACK);
            // Draw cities
            centers.stream().filter(c -> c.city).forEach((s) -> {
                g.fillOval((int) (s.loc.x - 5), (int) (s.loc.y - 5), 10, 10);
            });
        }
        

        if (drawCorners) {
            g.setColor(Color.WHITE);
            corners.stream().forEach((c) -> {
                g.fillOval((int) (c.loc.x - 2), (int) (c.loc.y - 2), 4, 4);
            });
        }
        g.setColor(Color.WHITE);
        g.drawRect((int) bounds.x, (int) bounds.y, (int) bounds.width, (int) bounds.height);
    }

    private void buildGraph(Voronoi v) {
        final Map<Point, Center> pointCenterMap = new HashMap<>();
        final List<Point> points = v.siteCoords();
        points.stream().forEach((p) -> {
            Center c = new Center();
            c.loc = p;
            c.index = centers.size();
            centers.add(c);
            pointCenterMap.put(p, c);
        });

        //bug fix
        centers.stream().forEach((c) -> {
            v.region(c.loc);
        });

        final List<com.hoten.delaunay.voronoi.nodename.as3delaunay.Edge> libedges = v.edges();
        final Map<Integer, Corner> pointCornerMap = new HashMap<>();

        for (com.hoten.delaunay.voronoi.nodename.as3delaunay.Edge libedge : libedges) {
            final LineSegment vEdge = libedge.voronoiEdge();
            final LineSegment dEdge = libedge.delaunayLine();

            final Edge edge = new Edge();
            edge.index = edges.size();
            edges.add(edge);

            edge.v0 = makeCorner(pointCornerMap, vEdge.p0);
            edge.v1 = makeCorner(pointCornerMap, vEdge.p1);
            edge.d0 = pointCenterMap.get(dEdge.p0);
            edge.d1 = pointCenterMap.get(dEdge.p1);

            // Centers point to edges. Corners point to edges.
            if (edge.d0 != null) {
                edge.d0.borders.add(edge);
            }
            if (edge.d1 != null) {
                edge.d1.borders.add(edge);
            }
            if (edge.v0 != null) {
                edge.v0.protrudes.add(edge);
            }
            if (edge.v1 != null) {
                edge.v1.protrudes.add(edge);
            }

            // Centers point to centers.
            if (edge.d0 != null && edge.d1 != null) {
                addToCenterList(edge.d0.neighbors, edge.d1);
                addToCenterList(edge.d1.neighbors, edge.d0);
            }

            // Corners point to corners
            if (edge.v0 != null && edge.v1 != null) {
                addToCornerList(edge.v0.adjacent, edge.v1);
                addToCornerList(edge.v1.adjacent, edge.v0);
            }

            // Centers point to corners
            if (edge.d0 != null) {
                addToCornerList(edge.d0.corners, edge.v0);
                addToCornerList(edge.d0.corners, edge.v1);
            }
            if (edge.d1 != null) {
                addToCornerList(edge.d1.corners, edge.v0);
                addToCornerList(edge.d1.corners, edge.v1);
            }

            // Corners point to centers
            if (edge.v0 != null) {
                addToCenterList(edge.v0.touches, edge.d0);
                addToCenterList(edge.v0.touches, edge.d1);
            }
            if (edge.v1 != null) {
                addToCenterList(edge.v1.touches, edge.d0);
                addToCenterList(edge.v1.touches, edge.d1);
            }
        }
    }

    // Helper functions for the following for loop; ideally these
    // would be inlined
    private void addToCornerList(List<Corner> list, Corner c) {
        if (c != null && !list.contains(c)) {
            list.add(c);
        }
    }

    private void addToCenterList(List<Center> list, Center c) {
        if (c != null && !list.contains(c)) {
            list.add(c);
        }
    }

    //ensures that each corner is represented by only one corner object
    private Corner makeCorner(Map<Integer, Corner> pointCornerMap, Point p) {
        if (p == null) {
            return null;
        }
        int index = (int) ((int) p.x + (int) (p.y) * bounds.width * 2);
        Corner c = pointCornerMap.get(index);
        if (c == null) {
            c = new Corner();
            c.loc = p;
            c.border = bounds.liesOnAxes(p);
            c.index = corners.size();
            corners.add(c);
            pointCornerMap.put(index, c);
        }
        return c;
    }

    private void assignCornerElevations(HeightAlgorithm algorithm) {
        LinkedList<Corner> queue = new LinkedList<>();
        for (Corner c : corners) {
            c.water = algorithm.isWater(c.loc, bounds, r);
            if (c.border) {
                c.elevation = 0;
                queue.add(c);
            } else {
                c.elevation = Double.MAX_VALUE;
            }
        }

        while (!queue.isEmpty()) {
            Corner c = queue.pop();
            c.adjacent.forEach((a) -> {
                double newElevation = 0.01 + c.elevation;
                if (!c.water && !a.water) {
                    newElevation += 1;
                }
                if (newElevation < a.elevation) {
                    a.elevation = newElevation;
                    queue.add(a);
                }
            });
        }
    }

    private void assignOceanCoastAndLand() {
        LinkedList<Center> queue = new LinkedList<>();
        final double waterThreshold = .3;
        centers.forEach((center) -> {
            int numWater = 0;
            for (final Corner c : center.corners) {
                if (c.border) {
                    center.border = center.water = center.ocean = true;
                    queue.add(center);
                }
                if (c.water) {
                    numWater++;
                }
            }
            center.water = center.ocean || ((double) numWater / center.corners.size() >= waterThreshold);
        });
        while (!queue.isEmpty()) {
            final Center center = queue.pop();
            for (final Center n : center.neighbors) {
                if (n.water && !n.ocean) {
                    n.ocean = true;
                    queue.add(n);
                }
            }
        }
        centers.forEach((center) -> {
            boolean oceanNeighbor = false;
            boolean landNeighbor = false;
            for (Center n : center.neighbors) {
                oceanNeighbor |= n.ocean;
                landNeighbor |= !n.water;
            }
            center.coast = oceanNeighbor && landNeighbor;
        });

        corners.forEach((c) -> {
            int numOcean = 0;
            int numLand = 0;
            for (Center center : c.touches) {
                numOcean += center.ocean ? 1 : 0;
                numLand += !center.water ? 1 : 0;
            }
            c.ocean = numOcean == c.touches.size();
            c.coast = numOcean > 0 && numLand > 0;
            c.water = c.border || ((numLand != c.touches.size()) && !c.coast);
        });
    }

    private List<Corner> landCorners() {
        final List<Corner> list = new ArrayList<>();
        for (Corner c : corners) {
            if (!c.ocean && !c.coast) {
                list.add(c);
            }
        }
        return list;
    }

    private void redistributeElevations(List<Corner> landCorners) {
        Collections.sort(landCorners, (Corner o1, Corner o2) -> {
            if (o1.elevation > o2.elevation) {
                return 1;
            } else if (o1.elevation < o2.elevation) {
                return -1;
            }
            return 0;
        });

        final double SCALE_FACTOR = 1.1;
        for (int i = 0; i < landCorners.size(); i++) {
            double y = (double) i / landCorners.size();
            double x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y));
            x = Math.min(x, 1);
            landCorners.get(i).elevation = x;
        }

        for (Corner c : corners) {
            if (c.ocean || c.coast) {
                c.elevation = 0.0;
            }
        }
    }

    private void assignPolygonElevations() {
        centers.forEach((center) -> {
            double total = 0;
            for (Corner c : center.corners) {
                total += c.elevation;
            }
            center.elevation = total / center.corners.size();
        });
    }

    private void calculateDownslopes() {
        corners.forEach((c) -> {
            Corner down = c;
            //System.out.println("ME: " + c.elevation);
            for (Corner a : c.adjacent) {
                //System.out.println(a.elevation);
                if (a.elevation <= down.elevation) {
                    down = a;
                }
            }
            c.downslope = down;
        });
    }

    private void createRivers() {
        for (int i = 0; i < bounds.width / 2; i++) {
            Corner c = corners.get(r.nextInt(corners.size()));
            if (c.ocean || c.elevation < 0.3 || c.elevation > 0.9) {
                continue;
            }
            // Bias rivers to go west: if (q.downslope.x > q.x) continue;
            while (!c.coast) {
                if (c == c.downslope) {
                    break;
                }
                Edge edge = lookupEdgeFromCorner(c, c.downslope);
                if (!edge.v0.water || !edge.v1.water) {
                    edge.river++;
                    c.river++;
                    c.downslope.river++;  // TODO: fix double count
                }
                c = c.downslope;
            }
        }
    }

    private Edge lookupEdgeFromCorner(Corner c, Corner downslope) {
        for (Edge e : c.protrudes) {
            if (e.v0 == downslope || e.v1 == downslope) {
                return e;
            }
        }
        return null;
    }

    private void assignCornerMoisture() {
        LinkedList<Corner> queue = new LinkedList<>();
        corners.forEach((c) -> {
            if ((c.water || c.river > 0) && !c.ocean) {
                c.moisture = c.river > 0 ? Math.min(3.0, (0.2 * c.river)) : 1.0;
                queue.push(c);
            } else {
                c.moisture = 0.0;
            }
        });

        while (!queue.isEmpty()) {
            Corner c = queue.pop();
            c.adjacent.forEach((a) -> {
                double newM = .9 * c.moisture;
                if (newM > a.moisture) {
                    a.moisture = newM;
                    queue.add(a);
                }
            });
        }

        // Salt water
        for (Corner c : corners) {
            if (c.ocean || c.coast) {
                c.moisture = 1.0;
            }
        }
    }

    private void redistributeMoisture(List<Corner> landCorners) {
        Collections.sort(landCorners, (Corner o1, Corner o2) -> {
            if (o1.moisture > o2.moisture) {
                return 1;
            } else if (o1.moisture < o2.moisture) {
                return -1;
            }
            return 0;
        });
        for (int i = 0; i < landCorners.size(); i++) {
            landCorners.get(i).moisture = (double) i / landCorners.size();
        }
    }

    private void assignPolygonMoisture() {
        centers.forEach((center) -> {
            double total = 0;
            for (Corner c : center.corners) {
                total += c.moisture;
            }
            center.moisture = total / center.corners.size();
        });
    }

    private void assignBiomes() {
        centers.forEach((center) -> {
            center.biome = getBiome(center);
        });
    }
}
