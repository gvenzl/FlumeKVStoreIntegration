# FlumeKVStoreIntegration
=========================

Integration package for Apache Flume with Oracle NoSQL DB.
This is a collection of extensions which provide Apache Flume integration with the Oracle NoSQL DB CE & EE.

## 1. Installation
### Apache Flume installation
Apache Flume is simply installed by downloading and extracting the tarball from <http://flume.apache.org/download.html>  
For detailed instruction on how to start Apache Flume please see the [Apache Flume User Guide](http://flume.apache.org/documentation.html)

### Oracle NoSQL DB installation
For detailed instructions on how to install and configure the Oracle NoSQL DB please see the [Oracle NoSQL DB Administrator's Guide](http://docs.oracle.com/cd/NOSQL/html/AdminGuide/index.html)

### FlumeKVStoreIntegration installation
The FlumeKVStoreIntegration does not require any installation. It is merely a library of Java classes to be integrated with Apache Flume. All that is required is to download the jar file and place it somewhere into the classpath of Apache Flume, usually something like $FLUME_HOME/lib.

## 2. Configuration
For a detailed documentation of how to configure Apache Flume, please see the [Apache Flume User Guide](http://flume.apache.org/documentation.html).  
FlumeKVStoreIntegration comes with a default configuration file for Apache Flume either to be used as templates or just as a reference. The default configuration file(s) can be found in the *conf* folder.

### Sink properties
Following properties are available for a NoSQL DB Flume sink:

**noSqlDbSink.type = com.gvenzl.flumekvstore.sink.NoSQLDBSink**  
Defines the type of the Flume sink, in this case the NoSQLDBSink (this must not be modified).  
***
**noSqlDbSink.kvHost = localhost**  
Defines a host of the NoSQL DB cluster to connect to.  
***
**noSqlDbSink.kvPort = 5000**  
Defines the port of the NoSQL DB cluster to connect to.  
***
**noSqlDbSink.kvStoreName = kvstore**  
Defines the KV store name to use within the NoSQL DB cluster  
***
**noSqlDbSink.durability = WRITE_NO_SYNC**  
Defines the durability requirements for the events:

**SYNC** => Commit onto disk at master and replicate to simple majority of replicas (highest durability, slowest persistence)  
**WRITE_NO_SYNC** => Commit onto disk at master but do not replicate (medium durability, medium persistence)  
**NO_SYNC** => Commit only into master memory and do not replicate (lowest durability, fastest persistence)  
***
**noSqlDbSink.keyPolicy = generate**  
Defines the key retrieval policy to be used:

**generate** => Generates a simple key for each individual event  
**header** => Retrieves the key from the header of the event  
**regex** => Retrieves the key directly from the event via a regular expression (see keyType for further explanation.)  
***
**noSqlDbSink.keyType = random**  
Specifies the type of the key based upon the key policy chosen. As a guideline you should only specify key types other than random when a specific key is given within the header or the event itself. Following policies are available:

**generate:**

- *random* => Generates a new key for each event based on a random **long**.
- *timestamp* => Generates a new milli seconds timestamp based key for each event
- *nanotimestamp* => Generates a new nano seconds timestamp based key for each event
	
**header:**

- *HEADER KEY* = The HashMap key to be used to retrieve the actual key from the event header. The HashMap key can be a String of any value that identifies the object within the Header as the value to be used as NoSQL DB Key entry for the event. The value in the header has to be a String and its layout has to conform to the [com.oracle.kv.Key.toString()](http://docs.oracle.com/cd/NOSQL/html/javadoc/oracle/kv/Key.html#toString(\)) format. If a prefix for the key is defined, the prefix will be added to the key retrieved from the header.

**regex:**  
Regular expression provide great flexibility when used correctly. However, regular expression can also become very hard to understand quite quickly and should only be used when exactly known what to achieve. With the below recognized patterns it is not only possible to specify what part of the event should server as major/minor key and the value, it also allows to filter certain parts out of the event by putting them into separate (non)named groups other than below. You have to define either the *key* or *majorKey* group in order for a successful parse of the event. *minorKey* and *value* are optional but provide just mentioned flexibilities.
  
- *REGULAR EXPRESSION* = The regular expression to be used to retrieve the key from the event directly. The regular expression uses Regular Expression [named-capturing groups](http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html#groupname) to retrieve the key from the event directly. The named groups are **case sensitive** which means they have to appear as listed below! Following named groups are recognized: -   
*key* -> The entire group is treated as a major key. Useful when the event starts with some information that makes the line almost unique. Examples are: Weblogs with Timestamp and IP address. TCP-Dumps with timestamp and from and to IP addresses.  
*majorKey* -> The group is treated as major key. If no minor key (see below) is defined, this would be the name as just using *key* instead.  
*minorKey* -> The group is treated as minor key. **Note: This only works in conjunction with the *majorKey* group!**  
*value* -> The group is treated as the value for the event. This is useful when not the entire event is needed as value but just parts of it. A good example are Apache Webserver logs where you would like to analyse access patterns of your website. For example the IP address and potentially timestamp can be happily used as the Key but are not necessarily relevant for the actual access patterns. Only the request and perhaps the status code are relevant for analysing the access patterns.  
Optionally this group can be omitted. In such case, the entire event will be used as the Value.
***
**noSqlDbSink.keyPrefix = *PREFIX***
A defined prefix that will be used as the head element for the major key. This can be useful when multiple sources are directed into the same sink as it allows to logically separate events from different sources.
***
## 3. Build from source
The FlumeKVStoreIntegration build is mavenized. You can compile, test and build FlumeKVStoreIntegration via standard Maven commands.
However, in order to successfully do this you will first have to load the Oracle NoSQL DB Client into your Maven repository:
 
* Download Oracle NoSQL DB client from [here](http://www.oracle.com/technetwork/database/database-technologies/nosqldb/downloads/index.html)
* Install client into local Maven repository: ```mvn install:install-file -Dfile=./kvclient.jar -DgroupId=com.oracle.kv -DartifactId=kvclient -Dversion=2.1.57 -Dpackaging=jar```
* Clone project from Github: ```git clone https://github.com/gvenzl/FlumeKVStoreIntegration.git```
* Change into working directory: ```cd FlumeKVStoreIntegration```
* Compile FlumeKVStoreIntegration: ```mvn clean compile```
* Compile and run JUnit tests: ```mvn clean test```
* Create JAR package: ```mvn clean package```
