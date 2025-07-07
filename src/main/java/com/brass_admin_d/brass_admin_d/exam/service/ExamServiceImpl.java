package com.brass_admin_d.brass_admin_d.exam.service;

import com.brass_admin_d.brass_admin_d.exam.dao.ExamRepository;
import com.brass_admin_d.brass_admin_d.exam.dao.ExamResultRepository;
import com.brass_admin_d.brass_admin_d.exam.dto.ExamDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ExamResultDto;
import com.brass_admin_d.brass_admin_d.exam.dto.NewExamDto;
import com.brass_admin_d.brass_admin_d.exam.dto.ScoreDto;
import com.brass_admin_d.brass_admin_d.exam.model.Exam;
import com.brass_admin_d.brass_admin_d.exam.model.ExamResult;
import com.brass_admin_d.brass_admin_d.exam.model.ExamStatus;
import com.brass_admin_d.brass_admin_d.session.dto.GlobalSessionsDto;
import com.brass_admin_d.brass_admin_d.session.model.SessionsGlobal;
import com.brass_admin_d.brass_admin_d.session.dao.SessionsGlobalRepository;
import com.brass_admin_d.brass_admin_d.student.dao.StudentRepository;
import com.brass_admin_d.brass_admin_d.student.model.Student;
import com.brass_admin_d.brass_admin_d.subjects.dto.SubjectDto;
import com.brass_admin_d.brass_admin_d.subjects.model.Subject;
import com.brass_admin_d.brass_admin_d.subjects.dao.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    // Добавляем логгер
    private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final SessionsGlobalRepository sessionsGlobalRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional // Очень важно! Операция должна быть атомарной.
    public ExamDto addExam(NewExamDto newExamDto) {
        // 1. Получаем связанные сущности
        Subject subject = subjectRepository.findById(newExamDto.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + newExamDto.getSubjectId()));

        SessionsGlobal session = sessionsGlobalRepository.findById(newExamDto.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session not found with id: " + newExamDto.getSessionId()));

        // 2. Проверка на дубликат экзамена (логика оставлена как есть)
        examRepository.findBySessionIdAndSubjectIdAndSessionNumAndExamName(
                session.getId(), subject.getId(), newExamDto.getSessionNum(), newExamDto.getExamName()
        ).ifPresent(e -> {
            throw new IllegalArgumentException("This exam already exists in the specified session.");
        });

        // 3. Создаем и сохраняем сам экзамен. Он должен быть сохранен ПЕРЕД созданием результатов, чтобы получить ID.
        Exam newExam = new Exam();
        // Убедитесь, что ваша сущность Exam имеет эти сеттеры и поля
        newExam.setSubject(subject);
        newExam.setSession(session);
        // Предполагаю, что в Exam есть поля examName и examNameRu, как в вашем коде. По схеме их нет (там academic_subject).
        newExam.setExamName(newExamDto.getExamName());
        newExam.setExamNameRu(newExamDto.getExamNameRu());
        newExam.setSessionNum(newExamDto.getSessionNum());
        newExam.setExamDate(LocalDate.now()); // Устанавливаем текущую дату

        Exam savedExam = examRepository.save(newExam);

        // 4. Находим всех студентов, для которых предназначен этот экзамен
        List<Student> studentsForExam = studentRepository.findByGroup_Session_IdAndSessionNum(
                savedExam.getSession().getId(), // ID сессии из сохраненного экзамена
                savedExam.getSessionNum()       // Номер курса из сохраненного экзамена
        );

        // ЛОГИРУЕМ РЕЗУЛЬТАТ ПОИСКА
        if (studentsForExam.isEmpty()) {
            log.warn("No students found for this exam! ExamResult records will not be created.");
        } else {
            log.info("Found {} students to create exam results for. Student IDs: {}",
                    studentsForExam.size(),
                    studentsForExam.stream().map(Student::getId).collect(Collectors.toList()));
        }

        // 5. Создаем ExamResult для каждого найденного студента
        List<ExamResult> examResults = studentsForExam.stream()
                .map(student -> {
                    ExamResult result = new ExamResult();
                    result.setExam(savedExam);
                    result.setStudent(student);
                    result.setScore(null); // Изначально оценка отсутствует
                    result.setExamStatus(ExamStatus.ABSENT); // Логично установить статус "Не явился" по умолчанию
                    return result;
                })
                .collect(Collectors.toList());

        // 6. Сохраняем все созданные результаты одним пакетом
        examResultRepository.saveAll(examResults);

        // 7. Возвращаем DTO созданного экзамена
        return examToDto(savedExam); // Предполагается, что у вас есть этот метод-маппер
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + id));
        return examToDto(exam);
    }

    @Override
    @Transactional
    public Boolean deleteExamById(Long id) {
        if (!examRepository.existsById(id)) {
            return false;
        }
        examRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream()
                .map(this::examToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsBySessionId(Long id) {
        return examRepository.findBySessionId(id).stream()
                .map(this::examToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsBySessionIdAndSubjectId(Long sessionId, Long subjectId) {
        return examRepository.findBySessionIdAndSubjectId(sessionId, subjectId).stream()
                .map(this::examToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDto> getExamsBySessionIdAndGroupId(Long sessionId, Long groupId) {
        // Этот метод использует кастомный JPQL-запрос в репозитории
        return examRepository.findBySessionIdAndGroupId(sessionId, groupId).stream()
                .map(this::examToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResultDto updateResult(ScoreDto dto) {
        ExamResult result = examResultRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Not found"));

        result.setScore(dto.getScore());

        if(dto.getScore() > 3) {
            result.setExamStatus(ExamStatus.PASSED);
        } else {
            result.setExamStatus(ExamStatus.FAILED);
        }
        result.setTeachersName(dto.getTeachersName());
        result.setTeachersNameRu(dto.getTeachersNameRu());

        // save может быть опущен, если Entity в Persistence Context (но оставить можно для явности)
        examResultRepository.save(result);

        return mapToExamResDto(result);
    }


    // =================================================================
    // ПРИВАТНЫЕ МЕТОДЫ-ХЕЛПЕРЫ ДЛЯ ПРЕОБРАЗОВАНИЯ В DTO
    // =================================================================

    private ExamDto examToDto(Exam exam) {
        if (exam == null) return null;


        ExamDto dto = new ExamDto();
        dto.setId(exam.getId());
        dto.setExamDate(exam.getExamDate());
        dto.setSessionNum(exam.getSessionNum());
        dto.setSubjectId(exam.getSubject().getId());
        dto.setExamName(exam.getSubject().getName());
        dto.setExamNameRu(exam.getSubject().getNameRu());
        dto.setSessionId(exam.getSession().getId());
        return dto;
    }

    public ExamResultDto mapToExamResDto(ExamResult result) {
        ExamResultDto dto = new ExamResultDto();

        dto.setId(result.getId());
        dto.setExamId(result.getExam() != null ? result.getExam().getId() : null);
        dto.setExamName(result.getExam() != null ? result.getExam().getExamName() : null); // предполагаю, что в Exam есть поле name
        dto.setExamNameRu(result.getExam() != null ? result.getExam().getExamNameRu() : null); // предполагаю, что в Exam есть поле name
        dto.setStudentId(result.getStudent() != null ? result.getStudent().getId() : null);
        dto.setScore(result.getScore());
        dto.setTeachersName(result.getTeachersName());
        dto.setTeachersNameRu(result.getTeachersNameRu());
        dto.setExamStatus(result.getExamStatus() != null ? result.getExamStatus().name() : null);

        return dto;
    }


}