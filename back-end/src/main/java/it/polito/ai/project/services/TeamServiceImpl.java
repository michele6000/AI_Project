package it.polito.ai.project.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.project.dtos.*;
import it.polito.ai.project.entities.*;
import it.polito.ai.project.exceptions.*;
import it.polito.ai.project.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProfessorRepository profRepo;

    @Autowired
    private NotificationService notification;

    @Autowired
    private VMTypeRepository vmTypeRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private SolutionRepository solutionRepository;

    @Autowired
    private VMRepository vmRepo;


    @Override
    public boolean addCourse(CourseDTO course) {
        if (course == null || course.getName().length() == 0) return false;
        Course courseEntity = modelMapper.map(course, Course.class);
        if (courseRepo.findById(courseEntity.getName()).isPresent()) return false;
        courseRepo.save(courseEntity);
        return true;
    }

    @Override
    public StudentDTO addStudent(UserDTO user, MultipartFile file) {
        if (user == null ||
                user.getUsername().length() == 0 ||
                user.getName().length() == 0 ||
                user.getFirstName().length() == 0
        )
            return new StudentDTO();

        if (studentRepo.existsById(user.getUsername()))
            return new StudentDTO();

        User _user = new User();
        _user.setUsername(user.getEmail());
        _user.setPassword(passwordEncoder.encode(user.getPassword()));
        _user.setRoles(Collections.singletonList("ROLE_STUDENT"));

        Student _student = new Student();
        _student.setId(user.getUsername());
        _student.setName(user.getName());
        _student.setFirstName(user.getFirstName());
        _student.setEmail(user.getEmail());

        try{
            Byte[] byteObjects = new Byte[file.getBytes().length];
            int i = 0;
            for (byte b : file.getBytes())
                byteObjects[i++] = b;
            _student.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + file.getName());
        }

        userRepo.save(_user);
        studentRepo.save(_student);

        notification.sendMessage(
                user.getEmail(),
                "New Registration",
                "Username: " + user.getUsername() + " password: " + user.getPassword()
        );
        return modelMapper.map(_student,StudentDTO.class);
    }

    @Override
    public boolean addStudentToCourse(String studentId, String courseName) {
        if (studentId.length() == 0 || courseName.length() == 0)
            return false;
        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");
        if (!courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");

        Course course = courseRepo.getOne(courseName);
        Student student = studentRepo.getOne(studentId);

        if (!course.isEnabled())
            return false;

        Optional<Student> found = course
                .getStudents()
                .stream()
                .filter(s -> s.getId().equals(studentId))
                .findFirst();
        if (found.isPresent()) return false;

        course.addStudent(student);

        return true;
    }

    @Override
    public Optional<CourseDTO> getCourse(String name) {
        if (!courseRepo.findById(name).isPresent()) return Optional.empty();

        return Optional.of(
                modelMapper.map(courseRepo.getOne(name), CourseDTO.class)
        );
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        return courseRepo
                .findAll()
                .stream()
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentDTO> getStudent(String studentId) {
        if (!studentRepo.findById(studentId).isPresent()) return Optional.empty();

        return Optional.of(modelMapper.map(studentRepo.getOne(studentId),StudentDTO.class));
    }

    @Override
    public List<StudentDTO> getAllStudents() {
        return studentRepo
                .findAll()
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getEnrolledStudents(String courseName) {
        if (!courseRepo.findById(courseName).isPresent())
            throw new CourseNotFoundException("Course not found!");

        return courseRepo
                .getOne(courseName)
                .getStudents()
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void enableCourse(String courseName, String username) {

        if (courseRepo.findById(courseName).isPresent()) {
            if(!profRepo.findById(username).isPresent())
                throw new ProfessorNotFoundException("Professor not found!");
            if (!isProfessorCourse(courseName,username))
                throw new TeamServiceException( "You are not a professor of this course!");
            courseRepo
                    .getOne(courseName)
                    .setEnabled(true);
            courseRepo.getOne(courseName).getTeams().forEach(t -> {
                if (t.getMembers().size() == t.getConfirmedStudents().size())
                    setActive(t.getId());
            });

        } else throw new CourseNotFoundException("Course not found!");
    }

    @Override
    public void disableCourse(String courseName, String username) {
        if (courseRepo.findById(courseName).isPresent()) {
            if(!profRepo.findById(username).isPresent())
                throw new ProfessorNotFoundException("Professor not found!");
            if (!isProfessorCourse(courseName,username))
                throw new TeamServiceException( "You are not a professor of this course!");
            courseRepo.getOne(courseName).setEnabled(false);
            courseRepo.getOne(courseName).getTeams().forEach(t -> t.setStatus(0));
            courseRepo.getOne(courseName).getStudents().forEach(s ->
                    notification.sendMessage(s.getEmail(),
                            "Course disabled",
                            "We inform you that the course " + courseName + " has been disabled."));
        } else throw new CourseNotFoundException("Course not found!");
    }

    @Override
    public void deleteCourse(String courseName, String username) {
        if (courseRepo.findById(courseName).isPresent()) {
            if(!profRepo.findById(username).isPresent())
                throw new ProfessorNotFoundException("Professor not found!");
            if (!isProfessorCourse(courseName,username))
                throw new TeamServiceException( "You are not a professor of this course!");

            courseRepo.getOne(courseName).getTeams().forEach(t -> evictTeam(t.getId()));
            profRepo.getOne(username).getCourses().remove(courseRepo.getOne(courseName));
            courseRepo.getOne(courseName).getStudents().forEach(s ->{
                    s.getCourses().remove(courseRepo.getOne(courseName));
                    notification.sendMessage(s.getEmail(),
                            "Course deleted",
                            "We inform you that the course " + courseName + " has been deleted.");
            });
            Optional<VMType> vmType = Optional.ofNullable(courseRepo.getOne(courseName).getVmType());
            if(vmType.isPresent())
                vmTypeRepository.delete(courseRepo.getOne(courseName).getVmType());

            courseRepo.getOne(courseName).getSubmissions().forEach( s -> {
                submissionRepository.getOne(s.getId()).getSolutions().forEach( sol -> {
                    solutionRepository.delete(sol);
                });

                submissionRepository.delete(s);
            });
            courseRepo.delete(courseRepo.getOne(courseName));
        } else throw new CourseNotFoundException("Course not found!");

    }

    @Override
    public void updateCourse(String courseName, CourseDTO course, String username) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        if (!optionalCourseEntity.isPresent()) {
            throw new CourseNotFoundException("Course not found!");
        }

        if(!profRepo.findById(username).isPresent())
            throw new ProfessorNotFoundException("Professor not found!");
        if (!isProfessorCourse(courseName,username))
            throw new TeamServiceException( "You are not a professor of this course!");

        Optional<String> _courseName = Optional.ofNullable(course.getName());
        _courseName.ifPresent(l -> optionalCourseEntity.get().setName(l));

        Optional<String> _acronymous = Optional.ofNullable(course.getAcronymous());
        _acronymous.ifPresent(l -> optionalCourseEntity.get().setAcronymous(l));

        Optional<Integer> _min = Optional.of(course.getMin());
        _min.ifPresent(l -> optionalCourseEntity.get().setMin(l));

        Optional<Integer> _max = Optional.of(course.getMax());
        _max.ifPresent(l -> optionalCourseEntity.get().setMax(l));
        
    }

    @Override
    public List<StudentDTO> addAll(List<StudentDTO> students) {
        List<StudentDTO> studentsAdded = new ArrayList<>();
        students.forEach(s -> {

            UserDTO user = new UserDTO();
            user.setFirstName(s.getFirstName());
            user.setName(s.getName());
            user.setEmail(s.getEmail());
            user.setUsername(s.getId());
            user.setPassword("bestPassw0rd");
            studentsAdded.add(addStudent(user, null));
        });
        return studentsAdded;
    }

    @Override
    public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
        List<Boolean> studentsEnrolled = new ArrayList<>();
        studentIds.forEach(
                s -> {
                    try {
                        studentsEnrolled.add(addStudentToCourse(s, courseName));
                    } catch (StudentNotFoundException | CourseNotFoundException e) {
                        throw e;
                    }
                }
        );
        return studentsEnrolled;
    }

    @Override
    public List<Boolean> addAndEnroll(Reader r, String courseName) {
        CsvToBean<StudentDTO> csvToBean = new CsvToBeanBuilder<StudentDTO>(r)
                .withType(StudentDTO.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        List<StudentDTO> students = csvToBean.parse();

        List<StudentDTO> added = addAll(students);

        List<String> studentIds = students
                .stream()
                .map(StudentDTO::getId)
                .collect(Collectors.toList());
        try {
            List<Boolean> enrolled = enrollAll(studentIds, courseName);
            List<Boolean> addedAndEnrolled = new ArrayList<>();

            for (int i = 0; i < added.size(); i++)
                addedAndEnrolled.add(
                        added.get(i).getId().length() > 0 || enrolled.get(i)
                );

            return addedAndEnrolled;
        } catch (StudentNotFoundException | CourseNotFoundException e) {
            throw e;
        }
    }

    @Override
    public List<CourseDTO> getCourses(String studentId) {
        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        return studentRepo
                .getOne(studentId)
                .getCourses()
                .stream()
                .filter(Course::isEnabled)
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDTO> getTeamsForStudent(String studentId) {
        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Student not found!");

        return studentRepo
                .getOne(studentId)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getMembers(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent())
            throw new TeamNotFoundException("Team not found!");

        return teamRepo
                .getOne(teamId)
                .getMembers()
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getConfirmedStudents(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent())
            throw new TeamNotFoundException("Team not found!");

        return teamRepo
                .getOne(teamId)
                .getConfirmedStudents()
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getPendentStudents(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent())
            throw new TeamNotFoundException("Team not found!");

        return teamRepo
                .getOne(teamId)
                .getPendentStudents()
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO proposeTeam(String courseId, String name, List<String> membersIds, String proposer) {
        if (!courseRepo.findById(courseId).isPresent())
            throw new CourseNotFoundException("Course not found!");
        if (!courseRepo.getOne(courseId).isEnabled())
            throw new CourseDisabledException("Course not enabled!");

        if (membersIds.size() > courseRepo.getOne(courseId).getMax())
            throw new TeamServiceException("Too many members for this team!");

        if (membersIds.size() < courseRepo.getOne(courseId).getMin())
            throw new TeamServiceException("Too few members for this team!");

        if (membersIds.size() > membersIds.stream().distinct().count())
            throw new DuplicatedStudentException("Duplicated members in team proposal!");

        for (String m : membersIds) {
            if (!studentRepo.findById(m).isPresent())
                throw new StudentNotFoundException("Student not found!");

            if (!courseRepo.getOne(courseId).getStudents().contains(studentRepo.getOne(m)))
                throw new StudentNotFoundException("Student not enrolled in this course!");

            courseRepo.getOne(courseId).getTeams().forEach(t -> {
                        if (t.getMembers().contains(studentRepo.getOne(m)))
                            throw new DuplicatedStudentException("Student " + m + " is already member of a team (" + t.getName() + ") for this course!");
                    }
            );
        }

        Team team = new Team();
        team.setLimit_active_instance(0);
        team.setLimit_instance(0);
        team.setLimit_cpu(0);
        team.setLimit_hdd(0);
        team.setLimit_ram(0);
        team.setName(name);
        team.setProposer(proposer);

        Optional<VMType> optionalVMTypeEntity = Optional.ofNullable(courseRepo.getOne(courseId).getVmType());
        optionalVMTypeEntity.ifPresent(team::setVmType);

        membersIds.forEach(
                m -> {
                    team.addMember(studentRepo.getOne(m));
                    team.getPendentStudents().add(studentRepo.getOne(m));
                }

        );
        team.setCourse(courseRepo.getOne(courseId));
        team.setId((teamRepo.save(team).getId()));

        return modelMapper.map(team, TeamDTO.class);
    }


    @Override
    public void addMember(Long teamId, String studentId) {
        if (!teamRepo.findById(teamId).isPresent())
            throw new TeamNotFoundException("Team not found!");

        if (!studentRepo.findById(studentId).isPresent())
            throw new StudentNotFoundException("Team not found!");

        Team team = teamRepo.getOne(teamId);
        Course course = team.getCourse();
        Student student = studentRepo.getOne(studentId);

        if (!course.isEnabled())
            throw new CourseDisabledException("Course not enabled!");
        if (team.getMembers().size() == course.getMax())
            throw new TeamServiceException("Too many members for this team!");

        if (team.getMembers().contains(student))
            throw new DuplicatedStudentException("Student is already a member of this team!");


        if (!course.getStudents().contains(studentRepo.getOne(studentId))
        ) throw new StudentNotFoundException("Student not enrolled in this course!");
        course.getTeams().forEach(t -> {
                    if (t.getMembers().contains(studentRepo.getOne(studentId)))
                        throw new DuplicatedStudentException("Student " + student.getId() + " is already member of a team (" + t.getName() + ") for this course!");
                }
        );

        team.addMember(studentRepo.getOne(studentId));
        teamRepo.save(team);
    }


    @Override
    public List<TeamDTO> getTeamForCourse(String courseName) {
        if (!courseRepo.existsById(courseName)) throw new CourseNotFoundException("Course not found!");

        return courseRepo
                .getOne(courseName)
                .getTeams()
                .stream()
                .map(t -> modelMapper.map(t, TeamDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getStudentsInTeams(String courseName) {
        if (!courseRepo.findById(courseName).isPresent()) throw new CourseNotFoundException("Course not found!");

        return courseRepo
                .getStudentsInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentDTO> getAvailableStudents(String courseName) {
        if (!courseRepo.findById(courseName).isPresent()) throw new CourseNotFoundException("Course not found!");

        return courseRepo
                .getStudentsNotInTeams(courseName)
                .stream()
                .map(s -> modelMapper.map(s,StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void setActive(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent()) throw new TeamNotFoundException("Team not found!");

        teamRepo.getOne(teamId).setStatus(1);
        teamRepo.getOne(teamId).setVmType(teamRepo.getOne(teamId).getCourse().getVmType());
    }

    @Override
    public void evictTeam(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent()) throw new TeamNotFoundException("Team not found!");

        teamRepo
                .getOne(teamId)
                .getVMInstance()
                .forEach(vm -> {
                    teamRepo.getOne(teamId).getMembers().forEach(student -> student.getVms().remove(vm)); //elimino in student_vms
                    teamRepo.getOne(teamId).getVMInstance().remove(vm); //elimino nella lista del team
                    vmRepo.delete(vm); // elimino la vm dalla repo
                });

        teamRepo
                .getOne(teamId)
                .getMembers()
                .forEach(s -> teamRepo.getOne(teamId).removeMember(s)); //cancello tutti i membri
        teamRepo.deleteById(teamId);
    }

    @Override
    public void deleteMember(Long teamId, String studentId) {
        Optional<Team> team = teamRepo.findById(teamId);
        Optional<Student> student = studentRepo.findById(studentId);

        if (!team.isPresent())
            throw new TeamNotFoundException("Team not found!");

        if (!student.isPresent())
            throw new StudentNotFoundException("Student not found!");

        if (!team.get().getMembers().contains(student.get()))
            throw new StudentNotFoundException("Student is not a member of this team!");

        team.get().removeMember(student.get());

        if (team.get().getMembers().size() < team.get().getCourse().getMin())
            evictTeam(teamId);

        if (team.get().getStatus()==0)
            evictTeam(teamId);
    }

    @Override
    public TeamDTO getTeam(Long teamId) {
        if (!teamRepo.findById(teamId).isPresent())
            throw new TeamNotFoundException("Team not found!");

        return modelMapper.map(teamRepo
                .getOne(teamId), TeamDTO.class);
    }

    @Override
    public List<ProfessorDTO> getAllProfessors() {
        return profRepo.findAll()
                .stream()
                .map(professor ->
                    modelMapper
                            .typeMap(Professor.class,ProfessorDTO.class)
                            .map(professor)
                )
                .collect(Collectors.toList());
    }

    @Override
    public ProfessorDTO addProfessor(UserDTO user, MultipartFile file) {
        if (user == null ||
                user.getUsername().length() == 0 ||
                user.getName().length() == 0 ||
                user.getFirstName().length() == 0
        )
            return new ProfessorDTO();

        if (studentRepo.existsById(user.getUsername()))
            return new ProfessorDTO();


        User _user = new User();
        _user.setUsername(user.getEmail());
        _user.setPassword(passwordEncoder.encode(user.getPassword()));
        _user.setRoles(Collections.singletonList("ROLE_PROFESSOR"));

        Professor _professor = new Professor();
        _professor.setId(user.getUsername());
        _professor.setName(user.getName());
        _professor.setFirstName(user.getFirstName());
        _professor.setEmail(user.getEmail());

        try{
            Byte[] byteObjects = new Byte[file.getBytes().length];
            int i = 0;
            for (byte b : file.getBytes())
                byteObjects[i++] = b;
            _professor.setImage(byteObjects);
        } catch (IOException e) {
            throw new TeamServiceException("Error saving image: " + file.getName());
        }

        userRepo.save(_user);
        profRepo.save(_professor);

        notification.sendMessage(
                user.getEmail(),
                "New Registration",
                "Username: " + user.getUsername() + " password: " + user.getPassword()
        );
        return modelMapper.map(_professor,ProfessorDTO.class);
    }

    @Override
    public boolean deleteProfessor(String professorId, String courseName) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        if (!optionalCourseEntity.isPresent()) {
            throw new CourseNotFoundException("Course not found!");
        }

        Optional<Professor> optionalProfessorEntity = profRepo.findById(
                professorId
        );

        if (!optionalProfessorEntity.isPresent()) {
            throw new ProfessorNotFoundException("Professor not found!");
        }
        if (!optionalCourseEntity.get().isEnabled()) {
            throw new CourseDisabledException("Course not enabled");
        }
        optionalCourseEntity.get().getProfessors().remove(optionalProfessorEntity.get());
        profRepo.delete(optionalProfessorEntity.get());
        return true;
    }

    @Override
    public byte[] getImage(String username) {

        Byte[] image = new Byte[0];
        if(username.startsWith("s"))
            image = studentRepo.getOne(username).getImage();
        else if (username.startsWith("d"))
            image = profRepo.getOne(username).getImage();

        int j=0;
        byte[] bytes = new byte[image.length];
        for(Byte b: image)
            bytes[j++] = b;

        return bytes;
    }

    @Override
    public boolean addProfessorToCourse(String professorId, String courseName) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        if (!optionalCourseEntity.isPresent()) {
            throw new CourseNotFoundException("Course not found!");
        }

        Optional<Professor> optionalProfessorEntity = profRepo.findById(
                professorId
        );

        if (!optionalProfessorEntity.isPresent()) {
            throw new ProfessorNotFoundException("Professor not found!");
        }
        if (optionalCourseEntity.get().isEnabled()) {
            optionalCourseEntity.get().addProfessor(optionalProfessorEntity.get());
            return true;
        } else throw new CourseDisabledException("Course not enabled");
    }

    @Override
    public List<ProfessorDTO> getProfessors(String courseName) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        if (!optionalCourseEntity.isPresent())
            throw new CourseNotFoundException("Course not found!");

        return courseRepo
                .getOne(courseName)
                .getProfessors()
                .stream()
                .map(professor ->
                        modelMapper
                                .typeMap(Professor.class,ProfessorDTO.class)
                                .map(professor)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseDTO> getProfessorCourses(String professorId) {
        return courseRepo
                .findAll()
                .stream()
                .filter(c -> c.getProfessors()
                        .stream()
                        .map(Professor::getId)
                        .collect(Collectors.toList())
                        .contains(professorId)
                )
                .map(c -> modelMapper.map(c, CourseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean deleteOne(String studentId, String courseName) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        Optional<Student> optionalStudentEntity = studentRepo.findById(studentId);
        if (!optionalCourseEntity.isPresent())
            throw new CourseNotFoundException("Course not found!");

        if (!optionalStudentEntity.isPresent())
            throw new StudentNotFoundException("Student not found!");

        if (!optionalCourseEntity.get().getStudents().stream().map(Student::getId).collect(Collectors.toList()).contains(studentId))
            throw new StudentNotFoundException("Student not enrolled in this course!");

        optionalCourseEntity.get().deleteStudent(optionalStudentEntity.get());
        return true;

        //TODO:
        // - cancellare tutte le solution di quello studente nel corso
        // - (michele) spostare l'ownership delle vm del team in quel corso a qualcun altro, se c'è più di un owner rimuovere soltanto
        //  if(team.getvms.foreach( vm -> vm.getOwners().stram().count() > 1  // getOwners().remove(studente)
        //  else changeOwner (vmservice) changeOwner(me, team.getMembers.stream.filter(!me).collectt(tolist).get(0) )
        // - cancellarlo dal team,
        // -

    }

    private boolean isProfessorCourse(String courseName, String profId) {
        return courseRepo.getOne(courseName).getProfessors()
                .contains(profRepo.getOne(profId));
    }

}
