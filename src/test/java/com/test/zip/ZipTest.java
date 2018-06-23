package com.test.zip;

import com.tool.kj.zip.KjZip;

import java.io.IOException;

public class ZipTest {

    public static void main(String[] args) throws IOException {
        KjZip.unzip("/Users/kuojian21/Downloads/template/Brazil-6-1920x1080.zip",
                "/Users/kuojian21/Downloads/template");
    }

}
