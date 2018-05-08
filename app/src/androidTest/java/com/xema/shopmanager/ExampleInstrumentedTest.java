package com.xema.shopmanager;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.xema.shopmanager.utils.CommonUtil;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.xema.shopmanager", appContext.getPackageName());
    }

    @Test
    public void hypenTest() throws Exception {
        System.out.println(CommonUtil.toHypenFormat("01012341234"));
        System.out.println(CommonUtil.toHypenFormat("0101234123"));
        System.out.println(CommonUtil.toHypenFormat("010123"));
        System.out.println(CommonUtil.toHypenFormat("0101234"));
        System.out.println(CommonUtil.toHypenFormat("010123412342"));
        System.out.println(CommonUtil.toHypenFormat("0101234123433"));
        System.out.println(CommonUtil.toHypenFormat("01012341"));
        System.out.println(CommonUtil.toHypenFormat("010123412"));
        System.out.println(CommonUtil.toHypenFormat("01622221111"));
        System.out.println(CommonUtil.toHypenFormat("0162231111"));
        System.out.println(CommonUtil.toHypenFormat("0312618888"));
        System.out.println(CommonUtil.toHypenFormat("028415255"));
    }
}
