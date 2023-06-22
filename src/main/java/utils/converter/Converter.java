package utils.converter;


import dto.BaseDto;
import model.BaseEntity;

public interface Converter<Model extends BaseEntity<Long>, Dto extends BaseDto> {
    Model convertDtoToModel(Dto dto);

    Dto convertModelToDto(Model model);

}
