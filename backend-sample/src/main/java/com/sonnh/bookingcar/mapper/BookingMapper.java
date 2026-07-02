package com.sonnh.bookingcar.mapper;

import com.sonnh.bookingcar.data.domain.AirportTransferDetail;
import com.sonnh.bookingcar.data.domain.ServiceRequest;
import com.sonnh.bookingcar.data.domain.TourBookingDetail;
import com.sonnh.bookingcar.dto.response.admin.AdminBookingResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverAirportDetailResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverRequestResDto;
import com.sonnh.bookingcar.dto.response.driver.DriverTourDetailResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingDetailResDto;
import com.sonnh.bookingcar.dto.response.tourist.TouristBookingResDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.sonnh.bookingcar.dto.response.admin.AdminAirportBookingResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourBookingResDto;
import com.sonnh.bookingcar.dto.request.tourist.AirportTransferReqDto;
import com.sonnh.bookingcar.dto.request.tourist.TourBookingReqDto;
import com.sonnh.bookingcar.data.domain.enums.ServiceType;
import com.sonnh.bookingcar.data.domain.enums.BookingStatus;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.time.LocalTime;

@Mapper(componentModel = "spring", 
        uses = {TourMapper.class},
        imports = {DateTimeFormatter.class, UUID.class, LocalTime.class, ServiceType.class, BookingStatus.class})
public interface BookingMapper {

