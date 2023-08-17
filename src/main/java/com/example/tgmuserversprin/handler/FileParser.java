package com.example.tgmuserversprin.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

public class FileParser {
    private final DbHandler dbHandler = new DbHandler();
    private final List<String> keyWords = List.of(
            encodeStr("------WebKitFormBoundary"),
            encodeStr("\n------WebKitFormBoundar"),
            encodeStr("\r\n------WebKitFormBounda")
    );
    private final List<String> contentKeyWords = List.of(
            encodeStr("Content-Type"),
            encodeStr("\nContent-Typ"),
            encodeStr("\r\nContent-Ty")
    );
    private final List<String> newStringKeyWords = List.of(
            encodeStr("\n\r\n"),
            "0K",
            encodeStr("\r\n\r")
    );
    List<Integer> fileOffsetBegin = List.of(3, 5, 4, 0);
    List<Integer> fileOffsetEnd = List.of(2, 1, 0);

    public FileParser() throws SQLException {
    }

    public void Handle(InputStream is) throws IOException, SQLException {
        byte[] firstBytes = is.readNBytes(6000);

        File file = new File("image.jpg");
        FileOutputStream fos = new FileOutputStream(file, true);

        int counter = 0;

        while (firstBytes.length > 0) {
            byte[] secondBytes = is.readNBytes(6000);
            String firstString = new String(encodeBytes(firstBytes));
            String secondString = new String(encodeBytes(secondBytes));

            int idxBracket = 0;
            int contentIdx;
            int newStringIdx;
            int strIdx = 0;
            int j = 3;
            int keyWordNum;
            int[] tmpArr;

            int idxMetaInfoBegin;
            int idxMetaInfoEnd;

            while (idxBracket > -1) {
                tmpArr = find(firstString.substring(strIdx), keyWords);
                idxBracket = tmpArr[0];
                keyWordNum = tmpArr[1];

                if (idxBracket > -1) {
                    if (counter > 0) {
                        String encodedStr = firstString.substring(strIdx, idxBracket + strIdx);
                        byte[] decodedBytes = decodeBytes(encodedStr.getBytes());

                        fos.write(subArray(decodedBytes, fileOffsetBegin.get(j), fileOffsetEnd.get(keyWordNum)));
                        fos.close();
                    }

                    strIdx += idxBracket;
                    idxMetaInfoBegin = strIdx;

                    tmpArr = find(firstString.substring(strIdx), contentKeyWords);
                    contentIdx = tmpArr[0];

                    if (contentIdx == -1) {
                        String connStr = firstString.substring(strIdx) + secondString;

                        tmpArr = find(connStr, contentKeyWords);
                        int contentIdxEnd = tmpArr[0];

                        if (contentIdxEnd > -1) {
                            System.out.println("тестовая ситуация контент");
                            strIdx += contentIdxEnd;

                            tmpArr = find(connStr.substring(contentIdxEnd), newStringKeyWords);
                            int newStringIdxEnd = tmpArr[0];
                            int newStringNumEnd = tmpArr[1];

                            strIdx += newStringIdxEnd;
                            idxMetaInfoEnd = strIdx;


                            String encoded = connStr.substring(contentIdxEnd + newStringIdxEnd, contentIdxEnd + newStringIdxEnd + 24);
                            byte[] decoded = decodeBytes(encoded.getBytes());

                            counter++;
                            String meta = decodeStr(connStr.substring(0, contentIdxEnd + newStringIdxEnd));
                            dbHandler.safeFile(getFileName(meta), getName(meta));
                            file = new File(getName(meta) + getFileName(meta));
                            fos = new FileOutputStream(file, true);
                            fos.write(subArray(decoded, fileOffsetBegin.get(newStringNumEnd), 0));

                            int len = strIdx + 24 - firstString.length();
                            secondBytes = steelArray(firstBytes, secondBytes, len * 3 / 4);

                            break;
                        }

                        fos.close();
                        break;
                    }

                    strIdx += contentIdx;

                    tmpArr = find(firstString.substring(strIdx), newStringKeyWords);
                    newStringIdx = tmpArr[0];
                    j = tmpArr[1];

                    if (newStringIdx == -1) {
                        System.out.println("тестовая ситуация нью стринг");

                        String connStr = firstString.substring(strIdx) + secondString;

                        tmpArr = find(connStr, newStringKeyWords);
                        int newStringIdxEnd = tmpArr[0];
                        int newStringNumEnd = tmpArr[1];

                        strIdx += newStringIdxEnd;
                        idxMetaInfoEnd = strIdx;

                        String encoded = connStr.substring(newStringIdxEnd, newStringIdxEnd + 24);
                        byte[] decoded = decodeBytes(encoded.getBytes());

                        System.out.println("тестовая ситуация нью стринг");

                        counter++;
                        String meta = decodeStr((firstString + secondString).substring(idxMetaInfoBegin, strIdx));
                        dbHandler.safeFile(getFileName(meta), getName(meta));
                        file = new File(getName(meta) + getFileName(meta));
                        fos = new FileOutputStream(file, true);
                        fos.write(subArray(decoded, fileOffsetBegin.get(newStringNumEnd), 0));

                        int len = strIdx + 24 - firstString.length();
                        secondBytes = steelArray(firstBytes, secondBytes, len * 3 / 4);

                        break;
                    }

                    strIdx += newStringIdx;
                    idxMetaInfoEnd = strIdx;

                    counter++;
                    String meta = decodeStr(firstString.substring(idxMetaInfoBegin, idxMetaInfoEnd));
                    dbHandler.safeFile(getFileName(meta), getName(meta));
                    file = new File(getName(meta) + getFileName(meta));
                    fos = new FileOutputStream(file, true);
                } else {
                    String connBlock = firstString.substring(strIdx) + secondString.substring(0, 28);

                    tmpArr = find(connBlock, keyWords);
                    idxBracket = tmpArr[0];
                    keyWordNum = tmpArr[1];

                    if (idxBracket > -1) {
                        System.out.println("тестовая ситуация" + counter);

                        String encoded = firstString.substring(strIdx, strIdx + idxBracket - 4);
                        byte[] decodedBytes = decodeBytes(encoded.getBytes());
                        fos.write(subArray(decodedBytes, fileOffsetBegin.get(j), 0));

                        int len = firstString.length() - (strIdx + idxBracket - 4);
                        secondBytes = mergeArrays(firstBytes, secondBytes, len * 3 / 4);
                        break;
                    }

                    String encodedStr = firstString.substring(strIdx);
                    byte[] decodedBytes = decodeBytes(encodedStr.getBytes());
                    fos.write(subArray(decodedBytes, fileOffsetBegin.get(j), 0));
                }
            }

            firstBytes = secondBytes;
        }

        System.out.println("конец");
    }

