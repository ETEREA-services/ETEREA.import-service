package eterea.migration.rest.service;

import eterea.migration.rest.model.InformacionPagador;
import eterea.migration.rest.repository.InformacionPagadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformacionPagadorService {

    private final InformacionPagadorRepository repository;

    public InformacionPagador save(InformacionPagador informacionPagador) {
        return repository.save(informacionPagador);
    }

}
