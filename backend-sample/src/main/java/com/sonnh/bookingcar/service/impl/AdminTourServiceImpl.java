package com.sonnh.bookingcar.service.impl;

import com.sonnh.bookingcar.data.repository.*;
import com.sonnh.bookingcar.data.domain.*;
import com.sonnh.bookingcar.data.specification.TourSpecification;
import org.springframework.data.jpa.domain.Specification;
import com.sonnh.bookingcar.dto.request.admin.AdminTourReqDto;
import com.sonnh.bookingcar.dto.request.admin.AdminTourUpdateReqDto;
import com.sonnh.bookingcar.dto.response.PageImplResDto;
import com.sonnh.bookingcar.dto.response.admin.AdminTourResDto;
import com.sonnh.bookingcar.exception.ResourceNotFoundException;
import com.sonnh.bookingcar.mapper.TourMapper;
import com.sonnh.bookingcar.service.interfaces.AdminTourService;
import com.sonnh.bookingcar.dto.request.admin.TourPriceUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminTourServiceImpl implements AdminTourService {

    private final TourRepository tourRepository;
    private final TourItineraryRepository tourItineraryRepository;
    private final TourMapper tourMapper;
    private final VehiclesTypePriceRepository vehiclesTypePriceRepository;
    private final SpecialLocationRepository specialLocationRepository;
    private final SpecialLocationTourRepository specialLocationTourRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

    @Override
    @Transactional
    public AdminTourResDto createTour(AdminTourReqDto dto) {
        Tour tour = tourMapper.toTour(dto);
        tour = tourRepository.save(tour);

        if (dto.getItineraries() != null) {
            for (AdminTourReqDto.ItineraryReqDto iDto : dto.getItineraries()) {
                TourItinerary itinerary = TourItinerary.builder()
                        .time(iDto.getTime())
                        .title(iDto.getTitle())
                        .description(iDto.getDescription())
                        .orderIndex(iDto.getOrderIndex())
                        .build();
                tour.addItinerary(itinerary);
                tourItineraryRepository.save(itinerary);
            }
        }

        if (dto.getVehiclePrices() != null) {
            for (AdminTourReqDto.VehiclesTypePriceDto vpDto : dto.getVehiclePrices()) {
                VehicleType vt = vehicleTypeRepository.findById(vpDto.getVehicleTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Vehicle type not found with id: " + vpDto.getVehicleTypeId()));
                VehiclesTypePrice vp = VehiclesTypePrice
                        .builder()
                        .price(vpDto.getPrice())
                        .build();
                vp.addVehicleType(vt);
                vehiclesTypePriceRepository.save(vp);
                tour.addVehiclePrice(vp);

            }
        }
        if (dto.getSpecialLocations() != null) {
            final Tour finalTour = tour;
            dto.getSpecialLocations().stream()
                    .sorted(Comparator.comparing(AdminTourReqDto.SpecialLocationReqDto::getPriority)).map(slDto -> {
                        SpecialLocation sl = specialLocationRepository.findById(slDto.getId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Special location not found with id: " + slDto.getId()));
                        SpecialLocationTour mapping = SpecialLocationTour.builder()
                                .priority(slDto.getPriority())
                                .build();
                        mapping.addTour(finalTour);
                        mapping.addSpecialLocation(sl);
                        return mapping;
                    }).forEach(specialLocationTourRepository::save);
        }

        tour = tourRepository.save(tour);

        return tourMapper.toAdminTourResDto(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public PageImplResDto<AdminTourResDto> getTours(String search, Pageable pageable) {
        Specification<Tour> spec = Specification.where(TourSpecification.isActive())
                .and(TourSpecification.hasNameLike(search));
        Page<Tour> page = tourRepository.findAll(spec, pageable);
        Page<AdminTourResDto> dtoPage = page.map(tourMapper::toAdminTourResDto);
        return PageImplResDto.fromPage(dtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminTourResDto getTourById(UUID id) {
        Tour tour = tourRepository.findById(id)
                .filter(t -> t.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found or inactive with id: " + id));
        return tourMapper.toAdminTourResDto(tour);
    }

    @Override
    @Transactional
    public AdminTourResDto updateTour(UUID id, AdminTourUpdateReqDto dto) {
        Tour tour = tourRepository.findById(id)
                .filter(t -> t.getAudit().getIsActive())
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found or inactive with id: " + id));

        tourMapper.updateTourFromDto(dto, tour);

        if (dto.getItineraries() != null) {
            tourItineraryRepository.deleteAll(tour.getItineraries());
            tour.getItineraries().clear();

            for (AdminTourUpdateReqDto.ItineraryReqDto iDto : dto.getItineraries()) {
                TourItinerary itinerary = TourItinerary.builder()
                        .time(iDto.getTime())
                        .title(iDto.getTitle())
                        .description(iDto.getDescription())
                        .orderIndex(iDto.getOrderIndex())
                        .build();
                tour.addItinerary(itinerary);
                tourItineraryRepository.save(itinerary);
            }
        }

        if (dto.getVehiclePrices() != null) {
            List<VehiclesTypePrice> vp = tour.getVehiclePrices();
            // tour.getVehiclePrices().clear();
            for (AdminTourUpdateReqDto.VehiclesTypePriceDto vpDto : dto.getVehiclePrices()) {
                VehiclesTypePrice vpt = vp.stream().filter(v -> v.getId().equals(vpDto.getVehiclesTypePriceId()))
                        .findFirst().orElse(null);
                if (vpt != null) {
                    log.info("Vehicle type price found: {}", vpt.getId());
                    vpt.setPrice(vpDto.getPrice());
                    vehiclesTypePriceRepository.save(vpt);
                    log.info("Đã save xong !!");
                }
            }
        }

        if (dto.getSpecialLocations() != null) {
            specialLocationTourRepository.deleteAll(tour.getSpecialLocationMappings());
            tour.getSpecialLocationMappings().clear();

            final Tour finalTour = tour;
            dto.getSpecialLocations().stream().map(slDto -> {
                SpecialLocation sl = specialLocationRepository.findById(slDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Special location not found with id: " + slDto.getId()));
                SpecialLocationTour mapping = SpecialLocationTour.builder()
                        .priority(slDto.getPriority())
                        .build();
                mapping.addTour(finalTour);
                mapping.addSpecialLocation(sl);
                return mapping;
            }).forEach(specialLocationTourRepository::save);
        }

        Tour updatedTour = tourRepository.save(tour);
        return tourMapper.toAdminTourResDto(updatedTour);
    }

    @Override
    @Transactional
    public void bulkUpdatePrices(List<TourPriceUpdateDto> dtos) {
        for (TourPriceUpdateDto dto : dtos) {
            Tour tour = tourRepository.findById(dto.getTourId())
                    .filter(t -> t.getAudit().getIsActive())
                    .orElse(null);
            log.info("Tour found: {}", tour.getName());
            List<VehiclesTypePrice> vp = tour.getVehiclePrices();
            for (TourPriceUpdateDto.VehiclesTypePriceUpdateDto vpDto : dto
                    .getVehiclePrices()) {
                VehiclesTypePrice vpt = vp.stream().filter(v -> v.getId().equals(vpDto.getVehiclesTypePriceId()))
                        .findFirst().orElse(null);
                if (vpt != null) {
                    log.info("Vehicle type price found: {}", vpt.getId());
                    vpt.setPrice(vpDto.getPrice());
                    vehiclesTypePriceRepository.save(vpt);
                    log.info("Đã save xong !!");
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteTour(UUID id) {
        Tour tour = tourRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tour not found with id: " + id));

        tour.getAudit().setIsActive(false);
        tourRepository.save(tour);
    }
}
