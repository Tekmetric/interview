package com.tekmetric.mapper;

import static com.tekmetric.util.ValidationUtil.validateYearFormat;

import com.tekmetric.model.CarModel;
import com.tekmetric.request.CarCreationRequest;
import com.tekmetric.response.PagedResponse;
import com.tekmetric.response.car.CarResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

public class CarMapper {
  public static List<CarResponse> toResponses(List<CarModel> cars) {
    return cars.stream().map(CarMapper::toResponse).collect(Collectors.toList());
  }

  public static CarResponse toResponse(CarModel car) {
    return CarResponse.builder()
        .id(car.getId())
        .carInfo(car.getCarInfo())
        .ownerInfo(car.getUserInfo())
        .build();
  }

  public static PagedResponse<CarResponse> toPagedResponse(final Page<CarModel> page) {
    return new PagedResponse<>(
        toResponses(page.getContent()),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }

  public static CarModel toModel(CarCreationRequest request) throws BadRequestException {
    return CarModel.builder()
        .make(request.getMake())
        .model(request.getModel())
        .color(request.getColor())
        .manufactureYear(validateYearFormat(request.getManufactureYear()))
        .build();
  }
}
