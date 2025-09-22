package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.ParcelDto.SaveRequest;

@Service
public class ParcelService {
	
	private final ParcelRepository parcelRepository;
	
	@Autowired
	public ParcelService(ParcelRepository parcelRepository) {
		this.parcelRepository = parcelRepository;
	}

	
	public List<Parcel> findAll() {
		return parcelRepository.findAll();
		
	}
	
	@Transactional
	public void registerParcel(ParcelDto.SaveRequest dto) {
		// TODO Auto-generated method stub
		parcelRepository.save(dto.toEntity());
		
	}
	
    public Parcel findById(Long id) {
        return parcelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 소포가 없습니다. id=" + id));
    }

	
	
	
	
	
}
