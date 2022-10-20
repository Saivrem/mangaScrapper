package org.dustyroom;

import org.dustyroom.manga.scrapper.MangaLiveScrapper;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length >= 2) {
            String mangaName = args[0];
            String mangaLink = args[1];
            boolean mature = args.length == 3 && args[2].equals("m");
            MangaLiveScrapper scrapper = MangaLiveScrapper.builder()
                                                          .mangaName(mangaName)
                                                          .mangaRoot(mangaLink)
                                                          .needMature(mature)
                                                          .build();
            scrapper.scrap();
        } else {
            System.out.println("""
                    You need to specify manga name and link as args[0] and args[1] respectively
                    args[2] == m is for mature manga (optional)
                    """);
        }
    }
}