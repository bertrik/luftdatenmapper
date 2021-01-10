package nl.bertriksikken.luftdaten.render;

import java.util.List;

import nl.bertriksikken.luftdaten.config.Coord;
import nl.bertriksikken.luftdaten.config.RenderJob;

public class InverseDistanceWeightShader implements IShader {

    private static final double KM_PER_DEGREE_LAT = 40075.0 / 360.0;

    private final double minRadiusSquared;
    private final double maxDistanceSquared;
    private final double[] aspect;

    /**
     * Constructor.
     * 
     * @param job the render job
     */
    public InverseDistanceWeightShader(RenderJob job) {
    	this.minRadiusSquared = Math.pow(job.getMinRadius(), 2);
        this.maxDistanceSquared = Math.pow(job.getMaxDistance(), 2);

        // calculate km per degree
        Coord center = new Coord((job.getWest() + job.getEast()) / 2, (job.getNorth() + job.getSouth()) / 2);
        this.aspect = new double[] { KM_PER_DEGREE_LAT * Math.cos(Math.toRadians(center.getY())), KM_PER_DEGREE_LAT };
    }

    @Override
    public double calculatePixel(List<SensorValue> sensorValues, Coord coordinate) {
        double weightSum = 0.0;
        double valueSum = 0.0;
        double closestDistSquared = Double.MAX_VALUE;
        double closestDistValue = 0.0;
        for (SensorValue dp : sensorValues) {
            Coord c1 = new Coord(dp.getX(), dp.getY());
            double d2 = distanceSquared(aspect, coordinate, c1);
            double w = 1.0 / d2;
            double v = dp.getValue();
            valueSum += (v * w);
            weightSum += w;
            if (d2 < closestDistSquared) {
            	closestDistSquared = d2;
            	closestDistValue = v;
            }
        }
        if (closestDistSquared < minRadiusSquared) {
        	return -closestDistValue;
        }
        return (closestDistSquared < maxDistanceSquared) ? valueSum / weightSum : Double.NaN;
    }

    /**
     * Calculates a measure of the distance between two coordinates.
     * 
     * @param aspect the x/y aspect ratio
     * @param c1 the first coordinate
     * @param c2 the second coordinate
     * @return distance-squared
     */
    private double distanceSquared(double[] aspect, Coord c1, Coord c2) {
        double dx = aspect[0] * (c1.getX() - c2.getX());
        double dy = aspect[1] * (c1.getY() - c2.getY());
        return (dx * dx) + (dy * dy);
    }

}
