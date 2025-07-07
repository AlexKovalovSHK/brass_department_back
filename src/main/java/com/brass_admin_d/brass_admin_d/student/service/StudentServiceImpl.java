package com.brass_admin_d.brass_admin_d.student.service;

import com.brass_admin_d.brass_admin_d.department.dao.DepartmentRepository;
import com.brass_admin_d.brass_admin_d.department.model.Department;
import com.brass_admin_d.brass_admin_d.group.model.Group;
import com.brass_admin_d.brass_admin_d.group.dao.GroupRepository;
import com.brass_admin_d.brass_admin_d.student.dto.NewStudentDto;
import com.brass_admin_d.brass_admin_d.student.dto.StudentDto;
import com.brass_admin_d.brass_admin_d.student.model.Student;
import com.brass_admin_d.brass_admin_d.student.dao.StudentRepository;
import com.brass_admin_d.brass_admin_d.user.dao.RoleRepository;
import com.brass_admin_d.brass_admin_d.user.dto.NewUserDto;
import com.brass_admin_d.brass_admin_d.user.dto.UserDto;
import com.brass_admin_d.brass_admin_d.user.model.Role;
import com.brass_admin_d.brass_admin_d.user.model.User;
import com.brass_admin_d.brass_admin_d.user.dao.UserRepository;
import com.brass_admin_d.brass_admin_d.user.service.UserAccountServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public StudentDto addStudent(NewStudentDto studentDto) {
        // 1. Проверка на дубликат записи Student (остается без изменений)
        studentRepository.findByNumberBook(studentDto.getNumberBook()).ifPresent(s -> {
            throw new IllegalArgumentException("Student record with number book " + studentDto.getNumberBook() + " already exists.");
        });

        // 2. Логика "Найти или Создать" для Пользователя (User)
        User user = userRepository.findByNumberBook(studentDto.getNumberBook())
                .orElseGet(() -> {
                    // ---- Блок "Создать", если пользователь не найден ----
                    System.out.println("User with number book " + studentDto.getNumberBook() + " not found. Creating a new one.");

                    // --- РУЧНАЯ ПРОВЕРКА УНИКАЛЬНОСТИ ---
                    // Поскольку numberBook должен быть уникальным для студентов, мы должны проверить это здесь.
                    // Эта проверка не нужна в блоке "Найти", т.к. мы бы уже нашли пользователя на шаге выше.
                    // Но она КРИТИЧЕСКИ важна в блоке "Создать".
                    if (studentDto.getNumberBook() != null && !studentDto.getNumberBook().isEmpty()) {
                        userRepository.findByNumberBook(studentDto.getNumberBook()).ifPresent(u -> {
                            // Эта ситуация не должна произойти, если логика "Найти" отработала, но для безопасности оставляем.
                            throw new IllegalArgumentException("A user with number book " + studentDto.getNumberBook() + " already exists.");
                        });
                    }

                    // Проверяем, не занят ли email (это отдельная проверка)
                    userRepository.findByUsernameIgnoreCase(studentDto.getEmail()).ifPresent(u -> {
                        throw new IllegalArgumentException("User with email " + studentDto.getEmail() + " already exists.");
                    });
                    // --- КОНЕЦ РУЧНОЙ ПРОВЕРКИ ---


                    Role userRole = roleRepository.findByName("USER")
                            .orElseThrow(() -> new IllegalStateException("Role 'USER' not found in database."));

                    Department department = departmentRepository.findById(studentDto.getDepartmentId())
                            .orElseThrow(() -> new EntityNotFoundException("Department not found with id: " + studentDto.getDepartmentId()));

                    User newUser = new User();
                    newUser.setUsername(studentDto.getEmail());
                    newUser.setPassword(passwordEncoder.encode("brass"));
                    newUser.addRole(userRole);
                    newUser.setDepartment(department);
                    newUser.setNumberBook(studentDto.getNumberBook()); // Присваиваем номер

                    return userRepository.save(newUser);
                });

        // ... остальная часть метода (создание и сохранение Student) без изменений ...
        Group group = groupRepository.findById(studentDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + studentDto.getGroupId()));

        Student student = toEntity(studentDto);
        student.setUser(user);
        student.setGroup(group);

        Student savedStudent = studentRepository.save(student);
        return toDto(savedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return toDto(student);
    }

    @Override
    @Transactional
    public Boolean removeStudentById(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }
        studentRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByGroupId(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("Group not found with id: " + id);
        }
        return studentRepository.findByGroupId(id).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsBySessionId(Long id) {
        return studentRepository.findBySessionId(id).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Iterable<StudentDto> getStudentsBySessionLocal(Long globalSessionId, Integer num) {
        return studentRepository.findBySessionId(globalSessionId).stream()
                .filter(student -> student.getSessionNum().equals(num))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getStudentsByDepartmentId(Long id) {
        return studentRepository.findByDepartmentId(id).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    // =================================================================
    // ПРИВАТНЫЕ МЕТОДЫ-ХЕЛПЕРЫ
    // =================================================================

    private static Student toEntity(NewStudentDto dto) {
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setFirstNameRu(dto.getFirstNameRu());
        student.setLastNameRu(dto.getLastNameRu());
        student.setSessionNum(dto.getSessionNum());
        student.setDateBirth(dto.getDateBirth());
        student.setDateBaptism(dto.getDateBaptism());
        student.setDateReceipt(dto.getDateReceipt());
        student.setDateEnd(dto.getDateEnd());
        student.setTel(dto.getTel());
        student.setInstrument(dto.getInstrument());
        student.setSpeciality(dto.getSpeciality());
        student.setEmail(dto.getEmail());
        student.setNumberBook(dto.getNumberBook());
        student.setNote(dto.getNote());
        return student;
    }

    private StudentDto toDto(Student student) {
        Group group = groupRepository.findById(student.getGroup().getId()).orElseThrow(() -> new RuntimeException("Error!"));
        StudentDto dto = new StudentDto();
        dto.setId(student.getId());
        dto.setUserId(Long.valueOf(student.getUser() != null ? student.getUser().getId() : null));
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setFirstNameRu(student.getFirstNameRu());
        dto.setLastNameRu(student.getLastNameRu());
        dto.setSessionNum(student.getSessionNum());
        dto.setDateBirth(student.getDateBirth());
        dto.setDateBaptism(student.getDateBaptism());
        dto.setDateReceipt(student.getDateReceipt());
        dto.setDateEnd(student.getDateEnd());
        dto.setTel(student.getTel());
        dto.setInstrument(student.getInstrument());
        dto.setSpeciality(student.getSpeciality());
        dto.setEmail(student.getEmail());
        dto.setNumberBook(student.getNumberBook());
        dto.setNote(student.getNote());
        dto.setGroupNum(group.getGroupNumber());
        return dto;
    }
}