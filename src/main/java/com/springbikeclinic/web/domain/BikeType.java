package com.springbikeclinic.web.domain;

public enum BikeType {
    ROAD("Road"),
    MOUNTAIN("Mountain"),
    GRAVEL("Gravel"),
    CYCLOCROSS("Cyclocross"),
    DIRT_JUMP("Dirt Jump"),
    DOWNHILL("Downhill"),
    TOURING("Touring"),
    HYBRID("Hybrid"),
    E_BIKE("E-Bike"),
    RECREATIONAL("Recreational"),
    CRUISER("Cruiser"),
    BMX("BMX"),
    CHILDRENS("Children's"),
    OTHER("Other");

    private final String description;

    BikeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
