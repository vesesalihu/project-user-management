package com.xogito.project.user.management.mapper;

import com.xogito.project.user.management.dto.ProjectDTO;
import com.xogito.project.user.management.model.Project;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProjectUserMapper {
    public Project mapProjectDtoToProject(ProjectDTO projectDto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(projectDto, Project.class);
    }
}
