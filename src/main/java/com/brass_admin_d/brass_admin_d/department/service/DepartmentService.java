package com.brass_admin_d.brass_admin_d.department.service;

import com.brass_admin_d.brass_admin_d.department.dto.DepartmentDto;
import com.brass_admin_d.brass_admin_d.department.dto.NewDepartmentDto;
import com.brass_admin_d.brass_admin_d.department.model.Department;
import com.brass_admin_d.brass_admin_d.department.dao.DepartmentRepository;
import com.brass_admin_d.brass_admin_d.user.dao.RoleRepository;
import com.brass_admin_d.brass_admin_d.user.dao.UserRepository;
import com.brass_admin_d.brass_admin_d.user.model.Role;
import com.brass_admin_d.brass_admin_d.user.model.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Автоматически создает конструктор для final полей (Lombok)
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper departmentMapper;

    /**
     * Создает новый департамент.
     * @param requestDto DTO с данными для создания.
     * @return DTO созданного департамента.
     */
    @Transactional
    public DepartmentDto createDepartment(NewDepartmentDto requestDto) {
        departmentRepository.findByDepartName(requestDto.getDepartName()).ifPresent(d -> {
            throw new IllegalArgumentException("Department with name '" + requestDto.getDepartName() + "' already exists.");
        });

        // 1. СНАЧАЛА создаем и сохраняем отдел, чтобы он получил ID и стал 'persistent'
        Department department = new Department();
        department.setDepartName(requestDto.getDepartName());
        department.setCreationDate(LocalDate.now());
        Department savedDepartment = departmentRepository.save(department);

        // 2. Создаем админа
        User adminUser = createDefaultAdminForDepartment(savedDepartment);

        // 3. --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ ---
        // Синхронизируем связь с ОБЕИХ сторон, используя наш вспомогательный метод.
        // Это установит и user.setDepartment(), и добавит user в department.getUsers().
        savedDepartment.addUser(adminUser);

        // 4. Явно сохраняем пользователя. Теперь Hibernate точно знает все о связи.
        userRepository.save(adminUser);

        return departmentMapper.map(savedDepartment, DepartmentDto.class);
    }

    /**
     * Получает департамент по его ID.
     * @param id Уникальный идентификатор департамента.
     * @return DTO найденного департамента.
     * @throws EntityNotFoundException если департамент не найден.
     */
    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + id));
        return departmentMapper.map(department, DepartmentDto.class);
    }

    /**
     * Получает список всех департаментов.
     * @return Список DTO всех департаментов.
     */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(d -> departmentMapper.map(d, DepartmentDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Удаляет департамент по его ID.
     * @param id ID департамента для удаления.
     * @throws EntityNotFoundException если департамент не найден.
     */
    @Transactional
    public Boolean deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
        return true;
    }

    /**
     * Приватный вспомогательный метод для создания пользователя-администратора.
     * @param department Департамент, к которому привязывается администратор.
     * @return Созданный, но еще не сохраненный объект User.
     */
    private User createDefaultAdminForDepartment(Department department) { // Теперь department - persistent
        Role adminRole = roleRepository.findByName("ADMINISTRATOR")
                .orElseThrow(() -> new IllegalStateException("Role 'ADMINISTRATOR' not found in the database. Please, seed the roles table."));

        User admin = new User();
        String username = "admin_" + department.getDepartName().toLowerCase().replaceAll("\\s+", "_");
        admin.setUsername(username);

        String rawPassword = "admin";
        System.out.println("Generated password for user '" + username + "': " + rawPassword);

        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRoles(Set.of(adminRole));
        admin.setDateRegistered(LocalDate.now());

        // --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ ---
        // Устанавливаем связь с уже сохраненным отделом.
        admin.setDepartment(department);

        return admin;
    }
}