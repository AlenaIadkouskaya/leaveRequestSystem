/*
 * Copyright 2024 Alena Iadkouskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.iodkovskaya.leaveRequestSystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.iodkovskaya.leaveRequestSystem.model.dto.VacationBalanceDto;
import pl.iodkovskaya.leaveRequestSystem.service.VacationBalanceService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/vacation-balance")
public class VacationBalanceController {
    private final VacationBalanceService vacationBalanceService;
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ACCOUNTANT')")
    @PostMapping("/new")
    public ResponseEntity<String> createVacationBalance(@Valid @RequestBody VacationBalanceDto vacationBalanceDto) {
        vacationBalanceService.addRecord(vacationBalanceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("The data regarding the remaining days has been successfully added");
    }
}
