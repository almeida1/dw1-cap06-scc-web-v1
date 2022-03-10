package com.fatec.scc.services;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.fatec.scc.model.Cliente;
import com.fatec.scc.model.Endereco;
import com.fatec.scc.ports.ClienteRepository;
import com.fatec.scc.ports.MantemCliente;
@Service
public class MantemClienteI implements MantemCliente {
	Logger logger = LogManager.getLogger(this.getClass());
	@Autowired
	ClienteRepository repository;

	public List<Cliente> consultaTodos() {
		logger.info(">>>>>> servico consultaTodos chamado");
		return repository.findAll();
	}

	@Override
	public Optional<Cliente> consultaPorCpf(String cpf) {
		logger.info(">>>>>> servico consultaPorCpf chamado");
		return repository.findByCpf(cpf);
	}

	@Override
	public Optional<Cliente> consultaPorId(Long id) {
		logger.info(">>>>>> servico consultaPorId chamado");
		return repository.findById(id);
	}

	@Override
	public Optional<Cliente> save(Cliente cliente) {
		logger.info(">>>>>> servico save chamado ");
		Optional<Cliente> umCliente = consultaPorCpf(cliente.getCpf());
		 Optional<Cliente> novoCliente = Optional.empty();
		Endereco endereco = obtemEndereco(cliente.getCep());
		try {
			if (umCliente.isEmpty() & endereco != null) {
				cliente.obtemDataAtual(new DateTime());
				cliente.setEndereco(endereco.getLogradouro());
                novoCliente = Optional.ofNullable(repository.save(cliente));
                logger.info(">>>>>> servico save - dados validos");
			}
		} catch (Exception e) { // org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
			logger.info(">>>>>> servico save erro - org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException");
			
		}
		return novoCliente;
	}

	@Override
	public void delete(Long id) {
		logger.info(">>>>>> servico delete por id chamado");
		repository.deleteById(id);
	}

	@Override
	public Optional<Cliente> altera(Cliente cliente) {
		logger.info(">>>>>> 1.servico altera cliente chamado");
		Optional<Cliente> umCliente = consultaPorId(cliente.getId());
		Endereco endereco = obtemEndereco(cliente.getCep());
		if (umCliente.isPresent() & endereco != null) {
			Cliente clienteModificado = new Cliente(cliente.getNome(), cliente.getDataNascimento(), cliente.getSexo(),
					cliente.getCpf(), cliente.getCep(), cliente.getComplemento());
			clienteModificado.setId(cliente.getId());
			clienteModificado.obtemDataAtual(new DateTime());
			clienteModificado.setEndereco(endereco.getLogradouro());
			logger.info(">>>>>> 2. servico altera cliente cep valido para o id => " + clienteModificado.getId());
			return Optional.ofNullable(repository.save(clienteModificado));
		} else {
			return null;
		}

	}

	public Endereco obtemEndereco(String cep) {
		RestTemplate template = new RestTemplate();
		String url = "https://viacep.com.br/ws/{cep}/json/";
		logger.info(">>>>>> servico consultaCep - " + cep);
		ResponseEntity<Endereco> resposta = null;

		try {
			resposta = template.getForEntity(url, Endereco.class, cep);
			return resposta.getBody();
		} catch (ResourceAccessException e) {
			logger.info(">>>>>> consulta CEP erro nao esperado ");
			return null;
		} catch (HttpClientErrorException e) {
			logger.info(">>>>>> consulta CEP inválido erro HttpClientErrorException =>" + e.getMessage());
			return null;
		}
	}
}
