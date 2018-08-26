package com.faderw.venus;

import com.faderw.venus.config.Config;
import com.faderw.venus.response.Response;
import com.faderw.venus.response.Result;
import com.faderw.venus.spider.Spider;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
    

    @org.junit.Test
    public void test() {
        
    }

    public static void main(String[] args) {
        Venus.me(new Spider("测试爬虫"){
        
            @Override
            protected Result<String> parse(Response response) {
                return new Result(response.body().toString());
            }
        
            @Override
            public void onStart(Config config) {
                
            }
        }, Config.me()).onStart(config -> System.out.print("Hello Venus")).start();;
    }

}
