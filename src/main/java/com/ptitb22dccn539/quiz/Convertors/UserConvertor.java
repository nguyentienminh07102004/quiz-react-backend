package com.ptitb22dccn539.quiz.Convertors;

import com.ptitb22dccn539.quiz.Exceptions.DataInvalidException;
import com.ptitb22dccn539.quiz.Exceptions.ServerErrorException;
import com.ptitb22dccn539.quiz.Model.DTO.UserDTO;
import com.ptitb22dccn539.quiz.Model.Entity.RoleEntity;
import com.ptitb22dccn539.quiz.Model.Entity.UserEntity;
import com.ptitb22dccn539.quiz.Model.Request.User.UserUpdate;
import com.ptitb22dccn539.quiz.Model.Response.UserResponse;
import com.ptitb22dccn539.quiz.Repositoty.IRoleRepository;
import com.ptitb22dccn539.quiz.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserConvertor implements IConvertor<UserDTO, UserEntity, UserResponse> {
    private final ModelMapper modelMapper;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserEntity dtoToEntity(UserDTO dto) {
        if (!dto.getPassword().equals(dto.getRePassword())) {
            throw new DataInvalidException("Password and repeat password is invalid!");
        }
        UserEntity userEntity = modelMapper.map(dto, UserEntity.class);
        if(dto.getDateOfBirth() != null) {
            if (userEntity.getDateOfBirth().after(new Date(System.currentTimeMillis()))) {
                throw new DataInvalidException("Date of birth is invalid!");
            }
        }
        if (dto.getStatus() == null || !StringUtils.isNotBlank(dto.getStatus())) {
            userEntity.setStatus(UserStatus.ACTIVE);
        } else {
            userEntity.setStatus(UserStatus.valueOf(dto.getStatus()));
        }
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            List<RoleEntity> roles = dto.getRoles().stream()
                    .map(role -> roleRepository.findById(role)
                            .orElseThrow(() -> new DataInvalidException("Role %s does not exist!".formatted(role))))
                    .toList();
            userEntity.setRoles(roles);
        } else {
            RoleEntity role = roleRepository.findById("USER")
                    .orElseGet(() -> roleRepository.save(new RoleEntity("Người dùng", "USER")));
            userEntity.setRoles(List.of(role));
        }
        return userEntity;
    }

    @Override
    public UserResponse entityToResponse(UserEntity entity) {
        UserResponse response = modelMapper.map(entity, UserResponse.class);
        if(entity.getDateOfBirth() != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateOfBirth = formatter.format(new java.util.Date(entity.getDateOfBirth().getTime()));
                response.setDateOfBirth(dateOfBirth);
            } catch (Exception exception) {
                throw new ServerErrorException(exception.getMessage());
            }
        }
        response.setRoles(entity.getRoles().stream().map(RoleEntity::getCode).toList());
        return response;
    }

    public UserEntity updateToEntity(UserUpdate userUpdate) {
        UserEntity res = modelMapper.map(userUpdate, UserEntity.class);
        if (userUpdate.getRoles() != null && !userUpdate.getRoles().isEmpty()) {
            List<RoleEntity> roles = userUpdate.getRoles().stream()
                    .map(role -> roleRepository.findById(role)
                            .orElseThrow(() -> new DataInvalidException("Role %s does not exist!".formatted(role))))
                    .toList();
            res.setRoles(roles);
        }
        return res;
    }
}
