package io.github.vectr.community.member.repository;

import io.github.vectr.community.member.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    // 사용자 식별자를 기반으로 프로필 단건 조회
    Optional<Profile> findByMember_Id(Long memberId);

    // nickname 의 유무 조회
    boolean existsByNickname(String nickname);

    // 특정 사용자의 Profile 정보 삭제
    void deleteByMember_Id(Long memberId);

    /*
        중복 닉네임 존재 여부 확인
        자신의 현재 닉네임은 포함 안 함
     */
    boolean existsByNicknameAndMember_IdNot(String nickname, Long memberId);
}