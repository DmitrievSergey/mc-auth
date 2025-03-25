package com.skillbox.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    @GetMapping("/account/me")
    public ResponseEntity<?> accountMe() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/geo/country")
    public ResponseEntity<?> getCountry() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/friends/count")
    public ResponseEntity<?> getFriendsCount() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dialogs/unread")
    public ResponseEntity<?> getUnreadMessages() {
        return ResponseEntity.ok().build();
    }
}
