package org.easetech.easytest.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw  data in to a user defined Map type instance
 * 
 * @author Anuj Kumar
 *
 */
public class MapConverter implements BaseConverter<List<Map<String , Object>> , List<PotentialAssignment>> {
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(MapConverter.class);
    
    /**
     * The type of Map to convert to
     */
    private final Class<?> mapType;
    
    /**
     * 
     * Construct a new MapConverter
     * @param mapType the type of map to convert to
     */
    public MapConverter(Class<?> mapType) {
        this.mapType = mapType;
    }


    /**
     * Convert the given list of raw data to a list of map instance 
     * @param convertFrom the data to convert from
     * @return the list of {@link PotentialAssignment}
     */
    @SuppressWarnings("unchecked")
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        List<PotentialAssignment> potentialAssignments = null;
        if (Map.class.isAssignableFrom(mapType)) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            for (Map<String, Object> map : convertFrom) {
                if (mapType.isInterface()) {
                    potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, map));
                } else {
                    Map dataValues;
                    try {
                        dataValues = (Map) mapType.newInstance();
                    } catch (Exception e) {
                        LOG.error(
                            "Exception occured while trying to convert the data to Map(using newInstance() method). "
                                + "The type of Map passed as input parameter is :" + mapType, e);
                        throw new RuntimeException(e);
                    } 
                    dataValues.putAll(map);
                    potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, dataValues));
                }

            }
        }
        
        return potentialAssignments;
    }

}
