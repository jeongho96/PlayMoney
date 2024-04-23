package com.reboot.playmoney.repository;


import com.reboot.playmoney.domain.User;
import com.reboot.playmoney.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, Long> {

}

