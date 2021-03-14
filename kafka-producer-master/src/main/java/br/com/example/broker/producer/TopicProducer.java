package br.com.example.broker.producer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicProducer {

    @Value("${topic.name.producer}")
    private String topicName = "itemlocation";
    
    public TopicProducer() {
    	this.kafkaTemplate = kafkaTemplate();
    }

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message){
        //log.info("Payload enviado: {}",  message);
    	
    	listTopics();    	
    	
    	System.out.println("enviando mensagem ....");
        kafkaTemplate.send(topicName, message);
        System.out.println("mensagem enviada!!!");
    }
    
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, "teste-hugo");
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    public void listTopics() {
    	
    	Map<String, List<PartitionInfo> > topics;

    	Properties props = new Properties();
    	props.put("bootstrap.servers", "kafka:9092");
    	props.put("group.id", "test-consumer-group");
    	props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    	props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    	KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
    	topics = consumer.listTopics();
    	
    	for(String key: topics.keySet()) {
    		
    		List<PartitionInfo> ps = topics.get(key);
    		for(PartitionInfo p : ps) {
    			System.out.println( p.topic() );
    		}
    		
    	}
    	
    	consumer.close();    	
    	
    }

}