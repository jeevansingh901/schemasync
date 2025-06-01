package com.schemasync.utils;

import java.io.File;

public class FilesUtils {

    public static int getMaxVersion(File[] files) {
        int max = 0;
        for (File file : files) {
            String name = file.getName();
            try {
                String versionPart = name.split("__")[0].substring(1); // remove 'V'
                int version = Integer.parseInt(versionPart);
                if (version > max) max = version;
            } catch (Exception e) {
                // ignore malformed filenames
            }
        }
        return max;
    }
}
