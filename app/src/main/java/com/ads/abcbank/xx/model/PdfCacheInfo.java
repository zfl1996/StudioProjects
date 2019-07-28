package com.ads.abcbank.xx.model;

public class PdfCacheInfo {
    public int pageCount;
    public String cachePath;
    public String[] allCachePaths;

    public PdfCacheInfo(int c, String p) {
        pageCount = c;
        cachePath = p;
    }

    public PdfCacheInfo(int c, String[] ps) {
        pageCount = c;
        allCachePaths = ps;
    }
}
