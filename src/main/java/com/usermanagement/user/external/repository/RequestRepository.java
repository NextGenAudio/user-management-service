package com.usermanagement.user.external.repository;

import com.usermanagement.user.domain.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {

}
