package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.FormChan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormChanRepository extends JpaRepository<FormChan, Integer> {

    List<FormChan> findAllByXoaMemFalseOrderByIdDesc();

    List<FormChan> findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc();

    Optional<FormChan> findByIdAndXoaMemFalse(Integer id);
}
