package com.fatec.scc.ports;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.fatec.scc.model.Cliente;
@Repository
public interface ClienteRepository extends JpaRepository <Cliente, Long>{
	Optional<Cliente> findByCpf(String cpf);
	List<Cliente> findAllByNomeIgnoreCaseContaining(String nome);
}

