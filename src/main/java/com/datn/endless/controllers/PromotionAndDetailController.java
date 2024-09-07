package com.datn.endless.controllers;

import com.datn.endless.entities.Promotion;
import com.datn.endless.entities.Promotiondetail;
import com.datn.endless.repositories.PromotionRepository;
import com.datn.endless.repositories.PromotiondetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/promotions")
public class PromotionAndDetailController {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotiondetailRepository PromotiondetailRepository;

    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Promotion getPromotionById(@PathVariable String id) {
        return promotionRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Promotion createPromotion(@RequestBody Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @PutMapping("/{id}")
    public Promotion updatePromotion(@PathVariable String id, @RequestBody Promotion promotion) {
        if (promotionRepository.existsById(id)) {
            promotion.setPromotionID(id);
            return promotionRepository.save(promotion);
        }
        return null;
    }


    @DeleteMapping("/{id}")
    public void deletePromotion(@PathVariable String id) {
        promotionRepository.deleteById(id);
    }

// ------------------------------------------------Details---------------------------------------------------------

    @GetMapping("/details")
    public List<Promotiondetail> getAllPromotiondetails() {
        return PromotiondetailRepository.findAll();
    }

    @GetMapping("/details/{id}")
    public Promotiondetail getPromotiondetailById(@PathVariable String id) {
        return PromotiondetailRepository.findById(id).orElse(null);
    }

    @PostMapping("/details")
    public Promotiondetail createPromotiondetail(@RequestBody Promotiondetail Promotiondetail) {
        return PromotiondetailRepository.save(Promotiondetail);
    }


    @PutMapping("/details/{id}")
    public Promotiondetail updatePromotiondetail(@PathVariable String id, @RequestBody Promotiondetail Promotiondetail) {
        if (PromotiondetailRepository.existsById(id)) {
            Promotiondetail.setPromotionDetailID(id);
            return PromotiondetailRepository.save(Promotiondetail);
        }
        return null;
    }

    @DeleteMapping("/details/{id}")
    public void deletePromotiondetail(@PathVariable String id) {
        PromotiondetailRepository.deleteById(id);
    }

}
