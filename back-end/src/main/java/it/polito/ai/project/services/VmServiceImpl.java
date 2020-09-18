package it.polito.ai.project.services;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.dtos.VMTypeDTO;
import it.polito.ai.project.entities.*;
import it.polito.ai.project.exceptions.*;
import it.polito.ai.project.repositories.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
public class VmServiceImpl implements VmService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private VMRepository vmRepo;

    @Autowired
    private VMTypeRepository vmtRepo;


    @Override
    public List<VMDTO> getTeamVMs(Long teamId) {
        if (!teamRepo.existsById(teamId))
            throw new TeamNotFoundException("Team not found!");

        return vmRepo.findAll()
                .stream()
                .filter(v -> v.getTeam().equals(teamRepo.getOne(teamId)))
                .map(v -> modelMapper.map(v, VMDTO.class))
                .collect(Collectors.toList())
                ;
    }

    @Override
    public Long createVMType(VMTypeDTO vmType, String courseName) {
        VMType vmt = new VMType();
        vmt.setDockerFile(vmType.getDockerFile());
        Long id = vmtRepo.save(vmt).getId();
        setVMType(courseName, id);
        return id;
    }

    @Override
    public String getTeamStat(Long teamId) {
        Optional<Team> optionalTeamEntity = teamRepo.findById(teamId);
        if (!optionalTeamEntity.isPresent()) {
            throw new TeamNotFoundException("Team not found!");
        }
        AtomicReference<Integer> totalRam = new AtomicReference<>(0);
        AtomicReference<Integer> totalCPU = new AtomicReference<>(0);
        AtomicReference<Integer> totalHdd = new AtomicReference<>(0);
        optionalTeamEntity.get().getVMInstance().forEach(vm -> {
            totalRam.updateAndGet(v -> v + vm.getRam());
            totalCPU.updateAndGet(v -> v + vm.getCpu());
            totalHdd.updateAndGet(v -> v + vm.getHdd());
        });
        return "Current usage:\nTotal Ram: " + totalRam.toString() + "\nTotal CPU: " + totalCPU.toString() + "\nTotal Hdd: " + totalHdd.toString();
    }

    @Override
    public Boolean setVMType(String courseName, Long vmtId) {
        Optional<Course> optionalCourseEntity = courseRepo.findById(courseName);
        Optional<VMType> optionalVMTypeEntity = vmtRepo.findById(vmtId);
        if (!optionalCourseEntity.isPresent()) {
            throw new CourseNotFoundException("Course not found!");
        }
        if (!optionalVMTypeEntity.isPresent()) {
            throw new VmNotFoundException("VMType not found!");
        }

        optionalCourseEntity.get().setVmType(optionalVMTypeEntity.get());
        optionalVMTypeEntity.get().getCourses().add(optionalCourseEntity.get());
        optionalCourseEntity.get().getTeams().forEach(t -> t.setVmType(optionalVMTypeEntity.get()));
        return true;

    }

    @Override
    public VMDTO getVMConfig(Long vmId) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }
        return modelMapper.map(optionalVMEntity.get(), VMDTO.class);
    }

    @Override
    public Boolean modifyVMConfiguration(Long vmId, VMDTO vm) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }


        Optional<Team> optionalTeamEntity = teamRepo.findById(optionalVMEntity.get().getTeam().getId());
        if (!optionalTeamEntity.isPresent()) {
            throw new TeamNotFoundException("Team not found!");
        }

        if (!optionalVMEntity.get().getStatus().equals("poweroff")) return false;
        if (vm.getRam() > optionalTeamEntity.get().getLimit_ram()) return false;
        if (vm.getCpu() > optionalTeamEntity.get().getLimit_cpu()) return false;
        if (vm.getHdd() > optionalTeamEntity.get().getLimit_hdd()) return false;

        optionalVMEntity.get().setRam(vm.getRam());
        optionalVMEntity.get().setCpu(vm.getCpu());
        optionalVMEntity.get().setHdd(vm.getHdd());
        return true;
    }

    @Override
    public Boolean modifyVMOwner(Long vmId, String studentID) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        Optional<Student> optionalStudentEntity = studentRepo.findById(studentID);

        System.out.println(studentID);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }
        if (!optionalStudentEntity.isPresent()) {
            throw new StudentNotFoundException("Student not found!");
        }

        Student tmp = optionalVMEntity.get().getOwners().get(0);
        optionalVMEntity.get().getOwners().clear();
        tmp.getVms().remove(optionalVMEntity.get());
        optionalVMEntity.get().getOwners().add(optionalStudentEntity.get());
        optionalStudentEntity.get().getVms().add(optionalVMEntity.get());

        return true;
    }

    @Override
    public Boolean addVMOwner(Long vmId, String studentID) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        Optional<Student> optionalStudentEntity = studentRepo.findById(studentID);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }
        if (!optionalStudentEntity.isPresent()) {
            throw new StudentNotFoundException("Student not found!");
        }
        optionalVMEntity.get().getOwners().add(optionalStudentEntity.get());
        optionalStudentEntity.get().getVms().add(optionalVMEntity.get());
        return true;
    }

    @Override
    public List<StudentDTO> getVMOwners(Long vmId) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }

        return vmRepo.getOne(vmId)
                .getOwners()
                .stream()
                .map(s -> modelMapper.map(s, StudentDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Boolean powerVMOn(Long vmId) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }

        Optional<Team> optionalTeamEntity = teamRepo.findById(optionalVMEntity.get().getTeam().getId());
        if (!optionalTeamEntity.isPresent()) {
            throw new TeamNotFoundException("Team not found!");
        }


        Long team = optionalVMEntity.get().getTeam().getId();
        Long type = optionalVMEntity.get().getVmType().getId();
        int max_instance = optionalTeamEntity.get().getLimit_active_instance();

        if (optionalVMEntity.get().getStatus().equals("poweroff"))
            if (vmRepo.findAll()
                    .stream()
                    .filter(vm -> vm.getTeam().getId().equals(team))
                    .filter(vm -> vm.getVmType().getId().equals(type))
                    .filter(vm -> vm.getStatus().equals("poweron"))
                    .count() <= max_instance) {
                optionalVMEntity.get().setStatus("poweron");
                return true;
            }

        return false;
    }

    @Override
    public Boolean powerVMOff(Long vmId) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }

        if (optionalVMEntity.get().getStatus().equals("poweron")) {
            optionalVMEntity.get().setStatus("poweroff");
            return true;
        }

        return false;
    }

    @Override
    public Boolean deleteVM(Long vmId) {
        Optional<VM> optionalVMEntity = vmRepo.findById(vmId);
        if (!optionalVMEntity.isPresent()) {
            throw new VmNotFoundException("Vm not found!");
        }

        optionalVMEntity.get().getTeam().getVMInstance().remove(optionalVMEntity.get());
        optionalVMEntity.get().getOwners().forEach(student -> student.getVms().remove(optionalVMEntity.get()));
        optionalVMEntity.get().getVmType().getVMs().remove(optionalVMEntity.get());
        vmRepo.delete(optionalVMEntity.get());
        return true;
    }

    @Override
    public VMDTO createVmInstance(Long teamId, VMDTO vm, String studentID) {
        Optional<Team> optionalTeamEntity = teamRepo.findById(teamId);
        if (!optionalTeamEntity.isPresent()) {
            throw new TeamNotFoundException("Team not found!");
        }

        Optional<Student> optionalStudentEntity = studentRepo.findById(studentID);
        if (!optionalStudentEntity.isPresent()) {
            throw new StudentNotFoundException("Student not found!");
        }

        Optional<VMType> optionalVMTypeEntity = Optional.ofNullable(optionalTeamEntity.get().getVmType());
        if (!optionalVMTypeEntity.isPresent()) {
            throw new TeamServiceException("You must wait, professor have to setup vmType of the course!");
        }


        boolean quota = false;
        if (vm.getRam() > optionalTeamEntity.get().getLimit_ram()) quota = true;
        if (vm.getCpu() > optionalTeamEntity.get().getLimit_cpu()) quota = true;
        if (vm.getHdd() > optionalTeamEntity.get().getLimit_hdd()) quota = true;
        if (vmRepo.findAll().stream()
                .filter(_vm -> _vm.getTeam().getId().equals(teamId))
                .filter(_vm -> _vm.getVmType().getId().equals(optionalVMTypeEntity.get().getId()))
                .count() + 1 > optionalTeamEntity.get().getLimit_instance())
            quota = true;

        if (quota)
            throw new TeamServiceException("Quota excedeed!");

        VM _vm = new VM();
        _vm.setStatus("poweroff");

        optionalVMTypeEntity.get().getVMs().add(_vm);
        _vm.setVmType(optionalVMTypeEntity.get());

        optionalTeamEntity.get().getVMInstance().add(_vm);
        _vm.setTeam(optionalTeamEntity.get());

        _vm.getOwners().add(optionalStudentEntity.get());
        optionalStudentEntity.get().getVms().add(_vm);

        _vm.setHdd(vm.getHdd());
        _vm.setCpu(vm.getCpu());
        _vm.setRam(vm.getRam());
        _vm.setAccessLink("localhost:4200/genericVmPage/" + teamId + "/" + optionalVMTypeEntity.get().getId());

        return modelMapper.map(vmRepo.save(_vm), VMDTO.class);
    }

    @Override
    public TeamDTO setTeamLimit(TeamDTO team) {
        Optional<Team> optionalTeamEntity = teamRepo.findById(team.getId());
        if (!optionalTeamEntity.isPresent())
            throw new TeamNotFoundException("Team not found!");

        optionalTeamEntity.get().setLimit_ram(team.getLimit_ram());
        optionalTeamEntity.get().setLimit_cpu(team.getLimit_cpu());
        optionalTeamEntity.get().setLimit_hdd(team.getLimit_hdd());
        optionalTeamEntity.get().setLimit_instance(team.getLimit_instance());
        optionalTeamEntity.get().setLimit_active_instance(team.getLimit_active_instance());
        return team;
    }
}
