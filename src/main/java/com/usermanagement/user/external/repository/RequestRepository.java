package com.usermanagement.user.external.repository;

import com.usermanagement.user.domain.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    // Count requests where the associated Status.name equals the supplied name (case-insensitive)
    long countByStatus_NameIgnoreCase(String name);

    // Alternative by status id (useful if you prefer numeric ids)
    long countByStatus_Id(Integer id);
}
