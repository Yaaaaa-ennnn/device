// package com.individual.device.controller;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import com.individual.device.entity.User;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.*;

// @RequestMapping("/user")
// @RestController
// public class UserController {
// @PostMapping
// public String save(@RequestBody User user) {
// /**
// * \新增用户
// */
// return "用户新增成功";
// }

// @GetMapping
// public String get_all() {
// /**
// * \查询用户
// */
// return "用户查询成功";
// }

// @GetMapping("/{id}")
// public String get_individual(@PathVariable Long id) {
// /**
// * \查询单个用户
// */
// return "单个用户查询成功";
// }

// @PutMapping
// public String update(@RequestBody User user) {
// /**
// * \修改用户
// */
// return "用户修改成功";
// }

// @DeleteMapping
// public String delete(@RequestBody User user) {
// /**
// * 删除用户
// */
// return "用户删除成功";
// }
// }
