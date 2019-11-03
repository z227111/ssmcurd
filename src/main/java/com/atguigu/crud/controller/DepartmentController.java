package com.atguigu.crud.controller;

import com.atguigu.crud.bean.Department;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class DepartmentController {
//    跟部门有关的业务逻辑
        @Autowired
     DepartmentService departmentService;

//    访回所有部门信息

@ResponseBody
@RequestMapping("/depts")
    public Msg getDepts(){
        List<Department> list =departmentService.getDepts();
        return Msg.success().add("depts",list);
    }

}

