package com.cloud.job.admin.controller;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @WebAppConfiguration：测试环境使用，用来表示测试环境使用的ApplicationContext将是WebApplicationContext类型的；
 * value指定web应用的根；
 * 通过@Autowired WebApplicationContext wac：注入web环境的ApplicationContext容器；
 * 然后通过MockMvcBuilders.webAppContextSetup(wac).build()创建一个MockMvc进行测试；
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractSpringMvcTest {

  @Autowired
  private WebApplicationContext applicationContext;
  protected MockMvc mockMvc;

  @Before
  public void setup() {
    //构造MockMvc
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
  }

}
