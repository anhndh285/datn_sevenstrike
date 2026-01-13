package com.example.datn_sevenstrike.repository;

import com.example.datn_sevenstrike.entity.FormChan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormChanRepository extends JpaRepository<FormChan, Integer> {

    java.util.Optional<FormChan> findByIdAndXoaMemFalse(Integer id);
    java.util.List<FormChan> findAllByXoaMemFalseOrderByIdDesc();

}
