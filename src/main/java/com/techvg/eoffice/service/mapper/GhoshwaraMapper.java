package com.techvg.eoffice.service.mapper;

import com.techvg.eoffice.domain.Ghoshwara;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.service.dto.GhoshwaraDTO;
import com.techvg.eoffice.service.dto.SecurityUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ghoshwara} and its DTO {@link GhoshwaraDTO}.
 */
@Mapper(componentModel = "spring")
public interface GhoshwaraMapper extends EntityMapper<GhoshwaraDTO, Ghoshwara> {
    @Mapping(target = "securityUser", source = "securityUser", qualifiedByName = "securityUserUsername")
    GhoshwaraDTO toDto(Ghoshwara s);

    @Named("securityUserUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    SecurityUserDTO toDtoSecurityUserUsername(SecurityUser securityUser);
}
