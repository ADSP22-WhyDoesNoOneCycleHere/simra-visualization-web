package com.simra.app.csvimporter.service;

import com.mongodb.client.model.geojson.LineString;
import com.mongodb.client.model.geojson.Position;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBeanBuilder;
import com.simra.app.csvimporter.controller.RideRepository;
import com.simra.app.csvimporter.filter.MapMatchingService;
import com.simra.app.csvimporter.filter.RideFilter;
import com.simra.app.csvimporter.model.RideCSV;
import com.simra.app.csvimporter.model.RideEntity;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RideParserThreaded extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(RideParserThreaded.class);


    private File file;

    private RideRepository rideRepository;

    private RideFilter rideFilter;

    private MapMatchingService mapMatchingService;


    public RideParserThreaded(File f, RideRepository rideRepository, Float min_accuracy, double rdp_epsilion, MapMatchingService mapMatchingService) {
        this.file = f;
        this.rideRepository = rideRepository;
        this.rideFilter = new RideFilter(min_accuracy, rdp_epsilion);
        this.mapMatchingService = mapMatchingService;
    }


    @Override
    public void run() {
        LOG.info("Ride parser running " + this.file.getName());
        try (BufferedReader reader = new BufferedReader(new FileReader(this.file))) {
            String line = reader.readLine();
            String[] arrOfStr = line.split("#");

            while (line != null) {
                line = reader.readLine();
                if (line.contains("==")) {
                    break;
                }
            }
            StringBuilder rideCSVwithHeader = new StringBuilder();
            String currentLine = reader.readLine();
            while (currentLine != null) {
                currentLine = reader.readLine();
                /*
                 * add ride information only if it has location data.
                 */
                if (currentLine == null) {
                    break;
                }
                if (currentLine.trim().length() > 0 && !currentLine.isEmpty() && !currentLine.trim().startsWith(",")) {
                    rideCSVwithHeader.append(currentLine.trim()).append("\r\n");
                }
            }
            ColumnPositionMappingStrategy<RideEntity> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(RideEntity.class);
            String[] memberFieldsToBindTo = {"lat", "lon", "X", "Y", "Z", "timeStamp", "acc", "a", "b", "c"};
            strategy.setColumnMapping(memberFieldsToBindTo);

            List<RideCSV> rideBeans = new CsvToBeanBuilder<RideCSV>(new StringReader(rideCSVwithHeader.toString().trim()))
                    .withMappingStrategy(strategy)
                    .withSkipLines(1)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build().parse();

            /*
             * ALl filters before DB Entity must chain here.
             */
            // RDP Filter
            List<RideCSV> optimisedRideBeans = this.rideFilter.filterRide(rideBeans);
            // Map Matching
            List mapMatchedRideBeans=null;
                mapMatchedRideBeans = mapMatchingService.matchToMap(optimisedRideBeans);


            /**
             * All filters must end before this
             */

            RideEntity rideEntity = getRideEntity(arrOfStr, optimisedRideBeans);
            // adding to entity
            rideEntity.setMapMatchedRideBeans(mapMatchedRideBeans);
            rideEntity.setDistance(mapMatchingService.getCurrentRouteDistance());
            rideEntity.setDuration(mapMatchingService.getCurrentRouteDuration());

            rideEntity.setMinuteOfDay(Utils.getMinuteOfDay(rideEntity.getTimeStamp()));
            rideEntity.setWeekday(Utils.getWeekday(rideEntity.getTimeStamp()));

            rideRepository.save(rideEntity);

        } catch (Exception e) {
            LOG.error(String.valueOf(e));
        }
        LOG.info("Ride parser complete " + this.file.getName());


    }

    @NotNull
    private RideEntity getRideEntity(String[] arrOfStr, List<RideCSV> rideBeans) {
        RideEntity rideEntity = new RideEntity();
        rideEntity.setId(this.file.getName());
        rideEntity.setFileId(this.file.getName());
        rideEntity.setAppVersion(arrOfStr[0]);
        rideEntity.setFileVersion(Integer.parseInt(arrOfStr[1]));

        ArrayList<Position> coordinates = new ArrayList<>();

        rideBeans.forEach(ride -> {
            List<Double> places = Arrays.asList(Double.parseDouble(ride.getLon()), Double.parseDouble(ride.getLat()));
            Position pos = new Position(places);
            coordinates.add(pos);
        });
        LineString coordinatesMulti = new LineString(coordinates);
        rideEntity.setLocation(coordinatesMulti);

        ArrayList<Long> ts = new ArrayList<>();
        rideBeans.forEach(ride -> ts.add((ride).getTimeStamp()));
        rideEntity.setTs(ts);

        rideEntity.setAddedAt(new Date());
        return rideEntity;
    }
}
