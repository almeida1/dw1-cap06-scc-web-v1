package com.fatec.scc.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fatec.scc.model.Cliente;
import com.fatec.scc.ports.MantemCliente;


@Configuration
class LoadDatabase {

	Logger log = LogManager.getLogger(this.getClass());
	
	@Bean
	CommandLineRunner initDatabase(MantemCliente mantemCliente) {

		return args -> {
			Cliente cliente1 = new Cliente("Miguel Soares", "10/02/1960", "M", "99504993052", "04280130", "2983");
			log.info("Preloading - cliente => " + mantemCliente.save(cliente1));

			Cliente cliente2 = new Cliente("Marcos Silva", "04/10/1974", "M", "43011831084", "08545160", "2983");
			log.info("Preloading " + mantemCliente.save(cliente2));
			
			
		};
	}
}
