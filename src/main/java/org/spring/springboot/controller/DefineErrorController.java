package org.spring.springboot.controller;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
/**
 * 
 * @author 增加自定义错误
 *
 */
@Controller
@RequestMapping(value = "error")
public class DefineErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "error/error";
    }

    @RequestMapping
    public String error() {
        return getErrorPath();
    }

}