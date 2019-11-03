package com.atguigu.crud.controller;

import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *
 * 处理员工CRUD
 *
 * */
@Controller
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;


    /**
     * 单个批量二合一
     * 批量删除：1-2-3
     * 单个删除：1
     *
     *
     */
    @ResponseBody
    @RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
    public Msg deleteEmp(@PathVariable("ids")String ids){
        //批量删除
        if(ids.contains("-")){
            List<Integer> del_ids = new ArrayList<Integer>();
            String[] str_ids = ids.split("-");
            //组装id的集合
            for (String string : str_ids) {
                del_ids.add(Integer.parseInt(string));
            }
            employeeService.deleteBatch(del_ids);
        }else{
            Integer id = Integer.parseInt(ids);
            employeeService.deleteEmp(id);
        }
        return Msg.success();
    }


//    单个删除1
    //员工删除
//    @ResponseBody
//    @RequestMapping(value = "/emp/{id}",method = RequestMethod.DELETE)
//    public Msg deleteEmpById(@PathVariable("id") Integer id){
//        employeeService.deleteEmp(id);
//        return Msg.success();
//    }


//     员工更新

    @ResponseBody
    @RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
    public Msg saveEmp(Employee employee){
        employeeService.updateEmp(employee);
        return Msg.success();
    }



    @RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
    @ResponseBody
    //地址中的id变量
//    PathVariable 得到{id}的值
    public Msg getEmp(@PathVariable("id") Integer id){
      Employee employee=  employeeService.getEmp(id);
      return Msg.success().add("emp",employee);
    }

        //检查用户名是否可用
        //访回数据
        @ResponseBody
        @RequestMapping("/checkuser")
        public  Msg checkuser(@RequestParam("empName") String empName){

            String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";

            if (!empName.matches(regx)){
                return  Msg.fail().add("va_msg","用户名必须是6-16位数字和字母的组合或者2-5位中文");
            }
            boolean b=employeeService.checkUser(empName);
            if (b){
                return Msg.success();
            }else {
                return Msg.fail().add("va_msg","用户名不可用");
            }
        }

    /*
    * 定义员工保存
    * */
//    @ResponseBody 表示该方法的返回结果直接写入 HTTP response body 中
// ，一般在异步获取数据时使用【也就是AJAX】
//@Valid用于验证注解是否符合要求 与@https://blog.csdn.net/weixin_38118016/article/details/80977207
    @RequestMapping(value = "/emp" , method = RequestMethod.POST)
    @ResponseBody
        public  Msg saveEmp(@Valid Employee employee, BindingResult result){
        if (result.hasErrors()){
            Map<String ,Object> map=new HashMap<String, Object>();
            List<FieldError> errors=result.getFieldErrors();
            for (FieldError fieldError:errors){
                System.out.println("错误字段："+fieldError.getField());
                System.out.println("错误信息："+fieldError.getDefaultMessage());
                map.put(fieldError.getField(),fieldError.getDefaultMessage());
            }
            return Msg.fail().add("errorFields",map);
        }
        employeeService.saveEmp(employee);
            return Msg.success();
    }


    /*
    *
    * 访回JSON数据类型
    * */
    @ResponseBody
    @RequestMapping("/emps")
    public Msg getEmpsWithJson(@RequestParam(value = "pn", defaultValue = "1") Integer pn,
                               Model model){
        // 这不是一个分页查询；
        // 引入PageHelper分页插件
        // 在查询之前只需要调用，传入页码，以及每页的大小
        PageHelper.startPage(pn, 5);
        // startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        // 使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了。
        // 封装了详细的分页信息,包括有我们查询出来的数据，传入连续显示的页数
        PageInfo page = new PageInfo(emps, 5);
            return Msg.success().add("pageInfo",page);
    }
    /*
     * 查询员工数据
     *
     * */
//     @RequestMapping("/emps")
    public String getEmps(
            @RequestParam(value = "pn", defaultValue = "1") Integer pn,
            Model model) {
        // 这不是一个分页查询；
        // 引入PageHelper分页插件
        // 在查询之前只需要调用，传入页码，以及每页的大小
        PageHelper.startPage(pn, 5);
        // startPage后面紧跟的这个查询就是一个分页查询
        List<Employee> emps = employeeService.getAll();
        // 使用pageInfo包装查询后的结果，只需要将pageInfo交给页面就行了。
        // 封装了详细的分页信息,包括有我们查询出来的数据，传入连续显示的页数
        PageInfo page = new PageInfo(emps, 5);
        model.addAttribute("pageInfo", page);
//            视图解析自动拼串、
//         <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
//        <!--前置-->
//        <property name="prefix" value="/WEB-INF/views"></property>
//        <!--后置-->
//        <property name="suffix" value=".jsp"></property>
//
//    </bean>
        return "list";
    }
}
