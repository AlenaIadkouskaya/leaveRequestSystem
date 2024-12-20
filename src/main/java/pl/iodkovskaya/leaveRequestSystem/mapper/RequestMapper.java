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
package pl.iodkovskaya.leaveRequestSystem.mapper;

import org.springframework.stereotype.Component;
import pl.iodkovskaya.leaveRequestSystem.model.dto.RequestResponseDto;
import pl.iodkovskaya.leaveRequestSystem.model.entity.request.RequestEntity;

@Component
public class RequestMapper {
    public RequestResponseDto fromEntity(RequestEntity requestEntity) {
        return new RequestResponseDto(requestEntity.getTechnicalId(),
                requestEntity.getUser().getFirstName() + " " + requestEntity.getUser().getLastName(),
                requestEntity.getStatus(),
                requestEntity.getStartDate(),
                requestEntity.getEndDate()
        );
    }
}
