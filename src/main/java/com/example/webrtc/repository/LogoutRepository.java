package com.example.webrtc.repository;

import com.example.webrtc.entity.LogoutToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutRepository extends JpaRepository<LogoutToken, String> {

}
