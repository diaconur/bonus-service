package com.bonus.service.bonus.controller;

import com.bonus.service.common.entity.Bonus;
import com.bonus.service.common.entity.CreateBonusRequest;
import com.bonus.service.common.exceptions.BonusException;
import com.bonus.service.bonus.repository.BonusOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/bonus")
public class BonusController {

    @Autowired
    BonusOperations bonusOperations;

    @PostMapping("/addBonus")
    public void addBonus(@RequestBody CreateBonusRequest bonus) {
        bonusOperations.addBonus(bonus);
    }

    @GetMapping(value = "/getAllBonuses", produces = "application/json")
    public List<Bonus> getBonuses() {
        return bonusOperations.getAllBonuses();
    }

    @GetMapping("/getBonusById")
    public Optional<Bonus> getBonusById(@RequestParam Integer id) {
        return bonusOperations.getbonusById(id);
    }

    @PutMapping("/updateBonus")
    private Bonus updateBonus(@RequestParam Integer bonusId, @RequestBody CreateBonusRequest createBonusRequest) throws BonusException {
        return bonusOperations.updateBonus(bonusId, createBonusRequest);
    }

    @DeleteMapping("/deleteBonus/{id}")
    private void deleteBonusById(int id) {
        bonusOperations.deleteBonusById(id);
    }
}
