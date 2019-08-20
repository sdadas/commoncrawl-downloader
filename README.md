### Common Crawl Downloader
A simple application for downloading multilingual text corpora from Common Crawl. 
It works by extracting texts from CC WET files, cleaning them and splitting into language specific directories.

### Usage

`java -jar commoncrawl-downloader.jar [directory containing wet.paths file] [number of threads] [max size of language specific directory in bytes]`

For example, to download up to 10GB of text in each language: \
`java -jar commoncrawl-downloader.jar /home/user/commoncrawl/ 8 10073741824`
