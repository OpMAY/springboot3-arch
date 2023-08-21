package com.architecture.springboot.controller;

import com.architecture.springboot.service.SampleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@Controller
@RequiredArgsConstructor
public class SampleController {
    private final SampleService sampleService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView home() {
        return new ModelAndView("home");
    }

    @RequestMapping(value = "/test/error", method = RequestMethod.GET)
    public ModelAndView errorTest() throws Exception {
        throw new Exception();
    }

    @RequestMapping(value = "/test/file", method = RequestMethod.GET)
    public ModelAndView fileTest() {
        return new ModelAndView("test/file");
    }

    @RequestMapping(value = "/test/db", method = RequestMethod.GET)
    public ModelAndView dbTest() {
        sampleService.dbTest();
        return new ModelAndView("home");
    }
}