    default String getDisplayPickup(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            String shortAddr = req.getAirportTransferDetail().getShortPickupLocation();
            return (shortAddr != null && !shortAddr.isEmpty()) ? shortAddr : req.getAirportTransferDetail().getPickupLocation();
        }
        if (req.getTourBookingDetail() != null) {
            return req.getTourBookingDetail().getPickupLocation();
        }
        return "";
    }

    default String getDisplayDropoff(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            String shortAddr = req.getAirportTransferDetail().getShortDropoffLocation();
            return (shortAddr != null && !shortAddr.isEmpty()) ? shortAddr : req.getAirportTransferDetail().getDropoffLocation();
        }
        return "";
    }
    
    default String getBookingDateStr(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getPickupDate() != null) {
            return req.getAirportTransferDetail().getPickupDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getPickupDate() != null) {
            return req.getTourBookingDetail().getPickupDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
        return "";
    }

    default String getBookingTimeStr(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getPickupTime() != null) {
            return req.getAirportTransferDetail().getPickupTime().toString();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getPickupDate() != null) {
            return "08:00";
        }
        return "";
    }

    default String getCreatedAtStr(ServiceRequest req) {
        if (req.getAudit() != null && req.getAudit().getCreatedAt() != null) {
            return req.getAudit().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }
        return "";
    }

    default String getDriverNameSnapshotOrCurrent(ServiceRequest req) {
        if (req.getHistory() != null && req.getHistory().getDriverNameSnapshot() != null) {
            return req.getHistory().getDriverNameSnapshot();
        }
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getCurrentShift() != null && req.getAirportTransferDetail().getCurrentShift().getDriver() != null) {
            return req.getAirportTransferDetail().getCurrentShift().getDriver().getFullName();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getCurrentShift() != null && req.getTourBookingDetail().getCurrentShift().getDriver() != null) {
            return req.getTourBookingDetail().getCurrentShift().getDriver().getFullName();
        }
        return "-";
    }

    default String getVehicleInfoSnapshotOrCurrent(ServiceRequest req) {
        if (req.getHistory() != null && req.getHistory().getVehiclePlateSnapshot() != null) {
            return req.getHistory().getVehiclePlateSnapshot();
        }
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getCurrentShift() != null && req.getAirportTransferDetail().getCurrentShift().getVehicle() != null) {
            return req.getAirportTransferDetail().getCurrentShift().getVehicle().getModel() + " - " + req.getAirportTransferDetail().getCurrentShift().getVehicle().getPlateNumber();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getCurrentShift() != null && req.getTourBookingDetail().getCurrentShift().getVehicle() != null) {
            return req.getTourBookingDetail().getCurrentShift().getVehicle().getModel() + " - " + req.getTourBookingDetail().getCurrentShift().getVehicle().getPlateNumber();
        }
        return "-";
    }

    default UUID getCurrentShiftIdNullable(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getCurrentShift() != null) {
            return req.getAirportTransferDetail().getCurrentShift().getId();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getCurrentShift() != null) {
            return req.getTourBookingDetail().getCurrentShift().getId();
        }
        return null;
    }

    default Integer getPassengersNullable(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            return req.getAirportTransferDetail().getPassengers();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getNumberOfPeople() != null) {
            return req.getTourBookingDetail().getNumberOfPeople();
        }
        return 0;
    }

    default String getNotesNullable(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            return req.getAirportTransferDetail().getNotes();
        }
        if (req.getTourBookingDetail() != null) {
            return req.getTourBookingDetail().getNotes();
        }
        return "";
    }

    default String getDescriptionNullable(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            return req.getAirportTransferDetail().getDescription();
        }
        return "";
    }

    default String getVehicleTypeRequestedObj(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            return req.getAirportTransferDetail().getVehicleTypeName();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getVehicleTypePrice() != null && req.getTourBookingDetail().getVehicleTypePrice().getVehicleType() != null) {
            return req.getTourBookingDetail().getVehicleTypePrice().getVehicleType().getName();
        }
        return null;
    }

    default String getPaymentMethodNullable(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) {
            return req.getAirportTransferDetail().getPaymentMethod();
        }
        if (req.getTourBookingDetail() != null) {
            return req.getTourBookingDetail().getPaymentMethod();
        }
        return "";
    }

    default Double getPickupLatNullable(ServiceRequest req) {
        return req.getAirportTransferDetail() != null ? req.getAirportTransferDetail().getPickupLat() : null;
    }

    default Double getPickupLonNullable(ServiceRequest req) {
        return req.getAirportTransferDetail() != null ? req.getAirportTransferDetail().getPickupLon() : null;
    }

    default Double getDropoffLatNullable(ServiceRequest req) {
        return req.getAirportTransferDetail() != null ? req.getAirportTransferDetail().getDropoffLat() : null;
    }

    default Double getDropoffLonNullable(ServiceRequest req) {
        return req.getAirportTransferDetail() != null ? req.getAirportTransferDetail().getDropoffLon() : null;
    }

    @Mapping(target = "customerName", source = "tourist.fullName")
    @Mapping(target = "customerPhone", source = "tourist.phone")
    @Mapping(target = "location", expression = "java(getDisplayPickup(req))")
    @Mapping(target = "date", expression = "java(getBookingDateStr(req))")
    @Mapping(target = "time", expression = "java(getBookingTimeStr(req))")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtStr(req))")
    @Mapping(target = "driverName", expression = "java(getDriverNameSnapshotOrCurrent(req))")
    @Mapping(target = "vehicleInfo", expression = "java(getVehicleInfoSnapshotOrCurrent(req))")
    @Mapping(target = "currentShiftId", expression = "java(getCurrentShiftIdNullable(req))")
    @Mapping(target = "pickupLocation", expression = "java(getDisplayPickup(req))")
    @Mapping(target = "dropoffLocation", expression = "java(getDisplayDropoff(req))")
    @Mapping(target = "passengers", expression = "java(getPassengersNullable(req))")
    @Mapping(target = "notes", expression = "java(getNotesNullable(req))")
    @Mapping(target = "description", expression = "java(getDescriptionNullable(req))")
    @Mapping(target = "vehicleTypeRequested", expression = "java(getVehicleTypeRequestedObj(req))")
    @Mapping(target = "paymentMethod", expression = "java(getPaymentMethodNullable(req))")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "pickupLat", expression = "java(getPickupLatNullable(req))")
    @Mapping(target = "pickupLon", expression = "java(getPickupLonNullable(req))")
    @Mapping(target = "dropoffLat", expression = "java(getDropoffLatNullable(req))")
    @Mapping(target = "dropoffLon", expression = "java(getDropoffLonNullable(req))")
    @Mapping(target = "tour", source = "tourBookingDetail.tour")
    @Mapping(target = "estimatedEndDate", source = "estimateEndTime")
    AdminBookingResDto toAdminBookingResDto(ServiceRequest req);

    @Mapping(target = "tourist.id", source = "tourist.id")
    @Mapping(target = "tourist.name", source = "tourist.fullName")
    @Mapping(target = "tourist.email", source = "tourist.email")
    @Mapping(target = "tourist.phone", source = "tourist.phone")
    @Mapping(target = "pickupLocation", source = "airportTransferDetail.pickupLocation")
    @Mapping(target = "dropoffLocation", source = "airportTransferDetail.dropoffLocation")
    @Mapping(target = "date", expression = "java(getBookingDateStr(req))")
    @Mapping(target = "time", source = "airportTransferDetail.pickupTime")
    @Mapping(target = "passengers", source = "airportTransferDetail.passengers")
    @Mapping(target = "flightNumber", source = "airportTransferDetail.flightNumber")
    @Mapping(target = "notes", source = "airportTransferDetail.notes")
    @Mapping(target = "description", source = "airportTransferDetail.description")
    @Mapping(target = "vehicleType", source = "airportTransferDetail.vehicleType")
    @Mapping(target = "paymentMethod", source = "airportTransferDetail.paymentMethod")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "pickupLat", source = "airportTransferDetail.pickupLat")
    @Mapping(target = "pickupLon", source = "airportTransferDetail.pickupLon")
    @Mapping(target = "dropoffLat", source = "airportTransferDetail.dropoffLat")
    @Mapping(target = "dropoffLon", source = "airportTransferDetail.dropoffLon")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtStr(req))")
    @Mapping(target = "driverName", expression = "java(getDriverNameSnapshotOrCurrent(req))")
    @Mapping(target = "vehicleInfo", expression = "java(getVehicleInfoSnapshotOrCurrent(req))")
    @Mapping(target = "currentShift.id", source = "airportTransferDetail.currentShift.id")
    @Mapping(target = "currentShift.driver.id", source = "airportTransferDetail.currentShift.driver.id")
    @Mapping(target = "currentShift.driver.name", source = "airportTransferDetail.currentShift.driver.fullName")
    @Mapping(target = "currentShift.driver.phone", source = "airportTransferDetail.currentShift.driver.phone")
    @Mapping(target = "currentShift.vehicle.id", source = "airportTransferDetail.currentShift.vehicle.id")
    @Mapping(target = "currentShift.vehicle.name", source = "airportTransferDetail.currentShift.vehicle.model")
    @Mapping(target = "currentShift.vehicle.plateNumber", source = "airportTransferDetail.currentShift.vehicle.plateNumber")
    @Mapping(target = "estimatedEndDate", source = "estimateEndTime")
    @Mapping(target = "cancelReason", ignore = true)
    AdminAirportBookingResDto toAdminAirportBookingResDto(ServiceRequest req);

    @Mapping(target = "tourist.id", source = "tourist.id")
    @Mapping(target = "tourist.fullName", source = "tourist.fullName")
    @Mapping(target = "tourist.email", source = "tourist.email")
    @Mapping(target = "tourist.phone", source = "tourist.phone")
    @Mapping(target = "pickupLocation", source = "tourBookingDetail.pickupLocation")
    @Mapping(target = "date", expression = "java(getBookingDateStr(req))")
    @Mapping(target = "time", expression = "java(getBookingTimeStr(req))")
    @Mapping(target = "passengers", source = "tourBookingDetail.numberOfPeople")
    @Mapping(target = "notes", source = "tourBookingDetail.notes")
    @Mapping(target = "paymentMethod", source = "tourBookingDetail.paymentMethod")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "tour", source = "tourBookingDetail.tour")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtStr(req))")
    @Mapping(target = "driverName", expression = "java(getDriverNameSnapshotOrCurrent(req))")
    @Mapping(target = "vehicleInfo", expression = "java(getVehicleInfoSnapshotOrCurrent(req))")
    @Mapping(target = "currentShift.id", source = "tourBookingDetail.currentShift.id")
    @Mapping(target = "currentShift.driver.id", source = "tourBookingDetail.currentShift.driver.id")
    @Mapping(target = "currentShift.driver.name", source = "tourBookingDetail.currentShift.driver.fullName")
    @Mapping(target = "currentShift.driver.phone", source = "tourBookingDetail.currentShift.driver.phone")
    @Mapping(target = "currentShift.vehicle.id", source = "tourBookingDetail.currentShift.vehicle.id")
    @Mapping(target = "currentShift.vehicle.name", source = "tourBookingDetail.currentShift.vehicle.model")
    @Mapping(target = "currentShift.vehicle.plateNumber", source = "tourBookingDetail.currentShift.vehicle.plateNumber")
    @Mapping(target = "dropoffLon", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "estimatedEndDate", source = "estimateEndTime")
    AdminTourBookingResDto toAdminTourBookingResDto(ServiceRequest req);

    default String getPickupDateStr(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getPickupDate() != null) {
            return req.getAirportTransferDetail().getPickupDate().toString();
        }
        if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getPickupDate() != null) {
            return req.getTourBookingDetail().getPickupDate().toString();
        }
        return null;
    }

    default String getPickupTimeStr(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null && req.getAirportTransferDetail().getPickupTime() != null) {
            return req.getAirportTransferDetail().getPickupTime().toString();
        }
         return "";
    }

    @Mapping(target = "pickupLocation", expression = "java(getDisplayPickup(req))")
    @Mapping(target = "dropoffLocation", expression = "java(getDisplayDropoff(req))")
    @Mapping(target = "pickupDate", expression = "java(getPickupDateStr(req))")
    @Mapping(target = "pickupTime", expression = "java(getPickupTimeStr(req))")
    @Mapping(target = "driverName", expression = "java(getDriverNameSnapshotOrCurrent(req))")
    @Mapping(target = "vehicleInfo", expression = "java(getVehicleInfoSnapshotOrCurrent(req))")
    TouristBookingResDto toTouristBookingResDto(ServiceRequest req);

    default String getRawPickupLocation(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) return req.getAirportTransferDetail().getPickupLocation();
        if (req.getTourBookingDetail() != null) return req.getTourBookingDetail().getPickupLocation();
        return "";
    }

    default String getRawDropoffLocation(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) return req.getAirportTransferDetail().getDropoffLocation();
        return "";
    }

    default String getFlightNumberStr(ServiceRequest req) {
        if (req.getAirportTransferDetail() != null) return req.getAirportTransferDetail().getFlightNumber();
        return "";
    }

    default String getVehicleCategoryObj(ServiceRequest req) {
         if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getVehicleTypePrice() != null && req.getTourBookingDetail().getVehicleTypePrice().getVehicleType() != null) {
            return req.getTourBookingDetail().getVehicleTypePrice().getVehicleType().getName();
        }
        return null;
    }

    default java.math.BigDecimal getTourPriceObj(ServiceRequest req) {
         if (req.getTourBookingDetail() != null && req.getTourBookingDetail().getVehicleTypePrice() != null) {
            return req.getTourBookingDetail().getVehicleTypePrice().getPrice();
        }
        return null;
    }
    
    default String getCreatedAtStrSimple(ServiceRequest req) {
        if (req.getAudit() != null && req.getAudit().getCreatedAt() != null) {
            return req.getAudit().getCreatedAt().toString();
        }
        return "";
    }

    @Mapping(target = "pickupLocation", expression = "java(getRawPickupLocation(req))")
    @Mapping(target = "dropoffLocation", expression = "java(getRawDropoffLocation(req))")
    @Mapping(target = "pickupDate", expression = "java(getPickupDateStr(req))")
    @Mapping(target = "pickupTime", expression = "java(getPickupTimeStr(req))")
    @Mapping(target = "passengers", expression = "java(getPassengersNullable(req))")
    @Mapping(target = "flightNumber", expression = "java(getFlightNumberStr(req))")
    @Mapping(target = "notes", expression = "java(getNotesNullable(req))")
    @Mapping(target = "vehicleTypeRequested", expression = "java(getVehicleTypeRequestedObj(req))")
    @Mapping(target = "vehicleCategory", expression = "java(getVehicleCategoryObj(req))")
    @Mapping(target = "tourPrice", expression = "java(getTourPriceObj(req))")
    @Mapping(target = "paymentMethod", expression = "java(getPaymentMethodNullable(req))")
    @Mapping(target = "driverName", expression = "java(getDriverNameSnapshotOrCurrent(req))")
    @Mapping(target = "vehicleInfo", expression = "java(getVehicleInfoSnapshotOrCurrent(req))")
    @Mapping(target = "createdAt", expression = "java(getCreatedAtStrSimple(req))")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "estimatedEndDate", source = "estimateEndTime")
    @Mapping(target = "cancelReason", ignore = true)
    TouristBookingDetailResDto toTouristBookingDetailResDto(ServiceRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tourist", ignore = true)
    @Mapping(target = "type", expression = "java(ServiceType.AIRPORT)")
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    @Mapping(target = "totalPrice", source = "estimatedPrice")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "airportTransferDetail", ignore = true)
    @Mapping(target = "tourBookingDetail", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "statusChangedBy", ignore = true)
    @Mapping(target = "statusChangeReason", ignore = true)
    @Mapping(target = "driverAmount", ignore = true)
    @Mapping(target = "bookingCode", ignore = true)
    @Mapping(target = "negotiatedPrice", ignore = true)
    @Mapping(target = "isNegotiated", ignore = true)
    @Mapping(target = "surcharges", ignore = true)
    @Mapping(target = "statusHistories", ignore = true)
    ServiceRequest toServiceRequest(AirportTransferReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tourist", ignore = true)
    @Mapping(target = "type", expression = "java(ServiceType.TOUR)")
    @Mapping(target = "status", expression = "java(BookingStatus.WAITING)")
    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "airportTransferDetail", ignore = true)
    @Mapping(target = "tourBookingDetail", ignore = true)
    @Mapping(target = "history", ignore = true)
    @Mapping(target = "statusChangedBy", ignore = true)
    @Mapping(target = "statusChangeReason", ignore = true)
    @Mapping(target = "driverAmount", ignore = true)
    @Mapping(target = "bookingCode", ignore = true)
    @Mapping(target = "negotiatedPrice", ignore = true)
    @Mapping(target = "isNegotiated", ignore = true)
    @Mapping(target = "surcharges", ignore = true)
    @Mapping(target = "statusHistories", ignore = true)
    ServiceRequest toServiceRequest(TourBookingReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pickupLocation", source = "pickupLocation")
    @Mapping(target = "shortPickupLocation", source = "shortPickupLocation")
    @Mapping(target = "dropoffLocation", source = "dropoffLocation")
    @Mapping(target = "shortDropoffLocation", source = "shortDropoffLocation")
    @Mapping(target = "vehicleType", ignore = true)
    @Mapping(target = "vehicleTypeName", ignore = true)
    @Mapping(target = "description", source = "description")
    @Mapping(target = "pickupLat", source = "pickupLat")
    @Mapping(target = "pickupLon", source = "pickupLon")
    @Mapping(target = "dropoffLat", source = "dropoffLat")
    @Mapping(target = "dropoffLon", source = "dropoffLon")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "serviceRequest", ignore = true)
    @Mapping(target = "flightNumber", ignore = true)
    @Mapping(target = "currentShift", ignore = true)
    AirportTransferDetail toAirportTransferDetail(AirportTransferReqDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "serviceRequest", ignore = true)
    @Mapping(target = "tour", ignore = true)
    @Mapping(target = "currentShift", ignore = true)
    @Mapping(target = "vehicleTypePrice", ignore = true)
    TourBookingDetail toTourBookingDetail(TourBookingReqDto dto);

    @Mapping(target = "requestId", source = "serviceRequest.id")
    @Mapping(target = "bookingCode", source = "serviceRequest.bookingCode")
    @Mapping(target = "type", source = "serviceRequest.type")
    @Mapping(target = "status", source = "serviceRequest.status")
    @Mapping(target = "customerName", source = "serviceRequest.tourist.fullName")
    @Mapping(target = "customerPhone", source = "serviceRequest.tourist.phone")
    @Mapping(target = "airportDetail.shortPickupLocation", source = "shortPickupLocation")
    @Mapping(target = "airportDetail.shortDropoffLocation", source = "shortDropoffLocation")
    @Mapping(target = "airportDetail.pickupDate", source = "pickupDate")
    @Mapping(target = "airportDetail.pickupTime", source = "pickupTime")
    @Mapping(target = "totalPrice", source = "serviceRequest.totalPrice")
    @Mapping(target = "estimateEndTime", source = "serviceRequest.estimateEndTime")
    @Mapping(target = "tourDetail", ignore = true)
    DriverRequestResDto airportDetailToDriverRequestResDto(AirportTransferDetail detail);

    @Mapping(target = "requestId", source = "serviceRequest.id")
    @Mapping(target = "bookingCode", source = "serviceRequest.bookingCode")
    @Mapping(target = "type", source = "serviceRequest.type")
    @Mapping(target = "status", source = "serviceRequest.status")
    @Mapping(target = "customerName", source = "serviceRequest.tourist.fullName")
    @Mapping(target = "customerPhone", source = "serviceRequest.tourist.phone")
    @Mapping(target = "tourDetail.pickupLocation", source = "pickupLocation")
    @Mapping(target = "tourDetail.pickupDate", source = "pickupDate")
    @Mapping(target = "totalPrice", source = "serviceRequest.totalPrice")
    @Mapping(target = "estimateEndTime", source = "serviceRequest.estimateEndTime")
    @Mapping(target = "airportDetail", ignore = true)
    DriverRequestResDto tourDetailToDriverRequestResDto(TourBookingDetail detail);

    @Mapping(target = "requestId", source = "id")
    @Mapping(target = "bookingCode", source = "serviceRequest.bookingCode")
    @Mapping(target = "type", source = "serviceRequest.type")
    @Mapping(target = "status", source = "serviceRequest.status")
    @Mapping(target = "customerName", source = "serviceRequest.tourist.fullName")
    @Mapping(target = "customerPhone", source = "serviceRequest.tourist.phone")
    @Mapping(target = "airportDetail.pickupLocation", source = "pickupLocation")
    @Mapping(target = "airportDetail.dropoffLocation", source = "dropoffLocation")
    @Mapping(target = "airportDetail.pickupLat", source = "pickupLat")
    @Mapping(target = "airportDetail.pickupLon", source = "pickupLon")
    @Mapping(target = "airportDetail.dropoffLat", source = "dropoffLat")
    @Mapping(target = "airportDetail.dropoffLon", source = "dropoffLon")
    @Mapping(target = "airportDetail.pickupDate", source = "pickupDate")
    @Mapping(target = "airportDetail.pickupTime", source = "pickupTime")
    @Mapping(target = "airportDetail.flightNumber", source = "flightNumber")
    @Mapping(target = "airportDetail.passengers", source = "passengers")
    @Mapping(target = "airportDetail.vehicleType", source = "vehicleTypeName")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "totalPrice", source = "serviceRequest.totalPrice")
    @Mapping(target = "estimateEndTime", source = "serviceRequest.estimateEndTime")
    @Mapping(target = "cancelReason", ignore = true)
    DriverAirportDetailResDto airportDetailToDriverAirportDetailResDto(AirportTransferDetail detail);

    @Mapping(target = "requestId", source = "serviceRequest.id")
    @Mapping(target = "bookingCode", source = "serviceRequest.bookingCode")
    @Mapping(target = "type", source = "serviceRequest.type")
    @Mapping(target = "status", source = "serviceRequest.status")
    @Mapping(target = "customerName", source = "serviceRequest.tourist.fullName")
    @Mapping(target = "customerPhone", source = "serviceRequest.tourist.phone")
    @Mapping(target = "tourDetail.pickupLocation", source = "pickupLocation")
    @Mapping(target = "tourDetail.pickupDate", source = "pickupDate")
    @Mapping(target = "tourDetail.pickupTime", expression = "java(LocalTime.of(8, 0))")
    @Mapping(target = "tourDetail.passengers", source = "numberOfPeople")
    @Mapping(target = "tourDetail.tour", source = "tour")
    @Mapping(target = "notes", source = "notes")
    @Mapping(target = "paymentMethod", source = "paymentMethod")
    @Mapping(target = "totalPrice", source = "serviceRequest.totalPrice")
    @Mapping(target = "estimateEndTime", source = "serviceRequest.estimateEndTime")
    @Mapping(target = "cancelReason", ignore = true)
    DriverTourDetailResDto tourDetailToDriverTourDetailResDto(TourBookingDetail detail);
}

