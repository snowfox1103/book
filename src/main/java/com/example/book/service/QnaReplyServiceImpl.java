package com.example.book.service;

import com.example.book.domain.qna.QnaReply;
import com.example.book.domain.user.MemberRole;
import com.example.book.domain.user.Users;
import com.example.book.dto.PageRequestDTO;
import com.example.book.dto.PageResponseDTO;
import com.example.book.dto.QnaReplyDTO;
import com.example.book.repository.QnaReplyRepository;
import com.example.book.repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QnaReplyServiceImpl implements QnaReplyService {

    private final QnaReplyRepository replyRepo;
    private final UsersRepository usersRepo;

    @Override
    public Long register(QnaReplyDTO dto, Long qbId, Long userNo) {
        Users writer = usersRepo.findById(userNo)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userNo));

        QnaReply entity = QnaReply.builder()
                .qbId(qbId)
                .userNo(writer.getUserNo())
                .qrContent(dto.getReplyText())
                .build();

        replyRepo.save(entity);
        return entity.getQRId();
    }

    @Override
    @Transactional(readOnly = true)
    public QnaReplyDTO read(Long replyId) {
        QnaReply r = replyRepo.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found: " + replyId));
        return toDTO(r);
    }

    @Override
    public void modify(QnaReplyDTO dto, Long editorUserNo) {
        QnaReply r = replyRepo.findById(dto.getRno())
                .orElseThrow(() -> new EntityNotFoundException("Reply not found: " + dto.getRno()));

        ensureOwnerOrAdmin(editorUserNo, r.getUserNo());
        r.changeContent(dto.getReplyText());
    }

    @Override
    public void remove(Long replyId, Long actorUserNo) {
        QnaReply r = replyRepo.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("Reply not found: " + replyId));

        ensureOwnerOrAdmin(actorUserNo, r.getUserNo());
        replyRepo.delete(r);
    }

    @Override
    public void removeByAdmin(Long replyId) {
        replyRepo.deleteById(replyId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<QnaReplyDTO> list(Long qbId, PageRequestDTO page) {
        Pageable pageable = page.getPageable("qRId"); // 정렬 컬럼은 엔티티 필드명
        Page<QnaReply> result = replyRepo.findByQbId(qbId, pageable);

        List<QnaReplyDTO> dtoList = result.getContent().stream()
                .map(this::toDTO)
                .toList();

        return PageResponseDTO.<QnaReplyDTO>withAll()
                .pageRequestDTO(page)
                .dtoList(dtoList)
                .total((int) result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QnaReplyDTO> listSimple(Long qbId, int limit) {
        List<QnaReply> rows = replyRepo.findByQbIdOrderByQRIdAsc(qbId);
        if (rows.size() > limit) rows = rows.subList(0, limit);
        return rows.stream().map(this::toDTO).toList();
    }

    private void ensureOwnerOrAdmin(Long actorUserNo, Long ownerUserNo) {
        if (actorUserNo == null) throw new AccessDeniedException("No actor");
        if (actorUserNo.equals(ownerUserNo)) return;

        Users actor = usersRepo.findById(actorUserNo)
                .orElseThrow(() -> new EntityNotFoundException("Actor not found: " + actorUserNo));
        if (actor.getRole() != MemberRole.ADMIN) {
            throw new AccessDeniedException("Not owner or admin");
        }
    }

    private QnaReplyDTO toDTO(QnaReply r) {
        String authorName = usersRepo.findById(r.getUserNo())
                .map(Users::getUserId)
                .orElse("Unknown");

        return QnaReplyDTO.builder()
                .rno(r.getQRId())
                .bno(r.getQbId())
                .replyText(r.getQrContent())
                .replyer(authorName)
                .regDate(r.getRegDate())
                .modDate(r.getModDate())
                .build();
    }
}
