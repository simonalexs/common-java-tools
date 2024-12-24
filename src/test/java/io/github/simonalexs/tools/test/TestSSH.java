package io.github.simonalexs.tools.test;

import io.github.simonalexs.tools.FileUtil;
import io.github.simonalexs.tools.other.OpenSSHClient;

public class TestSSH {
    public static void main(String[] args) {
        OpenSSHClient scpclient = new OpenSSHClient("196.168.12.79", 22, "Administrator", "admin888!");
        try {
            // 1.3.2.1 创建远程文件夹
//            String makeDirCommand = "mkdir " + uploadPath;
//            scpclient.execCommand(makeDirCommand);
            // 1.3.2.2 上传文件到远程服务器
            String localFileFolder = "C:\\Users\\SimonAlexs\\Desktop";
            String localFileName = "uuid.txt";
            String localFilePath = localFileFolder + "\\" + localFileName;
//            String remoteFileFolder = "C:\\Program Files\\FSmartWorx\\server\\node_modules\\uuid\\dist";
            String remoteFileFolder = "C:\\Users\\Administrator\\Desktop";
            String remoteFileName = "uuid.txt";
            getFile(scpclient, remoteFileFolder, remoteFileName, localFileFolder, localFilePath);

            FileUtil.write(localFilePath, "aaaaaaaa-2db3-4545-831f-3000000");
            scpclient.putFile(localFilePath,
                    remoteFileName,
                    remoteFileFolder,
                    null);

            getFile(scpclient, remoteFileFolder, remoteFileName, localFileFolder, localFilePath);
        } catch (Exception e) {
        }
    }

    private static void getFile(OpenSSHClient scpclient, String remoteFileFolder, String remoteFileName, String localFileFolder, String localFilePath) throws InterruptedException {
        scpclient.getFile(remoteFileFolder + "\\" + remoteFileName,
                localFileFolder);
        System.out.println("FileUtil.read(localFileName) = " + FileUtil.read(localFilePath));
    }
}
