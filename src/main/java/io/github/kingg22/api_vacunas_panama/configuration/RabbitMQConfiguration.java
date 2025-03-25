package io.github.kingg22.api_vacunas_panama.configuration;
/*
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
 */
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    public static final String QUEUE_GENERAL = "vacunas_panama";
    public static final String QUEUE_DOSIS = "vacunas_dosis";
    public static final String EXCHANGE_NAME = "vacunas_panama";
    public static final String ROUTING_KEY = "vacunas_panama";
/*
    @Bean
    public Queue queue() {
        return new org.springframework.amqp.core.Queue(QUEUE_GENERAL, true);
    }

    @Bean
    public Queue queueDosis() {
        return new org.springframework.amqp.core.Queue(QUEUE_DOSIS, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }
*/
}
