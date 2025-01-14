package com.gmail.doloiu22.dfss.service;

import com.gmail.doloiu22.dfss.model.UserEntity;
import com.gmail.doloiu22.dfss.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Configuration
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optUser = userRepository.findByUsername(username);
        if(optUser.isPresent()){
            UserEntity appUser = optUser.get();
            if (appUser.getUsername().equals("ADMIN")) {
                return new User(appUser.getUsername(), appUser.getPassword(), true, true, true
                        , true, new ArrayList<>(List.of(new SimpleGrantedAuthority("ADMIN"),new SimpleGrantedAuthority("AUTH"))));
            } else return new User(appUser.getUsername(), appUser.getPassword(), true, true, true
                    , true, new ArrayList<>(List.of(new SimpleGrantedAuthority("AUTH")))
            );
        }
        throw new UsernameNotFoundException(username);
    }

    public boolean register(UserEntity user) {

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

        userRepository.save(user);
        return true;
    }

    public void save(UserEntity user) {
        /* Encrypt password */
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }
    public void login(UserEntity user, Authentication authentication) {
        UserDetails userDetails = this.loadUserByUsername(user.getUsername());
        if(Objects.isNull(userDetails))
            return;

        authentication = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        // UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, user.getPassword(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findById(Long userId) {
        return userRepository.findById(userId);
    }
}
