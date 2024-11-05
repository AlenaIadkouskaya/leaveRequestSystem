//package pl.iodkovskaya.leaveRequestSystem.model.dto;
//
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Pattern;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import pl.iodkovskaya.leaveRequestSystem.model.entity.enums.RequestStatus;
//
//import java.util.UUID;
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class ChangeStatusDto {
//    @NotNull(message = "Technical ID must not be null")
//    private UUID technicalId;
//
//    @NotNull(message = "Action must not be null")
//    @Pattern(regexp = "approve|reject|cancel",
//            message = "Action must be either 'approve', 'reject' or 'cancel'")
//    private String action;
//}
