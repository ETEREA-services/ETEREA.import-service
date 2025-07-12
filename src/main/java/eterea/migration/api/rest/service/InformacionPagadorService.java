package eterea.migration.api.rest.service;

import eterea.migration.api.rest.model.InformacionPagador;
import eterea.migration.api.rest.repository.InformacionPagadorRepository;
import org.springframework.stereotype.Service;

@Service
public class InformacionPagadorService {

    private final InformacionPagadorRepository repository;

    public InformacionPagadorService(InformacionPagadorRepository repository) {
        this.repository = repository;
    }

    public InformacionPagador save(InformacionPagador informacionPagador) {
        return repository.save(informacionPagador);
    }

}
