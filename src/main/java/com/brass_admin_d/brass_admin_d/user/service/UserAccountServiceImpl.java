package com.brass_admin_d.brass_admin_d.user.service;

import com.brass_admin_d.brass_admin_d.department.dao.DepartmentRepository;
import com.brass_admin_d.brass_admin_d.department.model.Department;
import com.brass_admin_d.brass_admin_d.user.dao.RoleRepository;
import com.brass_admin_d.brass_admin_d.user.dao.UserRepository;
import com.brass_admin_d.brass_admin_d.user.dto.ChangeRoleResponseDto;
import com.brass_admin_d.brass_admin_d.user.dto.NewUserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserLoginRequestDto;
import com.brass_admin_d.brass_admin_d.user.dto.exceptions.BadCredentialsOnLoginException;
import com.brass_admin_d.brass_admin_d.user.dto.exceptions.RoleNotFoundException;
import com.brass_admin_d.brass_admin_d.user.dto.exceptions.UserNotFoundException;
import com.brass_admin_d.brass_admin_d.user.model.Role;
import com.brass_admin_d.brass_admin_d.user.model.User;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Order(10)
public class UserAccountServiceImpl implements UserAccountService, CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto loginUser(UserLoginRequestDto data) {

        Optional<User> userAccount = userRepository.findByUsernameIgnoreCase(data.getUsername());

        if (userAccount.isPresent()) {
            try {
                User user = userAccount.get();
                boolean isPasswordsMatch = passwordEncoder.matches(data.getPassword(), user.getPassword());
                if (isPasswordsMatch) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user.getUsername(),
                            passwordEncoder.encode(user.getPassword())
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    return modelMapper.map(user, UserDto.class);

                } else {
                    throw new BadCredentialsOnLoginException();
                }

            } catch (BadCredentialsException e) {
                throw new BadCredentialsOnLoginException();
            }
        }

        throw new BadCredentialsOnLoginException();

    }

    @Override
    public List<Cookie> logoutUser() {
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(resetCookie("access-token"));
        cookies.add(resetCookie("refresh-token"));
        return cookies;
    }

    @Override
    public UserDto findUserByUsername(String username) {
        User userAccount = userRepository.findByUsernameIgnoreCase(username).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public UserDto createUser(NewUserDto user) {
        try {
            Optional<User> existingUser = userRepository.findByUsernameIgnoreCase(user.getUsername());
            if (existingUser.isPresent()) {
                throw new RuntimeException("User with username " + user.getUsername() + " already exists.");
            }

            // Преобразование DTO в Entity
            User newUser = modelMapper.map(user, User.class);
            Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role USER not found"));
            newUser.addRole(userRole);
            String password = passwordEncoder.encode(user.getPassword());
            newUser.setPassword(password);

            // Сохранение пользователя
            User savedUser = userRepository.save(newUser);

            // Преобразование Entity в DTO и возврат
            return modelMapper.map(savedUser, UserDto.class);
        } catch (Exception e) {
            // Логирование ошибки (можно использовать любой логгер)
            System.err.println("Error creating user: " + e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    public UserDto findUserById(Integer userId) {
        User userAccount = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Transactional
    @Override
    public UserDto deleteUserById(Integer userId) {
        User userAccount = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(userId);
        return modelMapper.map(userAccount, UserDto.class);
    }

    @Override
    public Iterable<UserDto> findAll(PageRequest pageRequest) {
        List<User> userAccounts = userRepository.findAll();
        return userAccounts.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public ChangeRoleResponseDto changeRoleList(Integer userId, String roleName, Boolean isAddRole) {
        User userAccount = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        Role role = roleRepository.findByName(roleName.toUpperCase()).orElseThrow(RoleNotFoundException::new);
        boolean res;
        if (isAddRole) {
            res = userAccount.addRole(role);
        } else {
            res = userAccount.removeRole(role);
        }
        if (res) {
            userRepository.save(userAccount);
        }
        return modelMapper.map(userAccount, ChangeRoleResponseDto.class);
    }

    @Override
    public void changePassword(Integer userId, String password) {
        User userAccount = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userAccount.setPassword(passwordEncoder.encode(password));
        userRepository.save(userAccount);
    }

    @Override
    public void uploadAvatar(Integer userId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
    }

    @Override
    public void removeAvatar(Integer userId) {

    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // Сначала убедимся, что роли существуют
        if (roleRepository.findByName("SUPER_ADMINISTRATOR").isEmpty()) {
            roleRepository.save(new Role(1, "SUPER_ADMINISTRATOR"));
        }
        if (roleRepository.findByName("ADMINISTRATOR").isEmpty()) {
            roleRepository.save(new Role(2, "ADMINISTRATOR"));
        }
        if (roleRepository.findByName("USER").isEmpty()) {
            roleRepository.save(new Role(3, "USER"));
        }

        // 2. Создаем "главный" отдел, если его нет
        Department masterDepartment = departmentRepository.findByDepartName("System Administration")
                .orElseGet(() -> {
                    Department newMaster = new Department();
                    newMaster.setDepartName("System Administration");
                    newMaster.setCreationDate(LocalDate.now());
                    return departmentRepository.save(newMaster);
                });

        // 3. Создаем супер-администратора, если его нет
        if (!userRepository.existsByUsernameIgnoreCase("superadmin")) {
            User superAdmin = new User();
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("admin")); // Используйте надежный пароль!
            superAdmin.setDateRegistered(LocalDate.now());

            // Привязываем к главному отделу
            superAdmin.setDepartment(masterDepartment);

            // Даем ему роль СУПЕР АДМИНА
            Role superAdminRole = roleRepository.findByName("SUPER_ADMINISTRATOR").get();
            superAdmin.getRoles().add(superAdminRole);

            userRepository.save(superAdmin);
        }
    }

    private Cookie resetCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

}
