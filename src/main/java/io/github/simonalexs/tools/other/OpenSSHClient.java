package io.github.simonalexs.tools.other;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Title:  OpenSSH 工具类
 * @ClassName: com.ruoyi.release.utils.OpenSSHClient.java
 * @Description:
 *
 * @Copyright 2020-2021 - Powered By 研发中心
 * @author: 王延飞
 * @date:  2020/9/4 0004 14:20
 * @version V1.0
 */
public class OpenSSHClient {
 
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(OpenSSHClient.class);
 
    public OpenSSHClient(String IP, int port, String username, String passward) {
        this.ip = IP;
        this.port = port;
        this.username = username;
        this.password = passward;
    }
    /**
     * @Title:
     * @MethodName:  execCommand
     * @param command
     * @Return void
     * @Exception
     * @Description:
     *
     * @author: 王延飞
     * @date:  2020/9/4 0004 14:56
     */
    public void execCommand(String command) {
 
        Connection conn = new Connection(ip,port);
        Session sess = null;
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false) {
                log.error("authentication failed");
            }
            log.info("【主机】：{}连接成功,---执行命令：{}", ip, command);
            sess = conn.openSession();
            sess.execCommand(command);
 
            //将Terminal屏幕上的文字全部打印出来
            InputStream is = new StreamGobbler(sess.getStdout());
            BufferedReader brs = new BufferedReader(new InputStreamReader(is));
            while (true) {
                String line = brs.readLine();
                if (line == null) {
                    break;
                }
                //log.info("【主机】：{}连接成功,---执行命令：{},===返回结果：{}", ip, command, line);
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(SCPClient.class.getName()).log(Level.SEVERE, null,ex);
        }finally {
            //连接的Session和Connection对象都需要关闭
            if (sess != null) {
                sess.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
 
    public void getFile(String remoteFile, String localTargetDirectory) {
        Connection conn = new Connection(ip,port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false) {
                log.error("authentication failed");
            }
            log.info(ip+"【连接成功】");
            SCPClient client = new SCPClient(conn);
            client.get(remoteFile, localTargetDirectory);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(SCPClient.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
 
 
    public void putFile(String localFile, String remoteTargetDirectory) {
        Connection conn = new Connection(ip,port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false) {
                log.error("authentication failed");
            }
            log.info(ip+"【连接成功】");
            SCPClient client = new SCPClient(conn);
            client.put(localFile, remoteTargetDirectory);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(SCPClient.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
 
    /**
     * @Title: 文件远程上传
     * @MethodName:  putFile
     * @param localFile
     * @param remoteFileName
     * @param remoteTargetDirectory
     * @param mode
     * @Return void
     * @Exception
     * @Description:
     *
     * @author: 王延飞
     * @date:  2020/9/7 0007 16:23
     */
    public void  putFile(String localFile, String remoteFileName, String remoteTargetDirectory, String mode) {
 
        Connection conn = new Connection(ip,port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false) {
                log.error("【文件远程上传】"+ip+"authentication failed");
 
            }
            log.info("【文件远程上传】"+ip+"【连接成功】");
            SCPClient client = new SCPClient(conn);
            if((mode == null) || (mode.length() == 0)){
                mode = "0600";
            }
            client.put(localFile, remoteFileName, remoteTargetDirectory, mode);
 
            //重命名
            ch.ethz.ssh2.Session sess = conn.openSession();
            String tmpPathName = remoteTargetDirectory +File.separator+ remoteFileName;
            String newPathName = tmpPathName.substring(0, tmpPathName.lastIndexOf("."));
            sess.execCommand("mv " + remoteFileName + " " + newPathName);//重命名回来
            log.info("【文件远程上传】"+ip+"【上传成功】");
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(SCPClient.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
 
    public void putFile(String localFile, String remoteFileName,String remoteTargetDirectory) {
        Connection conn = new Connection(ip,port);
        try {
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(username,
                    password);
            if (isAuthenticated == false) {
                log.error("authentication failed");
            }
            log.info(ip+"【连接成功】");
            SCPClient client = new SCPClient(conn);
            client.put(getBytes(localFile), remoteFileName, remoteTargetDirectory);
            conn.close();
        } catch (IOException ex) {
            Logger.getLogger(SCPClient.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
 
 
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream(1024*1024);
            byte[] b = new byte[1024*1024];
            int i;
            while ((i = fis.read(b)) != -1) {
                byteArray.write(b, 0, i);
            }
            fis.close();
            byteArray.close();
            buffer = byteArray.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
 
 
 
    private String ip;
    private int port;
    private String username;
    private String password;
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public int getPort() {
        return port;
    }
 
    public void setPort(int port) {
        this.port = port;
    }
 
    public static String ScannerPortisAlive(int port) {
        String result = "OPEN";
        Socket socket = null;
        try {
            socket = new Socket();
            InetAddress ip = InetAddress.getLocalHost();
            SocketAddress address = new InetSocketAddress(ip, port);
            socket.connect(address, 100);
            socket.close();
//            Socket testPortSocket = new Socket(HostIP, port);
//            testPortSocket.close();
        } catch (IOException e) {
            result = "CLOSE";
        }
        return result;
    }
 
    public static void main(String[] args) {
 
        OpenSSHClient scpclient = new OpenSSHClient("10.0.50.236", 22, "root", "123456");
        scpclient.putFile("C:\\Users\\Administrator\\Desktop\\Middle.bat", "Middle.bat", "D:\\WebVr\\zip", null);
 
 
    }
 
}