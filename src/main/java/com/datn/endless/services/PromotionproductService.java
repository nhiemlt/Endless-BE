package com.datn.endless.services;

import com.datn.endless.entities.Promotionproduct;
import com.datn.endless.repositories.PromotionproductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionproductService {

    @Autowired
    private PromotionproductRepository promotionproductRepository;

    public List<Promotionproduct> getAllPromotionproducts() {
        return promotionproductRepository.findAll();
    }

    public Optional<Promotionproduct> getPromotionproductById(String id) {
        return promotionproductRepository.findById(id);
    }

    public Promotionproduct createPromotionproduct(Promotionproduct promotionproduct) {
        return promotionproductRepository.save(promotionproduct);
    }

    public Promotionproduct updatePromotionproduct(String id, Promotionproduct promotionproduct) {
        if (promotionproductRepository.existsById(id)) {
            promotionproduct.setPromotionProductID(id);
            return promotionproductRepository.save(promotionproduct);
        } else {
            return null;
        }
    }

    public boolean deletePromotionproduct(String id) {
        if (promotionproductRepository.existsById(id)) {
            promotionproductRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
