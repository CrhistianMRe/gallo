package com.crhistianm.springboot.gallo.springboot_gallo.account;

import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenAccountEntityAdmin;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenAccountEntityUser;
import static com.crhistianm.springboot.gallo.springboot_gallo.account.AccountData.givenUserAccountRequestDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AccountMapperUnitTest {

    @Nested
    class RequestToEntityTest {

        @Test
        void returnsAccountEntityFromAccountRequestDto(){
            Account account = AccountMapper.requestToEntity(givenUserAccountRequestDto().orElseThrow());
            assertThat(account.getEmail()).isEqualTo("erikuser@gmail.com");
            assertThat(account.getPassword()).isEqualTo("12345");
        }

        @Test
        void shouldThrowExceptionWhenFieldIsNull(){
            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> AccountMapper.requestToEntity(null));
        }

    }

    @Nested
    class EntityToResponseTest {

        @Test
        void returnsAccountUserResponseDtoFromAccountEntity(){
            AccountUserResponseDto accountDto = (AccountUserResponseDto) AccountMapper.entityToResponse(givenAccountEntityUser().orElseThrow());
            assertThat(accountDto.getEmail()).isEqualTo("user@gmail.com");
        }

        @Test
        void shouldThrowExceptionWhenAccountEntityIsNull(){
            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> AccountMapper.entityToResponse(null));
        }
        
    }

    @Nested
    class EntityToAdminResponseTest {

        @Test
        void returnAccountAdminResponseDtoFromAccountEntity() {
            Account accountEntity = givenAccountEntityAdmin().orElseThrow();
            //Each account has user account
            accountEntity.getRoles().stream().forEach(role -> {
                role.setAccounts(List.of(givenAccountEntityUser().orElseThrow()));
            });
            //Only admin role has both admin and user accounts
            accountEntity.getRoles().stream().filter(role -> role.getName().equals("ROLE_ADMIN"))
                .forEach(role -> role.setAccounts(List.of(givenAccountEntityAdmin().orElseThrow(), givenAccountEntityUser().orElseThrow())));

            AccountAdminResponseDto expectedResponse = (AccountAdminResponseDto) AccountMapper.entityToAdminResponse(accountEntity);

            //Check role response dto embedded attribute
            List<RoleResponseDto> expectedRoles = expectedResponse.getRoles();

            assertThat(expectedRoles).hasSize(2);
            assertThat(expectedRoles).contains(new RoleResponseDto(null, "ROLE_USER"));
            assertThat(expectedRoles).contains(new RoleResponseDto(null, "ROLE_ADMIN"));

            //assert that it role has list of accounts(idk why i did that)
            RoleResponseDto expectedRoleUser = expectedRoles.stream().filter(role -> role.getName().equals("ROLE_USER")).findFirst().orElseThrow();
            RoleResponseDto expectedRoleAdmin = expectedRoles.stream().filter(role -> role.getName().equals("ROLE_ADMIN")).findFirst().orElseThrow();

            assertThat(expectedRoleUser.getAccounts()).hasSize(1);
            assertThat(expectedRoleUser.getAccounts()).extracting("email").doesNotContain("admin@gmail.com");
            assertThat(expectedRoleUser.getAccounts()).extracting("email").contains("user@gmail.com");

            assertThat(expectedRoleAdmin.getAccounts()).hasSize(2);
            assertThat(expectedRoleAdmin.getAccounts()).extracting("email").contains("admin@gmail.com");
            assertThat(expectedRoleAdmin.getAccounts()).extracting("email").contains("user@gmail.com");

            //Person
            assertThat(expectedResponse.getPersonId()).isEqualTo(accountEntity.getPerson().getId());

            //Not embedded fields tests
            assertThat(expectedResponse.getId()).isEqualTo(accountEntity.getId());
            assertThat(expectedResponse.getEmail()).isEqualTo(accountEntity.getEmail());
            assertThat(expectedResponse.getAudit()).isEqualTo(accountEntity.getAudit());

        }

        @Test
        void shouldThrowExceptionWhenAccountEntityIsNull(){
            assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> AccountMapper.entityToAdminResponse(null));
        }

    }



    
}
