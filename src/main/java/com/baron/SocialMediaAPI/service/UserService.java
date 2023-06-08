package com.baron.SocialMediaAPI.service;
import com.baron.SocialMediaAPI.model.FriendRequest;
import com.baron.SocialMediaAPI.model.User;
import com.baron.SocialMediaAPI.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User NOT Found"));
    }


    public User getUserById(Long friendId) {
        return userRepository.findById(friendId).orElseThrow();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User)loadUserByUsername(authentication.getName());
    }

    public void save(User user) {
        userRepository.save(user);
    }



}
