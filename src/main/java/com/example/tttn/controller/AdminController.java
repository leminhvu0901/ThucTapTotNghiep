package com.example.tttn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping({"/admin"})
    public String homeAdmin(Model model) {

        return "admin/homeAdmin";
    }

    @GetMapping({"/admin/sanpham"})
    public String adminSanpham(Model model) {

        return "admin/sanpham";
    }

    @GetMapping({"/admin/donhang"})
    public String adminDonHang(Model model) {

        return "admin/donhang";
    }

}
