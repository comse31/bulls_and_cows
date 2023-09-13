package org.w2fc.covs.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.w2fc.covs.service.AdminService

@RestController
@RequestMapping("/admin")
class AdminController(
    val adminService: AdminService
) {

    @GetMapping("/set")
    fun initWords():ResponseEntity<Unit>{
        adminService.initWords()
        return ResponseEntity.ok().build()
    }
}