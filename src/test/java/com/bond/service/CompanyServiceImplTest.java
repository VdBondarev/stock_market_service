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
import com.bond.dto.company.CreateCompanyRequestDto;
import com.bond.mapper.CompanyMapper;
import com.bond.model.Company;
import com.bond.model.User;
import com.bond.repository.CompanyRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

import javax.persistence.EntityNotFoundException;

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
    @DisplayName("Verify that exception is thrown when company already exists")
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
                .setRegistrationNumber("test");
    }
}
