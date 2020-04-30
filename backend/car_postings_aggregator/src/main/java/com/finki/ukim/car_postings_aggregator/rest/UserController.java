package com.finki.ukim.car_postings_aggregator.rest;

import com.finki.ukim.car_postings_aggregator.models.SavedPosting;
import com.finki.ukim.car_postings_aggregator.models.exceptions.AlreadyExistingUsername;
import com.finki.ukim.car_postings_aggregator.models.exceptions.InvalidOldPasswordException;
import com.finki.ukim.car_postings_aggregator.models.exceptions.UserNotFoundException;
import com.finki.ukim.car_postings_aggregator.models.transferables.ChangePasswordRequest;
import com.finki.ukim.car_postings_aggregator.models.transferables.RequestRegistrationUser;
import com.finki.ukim.car_postings_aggregator.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerNewUser(@RequestBody RequestRegistrationUser requestRegistrationUser,
                                HttpServletResponse httpServletResponse) throws AlreadyExistingUsername {

        this.userService.registerNewUser(requestRegistrationUser, httpServletResponse);
    }

    @GetMapping("/my-saved-postings")
    public Set<SavedPosting> getUserSavedPostings(@ModelAttribute User user) throws UserNotFoundException {
        return this.userService.getSavedPostingsForUser(user.getUsername());
    }

    @PostMapping("/save-posting")
    public void savePostingForUser(@ModelAttribute User user, @RequestBody String postingUrl)
            throws UserNotFoundException, IOException {

        this.userService.savePostingForUser(postingUrl, user);
    }

    @PostMapping("/remove-posting")
    public void removeSavedPosting(@ModelAttribute User user, @RequestBody String postingUrl)
            throws UserNotFoundException, IOException {
        this.userService.removeSavedPostingForUser(postingUrl, user);
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, @ModelAttribute User user)
            throws InvalidOldPasswordException {

        this.userService.changePassword(changePasswordRequest, user.getUsername());
    }

    @DeleteMapping("/delete-account")
    public void deleteAccount(@ModelAttribute User user) {
        this.userService.deleteUserById(user.getUsername());
    }
}
