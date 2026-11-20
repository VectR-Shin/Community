package io.github.vectr.community.admin.repository;

import io.github.vectr.community.admin.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Integer> {

}
