package com.harifrizki.crimemapsappsapi.service.impl;

import com.harifrizki.crimemapsappsapi.service.CrimeLocationService;
import org.springframework.stereotype.Component;

@Component
public class CrimeLocationServiceImpl implements CrimeLocationService {

    @Override
    public double calculateDistance(double userLocationLatitude, double userLocationLongitude, double crimeLocationLatitude, double crimeLocationLongitude) {
        if (userLocationLatitude == crimeLocationLatitude &&
                userLocationLongitude == crimeLocationLongitude) {
            return 0.0;
        } else {
            userLocationLatitude = Math.toRadians(userLocationLatitude);
            userLocationLongitude = Math.toRadians(userLocationLongitude);
            crimeLocationLatitude = Math.toRadians(crimeLocationLatitude);
            crimeLocationLongitude = Math.toRadians(crimeLocationLongitude);

            // Haversine formula
            double longitude = crimeLocationLongitude - userLocationLongitude;
            double latitude = crimeLocationLatitude - userLocationLatitude;
            double a = Math.pow(Math.sin(latitude / 2), 2)
                    + Math.cos(userLocationLatitude) * Math.cos(crimeLocationLatitude)
                    * Math.pow(Math.sin(longitude / 2),2);

            double c = 2 * Math.asin(Math.sqrt(a));

            // Radius of earth in kilometers. Use 3956
            // for miles
            double r = 6371;

            // calculate the result
            return (c * r);
        }
    }
}
