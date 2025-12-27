package com.project.user_service.service;

import com.project.user_service.domain.dto.kafka.DataTransfer;
import com.project.user_service.domain.entity.users.Users;
import com.project.user_service.domain.enums.KafkaDataTransferFields;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaService {

    private KafkaTemplate<String , DataTransfer> kafkaTemplate;
    private static final String userTopic = "SaveUserDatabase";

    public void saveIntoUserDatabase(Users users){

        Map<String , String> map = new HashMap<>();
        map.put(KafkaDataTransferFields.ID.toString() , users.getId().toString());
        map.put(KafkaDataTransferFields.USERNAME.toString() , users.getUsername());
        map.put(KafkaDataTransferFields.DISPLAY_NAME.toString() , users.getDisplayName());
        map.put(KafkaDataTransferFields.BIO.toString() , users.getBio());

        DataTransfer transfer = DataTransfer
                .builder()
                .type(KafkaDataTransferFields.USER_SAVE.toString())
                .map(map)
                .build();

        try {
            kafkaTemplate.send(userTopic , transfer);
            log.info("User is send with ID ; {} and Username : {}" , users.getId().toString() , users.getUsername());
        }catch (Exception e){
            log.error("User saved is Failed with ID : {} and Username : {}" , users.getId().toString() , users.getUsername());
            log.error(e.getMessage());
        }
    }

}
