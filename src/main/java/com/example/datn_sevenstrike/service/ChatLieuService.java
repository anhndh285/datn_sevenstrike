package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.ChatLieuRequest;
import com.example.datn_sevenstrike.dto.response.ChatLieuResponse;
import com.example.datn_sevenstrike.entity.ChatLieu;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.ChatLieuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatLieuService {

    private final ChatLieuRepository repo;
    private final ModelMapper mapper;

    // list quản trị (kể cả không hoạt động, miễn chưa xóa mềm)
    public List<ChatLieuResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc()
                .stream().map(this::toResponse).toList();
    }

    // list combobox (chỉ hoạt động)
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

        if (req.getTenChatLieu() != null) db.setTenChatLieu(req.getTenChatLieu());
        if (req.getTrangThai() != null) db.setTrangThai(req.getTrangThai());
        if (req.getXoaMem() != null) db.setXoaMem(req.getXoaMem()); // thường không cho FE set, nhưng để demo vẫn ok

        applyDefaults(db);
        validate(db);

        return toResponse(repo.save(db));
    }

    @Transactional
    public void delete(Integer id) {
        ChatLieu db = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ChatLieu id=" + id));
        db.setXoaMem(true);
        repo.save(db);
    }

    private void applyDefaults(ChatLieu e) {
        if (e.getXoaMem() == null) e.setXoaMem(false);
        if (e.getTrangThai() == null) e.setTrangThai(true);
    }

    private void validate(ChatLieu e) {
        if (e.getTenChatLieu() == null || e.getTenChatLieu().isBlank())
            throw new BadRequestEx("Thiếu ten_chat_lieu");
    }

    private ChatLieuResponse toResponse(ChatLieu e) {
        return mapper.map(e, ChatLieuResponse.class);
    }
}
