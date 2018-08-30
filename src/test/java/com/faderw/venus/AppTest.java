package com.faderw.venus;


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
//        Venus.me(new Spider("测试爬虫"){
//
//            @Override
//            protected void parse(Page page) {
//
//            }
//
//            @Override
//            public void onStart(Config config) {
//
//            }
//        }, Config.me()).onStart(config -> System.out.print("Hello Venus")).start();
//    }
    }
}
