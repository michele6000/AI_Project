package it.polito.ai.project.services;

import it.polito.ai.project.dtos.StudentDTO;
import it.polito.ai.project.dtos.TeamDTO;
import it.polito.ai.project.dtos.VMDTO;
import it.polito.ai.project.dtos.VMTypeDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface VmService {
    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<VMDTO> getTeamVMs(Long teamId);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Long createVMType(VMTypeDTO vmType, String courseName);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    String getTeamStat(Long teamId);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean setVMType(String courseName, Long vmtId);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    VMTypeDTO getVMType(String courseName);

//    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    VMDTO getVMConfig(Long vmId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean modifyVMConfiguration(Long vmId, VMDTO vm);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean modifyVMOwner(Long vmId, String studentID);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean addVMOwner(Long vmId, String studentID);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    List<StudentDTO> getVMOwners(Long vmId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean powerVMOn(Long vmId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean powerVMOff(Long vmId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    Boolean deleteVM(Long vmId);

    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN')")
    VMDTO createVmInstance(Long teamId, VMDTO vm, String currentUsername);

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_PROFESSOR')")
    TeamDTO setTeamLimit(TeamDTO team);

//    @PreAuthorize("hasAnyRole('ROLE_STUDENT','ROLE_ADMIN', 'ROLE_PROFESSOR')")
    byte[] getVmImage(Long vmId);

     TeamDTO retriveTeamFromVm (Long vmId);
}
