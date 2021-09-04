package com.dgut.lab3.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class QuartzService {
    @Autowired
    UpdateDataService updateDataService;

    //    每天十五点二分启动任务更新数据
    @Scheduled(cron = "10 0 15  * * ? " )
    public void timerToNow() throws MalformedURLException, ParseException {
        //下载新数据
        downloadData();

        File srcFile=new File("src/main/resources/COVID-19Data/master.zip");
        //解压文件
        System.out.println("开始解压");
        unZip(srcFile,"src/main/resources/COVID-19Data/master");
        System.out.println("解压完成");

        DateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date star = dft.parse("2020-01-22");//开始时间
        Date endDay=new Date();//结束时间
        Long starTime=star.getTime();
        Long endTime=endDay.getTime();
        Long num=(endTime-starTime)/24/60/60/1000+4;//时间戳相差的毫秒数
        //数据列数
        System.out.println(num);
        int col= num.intValue();
        //解析csv文件
        List<String> fileData=readCSV("src/main/resources/COVID-19Data/master/COVID-19-master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv",
                col);

        String strCol="c"+String.valueOf(col);
        updateDataService.creatNewCol(strCol);
        for(int i=0;i<fileData.size();i++) {
            updateDataService.addColNum(strCol, Integer.valueOf(fileData.get(i)), i+1);
        }
    }
    public void downloadData() throws ParseException, MalformedURLException {
        int bytesum = 0;
        int byteread = 0;
        System.out.println("下载开始...");

        URL url = new URL("https://github.com/CSSEGISandData/COVID-19/archive/refs/heads/master.zip");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("src/main/resources/COVID-19Data/master.zip");

            byte[] buffer = new byte[1024];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                //System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("下载完成");
    }
    /**

     * zip解压

     * @param srcFile        zip源文件

     * @param destDirPath     解压后的目标文件夹

     * @throws RuntimeException 解压失败会抛出运行时异常

     */
    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在

        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }

        // 开始解压
        ZipFile zipFile = null;
        System.out.println("开始解压");
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                //System.out.println("解压" + entry.getName());

                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());

                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }

                    targetFile.createNewFile();

                    // 将压缩文件内容写入到这个文件中

                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);

                    int len;
                    byte[] buf = new byte[1024];

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    fos.close();
                    is.close();
                }
            }

            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);

        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @return List<List<String>>
     * @Description 读取CSV文件的内容（不含表头）
     * @Param filePath 文件存储路径，colNum 列数
     **/
    public static List<String> readCSV(String filePath, int colNum) {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filePath);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            CSVParser parser = CSVFormat.DEFAULT.parse(bufferedReader);
//          表内容集合，外层List为行的集合，内层List为字段集合

            int rowIndex = 0;
            List<String> res=new ArrayList<>();
            for (CSVRecord record : parser.getRecords()) {
//              跳过表头
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
//              每行的内容
                res.add(record.get(colNum-1));
                rowIndex++;
            }
            return res;
        } catch (IOException e) {
            System.out.println("解析CSV内容失败" + e.getMessage()+e);
        }finally {
            //关闭流
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
