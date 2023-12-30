# Manga Live and derivatives scrapping tool

## Config file structure

```json
{
  "targetDir": "/path/to/dir/",
  "mature": true,
  "blacklist": [
    "list","of","file","names","I",
    "want","skip","e.g.","credits","help",
    "and stuff not related to manga itself"
  ],
  "mangaList": [
    {
      "mangaName": "mangaName",
      "mangaPageLink": "mangaLink",
      "fallbackDomain": "fallbackDomain",
      "compressionNeeded": false,
      "needed": true
    }
  ]
}
```
**compressionNeeded** is a flag to indicate that chapters should be zipped, if set to true, everything except for zip archives will be removed once download is finished.
**needed** flag that manga should be loaded (so you can keep long config and only load several titles)
