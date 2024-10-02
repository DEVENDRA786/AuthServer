package com.example.ApiGatewayDemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gt")
public class GatewayController {

    @GetMapping("/getApi")
    public String getApi(){
        return "getApi";
    }
}
