package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChatLieuRequest;
import com.example.datn_sevenstrike.dto.response.ChatLieuResponse;
import com.example.datn_sevenstrike.entity.ChatLieu;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChatLieuRepository;
import com.example.datn_sevenstrike.repository.ChiTietSanPhamRepository;
import com.example.datn_sevenstrike.repository.SanPhamRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatLieuService {

    private final ChatLieuRepository repo;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ModelMapper mapper;

    public List<ChatLieuResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<ChatLieuResponse> allActive() {
        return repo.findAllByXoaMemFalseAndTrangThaiTrueOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    public ChatLieuResponse one(Integer id) {
        ChatLieu e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChatLieu id=" + id));
        return toResponse(e);
    }

    @Transactional
    public ChatLieuResponse create(ChatLieuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        ChatLieu e = mapper.map(req, ChatLieu.class);
        e.setId(null);

        applyDefaults(e);
        validate(e);

        return toResponse(repo.save(e));
    }

    @Transactional
    public ChatLieuResponse update(Integer id, ChatLieuRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        ChatLieu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChatLieu id=" + id));

        boolean activeCu = isActive(db);

        if (req.getTenChatLieu() != null) db.setTenChatLieu(req.getTenChatLieu());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem());

        applyDefaults(db);
        validate(db);

        ChatLieu saved = repo.save(db);
        boolean activeMoi = isActive(saved);

        if (activeCu && !activeMoi) {
            sanPhamRepository.ngungKinhDoanhTheoChatLieu(saved.getId());
            chiTietSanPhamRepository.ngungKinhDoanhTheoChatLieu(saved.getId());
        } else if (!activeCu && activeMoi) {
            sanPhamRepository.batKinhDoanhTheoChatLieu(saved.getId());
            chiTietSanPhamRepository.batKinhDoanhTheoChatLieu(saved.getId());
        }

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        ChatLieu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChatLieu id=" + id));

        db.setXoaMem(true);
        db.setTrangThai(false);
        repo.save(db);

        sanPhamRepository.ngungKinhDoanhTheoChatLieu(id);
        chiTietSanPhamRepository.ngungKinhDoanhTheoChatLieu(id);
    }

    private void applyDefaults(ChatLieu e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
        if (e.getTenChatLieu() != null) e.setTenChatLieu(e.getTenChatLieu().trim());
    }

    private void validate(ChatLieu e) {
        if (e.getTenChatLieu() == null || e.getTenChatLieu().isBlank()) {
            throw new BadRequestEx("Thiếu ten_chat_lieu");
        }
    }

    private boolean isActive(ChatLieu e) {
        return !Boolean.TRUE.equals(e.getXoaMem()) && Boolean.TRUE.equals(e.getTrangThai());
    }

    private ChatLieuResponse toResponse(ChatLieu e) {
        return mapper.map(e, ChatLieuResponse.class);
    }
}