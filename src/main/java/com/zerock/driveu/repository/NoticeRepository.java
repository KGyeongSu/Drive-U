package com.zerock.driveu.repository;

import com.zerock.driveu.domain.NoticeBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository <NoticeBoard, Long> {



}
