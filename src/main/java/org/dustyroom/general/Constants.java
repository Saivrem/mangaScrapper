package org.dustyroom.general;

import lombok.experimental.UtilityClass;
import org.dustyroom.manga.MangaUtils;

import java.io.File;

@UtilityClass
public class Constants {
    public static final String s = File.separator;
    public static final String jarPath = MangaUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
}
