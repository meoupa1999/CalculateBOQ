package com.sonnh.bookingcar.data;

import com.sonnh.bookingcar.data.domain.*;

import com.sonnh.bookingcar.data.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class TourDataSeeder implements CommandLineRunner {

    private final TourRepository tourRepository;
    private final TourItineraryRepository tourItineraryRepository;
    private final TourHighlightRepository tourHighlightRepository;
    private final VehiclesTypePriceRepository vehiclesTypePriceRepository;
    private final TourBookingDetailRepository tourBookingDetailRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Value("${app.data.seed-tours:false}")
    private boolean shouldSeed;

    private static final String[] CATEGORIES = {
            "C5_VIOS_SUV_MPV",
            "C7_AVANZA_VELOS",
            "C16_FORD_VAN",
            "C16_SOLATI_IVECO_VAN",
            "C18_FORD_IVECO",
            "C29_THACO_UNIVER",
            "C34_THACO_UNIVER",
            "C45_THACO_UNIVER",
            "SKYBUS_VIP_9S",
            "DCAR_VIP_9S",
            "LIMO_12S_VIP",
            "LIMO_19S_VIP",
            "LIMO_28S_VIP",
            "S7_VIP_CARNIVAL",
            "CAMRY_5S2_5_VIP",
            "MECEDES_5S_GLC200_VIP"
    };

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!shouldSeed) {
            log.info("Tour seeding is disabled.");
            return;
        }

        log.info("Starting tour data seeding from data.txt with UTF-8 fix...");
        
        // Clear old data to fix UTF-8 mangled entries - Delete children and bookings first
        tourBookingDetailRepository.deleteAll();
        vehiclesTypePriceRepository.deleteAll();
        tourItineraryRepository.deleteAll();
        tourHighlightRepository.deleteAll();
        tourRepository.deleteAll();
        
        String filePath = "data.txt";
        java.io.File file = new java.io.File(filePath);
        if (!file.exists()) {
            filePath = "../data.txt";
            file = new java.io.File(filePath);
        }
        
        if (!file.exists()) {
            log.error("Could not find data.txt in {} or ../data.txt. Skipping seeding.", new java.io.File(".").getAbsolutePath());
            return;
        }

        log.info("Loading seeding data from: {}", file.getAbsolutePath());
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        List<List<String>> rows = parseTSV(content.toString());
        
        // Skip header (first row might be spread across multiple lines in raw, but parseTSV should handle it)
        // In data.txt, lines 1-16 are header info.
        // My parseTSV needs to be smart.
        
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.size() < 18) continue;

            String rawName = row.get(0).trim();
            if (rawName.isEmpty() || rawName.equalsIgnoreCase("LỊCH TRÌNH")) continue;

            if (tourRepository.findByName(rawName).isPresent()) {
                log.info("Tour '{}' already exists, skipping.", rawName);
                continue;
            }

            // 1. Create Tour
            String capitalizedName = capitalizeFirstLetter(rawName);
            Tour tour = Tour.builder()
                    .name(capitalizedName)
                    .description(capitalizedName)
                    .build();
            
            // 2. Add vehicle prices & Find Lowest Price
            BigDecimal minPrice = null;
            for (int col = 1; col <= 16; col++) {
                BigDecimal price = parsePrice(row.get(col));
                if (price.compareTo(BigDecimal.ZERO) > 0) {
                    if (minPrice == null || price.compareTo(minPrice) < 0) {
                        minPrice = price;
                    }
                    String categoryName = CATEGORIES[col - 1];
                    VehicleType vt = vehicleTypeRepository.findByName(categoryName)
                            .orElseGet(() -> {
                                VehicleType newVt = VehicleType.builder()
                                        .name(categoryName)
                                        .isDistanceBookingEnabled(false) // Tours default to false for distance
                                        .build();
                                return vehicleTypeRepository.save(newVt);
                            });

                    VehiclesTypePrice vp = VehiclesTypePrice.builder()
                            .vehicleType(vt)
                            .price(price)
                            .tour(tour)
                            .build();
                    vehiclesTypePriceRepository.save(vp);
                    tour.addVehiclePrice(vp);
                }
            }
            
            tour.setPrice(minPrice != null ? minPrice : BigDecimal.ZERO);
            
            // Extract tracking points from Column 1
            extractTrackingPoints(capitalizedName, tour);

            tour = tourRepository.save(tour);

            // 3. Add Highlights (from Column 17)
            String highlightsStr = row.get(17).trim();
            parseAndAddHighlights(highlightsStr, tour);

            tourRepository.save(tour);
            log.info("Saved tour: {}", capitalizedName);
        }

        log.info("Tour data seeding completed.");
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) return str;
        str = str.trim();
        if (str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private List<List<String>> parseTSV(String content) {
        List<List<String>> rows = new ArrayList<>();
        // Simple TSV parser that handles quoted fields with newlines
        List<String> currentRow = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == '\t' && !inQuotes) {
                currentRow.add(currentField.toString().trim());
                currentField.setLength(0);
            } else if (c == '\n' && !inQuotes) {
                currentRow.add(currentField.toString().trim());
                rows.add(new ArrayList<>(currentRow));
                currentRow.clear();
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        if (!currentRow.isEmpty() || currentField.length() > 0) {
            currentRow.add(currentField.toString().trim());
            rows.add(currentRow);
        }
        return rows;
    }

    private BigDecimal parsePrice(String priceStr) {
        try {
            String clean = priceStr.replaceAll("[^0-9]", "");
            if (clean.isEmpty()) return BigDecimal.ZERO;
            return new BigDecimal(clean);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void extractTrackingPoints(String description, Tour tour) {
        // Simple extraction: look for keywords like "Nam Đảo", "Bắc Đảo", "Trung Tâm"
        String[] keywords = {"Nam Đảo", "Bắc Đảo", "Trung Tâm", "Vinpearl", "Sân Bay", "An Thới", "Bãi Trường"};
        int order = 0;
        for (String key : keywords) {
            if (description.toLowerCase().contains(key.toLowerCase())) {
                TourItinerary it = TourItinerary.builder()
                        .title(capitalizeFirstLetter(key))
                        .description("Điểm tracking: " + capitalizeFirstLetter(key))
                        .time("") // Use empty string to avoid not-null constraint
                        .orderIndex(order++)
                        .tour(tour)
                        .build();
                tourItineraryRepository.save(it);
                tour.addItinerary(it);
            }
        }
    }

    private void parseAndAddHighlights(String content, Tour tour) {
        String[] lines = content.split("\n");
        int order = 0;
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            // Extract title (remove leading numbers like "1. ")
            String title = line.replaceAll("^[0-9]+\\.\\s*", "");
            title = capitalizeFirstLetter(title);
            
            TourHighlight highlight = TourHighlight.builder()
                    .title(title)
                    .description(null) // Only title as per user request
                    .orderIndex(order++)
                    .tour(tour)
                    .build();
            tourHighlightRepository.save(highlight);
            tour.addHighlight(highlight);
        }
    }
}
