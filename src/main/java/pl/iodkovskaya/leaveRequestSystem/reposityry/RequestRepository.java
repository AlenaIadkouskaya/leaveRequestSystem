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
package pl.iodkovskaya.leaveRequestSystem.reposityry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;
import pl.iodkovskaya.leaveRequestSystem.model.entity.user.UserEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {
    @Query("SELECT r FROM RequestEntity r WHERE r.user = :user AND " +
            "r.status NOT IN (pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus.REJECTED) AND " +
            "((r.startDate BETWEEN :startDate AND :endDate) OR (r.endDate BETWEEN :startDate AND :endDate) OR " +
            "(r.startDate <= :startDate AND r.endDate >= :endDate))")
    List<RequestEntity> findAllByUserAndDateRange(
            @Param("user") UserEntity user,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    Optional<RequestEntity> findByTechnicalId(UUID technicalId);
    List<RequestEntity> findByUser(UserEntity userId);
    @Query("SELECT r FROM RequestEntity r WHERE r.status IN (pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus.CREATED, " +
            "pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus.PENDING)")
    List<RequestEntity> findAllRequestsToApprove();

}
