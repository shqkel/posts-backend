package com.sh.app.auth.service;

import com.sh.app.auth.entity.RoleAuth;
import com.sh.app.auth.entity.Authority;
import com.sh.app.auth.entity.Member;
import com.sh.app.auth.entity.MemberDetails;
import com.sh.app.auth.repository.AuthorityRepository;
import com.sh.app.auth.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AuthService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username);
        if(member == null)
            throw new UsernameNotFoundException(username);
        return new MemberDetails(member);
    }

    public MemberDetails createMember(Member member) {
        memberRepository.save(member);
        Authority authority = Authority.builder()
                                .name(RoleAuth.ROLE_USER)
                                .build();
        member.addAuthority(authority); // set member_id here!
        authorityRepository.save(authority);
        return (MemberDetails) loadUserByUsername(member.getUsername());
    }
}
