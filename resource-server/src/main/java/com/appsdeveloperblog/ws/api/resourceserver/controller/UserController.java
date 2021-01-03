package com.appsdeveloperblog.ws.api.resourceserver.controller;

import com.appsdeveloperblog.ws.api.resourceserver.response.UserRest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @GetMapping("/status/check")
    public String status() {
        return "Working";
    }

    //    @Secured("ROLE_developer")
    @PreAuthorize("hasRole('developer') or #id == #jwt.subject")
//    @PreAuthorize("hasAuthority('ROLE_developer')")
    @DeleteMapping(path = "/{id}")
    public String deleteUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return "Delete user with id " + id + " and jwt subject " + jwt.getSubject();
    }


    @PostAuthorize("returnObject.userId == #jwt.subject")
    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        return new UserRest("doyoung", "kim", "");
    }
}
