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
		Endereco endereco = obtemEndereco(cliente.getCep());
		if (umCliente.isEmpty() & endereco != null) {
			logger.info(">>>>>> servico save - dados validos");
			cliente.obtemDataAtual(new DateTime());
			cliente.setEndereco(endereco.getLogradouro());
			return Optional.ofNullable(repository.save(cliente));
		} else {
			return Optional.empty();
		}
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
			
			umCliente.get().setNome(cliente.getNome());
			umCliente.get().setDataNascimento(cliente.getDataNascimento());
			umCliente.get().setSexo(cliente.getSexo());
			umCliente.get().setCpf(cliente.getCpf());
			umCliente.get().setCep(cliente.getCep());
			umCliente.get().setComplemento(cliente.getComplemento());
			umCliente.get().setId(cliente.getId());
			umCliente.get().obtemDataAtual(new DateTime());
			umCliente.get().setEndereco(endereco.getLogradouro());
			
			logger.info(">>>>>> 2. servico altera cliente dados validos ");
			return Optional.ofNullable(repository.save(umCliente.get()));
		} else {
			return Optional.empty();
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
