package com.danylenko.workhelper.mapper;

import com.danylenko.workhelper.dto.ObjectCountDto;
import com.danylenko.workhelper.model.ObjectCount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface ObjectCountMapper {
    ObjectCountMapper INSTANCE = Mappers.getMapper(ObjectCountMapper.class);

    @Mapping(source = "hday", target = "hday")
    @Mapping(source = "cnt", target = "cnt")
    ObjectCount toModel(ObjectCountDto objectCountDto);

    @Mapping(source = "hday", target = "hday")
    @Mapping(source = "cnt", target = "cnt")
    ObjectCount toModel(String hday, int cnt);

    @Mapping(source = "hday", target = "hday")
    @Mapping(source = "cnt", target = "cnt")
    ObjectCountDto toDto(ObjectCount objectCount);


}
