package com.BillX.Service.impl;

import com.BillX.Model.User;
import com.BillX.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserImplementation implements UserDetailsService {
    @Autowired
    private UserRepository urepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = urepo.findByEmail(username);
        if(user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
                user.getRole().toString()
        );
        java.util.Collection<GrantedAuthority> authorities = Collections.singletonList(grantedAuthority);
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),user.getPassword(),authorities
        );
    }
}
