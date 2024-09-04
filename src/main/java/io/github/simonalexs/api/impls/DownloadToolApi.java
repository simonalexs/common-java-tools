package io.github.simonalexs.api.impls;

import io.github.simonalexs.api.ApiInterface;
import io.github.simonalexs.api.struct.ParamView;

import java.util.Map;

/**
 * DownloadToolApi
 */
public class DownloadToolApi implements ApiInterface {
    public static String downloadBilibiliVideo(ParamView bvid) {

//        java下载b站视频 https://blog.csdn.net/u014799602/article/details/116536614


        // return 返回视频下载地址
        return "";
    }

    public static String videoUrlToAudioFile(String videoUrl, Map<String, String> beginAndEndSeconds) {

        /*2.利用ffmpeg命令，用java将视频转音频，只截取 [beginSeconds, endSeconds] 区间的音频
        String aacFile = HOME_PATH + TMP_PATH + videoName + ".aac";
        String command = FFMPEG_PATH + "ffmpeg -i "+ videoUrl + " -y -f ac3 -vn "+ aacFile;
        logger.info("video to audio command : " + command);
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();*/
        // accFile这个路径下的文件就是那个音频文件

        // return 返回音频下载地址
        return "";
    }
}
