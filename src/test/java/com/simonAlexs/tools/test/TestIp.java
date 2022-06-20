package com.simonAlexs.tools.test;

import com.simonAlexs.tools.other.IpUtil;

import java.net.SocketException;

/**
 * @ClassName: TestIp
 * @Description: TODO-wcy
 * @Author: wcy
 * @Date: 2022/4/28 10:01
 * @Version: 1.0
 */
public class TestIp {
    public static void main(String[] args) throws Exception {
        System.out.println("IpUtil.getIpAddress() = " + IpUtil.getIpAddress());
        System.out.println("IpUtil.getSubnet() = " + IpUtil.getSubnet());
    }
}
