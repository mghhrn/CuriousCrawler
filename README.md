# CuriousCrawler

To run this application you need to have Java 11.

Run the application by executing below commands in the root directory of the project:

```
mvn clean package
java -jar target/curious-crawler-1.0.0-jar-with-dependencies.jar
```

### Features
* Saving data to SQLite. The data will be saved on `/tmp/curious-crawler/db/crawler.db` for Unix-based systems, and `{system-temp-directory}\curious-crawler\db\crawler.db` for Windows systems.
* Caching web pages on the file system to increase performance for the next runs. The pages will be cached on `/tmp/curious-crawler/cache` for Unix-based systems, and `{system-temp-directory}\curious-crawler\cache` for Windows systems.
* Using multithreading to achieve maximum performance based on the available hardware resources. 
* Prevention from visiting each web page more than once by using a ConcurrentHashMap to save visited URLs. 
