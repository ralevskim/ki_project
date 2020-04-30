package com.finki.ukim.car_postings_aggregator.services;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.models.User;
import com.finki.ukim.car_postings_aggregator.models.exceptions.AlreadyExistingUsername;
import com.finki.ukim.car_postings_aggregator.models.exceptions.InvalidOldPasswordException;
import com.finki.ukim.car_postings_aggregator.models.exceptions.UserNotFoundException;
import com.finki.ukim.car_postings_aggregator.models.transferables.ChangePasswordRequest;
import com.finki.ukim.car_postings_aggregator.models.transferables.RequestRegistrationUser;
import com.finki.ukim.car_postings_aggregator.repositories.UsersJpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UsersJpaRepository usersJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final SavedPostingService savedPostingService;

    public UserService(UsersJpaRepository usersJpaRepository,
                       PasswordEncoder passwordEncoder,
                       SavedPostingService savedPostingService) {
        this.usersJpaRepository = usersJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.savedPostingService = savedPostingService;
    }

    public void registerNewUser(RequestRegistrationUser requestRegistrationUser, HttpServletResponse response) throws AlreadyExistingUsername {
        Optional<User> userOptionalUsername = this.usersJpaRepository.findByUsername(requestRegistrationUser.getUsername());
        if (userOptionalUsername.isPresent())
            throw new AlreadyExistingUsername("Корисничкото име е зафатено. Обидете се со друго.");

        Optional<User> userOptionalEmail = this.usersJpaRepository.findByEmail(requestRegistrationUser.getEmail());
        if (userOptionalEmail.isPresent())
            throw new AlreadyExistingUsername("Емаил адресата е зафатена. Обидете се со друга.");

        User user = new User(requestRegistrationUser);
        user.setPassword(this.passwordEncoder.encode(requestRegistrationUser.getPassword()));
        this.usersJpaRepository.save(user);

        response.setHeader("location", "/api/users/" + user.getId());
    }

    public Set<SavedPosting> getSavedPostingsForUser(String usernameOrEmail) throws UserNotFoundException {
        User user = this.findUserByUsernameOrEmail(usernameOrEmail);

        return user.getSavedPostings();
    }

    public void savePostingForUser(String postingUrl, org.springframework.security.core.userdetails.User user) throws UserNotFoundException, IOException {
        User user1 = this.findUserByUsernameOrEmail(user.getUsername());

        postingUrl = URLDecoder.decode(postingUrl);

        SavedPosting savedPosting = this.savedPostingService.createSavedPosting(postingUrl, user);

        user1.getSavedPostings().add(savedPosting);
        this.usersJpaRepository.save(user1);
    }

    public void removeSavedPostingForUser(String postingUrl, org.springframework.security.core.userdetails.User user)
            throws UserNotFoundException, IOException {

        User user1 = this.findUserByUsernameOrEmail(user.getUsername());

        postingUrl = URLDecoder.decode(postingUrl);

        SavedPosting savedPosting = this.savedPostingService.createSavedPosting(postingUrl, user);

        user1.getSavedPostings().remove(savedPosting);
        this.usersJpaRepository.save(user1);
    }

    public void deleteUserById(String usernameOrEmail) {
        User user = this.findUserByUsernameOrEmail(usernameOrEmail);

        this.usersJpaRepository.deleteById(user.getId());
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, String usernameOrEmail) throws InvalidOldPasswordException {
        User user = this.findUserByUsernameOrEmail(usernameOrEmail);

        if (this.passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            String newPassword = this.passwordEncoder.encode(changePasswordRequest.getNewPassword());
            user.setPassword(newPassword);
            this.usersJpaRepository.save(user);
        } else
            throw new InvalidOldPasswordException("Погрешна стара лозинка. Обидете се повторно.");
    }

    public User findUserByUsernameOrEmail(String emailOrUsername) {
        return this.usersJpaRepository.findByUsernameOrEmail(emailOrUsername, emailOrUsername)
                .orElseThrow(() -> new UsernameNotFoundException("Корисник со даденото корисничко име/емаил не постои."));
    }

}
