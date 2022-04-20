package com.techvg.eoffice.service.mapper;

import com.techvg.eoffice.domain.CommentMaster;
import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.service.dto.CommentMasterDTO;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.service.dto.SecurityUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CommentMaster} and its DTO {@link CommentMasterDTO}.
 */
@Mapper(componentModel = "spring")
public interface CommentMasterMapper extends EntityMapper<CommentMasterDTO, CommentMaster> {
    @Mapping(target = "securityUser", source = "securityUser", qualifiedByName = "securityUserUsername")
    @Mapping(target = "dakMaster", source = "dakMaster", qualifiedByName = "dakMasterInwardNumber")
    CommentMasterDTO toDto(CommentMaster s);

    @Named("securityUserUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    SecurityUserDTO toDtoSecurityUserUsername(SecurityUser securityUser);

    @Named("dakMasterInwardNumber")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "inwardNumber", source = "inwardNumber")
    DakMasterDTO toDtoDakMasterInwardNumber(DakMaster dakMaster);
}
