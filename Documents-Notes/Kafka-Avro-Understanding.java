

1. Learnt basic Kafka using configurations at .yml file
2. Used Springboot KafkaTemplate classes 

AVRO

1. Java based basic schema is made
2. GenericRecord (Generic and Specific Record) --> Avro Object --> Avro Object to a file --> From File read the Object.

Very Important step, how code is generated ? - just run the maven install or in intellij just double click on the package 
This will generate the target folder and the code for us
	a. .avsh files - we write this
		.class file is generated from the attribute name in the .avsh file
	b. .avro files - code generate this
3. used avro tools to read the schema : (Simply download this jar and run the command)
	java -jar /usr/local/Cellar/avro-tools/1.10.2/avro-tools-1.10.2.jar
	
	>java -jar /Users/user/Desktop/Kafka/AVRO/tools/avro-tools-1.8.2.jar getschema customerV1.avro
	
4. Now KAFKA - AVRO working
	<!-- I have used docker images to setup Kafka + Landoop UI+Schema Registry-->
	Steps: (Very important):
		1. Go to directory where docker.file is present , docker-compose.yml
			E.g path on my local is "/Users/user/Desktop/Kafka/AVRO/code/2-start-kafka/docker-compose.yml"
		2. run command "docker compose up" --> This will do the entire setup
	Landoop UI - Its an amazing UI to show 	
		Schema's (This is a schema registry)
		Topics
		Connections
		Brokers
		3. kafka-avro-console-producer and kafka-avro-console-consumer , just like kafa-console-producer and kafka-console-consumer,
			only difference is 
5. Schema-registry - this is provided by confluent, so we can create schemas, maintain versions, evolve schemas.
6. 	Producer has to send to schema registry+kafka
	Consumer has to read from Kafka +SchemaRegistry
7. Binaries are to be downloaded either from Confluent or from Docker command
	All commands are in file
	Go to file "/Users/user/Desktop/Kafka/AVRO/code/3-schema-registry/1-kafka-avro-console-producer-consumer.sh" 				
 
 	Some important understandings:
 		a. Run the command or directly download the binaries
 			> docker run --net=host -it confluentinc/cp-schema-registry:3.3.0 bash
 							OR
 			https://www.confluent.io/download/
 		b. Now just like normal topic , create a topic and an avro-schema as a property : 
 		root@docker-desktop:/# "All the commands are given in file mentioned in point 7 above"
 8. Now [ CLI to UI ] 3 steps
 	s1-download confluent binaries as above
 	s2-write kafka producer-topic-schema
 	s3- write record with press enter for new record
 		Record will be shown on UI also at landoop url 127.0.0.1:3030
 		You will see:
 			 schema created
 			 topic created - data in the topics
 			 Exceptions are also evident while writing on CLI 

Command for bringing up the kafka ecosystem (At the docker compose file location)
=====================
docker compose up
 		 
Command for Producer 
===================== 			 

(
Once Confluent binaries are downloaded new path is opened :  
	command = docker run --net=host -it confluentinc/cp-schema-registry:3.3.0 bash
	path is root@docker-desktop
)
kafka-avro-console-producer --zookeeper:9092 --topic test-avro --property schema.registry.url=http://127.0.0.1:8081 \
--property value.schema='{"type":"record","name":"myrecord","fields":[{"name":"f1","type":"string"}]}'

Command for Consumer - All same only no need to make schema, just start from beginning
=====================
(
Once Confluent binaries are downloaded new path is opened :  
	command = docker run --net=host -it confluentinc/cp-schema-registry:3.3.0 bash
	path is root@docker-desktop
)

kafka-avro-console-consumer --topic test-avro --bootstrap-server 127.0.0.1:9092 --property \
schema.registry.url=http://127.0.0.1:8081 --from-beginning					



9. Error in schema evolutions , general error codes
	a. 409 - schema2 is incompatible with schema1
	
	
10. Very important point - the schema generated is moved to schema-registry only when the kafka-avro-console-producer publish to 
	kafka and schema registry	
	
