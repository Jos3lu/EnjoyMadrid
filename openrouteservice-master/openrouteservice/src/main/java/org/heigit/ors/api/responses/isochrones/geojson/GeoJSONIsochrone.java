package org.heigit.ors.api.responses.isochrones.geojson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import org.heigit.ors.isochrones.Isochrone;

public class GeoJSONIsochrone extends GeoJSONIsochroneBase {
    private final Isochrone isochrone;

    @JsonProperty("properties")
    public GeoJSONIsochroneProperties properties;

    public GeoJSONIsochrone(Isochrone isochrone, Coordinate center, int travellerId) {
        this.isochrone = isochrone;
        properties = new GeoJSONIsochroneProperties(this.isochrone, center, travellerId);
    }

    @Override
    Geometry getIsochroneGeometry() {
        return isochrone.getGeometry();
    }
}
