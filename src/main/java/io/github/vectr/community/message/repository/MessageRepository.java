package io.github.vectr.community.message.repository;

import io.github.vectr.community.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
