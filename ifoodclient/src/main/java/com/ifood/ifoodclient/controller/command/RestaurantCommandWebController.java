package com.ifood.ifoodclient.controller.command;

import com.ifood.ifoodclient.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RestaurantCommandWebController {

    @GetMapping("/restaurant")
    public String restaurantForm(Model model) {
        model.addAttribute("restaurant", Restaurant.builder().build());
        return "restaurantForm";
    }

    @PostMapping("/restaurant")
    public String restaurantCreate(@ModelAttribute Restaurant restaurant) {
        return "result";
    }

    @PatchMapping("/restaurant/{code}")
    public String restaurantPatch(@ModelAttribute Restaurant restaurant) {
        return "result";
    }
}
