package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/anomalies")
public class AnomalyController {

	@Autowired
	private AnomalyService anomalyService;
	
	public AnomalyController(AnomalyService anomalyService) {
		this.anomalyService = anomalyService;
	}

	@GetMapping("/list")
	public String readList(Model model) {
		List<Anomaly> alist = this.anomalyService.findAll();
		model.addAttribute("alist", alist);
		return "anomalies/list";
	}

	// 새로만들기 페이지 보기
	@GetMapping("/new")
	public String createView(Model model) {
		model.addAttribute("anomaly", new Anomaly());
		return "anomalies/new-form";
	}
	
	// 새로만들기 post
	@PostMapping("/new")
	public String create(
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("riskLevel") String riskLevel
    ) {
        Anomaly anomaly = new Anomaly(name, location, riskLevel);
        anomalyService.save(anomaly);

	    return "redirect:/anomalies/list";
	}
	
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") Long id, Model model) {
        Anomaly anomaly = this.anomalyService.findById(id);
        model.addAttribute("anomaly", anomaly);
        
        return "anomalies/new-form";
    }
    
    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("location") String location,
            @RequestParam("riskLevel") String riskLevel
    ) {
        Anomaly anomaly = this.anomalyService.findById(id);
        anomaly.update(name, location, riskLevel);
        this.anomalyService.save(anomaly);
        
        return "redirect:/anomalies/list";
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        anomalyService.delete(id); 
        return "redirect:/anomalies/list";
    }
}
