package com.bond.service;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.bond.dto.company.CompanyResponseDto;
import com.bond.dto.company.CompanyUpdateRequestDto;
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.mapper.CompanyMapper;
import com.bond.model.Company;
import com.bond.model.Role;
import com.bond.model.User;
import com.bond.repository.CompanyRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyMapper companyMapper;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyServiceImpl;

    @Test
    @DisplayName("Verify that getAll() method works as expected")
    public void getAll_ValidPageable_ReturnsValidList() {
        // just mocker companies
        Company firstCompany = createCompany(UUID.randomUUID());
        Company secondCompany = createCompany(UUID.randomUUID());
        Company thirdCompany = createCompany(UUID.randomUUID());

        List<Company> companyList = new ArrayList<>();
        companyList.add(firstCompany);
        companyList.add(secondCompany);
        companyList.add(thirdCompany);

        Pageable pageable = PageRequest.of(0, 5);

        Page<Company> companyPage;
        companyPage = new PageImpl<>(companyList, pageable, companyList.size());

        CompanyResponseDto firstResponseDto = createResponseDto(firstCompany);
        CompanyResponseDto secondResponseDto = createResponseDto(secondCompany);
        CompanyResponseDto thirdResponseDto = createResponseDto(thirdCompany);

        List<CompanyResponseDto> expectedList = new ArrayList<>();
        expectedList.add(firstResponseDto);
        expectedList.add(secondResponseDto);
        expectedList.add(thirdResponseDto);

        when(companyRepository.findAll(pageable)).thenReturn(companyPage);
        when(companyMapper.toResponseDto(firstCompany)).thenReturn(firstResponseDto);
        when(companyMapper.toResponseDto(secondCompany)).thenReturn(secondResponseDto);
        when(companyMapper.toResponseDto(thirdCompany)).thenReturn(thirdResponseDto);

        List<CompanyResponseDto> actualList = companyServiceImpl.getAll(pageable);

        assertEquals(expectedList, actualList);

        verify(companyMapper, times(3)).toResponseDto(any());
        verifyNoMoreInteractions(companyMapper);
    }

    @Test
    @DisplayName("Verify that create() method works as expected with valid input")
    public void create_ValidInput_ReturnsValidCompany() {
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto();
        requestDto.setAddress("Test Address");
        requestDto.setName("Test Name");
        requestDto.setRegistrationNumber("Test Registration Number");

        User user = new User()
                .setId(1L);

        Company company = createCompanyFromRequestDto(requestDto, user.getId());

        CompanyResponseDto expectedResponseDto = createResponseDto(company);

        when(companyRepository.findByName(requestDto.getName())).thenReturn(Optional.empty());
        when(companyRepository.findByRegistrationNumber(requestDto.getRegistrationNumber()))
                .thenReturn(Optional.empty());
        when(companyMapper.toModel(requestDto)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(companyMapper.toResponseDto(company)).thenReturn(expectedResponseDto);

        CompanyResponseDto actual = companyServiceImpl.create(requestDto, user);

        assertEquals(expectedResponseDto, actual);

        verify(companyRepository, times(1)).findByName(requestDto.getName());
        verify(companyRepository, times(1))
                .findByRegistrationNumber(requestDto.getRegistrationNumber());
        verify(companyRepository, times(1)).save(company);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    @DisplayName("Verify that create() method works as expected when company already exists")
    public void create_PassedCompanyAlreadyExists_ThrowsException() {
        CreateCompanyRequestDto requestDto = new CreateCompanyRequestDto();
        requestDto.setAddress("Existing Test Address");
        requestDto.setName("Existing Test Name");
        requestDto.setRegistrationNumber("Existing Test Registration Number");

        Company company = createCompanyFromRequestDto(requestDto, 1L);

        when(companyRepository.findByName(requestDto.getName())).thenReturn(Optional.of(company));
        when(companyRepository.findByRegistrationNumber(requestDto.getRegistrationNumber()))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> companyServiceImpl.create(requestDto, new User())
        );

        String expectedMessage =
                "A company with the specified name or registration number already exists";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);

        when(companyRepository.findByName(requestDto.getName())).thenReturn(Optional.empty());
        when(companyRepository.findByRegistrationNumber(requestDto.getRegistrationNumber()))
                .thenReturn(Optional.of(company));

        exception = assertThrows(
                IllegalArgumentException.class,
                () -> companyServiceImpl.create(requestDto, new User())
        );

        actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that getById() method works as expected with a valid id")
    public void getById_ValidId_ReturnsValidResponseDto() {
        UUID id = UUID.randomUUID();

        Company company = createCompany(id);

        CompanyResponseDto expectedResponseDto = createResponseDto(company);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyMapper.toResponseDto(company)).thenReturn(expectedResponseDto);

        CompanyResponseDto actualResponseDto = companyServiceImpl.getById(id);

        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Test
    @DisplayName("Verify that getById() method throws an exception when a non-valid id is passed")
    public void getById_NonValidPassedId_ThrowsException() {
        UUID id = UUID.randomUUID();

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class, () -> companyServiceImpl.getById(id)
        );

        String expectedMessage = "Company with id " + id + " not found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that update() method works as expected with valid input")
    public void update_ValidInput_ReturnsValidResponse() {
        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto();
        requestDto.setAddress("New Test Address");
        requestDto.setName("New Test Name");

        User user = new User()
                .setId(1L);

        UUID id = UUID.randomUUID();

        Company company = createCompany(id);
        company.setOwnerId(user.getId());

        Company updatedCompany = createCompany(id);
        updatedCompany.setOwnerId(user.getId());
        updatedCompany.setAddress(requestDto.getAddress());
        updatedCompany.setName(requestDto.getName());

        CompanyResponseDto expectedResponseDto = createResponseDto(company);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyRepository.findByName(requestDto.getName())).thenReturn(Optional.empty());
        when(companyMapper.updateModel(company, requestDto)).thenReturn(updatedCompany);
        when(companyMapper.toResponseDto(updatedCompany)).thenReturn(expectedResponseDto);
        when(companyRepository.save(updatedCompany)).thenReturn(updatedCompany);

        CompanyResponseDto actualResponseDto = companyServiceImpl.update(id, requestDto, user);

        assertEquals(expectedResponseDto, actualResponseDto);

        verify(companyRepository, times(1)).findById(id);
        verify(companyRepository, times(1)).findByName(requestDto.getName());
        verify(companyRepository, times(1)).save(updatedCompany);
    }

    @Test
    @DisplayName(
            "Verify that update() method works as expected when name-to-update already exists"
    )
    public void update_NonValidNameToUpdate_ThrowsException() {
        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto();
        requestDto.setName("test");

        UUID id = UUID.randomUUID();

        Company companyToUpdate = createCompany(id);
        companyToUpdate.setName("Test name");

        Company existingCompany = createCompany(UUID.randomUUID());

        User user = new User()
                .setId(1L);

        when(companyRepository.findById(id)).thenReturn(Optional.of(companyToUpdate));
        when(companyRepository.findByName(requestDto.getName()))
                .thenReturn(Optional.of(existingCompany));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> companyServiceImpl.update(id, requestDto, user)
        );

        String expectedMessage = "Company with name " + requestDto.getName() + " already exists";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Verify that update() method works as expected when passing non-valid id")
    public void update_NonValidId_ThrowsException() {
        UUID id = UUID.randomUUID();

        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto();
        requestDto.setName("Random name");

        User user = new User()
                .setId(1L);

        when(companyRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> companyServiceImpl.update(id, requestDto, user)
        );

        String expectedMessage = "Company with id " + id + " not found";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName(
            "Verify that update() method works as expected "
                    + "when user is not allowed to update a company's info "
                    + "(not a admin or a company's owner)"
    )
    public void update_NonValidUser_ThrowsException() {
        CompanyUpdateRequestDto requestDto = new CompanyUpdateRequestDto();
        requestDto.setName("Random name");

        // user is not an admin or a company's owner
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(1L));
        User user = new User();
        user.setId(1251661L)
                .setRoles(roles);

        UUID id = UUID.randomUUID();

        Company company = createCompany(id);
        company.setOwnerId(1L);

        when(companyRepository.findById(id)).thenReturn(Optional.of(company));
        when(companyRepository.findByName(requestDto.getName())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> companyServiceImpl.update(id, requestDto, user)
        );

        String expectedMessage = "You do not have permission to update this company";
        String actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    private Company createCompanyFromRequestDto(CreateCompanyRequestDto requestDto, Long userId) {
        return new Company()
                .setId(UUID.randomUUID())
                .setCreatedAt(now())
                .setName(requestDto.getName())
                .setRegistrationNumber(requestDto.getRegistrationNumber())
                .setAddress(requestDto.getAddress())
                .setOwnerId(userId);
    }

    private CompanyResponseDto createResponseDto(Company company) {
        return new CompanyResponseDto()
                .setId(company.getId())
                .setName(company.getName())
                .setAddress(company.getAddress())
                .setCreatedAt(company.getCreatedAt())
                .setOwnerId(company.getOwnerId())
                .setRegistrationNumber(company.getRegistrationNumber());
    }

    private Company createCompany(UUID uuid) {
        return new Company()
                .setId(uuid)
                .setName("test")
                .setCreatedAt(now())
                .setAddress("test")
                .setRegistrationNumber("test")
                .setOwnerId(1L);
    }
}

