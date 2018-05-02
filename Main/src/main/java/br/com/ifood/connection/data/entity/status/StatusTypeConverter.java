package br.com.ifood.connection.data.entity.status;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StatusTypeConverter implements AttributeConverter<StatusType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusType statusType) {
        return statusType.type();
    }

    @Override
    public StatusType convertToEntityAttribute(Integer type) {
        return StatusType.valueOf(type);
    }
}
