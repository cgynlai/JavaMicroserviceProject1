package com.cyl.api.users.service;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cyl.api.users.data.UserEntity;
import com.cyl.api.users.data.UsersRepository;
import com.cyl.api.users.shared.UserDto;

@Service
public class UserServiceImpl implements UsersService {
	
	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	public UserServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder ) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {
		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		 ModelMapper modelMapper =new ModelMapper();
		 modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		 UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);
		 //userEntity.setEncryptedPassword("test");
		 usersRepository.save(userEntity);
		 
		 UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
		return returnValue;
	}

}
