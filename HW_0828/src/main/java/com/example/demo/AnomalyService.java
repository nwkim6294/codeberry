package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.AnomalyException;

@Service
public class AnomalyService {
	private final AnomalyRepository anomalyRepository;
	
	@Autowired
	public AnomalyService(AnomalyRepository anomalyRepository) {
		this.anomalyRepository = anomalyRepository;
	}

	public List<Anomaly> findAll() {
		// TODO Auto-generated method stub
		return anomalyRepository.findAll();
	}

	public Anomaly save(Anomaly anomaly) {
		// TODO Auto-generated method stub
		return anomalyRepository.save(anomaly);
	}
	
	
    public Anomaly findById(Long id) {
        return anomalyRepository.findById(id)
                .orElseThrow(() -> new AnomalyException("해당 변칙 현상을 찾을 수 없다냥!"));
    }

    public void delete(Long id) {
        Anomaly anomaly = findById(id);
        anomalyRepository.delete(anomaly);
    }
	
    
    
	
}