11. Producer-1 produces on schema v1
	Consumer-2 consumes on schema v2
	
	All the common fields in producer schema version and consumer schema version will 
	
	Schema v1 = { name , age , sex}
	Schema v2 = {name , age , phone ,email}
	
	Why is default imp: lets look at each field remember producer users schema v1 to produce and consumer uses v2 to consume 
	
	1 name / age - common therefore no conflice 
	2. sex - producer-P1  produces but consumer-C2 cannot consume as its not present in schema v2 , therefore automatically 
	its handled and will not be consumed
	3. Pay attention - phone/email - it has deault values , becuase consument c2 is trying to read based on schema v2 but
		producer P2 has not sent based on schema v1 . Therfore default values are set ...
		
		
===== Schema Registry====
RULE 1:
	Avro has [ Avro Schme , Avro Content]

> Kafka Avro Serializer - when we use Kafka Avro Serializer , 2 things happen 
	1. Schema - 			Schema is registered with Schema Registry
	2. Schema Registry - 	Screma Registery returns the Schema ID (Which is 4 bytes) 


=----------------------=
What Serializer Does
=----------------------=	 		
> 	"Avro Content" goes to Kafka , see rule number 1 it has 2 parts second part the avto content goes to Kafka
	Here also 2 things happens
	
	1. Magic byts - Kafka Serializer will prepend the magic bytes basically magic bytes is a version number
		right now its 0
	2. Prepend the schema ID  
	3. Avro Content
	  Avro Content --> Kafka Serializer do -->	[Magic Byte , Schema ID, Avro Content]
	  
	  
	 Important - Actual schema resides in schema registry only , Kafka does not contain schema , the actual 
	 content at Kafka is very small	only [magic byte + Schema ID and not the whole schema + Avro Content]
	 Since schema is very huge generally , and we need not to send to Kafka and Kafka uses schema id for the reference of 
	 schema. 
=----------------------=
What Deserializer Does
=----------------------=

	1. Deserializer - does inverse of Serializer
	2. 
		a. It externalize the schema (Schema does not lives in Kafka on the Schema ID lives)
		b. Message size is v small , becuase actual schema is not sent only id is sent
		c. Schema Registry must be available else producer / consumer can break.
		
Note - Always write fully compatible schema evolution
		Schema Registry is the most critical component		 		
	
---------------------
	Confluent REST Proxy
--------------------	
This is to cater non-java clients - its a part of confluent

We will learn
1. REST proxy calls and versions
2. Topics operations
3. Producing and Consuming JSON
4. Producing and Consuming AVRO
5. Producing and Consuming Binary
6. Deploying and SCaling REST Proxy


Kavfa v1 vs v2
=============
Kafka v1 - was in kafka 0.8 where to store offsets zookeeper was used
kafka v2 - new version , now its using KAFKA to store the offsets and not the zookeeper to store the off sets



Coding and Important points
-----------------------------------

1. Content Type has to be specified in the Header (Plus the advance header)
Kunal ---->>>>>There is a convention which needs to be followed, this convention is defined by REST Proxy

Content Type :
application/vnd.kafka[.embeded_format].[api_version]+serializationformat 

>	Part 1 = 	aplication/vnd.kafka - mandatory
> 	Part 2 = 	[.embeded_format] -  can be [Json,Binary,Avro]
>	Part 3 =	[api_version]	-	v2 - now Kafka has released a version v2 therefore always v2
>	Part 4 =	serializationformat	- json

Example = below are example fo 2 headers 
Content-Type = application/vnd.kafka.avro.v2+json
Accept : application/vnd.kafka.v2+json

End Points :
GET/topic
GET/topic/topic_name	

Example : http://127.0.0.1:8082/topics === > REST Proxy at Lanoop is running on 8082
output : [
    "__consumer_offsets",
    "_schemas",
    "connect-configs",
    "connect-offsets",
    "connect-statuses",
    "customer-avro",
    "kunal-learning-avro"
]


[Kunal]LANDOOP -- All ports and services can be seen on 
SERVICES PORTS
9092 : Kafka Broker
9581 : JMX
8081 : Schema Registry
9582 : JMX
8082 : Kafka REST Proxy
9583 : JMX
8083 : Kafka Connect Distributed
9584 : JMX
2181 : ZooKeeper
9585 : JMX
3030 : Web Server




1. Sending binary data to Kafka - the data must be send in base 64





















	