# FlumeKVStoreIntegration
=========================

Integration for Apache Flume with Oracle NoSQL DB.
This is a collection of classes to provide Apache Flume integration with the Oracle NoSQL DB CE & EE.

## 1. Installation
TBD

## 2. Configuration
For the configuration of Apache Flume see http://flume.apache.org/FlumeUserGuide.html for details.
FlumeKVStoreIntegration comes with default configuration files either to be used as templates or just as a lookup.
For the default configuration files see the *conf* folder.

### Sink properties
Following properties are available for a NoSQL DB Flume sink:

#### noSqlDbSink.type = com.optit.flumekvstore.sink.NoSQLDBSink
Defines the type of the Flume sink, in this case the NoSQLDBSink (this must not be modified).

#### noSqlDbSink.kvHost = localhost
Defines a host of the NoSQL DB cluster to connect to.

#### noSqlDbSink.kvPort = 5000
Defines the port of the NoSQL DB cluster to connect to.

#### noSqlDbSink.kvStoreName = kvstore
Defines the KV store name to use within the NoSQL DB cluster

#### noSqlDbSink.durability = WRITE_NO_SYNC
Defines the durability requirements for the events:

**SYNC** => Commit onto disk at master and replicate to simple majority of replicas (highest durability, slowest persistence)

**WRITE_NO_SYNC** => Commit onto disk at master but do not replicate (medium durability, medium persistence)

**NO_SYNC** => Commit only into master memory and do not replicate (lowest durability, fastest persistence)

#### noSqlDbSink.keyPolicy = generate
Defines the key retrieval policy to be used:

**generate** => Generates a simple key for each individual event

**header** => Retrieves the key from the header of the event (see keyType, yet to be implemented)

**regex** => Retrieves the key directly from the event via a regular expression (see keyType, yet to be implemented)

#### noSqlDbSink.keyType = random
Specifies the type of the key based upon the key policy chosen:

**generate:**

  *random* => Generates a new key for each event based on a random **long**.

  *timestamp* => Generates a new milli seconds timestamp based key for each event

  *nanotimestamp* => Generates a new nano seconds timestamp based key for each event
	
**header:**

  *HEADER KEY* = The HashMap key to be used to retrieve the actual key from the event header

**regex:**

  *REGULAR EXPRESSION* = The regular expression to be used to retrieve the key from the event directly

#### noSqlDbSink.keyPrefix = *PREFIX*
A defined prefix that will be used as the head element for the major key. This can be useful when multiple sources are directed into the same sink as it allows to logically separate events from different sources.

## 3. Build from source
TBD 
