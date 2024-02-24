package org.dustyroom.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.dustyroom.utils.LoggingUtils.decodeAndLogException;

@Slf4j
@UtilityClass
public class FileUtils {

    public static void cleanVolumeDirectories(String targetDir, String mangaName) {
        // TODO adjust file walking to archive and clean at the same time
        try {
            Path clean = Path.of(targetDir + mangaName);
            Files.walk(clean)
                    .filter(Files::isDirectory)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException ioException) {
                            log.warn("Can't remove volume dir {}", ioException.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.warn("Can't remove volume dir {}", e.getMessage());
        }
    }

    /**
     * Creates a folder to store chapter files, if folder already exists returns null which should mean - no need to process.
     *
     * @param workdir         I should rename this one, probably it's a target directory for all scrapping
     * @param mangaName       the name of manga to load (root dir for it should have same name)
     * @param fullChapterName probably it's what stands for tom number
     * @return chapter path or null if it already exists, hence, shan't be processed.
     */
    public static Path prepareChapterFolder(String workdir, String mangaName, String fullChapterName) {
        String volume = fullChapterName.substring(fullChapterName.lastIndexOf("/vol"), fullChapterName.lastIndexOf("/"));
        String chapter = fullChapterName.substring(fullChapterName.lastIndexOf("/"));
        if (chapter.contains("?")) {
            chapter = chapter.substring(0, chapter.indexOf("?"));
        }

        Path path = Paths.get(String.format("%s%s%s%s", workdir, mangaName, volume, chapter));
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            decodeAndLogException(e, "Couldn't create folder; {}");
            return null;
        }
        return path;
    }

    public static void zipChapter(Path chapterFolder) {
        Path zipFilePath = createZipName(chapterFolder);
        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            Files.walk(chapterFolder)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Path relativePath = chapterFolder.relativize(file);
                            zos.putNextEntry(new ZipEntry(relativePath.toString()));
                            byte[] bytes = Files.readAllBytes(file);
                            zos.write(bytes, 0, bytes.length);
                            zos.closeEntry();
                            Files.delete(file);
                        } catch (IOException e) {
                            log.warn("Error during archiving {}", file);
                        }
                    });
            fos.flush();
            Files.delete(chapterFolder);
        } catch (IOException e) {
            log.warn("Error during archiving {}", chapterFolder);
        }
        log.info("Chapter is zipped to {}", zipFilePath);
    }

    public static Path createZipName(Path chapterFolder) {
        return Path.of(chapterFolder.getParent().toString() + "-ch" + chapterFolder.getFileName().toString() + ".zip");
    }
}
