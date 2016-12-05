package com.keyun.pan.data;

import java.io.File;
import java.io.Serializable;

/**
 * Created by cunguoyao on 2016/8/4.
 */
public class ChunksFile implements Serializable {

    private String chunkNo;
    private File file;
    private String md5;
    private String existChunks;

    public ChunksFile(String chunkNo, File file, String md5, String existChunks) {
        this.chunkNo = chunkNo;
        this.file = file;
        this.md5 = md5;
        this.existChunks = existChunks;
    }

    public String getChunkNo() {
        return chunkNo;
    }

    public void setChunkNo(String chunkNo) {
        this.chunkNo = chunkNo;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getExistChunks() {
        return existChunks;
    }

    public void setExistChunks(String existChunks) {
        this.existChunks = existChunks;
    }
}
