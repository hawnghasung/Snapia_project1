package com.snapia.domain.service;

import com.snapia.domain.auth.PrincipalDetails;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class PrincipalDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        Users users = userRepository.findByLoginIdentifier(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없음"));
        return new PrincipalDetails(users);
    }
}
