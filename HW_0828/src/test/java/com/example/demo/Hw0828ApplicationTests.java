package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Hw0828ApplicationTests {
	
	@Autowired
	private AnomalyRepository anomalyRepository;

	@Test
	void contextLoads() {
	    Anomaly a = new Anomaly();
	    a.setName("안뇽");
	    a.setRiskLevel("엄청 어려움");
	    a.setLocation("집");

	    Anomaly saved = this.anomalyRepository.save(a);


	}

}
