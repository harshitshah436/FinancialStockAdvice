package edu.rit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * IndexController will handle request by '/' and returns Index string which
 * will be resolved by viewResolver and redirected to Index view.
 *
 * @author Harshit
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping(method = RequestMethod.GET)
    public String getIndexPage() {
        return "Index";
    }
}
