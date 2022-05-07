package com.harifrizki.crimemapsappsapi.service;

public interface CrimeLocationService {
    double calculateDistance(double locationLatitude,
                             double locationLongitude,
                             double mapsLatitude,
                             double mapsLongitude);
}
