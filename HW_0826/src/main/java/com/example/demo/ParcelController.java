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

@Controller
@RequestMapping("/parcels")
public class ParcelController {
	
	@Autowired
	private ParcelService parcelService;
	
	public ParcelController(ParcelService parcelService) {
		this.parcelService = parcelService;
	}
	
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("parcels", parcelService.findAll());
        return "list";
    }
	
    @GetMapping("/new")
    public String registerParcel(Model model) {
    	model.addAttribute("parcel", new ParcelDto.SaveRequest());
        return "new-parcel-form";
    }
    
    @PostMapping("/new")
    public String registerParcel(@ModelAttribute ParcelDto.SaveRequest dto) {
        parcelService.registerParcel(dto);
        return "redirect:/parcels/list";
    }
    
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Parcel parcel = parcelService.findById(id);
        model.addAttribute("parcel", parcel);
        model.addAttribute("statuses", Status.values());
        return "edit-form"; 
    }
    
//    @PostMapping("/{id}/update")
//    public String update(@PathVariable Long id, @ModelAttribute ParcelDto.UpdateRequest dto) {
//        parcelService.updateStatus(id, status);
//        return "redirect:/parcels";
//    }
//    
    
}
