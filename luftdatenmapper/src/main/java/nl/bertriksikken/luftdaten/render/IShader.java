package nl.bertriksikken.luftdaten.render;

import java.util.List;

import nl.bertriksikken.luftdaten.config.Coord;

public interface IShader {

    /**
     * Calculates the value at some specific coordinate.
     * 
     * @param sensorValues the sensor data
     * @param coordinate the coordinate
     * @return the value, RGBA array
     */
    int[] calculatePixel(List<SensorValue> sensorValues, Coord coordinate);

}
