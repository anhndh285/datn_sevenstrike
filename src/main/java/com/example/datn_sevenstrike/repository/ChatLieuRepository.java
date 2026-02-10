package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChatLieu;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    List<ChatLieu> findAllByXoaMemFalseOrderByIdDesc();

    List<ChatLieu> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<ChatLieu> findByIdAndXoaMemFalse(Integer id);
}
