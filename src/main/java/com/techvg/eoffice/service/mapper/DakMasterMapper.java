package com.techvg.eoffice.service.mapper;

import com.techvg.eoffice.domain.DakMaster;
import com.techvg.eoffice.domain.Organization;
import com.techvg.eoffice.domain.SecurityUser;
import com.techvg.eoffice.service.dto.DakMasterDTO;
import com.techvg.eoffice.service.dto.OrganizationDTO;
import com.techvg.eoffice.service.dto.SecurityUserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link DakMaster} and its DTO {@link DakMasterDTO}.
 */
@Mapper(componentModel = "spring")
public interface DakMasterMapper extends EntityMapper<DakMasterDTO, DakMaster> {
    @Mapping(target = "organization", source = "organization", qualifiedByName = "organizationId")
    @Mapping(target = "securityUsers", source = "securityUsers", qualifiedByName = "securityUserUsernameSet")
    DakMasterDTO toDto(DakMaster s);

    @Mapping(target = "removeSecurityUser", ignore = true)
    DakMaster toEntity(DakMasterDTO dakMasterDTO);

    @Named("organizationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    OrganizationDTO toDtoOrganizationId(Organization organization);

    @Named("securityUserUsername")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    SecurityUserDTO toDtoSecurityUserUsername(SecurityUser securityUser);

    @Named("securityUserUsernameSet")
    default Set<SecurityUserDTO> toDtoSecurityUserUsernameSet(Set<SecurityUser> securityUser) {
        return securityUser.stream().map(this::toDtoSecurityUserUsername).collect(Collectors.toSet());
    }
}
