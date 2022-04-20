package com.techvg.eoffice.repository;

import com.techvg.eoffice.domain.DakMaster;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the DakMaster entity.
 */
@Repository
public interface DakMasterRepository
    extends DakMasterRepositoryWithBagRelationships, JpaRepository<DakMaster, Long>, JpaSpecificationExecutor<DakMaster> {
    default Optional<DakMaster> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<DakMaster> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<DakMaster> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
