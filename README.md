### Common Crawl Downloader
A simple application for downloading multilingual text corpora from Common Crawl. 
It works by extracting texts from CC WET files, cleaning them and splitting into language specific directories.
It uses FastText model ([https://fasttext.cc/blog/2017/10/02/blog-post.html](https://fasttext.cc/blog/2017/10/02/blog-post.html)) for language identification.

### Usage

```
java -jar commoncrawl-downloader.jar 
  [directory containing wet.paths file] 
  -threads [optionally: number of threads, by default equal to 1] 
  -limit [optionally: max size of language specific directory in bytes, unconstrained by default] 
  -languages [optionally: comma separated list of languages, all languages by default]
```

For example, to download up to 10GB of text in Polish and English: \
`java -jar commoncrawl-downloader.jar /home/user/commoncrawl/ -t 8 -limit 10073741824 -languages pl,en`
