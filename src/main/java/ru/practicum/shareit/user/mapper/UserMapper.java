package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.controller.dto.UserRequest;
import ru.practicum.shareit.user.controller.dto.UserResponse;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
/*    @Mapping(source = "userRequest.id", target = "id")
    @Mapping(source = "userRequest.name", target = "name")
    @Mapping(source = "userRequest.email", target = "email")*/
    User toUser(UserRequest userRequest);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

}
