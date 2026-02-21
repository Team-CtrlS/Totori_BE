package ctrlS.totori.auth.dto;

import ctrlS.totori.member.entity.Role;

import java.time.LocalDate;

public record CompleteProfileRequest(Role role, String name, LocalDate birthDate) {

}
