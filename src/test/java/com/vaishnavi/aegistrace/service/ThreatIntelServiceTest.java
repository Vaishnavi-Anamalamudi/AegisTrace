package com.vaishnavi.aegistrace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.vaishnavi.aegistrace.entity.ThreatIntel;
import com.vaishnavi.aegistrace.repository.ThreatIntelRepository;

class ThreatIntelServiceTest {

    @Test
    void findByIpUsesRepositoryQueryAndTrimsInput() {
        ThreatIntelRepository repository = mock(ThreatIntelRepository.class);
        ThreatIntelService service = new ThreatIntelService(repository);
        ThreatIntel intel = new ThreatIntel();
        intel.setIpAddress("203.0.113.42");
        when(repository.findFirstByIpAddressIgnoreCase("203.0.113.42")).thenReturn(Optional.of(intel));

        Optional<ThreatIntel> result = service.findByIp(" 203.0.113.42 ");

        assertThat(result).contains(intel);
        verify(repository).findFirstByIpAddressIgnoreCase("203.0.113.42");
    }

    @Test
    void findByIpRejectsBlankInputWithoutQueryingDatabase() {
        ThreatIntelRepository repository = mock(ThreatIntelRepository.class);
        ThreatIntelService service = new ThreatIntelService(repository);

        assertThat(service.findByIp(" ")).isEmpty();
        verifyNoInteractions(repository);
    }
}
