package com.techvg.eoffice.service.mapper;

import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.HearingDetails;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.service.dto.HearingDetailsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link HearingDetails} and its DTO {@link HearingDetailsDTO}.
 */
@Mapper(componentModel = "spring")
public interface HearingDetailsMapper extends EntityMapper<HearingDetailsDTO, HearingDetails> {
    @Mapping(target = "dakMaster", source = "dakMaster", qualifiedByName = "dakMasterInwardNumber")
    HearingDetailsDTO toDto(HearingDetails s);

    @Named("dakMasterInwardNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "inwardNumber", source = "inwardNumber")
    DakMasterDTO toDtoDakMasterInwardNumber(DakMaster dakMaster);
}
