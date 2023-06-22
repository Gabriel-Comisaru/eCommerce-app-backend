package utils.converter;

import dto.BaseDto;
import model.BaseEntity;

public abstract class BaseConverter<Model extends BaseEntity<Long>, Dto extends BaseDto>
        implements Converter<Model, Dto> {
}

