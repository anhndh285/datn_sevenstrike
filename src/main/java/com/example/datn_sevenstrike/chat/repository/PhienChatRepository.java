package com.example.datn_sevenstrike.chat.repository;

import com.example.datn_sevenstrike.chat.entity.PhienChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhienChatRepository extends JpaRepository<PhienChat, Integer> {

    List<PhienChat> findByTrangThaiOrderByThoiGianBatDauDesc(String trangThai);

    List<PhienChat> findByTrangThaiNotOrderByThoiGianBatDauDesc(String trangThai);

    List<PhienChat> findByKhachHang_IdOrderByThoiGianBatDauDesc(Integer khachHangId);

    // Filter theo loai
    List<PhienChat> findByLoaiAndTrangThaiNotOrderByThoiGianBatDauDesc(String loai, String trangThai);

    List<PhienChat> findByLoaiAndTrangThaiOrderByThoiGianBatDauDesc(String loai, String trangThai);
}