package com.tisza.bpcarsharing;

import com.google.android.gms.maps.model.LatLng;

import java.util.*;

public class Shape
{
    public CarsharingService carsharingService;
    public List<LatLng> coords = new ArrayList<>();
    public List<List<LatLng>> holes = new ArrayList<>();
}
