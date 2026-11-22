package io.github.vectr.community.log.repository;

import io.github.vectr.community.log.entity.ManagementLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminActionLogRepository extends JpaRepository<ManagementLog, Integer> {

}
