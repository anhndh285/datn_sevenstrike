package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Integer> {

    java.util.Optional<ChatLieu> findByIdAndXoaMemFalse(Integer id);
    java.util.List<ChatLieu> findAllByXoaMemFalseOrderByIdDesc();

}
