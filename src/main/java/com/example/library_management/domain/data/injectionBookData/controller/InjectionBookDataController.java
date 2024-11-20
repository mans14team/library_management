package com.example.library_management.domain.data.injectionBookData.controller;

import com.example.library_management.domain.data.injectionBookData.service.InjectionBookDataService;
import com.example.library_management.global.security.UserDetailsImpl;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InjectionBookDataController {
    private final InjectionBookDataService injectionBookDataService;

    @PostMapping("/bookData")
    public String importData(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            injectionBookDataService.injectionBookData(userDetails);
            return "success";
        } catch (IOException e) {
            return "error" + e.getMessage();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

}
