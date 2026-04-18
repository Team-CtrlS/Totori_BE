package ctrlS.totori.connect.service;

import ctrlS.totori.global.exception.CustomException;
import ctrlS.totori.global.exception.ErrorCode;
import ctrlS.totori.global.util.RedisUtil;
import ctrlS.totori.connect.dto.request.ConnectRequest;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.connect.entity.ParentChild;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.connect.repository.ParentChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ConnectService {

    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;
    private final ParentChildRepository parentChildRepository;

    private static final long CODE_EXPIRATION_SECONDS = 600; // 10분

    @Transactional(readOnly = true)
    public String createConnectCode(Long childId) {
        Member child = memberRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (child.getRole() != Role.CHILD) {
            throw new CustomException(ErrorCode.ONLY_CHILD_CAN_CREATE_CONNECT_CODE);
        }

        String code = generateConnectCode();

        redisUtil.setDataExpire(code, String.valueOf(childId), CODE_EXPIRATION_SECONDS);

        return code;
    }

    private String generateConnectCode() {
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(90000) + 10000;
            code = String.valueOf(number);
        } while (redisUtil.getData(code) != null);
        return code;
    }

    @Transactional
    public void connectToChild(Long parentId, ConnectRequest request) {
        Member parent = memberRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (parent.getRole() != Role.PARENT) {
            throw new CustomException(ErrorCode.ONLY_PARENT_CAN_CONNECT_CHILD);
        }

        String childIdStr = redisUtil.getAndDeleteData(request.getCode());

        if (childIdStr == null) {
            throw new CustomException(ErrorCode.INVALID_OR_EXPIRED_CONNECT_CODE);
        }

        Long childId = Long.parseLong(childIdStr);

        Member child = memberRepository.findById(childId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (parentChildRepository.existsByParentAndChild(parent, child)) {
            throw new CustomException(ErrorCode.ALREADY_CONNECTED_CHILD);
        }

        ParentChild connection = ParentChild.builder()
                .parent(parent)
                .child(child)
                .build();

        parentChildRepository.save(connection);
    }
}
