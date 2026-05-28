package com.zerock.driveu.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InfoController {

    @Value("${google.maps.api-key}")
    private String googleMapsApiKey;

    @GetMapping("/drive-u/userInfo/location")
    public String location(Model model) {
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        return "drive-u/userInfo/location";}
}