    private String getName(String str) {
        int idx = str.indexOf("\"");
        str = str.substring(idx + 1);

        idx = str.indexOf("\"");
        str = str.substring(0, idx);

        File theDir = new File("src/main/resources/" + str);
        if (!theDir.exists()) {
            theDir.mkdirs();
        }

        return "src/main/resources/" + str;
    }

    private String getFileName(String str) {
        int idx = str.indexOf("\"");
        str = str.substring(idx + 1);

        idx = str.indexOf("\"");
        str = str.substring(idx + 1);

        idx = str.indexOf("\"");
        str = str.substring(idx + 1);

        idx = str.indexOf("\"");
        str = str.substring(0, idx);

        return str;
    }

    private int[] find(String str, List<String> keys) {
        int[] arr = new int[2];

        int idx = -1;
        int key = 0;
        String curStr = str;

        for (int i = 0; i < keys.size(); i++) {
            int curIdx = curStr.indexOf(keys.get(i));

            if (curIdx > -1) {
                curStr = curStr.substring(0, curIdx);
                idx = curIdx;
                key = i;
            }
        }

        if (keys == newStringKeyWords) {
            if (idx > -1 && key == 1) {
                idx -= 2;
            }
        }

        arr[0] = idx;
        arr[1] = key;

        return arr;
    }

    private byte[] steelArray(byte[] first, byte[] second, int num) {
        byte[] res = new byte[second.length - num];

        for (int i = num; i < second.length; i++) {
            res[i - num] = second[i];
        }

        return res;
    }

    private byte[] mergeArrays(byte[] first, byte[] second, int num) {
        byte[] res = new byte[second.length + num];

        for (int i = 0; i < num; i++) {
            res[i] = first[first.length - num + i];
        }

        for (int i = num; i < second.length; i++) {
            res[i] = second[i - num];
        }

        return res;
    }

    private byte[] subArray(byte[] arr, int begin, int end) {
        byte[] bytes = new byte[arr.length - begin - end];

        for (int i = begin; i < arr.length - end; i++) {
            bytes[i - begin] = arr[i];
        }

        return bytes;
    }

    private String decodeStr(String str) {
        return new String(Base64.getDecoder().decode(str.getBytes()));
    }

    private String encodeStr(String str) {
        return new String(Base64.getEncoder().encode(str.getBytes()));
    }

    private byte[] decodeBytes(byte[] str) {
        return Base64.getDecoder().decode(str);
    }

    private byte[] encodeBytes(byte[] str) {
        return Base64.getEncoder().encode(str);
    }
}
