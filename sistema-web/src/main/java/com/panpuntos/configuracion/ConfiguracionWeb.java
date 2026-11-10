package com.panpuntos.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.config.BlockingExecutionConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class ConfiguracionWeb implements WebFluxConfigurer {

    private final ThreadPoolTaskExecutor ejecutorSolicitudes;

    public ConfiguracionWeb(ThreadPoolTaskExecutor ejecutorSolicitudes) {
        this.ejecutorSolicitudes = ejecutorSolicitudes;
    }

    @Override
    public void configureBlockingExecution(BlockingExecutionConfigurer configurador) {
        configurador.setExecutor(ejecutorSolicitudes);
    }

    @Configuration
    static class ConfiguracionEjecutor {

        @Bean
        ThreadPoolTaskExecutor ejecutorSolicitudes(@Value("${aplicacion.ejecutor.hilos-minimos}") int hilosMinimos,
                                                   @Value("${aplicacion.ejecutor.hilos-maximos}") int hilosMaximos,
                                                   @Value("${aplicacion.ejecutor.capacidad-cola}") int capacidadCola) {
            ThreadPoolTaskExecutor ejecutor = new ThreadPoolTaskExecutor();
            ejecutor.setCorePoolSize(hilosMinimos);
            ejecutor.setMaxPoolSize(hilosMaximos);
            ejecutor.setQueueCapacity(capacidadCola);
            ejecutor.setThreadNamePrefix("solicitud-");
            ejecutor.setWaitForTasksToCompleteOnShutdown(true);
            return ejecutor;
        }
    }
}
