package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.Tour;
import com.sonnh.bookingcar.dto.request.admin.AdminTourReqDto;
import com.sonnh.bookingcar.dto.request.admin.AdminTourUpdateReqDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import com.sonnh.bookingcar.data.domain.SpecialLocationTour;
import com.sonnh.bookingcar.data.domain.VehiclesTypePrice;
import com.sonnh.bookingcar.data.domain.CheckinTourHistory;
import com.sonnh.bookingcar.data.domain.User;
import com.sonnh.bookingcar.data.domain.VehicleType;

@Mapper(componentModel = "spring", imports = {Comparator.class, Collectors.class, SpecialLocationTour.class})
public interface TourMapper {

    @Mapping(target = "tourName", source = "name")
    @Mapping(target = "createdAt", expression = "java(tour.getAudit() != null && tour.getAudit().getCreatedAt() != null ? tour.getAudit().getCreatedAt().toString() : null)")
    @Mapping(target = "specialLocationMappings", expression = "java(tour.getSpecialLocationMappings() != null ? tour.getSpecialLocationMappings().stream().sorted(Comparator.comparing(SpecialLocationTour::getPriority)).map(this::toMappingResDto).collect(Collectors.toList()) : null)")
    @Mapping(target = "checkinTourHistories", source = "checkinTourHistories")
    AdminTourResDto toAdminTourResDto(Tour tour);

    @AfterMapping
    default void setMinPrice(@MappingTarget AdminTourResDto dto, Tour tour) {
        try {
            if (tour.getVehiclePrices() != null && !tour.getVehiclePrices().isEmpty()) {
                BigDecimal minPrice = tour.getVehiclePrices().stream()
                        .filter(vp -> vp != null && vp.getPrice() != null)
                        .map(VehiclesTypePrice::getPrice)
                        .filter(p -> p.compareTo(BigDecimal.ZERO) > 0)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                dto.setPrice(minPrice);
            } else {
                dto.setPrice(tour.getPrice() != null ? tour.getPrice() : BigDecimal.ZERO);
            }
        } catch (Exception e) {
            dto.setPrice(BigDecimal.ZERO);
        }
    }

    @Mapping(target = "tourId", source = "tour.id")
    @Mapping(target = "specialLocationId", source = "specialLocation.id")
    AdminTourResDto.SpecialLocationTourDto toMappingDto(SpecialLocationTour mapping);

    @Mapping(target = "specialLocationId", source = "specialLocation.id")
    @Mapping(target = "name", source = "specialLocation.locationName")
    AdminTourResDto.SpecialLocationMappingResDto toMappingResDto(SpecialLocationTour mapping);
 
    @Mapping(target = "checkinTourId", source = "id")
    @Mapping(target = "createdAt", expression = "java(history.getAudit() != null && history.getAudit().getCreatedAt() != null ? history.getAudit().getCreatedAt().toString() : null)")
    @Mapping(target = "driver", source = "user")
    AdminTourResDto.CheckinTourHistoryResDto toCheckinHistoryResDto(CheckinTourHistory history);
 
    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    AdminTourResDto.DriverDto toDriverDto(User user);

    AdminTourResDto.VehiclesTypePriceDto toVehiclesTypePriceDto(VehiclesTypePrice vp);
    
    AdminTourResDto.VehicleTypeDto toVehicleTypeDto(VehicleType vt);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "tourName")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "itineraries", ignore = true)
    @Mapping(target = "vehiclePrices", ignore = true)
    @Mapping(target = "highlights", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "specialLocationMappings", ignore = true)
    @Mapping(target = "checkinTourHistories", ignore = true)
    Tour toTour(AdminTourReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "tourName")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "itineraries", ignore = true)
    @Mapping(target = "vehiclePrices", ignore = true)
    @Mapping(target = "highlights", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "specialLocationMappings", ignore = true)
    @Mapping(target = "checkinTourHistories", ignore = true)
    void updateTourFromDto(AdminTourReqDto dto, @MappingTarget Tour tour);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "tourName")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "itineraries", ignore = true)
    @Mapping(target = "vehiclePrices", ignore = true)
    @Mapping(target = "highlights", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "specialLocationMappings", ignore = true)
    @Mapping(target = "checkinTourHistories", ignore = true)
    void updateTourFromDto(AdminTourUpdateReqDto dto, @MappingTarget Tour tour);
}
