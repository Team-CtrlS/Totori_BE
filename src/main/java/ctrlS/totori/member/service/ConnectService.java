package ctrlS.totori.member.service;

import ctrlS.totori.global.util.RedisUtil;
import ctrlS.totori.member.dto.ConnectRequest;
import ctrlS.totori.member.entity.Member;
import ctrlS.totori.member.entity.ParentChild;
import ctrlS.totori.member.entity.Role;
import ctrlS.totori.member.repository.MemberRepository;
import ctrlS.totori.member.repository.ParentChildRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (child.getRole() != Role.CHILD) {
            throw new IllegalArgumentException("아동 계정만 연결 코드를 생성할 수 있습니다.");
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
    public Void connectToChild(Long parentId, ConnectRequest request) {
        Member parent = memberRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        if (parent.getRole() != Role.PARENT) {
            throw new IllegalArgumentException("부모 계정만 연결 코드를 입력할 수 있습니다.");
        }

        String childIdStr = redisUtil.getAndDeleteData(request.getCode());

        if (childIdStr == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 코드입니다.");
        }

        Long childId = Long.parseLong(childIdStr);

        Member child = memberRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아동 계정입니다."));

        if (parentChildRepository.existsByChild(child)) {
            throw new IllegalArgumentException("이미 다른 보호자와 연결된 아동 계정입니다.");
        }

        ParentChild connection = ParentChild.builder()
                .parent(parent)
                .child(child)
                .build();

        parentChildRepository.save(connection);
    }
}
